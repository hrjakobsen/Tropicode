/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import JVM.JvmContext;
import JVM.JvmOpCode;
import JVM.JvmValue;

public class JvmLDC extends JvmOperation {
    private Object value;

    public JvmLDC(Object value) {
        super(JvmOpCode.LDC);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s %s", super.toString(), value.getClass().getName());
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        ctx.push(JvmValue.UNKNOWN);
    }
}
