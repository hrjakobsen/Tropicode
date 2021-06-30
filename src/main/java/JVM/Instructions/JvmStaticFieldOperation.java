/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     Tropicode is a Java bytecode analyser used to verify object protocols.
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

import CFG.GraphAnalyser;
import JVM.JvmClass;
import JVM.JvmContex;
import JVM.JvmOpCode;
import JVM.JvmValue;

public class JvmStaticFieldOperation extends JvmFieldOperation implements ClassReference {
    public JvmStaticFieldOperation(JvmOpCode opcode, String owner, String fieldName) {
        super(opcode, owner, fieldName);
    }

    @Override
    public void evaluateInstruction(JvmContex ctx, GraphAnalyser analyser) {
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
