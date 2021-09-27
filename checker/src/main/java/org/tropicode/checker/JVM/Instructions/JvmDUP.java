/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.Instructions;

import java.util.ArrayDeque;
import java.util.Queue;
import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;
import org.tropicode.checker.JVM.JvmValue;

public class JvmDUP extends JvmOperation {

    public JvmDUP(JvmOpCode opcode) {
        super(opcode);
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        // TODO: Check that this is actually safe with our abstractions of "Unknown" data
        int depth = 0;
        switch (this.opcode) {
            case DUP:
            case DUP2:
                break;
            case DUP_X1:
            case DUP_X2:
            case DUP2_X1:
            case DUP2_X2:
                depth = 1;
                break;
            default:
                throw new IllegalStateException("Invalid opcode");
        }
        JvmValue element = ctx.peek();
        Queue<JvmValue> popped = new ArrayDeque<>();

        for (int i = 0; i < depth; i++) {
            popped.add(ctx.pop());
        }

        ctx.push(element);

        for (int i = 0; i < depth; i++) {
            ctx.push(popped.poll());
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
