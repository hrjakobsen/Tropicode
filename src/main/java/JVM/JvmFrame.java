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

package JVM;

import JVM.JvmMethod.AccessFlags;
import JVM.JvmValue.Reference;
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
    protected Object clone() throws CloneNotSupportedException {
        JvmFrame copy = (JvmFrame) super.clone();
        copy.locals = locals.clone();
        copy.operandStack = (Stack<JvmValue>) operandStack.clone();
        return copy;
    }

    @Override
    public String toString() {
        return String.format(
                "JvmFrame{ %d items on stack, %d locals }",
                this.operandStack.size(), this.locals.length);
    }
}
