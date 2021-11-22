/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import java.util.Map;
import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;
import org.tropicode.checker.JVM.JvmValue;

public class JvmInstanceFieldOperation extends JvmFieldOperation {
    public JvmInstanceFieldOperation(JvmOpCode opcode, String owner, String fieldName) {
        super(opcode, owner, fieldName);
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        switch (this.opcode) {
            case PUTFIELD:
                JvmValue value = ctx.pop();
                JvmValue.Reference obj = (JvmValue.Reference) ctx.pop();
                Map<String, JvmValue> fields = ctx.getObject(obj.getIdentifier()).getFields();
                fields.put(fieldName, value);
                break;
            case GETFIELD:
                obj = (JvmValue.Reference) ctx.pop();
                fields = ctx.getObject(obj.getIdentifier()).getFields();
                value = fields.get(fieldName);
                assert value != null;
                ctx.push(value);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String toString() {
        return super.toString() + " " + fieldName;
    }
}
