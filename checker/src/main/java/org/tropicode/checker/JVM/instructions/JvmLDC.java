/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;
import org.tropicode.checker.JVM.JvmValue;

public class JvmLDC extends JvmOperation {
    private Object value;

    public JvmLDC(Object value) {
        super(JvmOpCode.LDC);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s)", super.toString(), value.getClass().getName(), value);
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        ctx.push(JvmValue.UNKNOWN);
    }
}
