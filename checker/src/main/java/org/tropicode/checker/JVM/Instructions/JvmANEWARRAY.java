/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.Instructions;

import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;

public class JvmANEWARRAY extends JvmOperation {

    private final String type;

    public JvmANEWARRAY(String type) {
        super(JvmOpCode.ANEWARRAY);
        this.type = type;
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        ctx.push(ctx.allocateArray(type));
    }
}
