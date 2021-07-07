/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import JVM.JvmContext;
import JVM.JvmOpCode;
import JVM.JvmValue;
import java.util.Map;

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
}
