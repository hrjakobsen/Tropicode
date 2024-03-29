/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.checker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.ClassReader;
import org.tropicode.checker.JVM.JvmClass;
import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmMethod;
import org.tropicode.checker.JVM.JvmMethod.AccessFlags;
import org.tropicode.checker.JVM.JvmOpCode;
import org.tropicode.checker.JVM.instructions.JvmINVOKE;
import org.tropicode.checker.cfg.BasicBlock;
import org.tropicode.checker.cfg.InstructionGraph;
import org.tropicode.checker.checker.exceptions.CheckerException;
import org.tropicode.checker.checker.extractor.CodeExtractorClassVisitor;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Log4j2
@Command(
        name = "Tropicode",
        mixinStandardHelpOptions = true,
        description = "Checks JVM bytecode for typestate violations")
public class TropicodeRunner implements Runnable {

    @Parameters(
            index = "0",
            description =
                    "The fully qualified name of the class that contains "
                            + "the entrypoint method. Specified like "
                            + "\"org/tropicode/examples/simpleclass/Main\".")
    private String entryClass;

    @Option(
            names = {"-m", "--entry-method"},
            description =
                    "The fully qualified method signature of the entry method. Specified with"
                            + "JVM types like \"main([Ljava/lang/String;)V\"",
            defaultValue = "main([Ljava/lang/String;)V")
    private String entryMethod;

    @Option(names = "-d", description = "Display the instruction graph of the entry method")
    boolean displayGraph = false;

    @Option(names = "-f", description = "Use flow analysis ")
    boolean useFlowAnalysis = false;

    @Option(
            names = {"-i", "--ignore-file-location"},
            description = "Classpath ignore file location")
    String ignoreFileLocation = null;

    @Option(
            names = "-p",
            description =
                    "Base directories for protocols. Multiple directories are separated by :.")
    String protocolsDirectory = null;

    @Option(
            names = "--ignore-default-protocol-directory",
            description = "Ignore the protocols found in $HOME/.tropicode/protocols ")
    boolean ignoreDefaultProtocolDirectory = false;

    private static final Set<String> ignoreDependencies =
            new HashSet<>() {
                {
                    add("sun/security/.*");
                    add("java/lang/invoke/.*");
                    add("java/nio/.*");
                    add("sun/nio/.*");
                    add("java/util/.*");
                    add("java/util/concurrent/.*");
                    add("java/security/.*");
                    add("javax/crypto/.*");
                    add("jdk/internal/.*");
                    add("java/lang/[^O].*");
                    add("java/io/.*");
                }
            };

    private ClassFilter classFilter;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new TropicodeRunner()).execute(args);
        System.exit(exitCode);
    }

    private JvmClass parseClass(String classname, Queue<DependencyNode> classesToLoad)
            throws IOException {
        return parseClass(new DependencyNode(classname), classesToLoad);
    }

    private JvmClass parseClass(DependencyNode node, Queue<DependencyNode> classesToLoad)
            throws IOException {
        log.debug("Parsing " + node.getClassName());
        ClassReader classReader = new ClassReader(node.getClassName());
        CodeExtractorClassVisitor cv = new CodeExtractorClassVisitor();
        classReader.accept(cv, ClassReader.SKIP_DEBUG);
        JvmClass klass = cv.getJvmClass();
        for (String dependency : cv.getClassDependencies()) {
            if (classFilter.rejects(dependency)) {
                continue;
            }
            DependencyNode depNode = new DependencyNode(dependency);
            node.getChildren().add(depNode);
            classesToLoad.add(depNode);
            log.debug("Adding " + dependency);
        }
        return klass;
    }

    @Override
    public void run() {
        classFilter = new ClassFilter();
        classFilter.addPatterns(TropicodeRunner.ignoreDependencies);
        ProtocolResolver resolver =
                new ProtocolResolver(protocolsDirectory, ignoreDefaultProtocolDirectory);
        try {
            if (ignoreFileLocation == null) {
                String ignoreFile =
                        System.getProperty("user.home")
                                + File.separator
                                + ".tropicode"
                                + File.separator
                                + ".tropicodeignore";
                if (Files.exists(Path.of(ignoreFile))) {
                    classFilter.addFile(Path.of(ignoreFile));
                }
            } else {
                if (Files.exists(Path.of(ignoreFileLocation))) {
                    classFilter.addFile(Path.of(ignoreFileLocation));
                } else {
                    throw new CheckerException("Invalid ignore path");
                }
            }
            Queue<DependencyNode> classesToLoad = new ArrayDeque<>();
            DependencyNode mainDependencyNode = new DependencyNode(this.entryClass);
            classesToLoad.add(mainDependencyNode);

            JvmContext ctx = new JvmContext();
            while (!classesToLoad.isEmpty()) {
                DependencyNode nextClass = classesToLoad.poll();
                if (ctx.getClasses().containsKey(nextClass.getClassName())) {
                    continue;
                }
                JvmClass klass = parseClass(nextClass, classesToLoad);
                if (klass.getProtocol() == null) {
                    // try to resolve the protocol elsewhere
                    Optional<Typestate> protocol = resolver.resolve(klass);
                    if (protocol.isPresent()) {
                        klass.setProtocol(protocol.get());
                    }
                }
                ctx.getClasses().put(nextClass.getClassName(), klass);
            }

            JvmClass klass = ctx.getClasses().get(mainDependencyNode.getClassName());
            JvmMethod m = klass.getMethods().get(entryMethod);

            if (!m.is(AccessFlags.ACC_STATIC)) {
                log.error("Entry method must be static");
                System.exit(1);
            }

            ctx.allocateFrame(null, m, new ArrayList<>());

            InstructionGraph iGraph = m.getInstructionGraph();
            InstructionGraph staticInitializers =
                    staticInitializationGraph(ctx, mainDependencyNode.getDependencyList());
            iGraph = iGraph.prepend(staticInitializers);
            iGraph.explodeGraph(ctx);

            if (displayGraph) {
                Runtime.getRuntime().exec("killall xdot || true");
                iGraph.show();
            }

            GraphAnalyzer analyzer = new GraphEvaluationAnalyzer();

            if (useFlowAnalysis) {
                analyzer = new FlowAnalyzer();
            }

            analyzer.checkGraph(iGraph, ctx);
        } catch (CheckerException | IOException ex) {
            System.err.println(ex);
        }
    }

    InstructionGraph staticInitializationGraph(JvmContext ctx, List<String> classOrdering) {
        List<InstructionGraph> callNodes = new ArrayList<>();
        for (String className : classOrdering) {
            JvmClass klass = ctx.getClasses().get(className);
            if (klass.hasStaticConstructor()) {
                InstructionGraph fakeCallNode =
                        new InstructionGraph(
                                new BasicBlock(
                                        new JvmINVOKE(
                                                JvmOpCode.INVOKESTATIC,
                                                className,
                                                "<clinit>",
                                                "()V",
                                                false)));
                fakeCallNode.setDepth(0);
                callNodes.add(fakeCallNode);
            }
        }

        for (int i = 0; i < callNodes.size() - 1; i++) {
            callNodes.get(i).getConnections().add(callNodes.get(i + 1));
        }

        if (callNodes.size() > 0) {
            return callNodes.get(0);
        }
        return null;
    }
}
