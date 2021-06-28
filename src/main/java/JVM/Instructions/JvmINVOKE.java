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
import Checker.Exceptions.CheckerException;
import Checker.Exceptions.InvalidProtocolOperationException;
import JVM.JvmContex;
import JVM.JvmMethod;
import JVM.JvmObject;
import JVM.JvmOpCode;
import JVM.JvmValue;
import JVM.JvmValue.Reference;
import JVM.JvmValue.UnknownReference;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JvmINVOKE extends JvmOperation {

    private final String owner;
    private final String name;
    private final String descriptor;
    private final boolean isInterface;

    public JvmINVOKE(
            JvmOpCode opcode, String owner, String name, String descriptor, boolean isInterface) {
        super(opcode);
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
        this.isInterface = isInterface;
    }

    @Override
    public void evaluateInstruction(JvmContex ctx, GraphAnalyser analyser) {
        int numParams = countParameters(this.descriptor);
        boolean shouldTaint = false;
        List<JvmValue> args = new ArrayList<>();
        for (int i = 0; i < numParams; i++) {
            JvmValue val = ctx.pop();
            args.add(val);
            if (val instanceof Reference && !(val instanceof UnknownReference)) {
                if (ctx.getObject(((Reference) val).getIdentifier()).isTainted()) {
                    shouldTaint = true;
                }
            }
        }

        JvmValue.Reference objRef = (Reference) ctx.pop();
        if (objRef == JvmValue.UNKNOWN_REFERENCE) {
            log.warn(
                    String.format(
                            "Unchecked call to method {%s} on class {%s} on an unknown reference. Beware.",
                            this.name, this.owner));
        } else {
            JvmObject object = ctx.getObject(objRef.getIdentifier());
            if (shouldTaint) {
                object.setTainted(true);
            }
            if (object.isTainted()) {
                args.forEach(
                        arg -> {
                            if (arg instanceof Reference) {
                                ctx.getObject(((Reference) arg).getIdentifier()).setTainted(true);
                            }
                        });
            }
            if (object.getProtocol() != null
                    && ctx.getCurrentFrame().getCalleeReference() != objRef) {
                // perform typestate check
                if (object.getProtocol().isAllowed(name.trim())) {
                    object.setProtocol(object.getProtocol().perform(name));
                } else {
                    throw new InvalidProtocolOperationException(object.getProtocol(), name.trim());
                }
            }
            if (object.isTainted() && ctx.getClasses().containsKey(this.owner)) {
                log.debug("I should analyse " + this.owner + "/" + this.name + this.descriptor);
                JvmMethod m = ctx.findMethod(this.owner, this.name, this.descriptor);
                ctx.allocateFrame(objRef, m, args);
            } else {
                log.debug("I can skip " + this.owner + "/" + this.name + this.descriptor);
            }
        }
    }

    @Override
    public String toString() {
        return "    "
                + opcode.toString()
                + " "
                + owner
                + "."
                + name
                + descriptor
                + (isInterface ? "(interface)" : "");
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public boolean isInterface() {
        return isInterface;
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
