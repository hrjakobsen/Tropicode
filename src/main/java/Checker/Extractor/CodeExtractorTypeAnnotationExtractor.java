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

package Checker.Extractor;

import Annotations.ArrayKeyLoad;
import Annotations.KeySet;
import JVM.Instructions.JvmInstruction;
import JVM.Instructions.JvmKEYLOAD;
import JVM.Instructions.JvmKEYSET;
import JVM.Instructions.JvmOperation;
import JVM.JvmMethod;
import JVM.JvmOpCode;
import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.TypePath;

import java.util.List;

import static org.objectweb.asm.Opcodes.ASM8;

@Log4j2
public class CodeExtractorTypeAnnotationExtractor extends AnnotationVisitor {
    private final int typeRef;
    private final TypePath typePath;
    private final String descriptor;
    private final boolean visible;
    private final JvmMethod method;

    public CodeExtractorTypeAnnotationExtractor(AnnotationVisitor annotationVisitor, int typeRef, TypePath typePath, String descriptor, boolean visible, JvmMethod method) {
        super(ASM8, annotationVisitor);
        this.typeRef = typeRef;
        this.typePath = typePath;
        this.descriptor = descriptor;
        this.visible = visible;
        this.method = method;
    }

    @Override
    public void visit(String name, Object value) {
        if (descriptor.equals(classNameToDescriptor(ArrayKeyLoad.class.getCanonicalName())) && name.equals("value")) {
            insertAfterLast(JvmOpCode.AALOAD, new JvmKEYLOAD((String) value));
        } else if (descriptor.equals(classNameToDescriptor(KeySet.class.getCanonicalName())) && name.equals("value")) {
            method.getInstructions().add(new JvmKEYSET((String) value));
        }
        super.visit(name, value);
    }

    private String classNameToDescriptor(String className) {
        return "L" + className.replaceAll("\\.", "/") + ";";
    }

    private void insertAfterLast(JvmOpCode opcode, JvmInstruction inst) {
        List<JvmInstruction> instructions = this.method.getInstructions();
        int lastIndex = instructions.size() - 1;
        for (int i = lastIndex; i >= 0; i--) {
            if (instructions.get(i) instanceof JvmOperation && ((JvmOperation) instructions.get(i)).getOpcode() == opcode) {
                instructions.add(i + 1, inst);
                return;
            }
        }
    }
}
