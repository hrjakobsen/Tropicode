/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package JVM.Instructions;

import JVM.JvmContex;
import JVM.JvmOpCode;
import JVM.JvmValue;

import java.util.HashMap;
import java.util.Map;

public class JvmOperationFIELDOPERATION extends JvmOperation {
    private final String owner;
    private final String fieldName;

    public JvmOperationFIELDOPERATION(JvmOpCode opcode, String owner, String fieldName) {
        super(opcode);
        this.owner = owner;
        this.fieldName = fieldName;
    }

    @Override
    public void evaluateInstruction(JvmContex ctx) {
        switch (this.opcode) {
            case PUTFIELD:
                JvmValue value = ctx.pop();
                JvmValue.ObjectReference obj = (JvmValue.ObjectReference) ctx.pop();
                Map<String, JvmValue> fields = ctx.getObject(obj.getIdentifer()).getFields();
                fields.put(fieldName, value);
                break;
            case GETFIELD:
                obj = (JvmValue.ObjectReference) ctx.pop();
                fields = ctx.getObject(obj.getIdentifer()).getFields();
                value = fields.get(fieldName);
                assert value != null;
                ctx.push(value);
            case PUTSTATIC:
                ctx.getClasses().get(owner).getStaticFields().put(fieldName, ctx.pop());
            case GETSTATIC:
                value = ctx.getClasses().get(owner).getStaticFields().get(fieldName);
                assert value != null;
                ctx.push(value);
        }
    }
}
