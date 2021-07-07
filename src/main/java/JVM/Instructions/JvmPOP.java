/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import JVM.JvmContext;
import JVM.JvmOpCode;

public class JvmPOP extends JvmOperation {

    public JvmPOP() {
        super(JvmOpCode.POP);
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        ctx.pop();
    }
}
