/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;
import org.tropicode.checker.JVM.JvmValue;
import org.tropicode.checker.JVM.MethodDescriptorExtractor;

public class JvmINVOKEDYNAMIC extends JvmUnsupportedOperation {
    private final String name;
    private final String descriptor;

    public JvmINVOKEDYNAMIC(String name, String descriptor) {
        super(JvmOpCode.INVOKEDYNAMIC);
        this.name = name;
        this.descriptor = descriptor;
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        MethodDescriptorExtractor descriptorExtractor =
                new MethodDescriptorExtractor(this.descriptor);
        int numParams = descriptorExtractor.getArgumentTypes().size();
        for (int i = 0; i < numParams; i++) {
            ctx.pop(); // args
        }
        if (descriptorExtractor.hasReturnValue()) {
            if (descriptorExtractor.returnsObject()) {
                ctx.push(JvmValue.UNKNOWN_REFERENCE);
            } else {
                ctx.push(JvmValue.UNKNOWN);
            }
        }
    }

    @Override
    public String toString() {
        return super.toString() + " " + this.name + this.descriptor;
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }
}
