/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;
import org.tropicode.checker.JVM.JvmValue;
import org.tropicode.checker.JVM.JvmValue.TaggedBoolean;

public class JvmReturnOperation extends JvmOperation {

    public JvmReturnOperation(JvmOpCode opcode) {
        super(opcode);
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        String returnType = ctx.popReturnType();
        JvmValue.Reference object = ctx.getCurrentFrame().getCalleeReference();
        if (returnType.equals("Z") && object != null) {
            // Boolean type
            ctx.pop();
            ctx.push(new TaggedBoolean(object));
        }
        ctx.deallocateFrame();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
