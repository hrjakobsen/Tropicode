/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     Tropicode is a Java bytecode analyser used to verify object protocols.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package Checker;

import CFG.InstructionGraph;
import Checker.Exceptions.CheckerException;
import Checker.Extractor.CodeExtractorClassVisitor;
import JVM.JvmClass;
import JVM.JvmContex;
import JVM.JvmMethod;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.ClassReader;
import picocli.CommandLine;
import picocli.CommandLine.Command;
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
                            + "the entrypoint method. Specified like \"simpleclass/Main\".")
    private String entryClass;

    @Parameters(
            index = "1",
            description =
                    "The fully qualified method signature of the entry method. Specified with"
                            + "JVM types like \"main([Ljava/lang/String;)V\"")
    private String entryMethod;

    private static final Set<String> ignoreDependencies =
            new HashSet<>() {
                {
                    add("java.lang.Object");
                    add("java.lang.System");
                    add("java.io.PrintStream");
                    add("java.lang.Enum");
                }
            };

    public static void main(String[] args) {
        int exitCode = new CommandLine(new TropicodeRunner()).execute(args);
        System.exit(exitCode);
    }

    private static boolean checkGraph(
            InstructionGraph node, JvmContex jvmContex, HashSet<InstructionGraph> seen) {
        if (seen.contains(node)) {
            return true;
        }
        seen.add(node);
        node.getBlock().evaluate(jvmContex);
        for (InstructionGraph next : node.getConnections()) {
            if (!checkGraph(next, jvmContex.copy(), seen)) {
                return false;
            }
        }
        return true;
    }

    private static JvmClass parseClass(String classname, Queue<String> classesToLoad)
            throws IOException {
        log.debug("Parsing " + classname);
        ClassReader classReader = new ClassReader(classname);
        CodeExtractorClassVisitor cv = new CodeExtractorClassVisitor();
        classReader.accept(cv, ClassReader.SKIP_DEBUG);
        JvmClass klass = cv.getJvmClass();
        if (!ignoreDependencies.contains(classname)) {
            classesToLoad.addAll(cv.getClassDependencies());
        }
        return klass;
    }

    @Override
    public void run() {
        try {
            Queue<String> classesToLoad = new ArrayDeque<>();
            classesToLoad.add(this.entryClass);

            JvmContex ctx = new JvmContex();

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

            InstructionGraph iGraph = m.getInstructionGraph();

            try {
                Path tempFile = Files.createTempFile(null, null);
                Files.writeString(tempFile, iGraph.getDotGraph());
                Runtime.getRuntime().exec("xdot " + tempFile.toAbsolutePath());
            } catch (IOException ex) {
                iGraph.dump();
            }
            checkGraph(iGraph, ctx, new HashSet<>());
        } catch (CheckerException | IOException ex) {
            System.err.println(ex);
        }
    }
}
