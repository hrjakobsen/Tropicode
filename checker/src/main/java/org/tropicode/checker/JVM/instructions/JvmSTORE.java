/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;
import org.tropicode.checker.JVM.JvmValue;

public class JvmSTORE extends JvmOperation {

    private final int index;

    public JvmSTORE(int opcode, int index) {
        super(JvmOpCode.getFromOpcode(opcode));

        this.index = index;
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        JvmValue val = ctx.pop();
        ctx.storeLocal(index, val);
    }

    @Override
    public String toString() {
        return super.toString() + " " + index;
    }
}
