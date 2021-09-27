/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.Instructions;

import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;

public class JvmNEW extends JvmOperation implements ClassReference {

    private final String type;

    public JvmNEW(String type) {
        super(JvmOpCode.NEW);
        this.type = type;
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        ctx.push(ctx.allocateObject(type));
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "    " + opcode.toString() + " " + type;
    }

    @Override
    public String getClassReference() {
        return this.getType();
    }
}
