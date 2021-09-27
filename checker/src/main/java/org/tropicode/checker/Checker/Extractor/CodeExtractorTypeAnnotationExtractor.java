/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.Checker.Extractor;

import static org.objectweb.asm.Opcodes.ASM8;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.TypePath;
import org.tropicode.checker.Annotations.ArrayKeyLoad;
import org.tropicode.checker.Annotations.KeySet;
import org.tropicode.checker.JVM.Instructions.JvmInstruction;
import org.tropicode.checker.JVM.Instructions.JvmKEYLOAD;
import org.tropicode.checker.JVM.Instructions.JvmKEYSET;
import org.tropicode.checker.JVM.Instructions.JvmOperation;
import org.tropicode.checker.JVM.JvmMethod;
import org.tropicode.checker.JVM.JvmOpCode;

@Log4j2
public class CodeExtractorTypeAnnotationExtractor extends AnnotationVisitor {

    private final int typeRef;
    private final TypePath typePath;
    private final String descriptor;
    private final boolean visible;
    private final JvmMethod method;

    public CodeExtractorTypeAnnotationExtractor(
            AnnotationVisitor annotationVisitor,
            int typeRef,
            TypePath typePath,
            String descriptor,
            boolean visible,
            JvmMethod method) {
        super(ASM8, annotationVisitor);
        this.typeRef = typeRef;
        this.typePath = typePath;
        this.descriptor = descriptor;
        this.visible = visible;
        this.method = method;
    }

    @Override
    public void visit(String name, Object value) {
        if (descriptor.equals(classNameToDescriptor(ArrayKeyLoad.class.getCanonicalName()))
                && name.equals("value")) {
            insertAfterLast(JvmOpCode.AALOAD, new JvmKEYLOAD((String) value));
        } else if (descriptor.equals(classNameToDescriptor(KeySet.class.getCanonicalName()))
                && name.equals("value")) {
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
            if (instructions.get(i) instanceof JvmOperation
                    && ((JvmOperation) instructions.get(i)).getOpcode() == opcode) {
                instructions.add(i + 1, inst);
                return;
            }
        }
    }
}
