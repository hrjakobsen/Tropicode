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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

public class JvmContex {

    private final Map<String, JvmArray> arrays = new HashMap<>();
    private Stack<JvmValue> stack = new Stack<>();
    private Map<String, JvmObject> heap = new HashMap<>();
    private Map<String, String> keys = new HashMap<>();
    private JvmValue[] locals = new JvmValue[65536];
    private Map<String, JvmClass> classes = new HashMap<>();
    private Map<String, JvmHeapSnapshot> snapshots = new HashMap<>();

    public Map<String, JvmClass> getClasses() {
        return classes;
    }

    public void push(JvmValue... values) {
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

    public JvmValue peek() {
        return stack.peek();
    }

    public void addKey(String key, String identifier) {
        keys.put(key, identifier);
    }

    public JvmValue allocateObject(String type) {
        String identifier = UUID.randomUUID().toString();
        JvmObject object = new JvmObject(type, identifier);
        heap.put(identifier, object);
        if (classes.containsKey(type) && classes.get(type).getProtocol() != null) {
            object.setProtocol(classes.get(type).getProtocol().deepCopy());
            object.setTainted(true);
        }
        for (String field : classes.get(type).getFields()) {
            object.getFields().put(field, JvmValue.UNKNOWN);
        }
        return new JvmValue.Reference(identifier);
    }

    public JvmObject getObject(String identifier) {
        return heap.get(identifier);
    }

    @Override
    public String toString() {
        return "JvmContex{" + stack.toString() + "}";
    }

    public void takeSnapshot(String label) {
        JvmHeapSnapshot snapshot = new JvmHeapSnapshot(heap);
        this.snapshots.put(label, snapshot);
    }

    public boolean compareToSnapshot(String label) {
        if (!snapshots.containsKey(label)) {
            return false;
        }
        JvmHeapSnapshot snapshot = snapshots.get(label);
        return snapshot.compareTo(heap);
    }

    public boolean hasSnapshot(String label) {
        return snapshots.containsKey(label);
    }

    public JvmContex copy() {
        JvmContex newContext = new JvmContex();
        newContext.locals = locals.clone();
        newContext.stack = (Stack<JvmValue>) stack.clone();
        HashMap<String, JvmObject> newHeap = new HashMap<>();
        for (String s : heap.keySet()) {
            newHeap.put(s, heap.get(s).copy());
        }
        newContext.heap = newHeap;
        newContext.snapshots = snapshots;
        newContext.classes = classes;
        HashMap<String, String> newKeys = new HashMap<>();
        for (String s : keys.keySet()) {
            newKeys.put(s, keys.get(s));
        }
        newContext.keys = newKeys;
        return newContext;
    }

    public JvmValue allocateArray(String type) {
        String identifier = UUID.randomUUID().toString();
        arrays.put(identifier, new JvmArray(identifier, type));
        return new JvmValue.Reference(identifier);
    }

    public String getKey(String key) {
        return keys.get(key);
    }
}
