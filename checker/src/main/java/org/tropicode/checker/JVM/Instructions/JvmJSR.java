/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.Instructions;

import org.objectweb.asm.Label;
import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;

public class JvmJSR extends JvmOperation {

    private final Label label;

    public JvmJSR(Label label) {
        super(JvmOpCode.JSR);
        this.label = label;
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        throw new UnsupportedOperationException("JSR instruction not supported");
    }
}
