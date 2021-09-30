/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;

public class JvmLOAD extends JvmOperation {

    private final int index;

    public JvmLOAD(int opcode, int i) {
        super(JvmOpCode.getFromOpcode(opcode));
        index = i;
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        ctx.push(ctx.getLocal(index));
    }

    @Override
    public String toString() {
        return super.toString() + " " + index;
    }
}
