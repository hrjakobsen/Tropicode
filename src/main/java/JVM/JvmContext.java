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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JvmContext {

    private final Map<String, JvmArray> arrays = new HashMap<>();
    private Stack<JvmFrame> frames = new Stack<>();
    private Map<String, JvmObject> heap = new HashMap<>();
    private Map<String, String> keys = new HashMap<>();
    private Map<String, JvmClass> classes = new HashMap<>();
    private Map<String, JvmHeapSnapshot> snapshots = new HashMap<>();

    public Map<String, JvmClass> getClasses() {
        return classes;
    }

    public JvmFrame getCurrentFrame() {
        return frames.peek();
    }

    public void allocateFrame(JvmValue.Reference objRef, JvmMethod method, List<JvmValue> args) {
        this.frames.push(new JvmFrame(method));
        int startIndex = 0;
        if (!method.is(AccessFlags.ACC_STATIC)) {
            startIndex = 1;
            storeLocal(0, objRef);
        }
        for (int i = 0; i < args.size(); i++) {
            storeLocal(i + startIndex, args.get(i));
        }
    }

    public void push(JvmValue... values) {
        for (JvmValue val : values) {
            getCurrentFrame().getStack().push(val);
        }
    }

    public void storeLocal(int i, JvmValue val) {
        getCurrentFrame().getLocals()[i] = val;
    }

    public JvmValue getLocal(int i) {
        return getCurrentFrame().getLocals()[i];
    }

    public JvmValue pop() {
        return getCurrentFrame().getStack().pop();
    }

    public JvmValue peek() {
        return getCurrentFrame().getStack().peek();
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
        return "JvmContext{" + frames.toString() + "}";
    }

    public void takeSnapshot(String label) {
        JvmHeapSnapshot snapshot = new JvmHeapSnapshot(heap);
        this.snapshots.put(label, snapshot);
    }

    public boolean isEqualToSnapshot(String label, List<String> errors) {
        if (!snapshots.containsKey(label)) {
            return false;
        }
        JvmHeapSnapshot snapshot = snapshots.get(label);
        return snapshot.compareTo(heap, errors);
    }

    public boolean hasSnapshot(String label) {
        return snapshots.containsKey(label);
    }

    public JvmContext copy() {
        JvmContext newContext = new JvmContext();
        newContext.frames = (Stack<JvmFrame>) frames.clone();
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

    public JvmMethod findMethod(String owner, String name, String descriptor) {
        JvmClass klass = this.classes.get(owner);
        return klass.getMethods().get(name + descriptor);
    }

    public void deallocateFrame() {
        JvmFrame frame = this.frames.pop();
        if (frame.hasReturnValue()) {
            push(frame.getStack().pop());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JvmContext)) return false;
        JvmContext other = (JvmContext) obj;
        boolean frameEquality = other.frames.equals(frames);
        boolean heapEquality = other.heap.equals(heap);
        return frameEquality && heapEquality;
    }
}
