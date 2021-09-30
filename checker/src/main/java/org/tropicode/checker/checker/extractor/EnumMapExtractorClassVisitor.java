/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.checker.extractor;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * This class extracts the integer array used for switch tables with the call to .ordinal(). It is
 * used to track which enum label corresponds to which jump label
 */
// TODO: 07/03/2021 Implement
public class EnumMapExtractorClassVisitor extends ClassVisitor {

    public EnumMapExtractorClassVisitor() {
        super(Opcodes.ASM8);
    }

    public EnumMapExtractorClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM8, cv);
    }

    @Override
    public MethodVisitor visitMethod(
            int access, String name, String descriptor, String signature, String[] exceptions) {
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return super.visitAnnotation(desc, visible);
    }
}
