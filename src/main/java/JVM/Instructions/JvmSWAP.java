/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import JVM.JvmContext;
import JVM.JvmOpCode;
import JVM.JvmValue;

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
