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
import Checker.Extractor.CodeExtractorVisitor;
import JVM.Instructions.JvmInstruction;
import JVM.JvmClass;
import JVM.JvmContex;
import JVM.JvmMethod;
import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class Main {
    public static void main(String[] args) throws IOException {
        try {
            final String ENTRYPOINT = "simplecall.Main";
            ClassReader classReader = new ClassReader(ENTRYPOINT);
            CodeExtractorVisitor cv = new CodeExtractorVisitor();
            classReader.accept(cv, ClassReader.SKIP_DEBUG);

            Map<String, Typestate> protocols = new HashMap<>();

            protocols.put("simplecall/C1", getProtocol("simplecall.C1"));
            log.debug(protocols.get("simplecall/C1"));

            JvmClass klass = cv.getJvmClass();
            JvmContex ctx = new JvmContex();
            ctx.setProtocolStore(protocols);

            JvmMethod m = klass.getMethods().get(1);
            List<JvmInstruction> instructions = m.getInstructions();
            for (int i = 0; i < instructions.size(); i++) {
                JvmInstruction instruction = instructions.get(i);
                log.debug(instruction);
                instruction.evaluateInstruction(ctx);
                log.debug(ctx);
            }
            simplecall.Main.main(new String[] {});
        } catch (CheckerException ex) {
            System.err.println(ex.toString());
        }
    }

    // Hack to get typestate before implementing all functionality
    private static Typestate getProtocol(String classname) throws IOException {
        ClassReader classReader = new ClassReader(classname);
        CodeExtractorVisitor cv = new CodeExtractorVisitor();
        classReader.accept(cv, ClassReader.SKIP_DEBUG);
        JvmClass klass = cv.getJvmClass();
        return klass.getProtocol();
    }
}
