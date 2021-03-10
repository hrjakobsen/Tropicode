/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
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

import Checker.Exceptions.CheckerException;
import Checker.Extractor.CodeExtractorClassVisitor;
import JVM.JvmClass;
import JVM.JvmContex;
import JVM.JvmInstructionNode;
import JVM.JvmMethod;
import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Log4j2
public class Main {
    public static void main(String[] args) throws IOException {
        try {
            final String ENTRYPOINT = "simplecall.Main";
            JvmClass klass = parseClass(ENTRYPOINT);

            Map<String, Typestate> protocols = new HashMap<>();

            protocols.put("simplecall/C1", getProtocol("simplecall.C1"));

            JvmContex ctx = new JvmContex();
            ctx.setProtocolStore(protocols);
            ctx.getClasses().put("simplecall/Main", klass);
            ctx.getClasses().put("simplecall/C1", parseClass("simplecall.C1"));

            JvmMethod m = klass.getMethods().get(1);

            JvmInstructionNode iGraph = m.getInstructionGraph();

            try {
                Path tempFile = Files.createTempFile(null, null);
                Files.writeString(tempFile, iGraph.getGraph());
                Runtime.getRuntime().exec("xdot " + tempFile.toAbsolutePath().toString());
            } catch (IOException ex) {
                log.debug(iGraph.getGraph());
            }
            checkGraph(iGraph, ctx, new HashSet<>());
        } catch (CheckerException ex) {
            System.err.println(ex.toString());
        }
    }

    private static boolean checkGraph(JvmInstructionNode node, JvmContex jvmContex, HashSet<JvmInstructionNode> seen) {
        if (seen.contains(node)) return true;
        seen.add(node);
        node.getInstruction().evaluateInstruction(jvmContex);
        for (JvmInstructionNode child : node.getChildren()) {
            if (!checkGraph(child, jvmContex.copy(), seen)) {
                return false;
            }
        }
        return true;
    }

    // Hack to get typestate before implementing all functionality
    private static Typestate getProtocol(String classname) throws IOException {
        return parseClass(classname).getProtocol();
    }

    private static JvmClass parseClass(String classname) throws IOException {
        ClassReader classReader = new ClassReader(classname);
        CodeExtractorClassVisitor cv = new CodeExtractorClassVisitor();
        classReader.accept(cv, ClassReader.SKIP_DEBUG);
        JvmClass klass = cv.getJvmClass();
        return klass;
    }
}
