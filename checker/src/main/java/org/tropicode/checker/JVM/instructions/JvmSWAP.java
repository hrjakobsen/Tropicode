/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;
import org.tropicode.checker.JVM.JvmValue;

public class JvmSWAP extends JvmOperation {

    public JvmSWAP() {
        super(JvmOpCode.SWAP);
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        JvmValue val1 = ctx.pop();
        JvmValue val2 = ctx.pop();
        ctx.push(val1);
        ctx.push(val2);
    }
}
