/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;

public class JvmNEWARRAY extends JvmOperation {

    public JvmNEWARRAY() {
        super(JvmOpCode.NEWARRAY);
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        ctx.pop(); // size of the array
        ctx.push(ctx.allocateArray(null));
    }
}
