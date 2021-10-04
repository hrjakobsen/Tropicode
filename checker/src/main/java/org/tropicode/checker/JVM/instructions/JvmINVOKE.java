/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmMethod;
import org.tropicode.checker.JVM.JvmObject;
import org.tropicode.checker.JVM.JvmOpCode;
import org.tropicode.checker.JVM.JvmValue;
import org.tropicode.checker.JVM.JvmValue.Reference;
import org.tropicode.checker.checker.exceptions.InvalidProtocolOperationException;

@Log4j2
public class JvmINVOKE extends JvmOperation implements ClassReference {

    private final String owner;
    private final String name;
    private final String descriptor;
    private final boolean isInterface;
    private boolean expanded = false;

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public JvmINVOKE(
            JvmOpCode opcode, String owner, String name, String descriptor, boolean isInterface) {
        super(opcode);
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
        this.isInterface = isInterface;
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        int numParams = countParameters(this.descriptor);
        boolean shouldTaint = false;
        List<JvmValue> args = new ArrayList<>();
        for (int i = 0; i < numParams; i++) {
            JvmValue val = ctx.pop();
            args.add(val);
            if (val instanceof Reference && !(val.isUnknownReference())) {
                if (ctx.getObject(((Reference) val).getIdentifier()).isTainted()) {
                    shouldTaint = true;
                }
            }
        }
        boolean hasReference = this.opcode != JvmOpCode.INVOKESTATIC;
        if (hasReference) {
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
                                    ctx.getObject(((Reference) arg).getIdentifier())
                                            .setTainted(true);
                                }
                            });
                }
                if (object.getProtocol() != null
                        && ctx.getCurrentFrame().getCalleeReference() != objRef) {
                    // perform typestate check
                    if (object.getProtocol().isAllowed(name.trim())) {
                        object.setProtocol(object.getProtocol().perform(name));
                    } else {
                        throw new InvalidProtocolOperationException(
                                object.getProtocol(), name.trim());
                    }
                }
                if (object.isTainted() && ctx.getClasses().containsKey(this.owner)
                        || isExpanded()) {
                    JvmMethod m = ctx.findMethod(this.owner, this.name, this.descriptor);
                    ctx.allocateFrame(objRef, m, args);
                }
            }
        } else if (ctx.getClasses().containsKey(this.owner)) {
            JvmMethod m = ctx.findMethod(this.owner, this.name, this.descriptor);
            ctx.allocateFrame(null, m, args);
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

    @Override
    public String getClassReference() {
        return this.getOwner();
    }
}
