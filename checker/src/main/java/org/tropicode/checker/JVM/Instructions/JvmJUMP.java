/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.Instructions;

import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.Label;
import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;

@Log4j2
public class JvmJUMP extends JvmOperation {

    private final Label label;
    private final int stackValues;

    public JvmJUMP(JvmOpCode opcode, Label s, int i) {
        super(opcode);
        label = s;
        stackValues = i;
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        // Remove values used for jump check
        for (int i = 0; i < stackValues; i++) {
            ctx.pop();
        }
    }

    @Override
    public String toString() {
        return "    " + this.opcode.toString() + " " + this.label;
    }

    @Override
    public boolean shouldFallThrough() {
        return !(opcode == JvmOpCode.GOTO || opcode == JvmOpCode.GOTO_W);
    }
}
