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
import JVM.JvmObject;
import JVM.JvmOpCode;
import JVM.JvmValue;

public class JvmINVOKE extends JvmOperation {
    private final String owner;
    private final String name;
    private final String descriptor;
    private final boolean isInterface;

    public JvmINVOKE(JvmOpCode opcode, String owner, String name, String descriptor, boolean isInterface) {
        super(opcode);
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
        this.isInterface = isInterface;
    }

    @Override
    public void evaluateInstruction(JvmContex ctx) {
        JvmValue.ObjectReference objRef = (JvmValue.ObjectReference)ctx.pop();
        JvmObject object = ctx.getObject(objRef.getIdentifer());
        if (object.getProtocol() != null) {
            // perform typestate check
            if (object.getProtocol().isAllowed(name.trim())) {
                object.setProtocol(object.getProtocol().perform(name));
            } else {
                throw new IllegalArgumentException("Invalid transition " + name + " for typestate " + object.getProtocol());
            }

        }
        boolean hasOutput = !descriptor.endsWith("V");
        int numParams = countParameters(this.descriptor);
        for (int i = 0; i < numParams; i++) {
            ctx.pop();
        }
        if (hasOutput) {
            ctx.push(JvmValue.UNKNOWN);
        }

    }

    @Override
    public String toString() {
        return "    " + opcode.toString() + " " + owner + "." + name + descriptor + (isInterface ? "(interface)" : "");
    }

    private int countParameters(String descriptor) {
        if (descriptor.length() < 2 || descriptor.charAt(0) != '(') {
            throw new IllegalArgumentException("Invalid parameter string: " + descriptor);
        }
        char[] chars = descriptor.toCharArray();
        int index = 1;
        int result = 0;
        while (chars[index] != ')') {
            char current = chars[index];
            if (current == 'L') {
                while (chars[index] != ';') {
                    index++;
                }
                result++;
            } else if (current != '[') {
                result++;
            }
            index++;
        }
        return result;
    }
}
