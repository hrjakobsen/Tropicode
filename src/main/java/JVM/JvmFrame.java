/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM;

import Checker.Exceptions.CheckerException;
import JVM.JvmMethod.AccessFlags;
import JVM.JvmValue.Reference;
import java.util.Arrays;
import java.util.Stack;

public class JvmFrame implements Cloneable {
    Stack<JvmValue> operandStack = new Stack<>();
    JvmValue[] locals;
    JvmMethod method;
    boolean hasInstance;

    public JvmFrame(JvmMethod m) {
        this.method = m;
        this.locals = new JvmValue[m.getNumberOfLocalVariables() + 1];
        this.hasInstance = !m.is(AccessFlags.ACC_STATIC);
    }

    public Stack<JvmValue> getStack() {
        return operandStack;
    }

    public JvmValue[] getLocals() {
        return locals;
    }

    public JvmValue.Reference getCalleeReference() {
        if (hasInstance) {
            return (Reference) this.locals[0];
        }
        return null;
    }

    public boolean hasReturnValue() {
        return this.method.hasReturnValue();
    }

    @Override
    protected Object clone() {
        JvmFrame copy = null;
        try {
            copy = (JvmFrame) super.clone();
            copy.locals = locals.clone();
            copy.operandStack = (Stack<JvmValue>) operandStack.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new CheckerException("Invalid clone");
        }
    }

    public JvmFrame copy() {
        return (JvmFrame) clone();
    }

    @Override
    public String toString() {
        return String.format(
                "JvmFrame{ %d items on stack, %d locals }",
                this.operandStack.size(), this.locals.length);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JvmFrame)) return false;
        JvmFrame other = (JvmFrame) obj;
        boolean localEquality = Arrays.equals(locals, other.locals);
        boolean stackEquality = operandStack.equals(other.operandStack);
        return localEquality && stackEquality;
    }
}
