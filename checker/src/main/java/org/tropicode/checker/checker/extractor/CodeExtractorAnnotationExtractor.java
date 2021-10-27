/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.checker.extractor;

import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.tropicode.checker.JVM.JvmAnnotation;
import org.tropicode.checker.JVM.JvmClass;

@Log4j2
public class CodeExtractorAnnotationExtractor extends AnnotationVisitor {

    private final String descriptor;
    private final boolean visible;
    private final JvmClass klass;

    public CodeExtractorAnnotationExtractor(
            AnnotationVisitor annotationVisitor,
            String descriptor,
            boolean visible,
            JvmClass klass) {
        super(Opcodes.ASM8, annotationVisitor);
        this.klass = klass;
        this.descriptor = descriptor;
        this.visible = visible;
    }

    @Override
    public void visit(String name, Object value) {
        /*
        FIXME: This only captures primitive values
          (see https://asm.ow2.io/javadoc/org/objectweb/asm/AnnotationVisitor.html#visit(java.lang.String,java.lang.Object))
        */
        if (name.equals("value")) {
            klass.getAnnotations()
                    .put(this.descriptor, new JvmAnnotation(this.descriptor, this.visible, value));
        }
        super.visit(name, value);
    }
}
