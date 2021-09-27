/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.Checker.Extractor;

import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.tropicode.checker.Checker.Typestate;
import org.tropicode.checker.JVM.JvmClass;

@Log4j2
public class CodeExtractorAnnotationExtractor extends AnnotationVisitor {

    private final String descriptor;
    private final boolean visible;
    private final JvmClass klass;
    private boolean isProtocol = false;

    public CodeExtractorAnnotationExtractor(
            AnnotationVisitor annotationVisitor,
            String descriptor,
            boolean visible,
            JvmClass klass) {
        super(Opcodes.ASM8, annotationVisitor);
        this.klass = klass;
        this.descriptor = descriptor;
        this.visible = visible;
        if (descriptor.equals("LAnnotations/Protocol;")) {
            isProtocol = true;
        }
    }

    @Override
    public void visit(String name, Object value) {
        if (isProtocol && name.equals("value")) {
            klass.setProtocol(Typestate.getInitialObjectProtocol(value.toString()));
        }
        super.visit(name, value);
    }
}
