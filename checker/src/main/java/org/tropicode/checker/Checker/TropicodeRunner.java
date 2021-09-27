/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.Checker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.ClassReader;
import org.tropicode.checker.CFG.InstructionGraph;
import org.tropicode.checker.Checker.Exceptions.CheckerException;
import org.tropicode.checker.Checker.Extractor.CodeExtractorClassVisitor;
import org.tropicode.checker.JVM.JvmClass;
import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmMethod;
import org.tropicode.checker.JVM.JvmMethod.AccessFlags;
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

    @Parameters(
            index = "1",
            description =
                    "The fully qualified method signature of the entry method. Specified with"
                            + "JVM types like \"main([Ljava/lang/String;)V\"")
    private String entryMethod;

    @Option(names = "-d", description = "Display the instruction graph of the entry method")
    boolean displayGraph = false;

    @Option(names = "-f", description = "Use flow analysis ")
    boolean useFlowAnalysis = false;

    @Option(names = "-i", description = "Classpath ignore file location")
    String ignoreFileLocation = null;

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

    private JvmClass parseClass(String classname, Queue<String> classesToLoad) throws IOException {
        log.debug("Parsing " + classname);
        ClassReader classReader = new ClassReader(classname);
        CodeExtractorClassVisitor cv = new CodeExtractorClassVisitor();
        classReader.accept(cv, ClassReader.SKIP_DEBUG);
        JvmClass klass = cv.getJvmClass();
        for (String dependency : cv.getClassDependencies()) {
            if (classFilter.rejects(dependency)) {
                continue;
            }
            classesToLoad.add(dependency);
            log.debug("Adding " + dependency);
        }
        return klass;
    }

    @Override
    public void run() {
        classFilter = new ClassFilter();
        classFilter.addPatterns(TropicodeRunner.ignoreDependencies);
        try {
            if (ignoreFileLocation == null) {
                String ignoreFile =
                        System.getProperty("user.home") + File.separator + ".tropicodeignore";
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
            Queue<String> classesToLoad = new ArrayDeque<>();
            classesToLoad.add(this.entryClass);

            JvmContext ctx = new JvmContext();
            while (!classesToLoad.isEmpty()) {
                String nextClass = classesToLoad.poll();
                String compiledClassName = nextClass.replaceAll("/", ".");
                if (ctx.getClasses().containsKey(nextClass)) {
                    continue;
                }
                JvmClass klass = parseClass(compiledClassName, classesToLoad);
                ctx.getClasses().put(nextClass, klass);
            }

            JvmClass klass = ctx.getClasses().get(entryClass);
            JvmMethod m = klass.getMethods().get(entryMethod);

            if (!m.is(AccessFlags.ACC_STATIC)) {
                log.error("Entry method must be static");
                System.exit(1);
            }

            ctx.allocateFrame(null, m, new ArrayList<>());

            InstructionGraph iGraph = m.getInstructionGraph();
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
}
