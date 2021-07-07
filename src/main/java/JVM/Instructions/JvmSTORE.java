/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import JVM.JvmContext;
import JVM.JvmOpCode;
import JVM.JvmValue;

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
