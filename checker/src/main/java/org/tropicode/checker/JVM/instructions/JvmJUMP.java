/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.Label;
import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;
import org.tropicode.checker.JVM.JvmValue.TaggedBoolean;

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
        if (!ctx.isEmpty() && ctx.peek() instanceof TaggedBoolean taggedBoolean) {
            ctx.pop();
            ctx.setConditional(taggedBoolean);
            for (int i = 0; i < stackValues - 1; i++) {
                ctx.pop();
            }
        } else {
            for (int i = 0; i < stackValues; i++) {
                ctx.pop();
            }
        }
    }

    public boolean isConditional() {
        return this.opcode != JvmOpCode.GOTO && this.opcode != JvmOpCode.GOTO_W;
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
