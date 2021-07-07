/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import JVM.JvmContext;
import JVM.JvmOpCode;

public class JvmDUP extends JvmOperation {

    public JvmDUP() {
        super(JvmOpCode.DUP);
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        ctx.push(ctx.peek());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
