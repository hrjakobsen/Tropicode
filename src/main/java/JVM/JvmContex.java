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

package JVM;

import Checker.Typestate;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

public class JvmContex {
    private Stack<JvmValue> stack = new Stack<>();
    private Map<String, JvmObject> heap = new HashMap<>();
    private final JvmValue[] locals = new JvmValue[65536];
    private Map<String, Typestate> protocolStore = new HashMap<>();

    public Map<String, Typestate> getProtocolStore() {
        return protocolStore;
    }

    public void setProtocolStore(Map<String, Typestate> protocolStore) {
        this.protocolStore = protocolStore;
    }

    public void push(JvmValue ... values) {
        for (JvmValue val : values) {
            stack.push(val);
        }
    }

    public void storeLocal(int i, JvmValue val) {
        locals[i] = val;
    }

    public JvmValue getLocal(int i) {
        return locals[i];
    }

    public JvmValue pop() {
        return stack.pop();
    }

    public  JvmValue peek() {
        return stack.peek();
    }

    public JvmValue allocateObject(String type) {
        String identifier = UUID.randomUUID().toString();
        JvmObject object = new JvmObject(type, identifier);
        heap.put(identifier, object);
        if (protocolStore.containsKey(type)) {
            object.setProtocol(protocolStore.get(type).deepCopy());
        }
        return new JvmValue.ObjectReference(identifier);
    }

    public JvmObject getObject(String identifier) {
        return heap.get(identifier);
    }

    @Override
    public String toString() {
        return "JvmContex{" +
                    stack.toString()
                + "}";
    }
}
