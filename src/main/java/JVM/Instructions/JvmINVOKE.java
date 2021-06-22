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

import Checker.Exceptions.CheckerException;
import Checker.Exceptions.InvalidProtocolOperationException;
import JVM.JvmContex;
import JVM.JvmObject;
import JVM.JvmOpCode;
import JVM.JvmValue;
import lombok.extern.log4j.Log4j2;

@Log4j2
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
        boolean hasOutput = !descriptor.endsWith("V");
        int numParams = countParameters(this.descriptor);
        for (int i = 0; i < numParams; i++) {
            ctx.pop();
        }

        JvmValue.Reference objRef = (JvmValue.Reference)ctx.pop();
        if (objRef == JvmValue.UNKNOWN_REFERENCE) {
            log.warn(String.format("Unchecked call to method {%s} on class {%s} on an unknown reference. Beware.", this.name, this.owner));
        } else {
            JvmObject object = ctx.getObject(objRef.getIdentifer());
            if (object.getProtocol() != null) {
                // perform typestate check
                if (object.getProtocol().isAllowed(name.trim())) {
                    object.setProtocol(object.getProtocol().perform(name));
                } else {
                    throw new InvalidProtocolOperationException(object.getProtocol(), name.trim());
                }
            }
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
            throw new CheckerException("Invalid parameter string: " + descriptor);
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
