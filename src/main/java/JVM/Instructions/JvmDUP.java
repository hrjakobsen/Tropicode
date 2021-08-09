/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import JVM.JvmContext;
import JVM.JvmOpCode;
import JVM.JvmValue;
import java.util.ArrayDeque;
import java.util.Queue;

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
