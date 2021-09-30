/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import org.tropicode.checker.JVM.JvmClass;
import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;
import org.tropicode.checker.JVM.JvmValue;

public class JvmStaticFieldOperation extends JvmFieldOperation implements ClassReference {
    public JvmStaticFieldOperation(JvmOpCode opcode, String owner, String fieldName) {
        super(opcode, owner, fieldName);
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        switch (this.opcode) {
            case PUTSTATIC:
                ctx.getClasses().get(owner).getStaticFields().put(fieldName, ctx.pop());
                break;
            case GETSTATIC:
                // TODO: Should handle non-indexed classes better than this
                JvmClass klass = ctx.getClasses().get(owner);
                if (klass == null) {
                    ctx.push(JvmValue.UNKNOWN_REFERENCE);
                } else {
                    JvmValue value = klass.getStaticFields().get(fieldName);
                    assert value != null;
                    ctx.push(value);
                }
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String getClassReference() {
        return this.getOwner();
    }
}
