/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.tropicode.checker.JVM.JvmMethod.AccessFlags;
import org.tropicode.checker.checker.exceptions.CheckerException;

@Log4j2
public class JvmContext {

    private final Map<String, JvmArray> arrays = new HashMap<>();
    private Stack<JvmFrame> frames = new Stack<>();
    private Map<String, JvmObject> heap = new HashMap<>();
    private Map<String, String> keys = new HashMap<>();
    private Map<String, JvmClass> classes = new HashMap<>();
    private Map<String, JvmHeapSnapshot> snapshots = new HashMap<>();
    private Stack<JvmExceptionFrame> exceptionHandlerStack = new Stack<>();

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
        if (!heap.containsKey(identifier)) {
            throw new CheckerException("Attempting to look up object that is not in the heap");
        }
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
        Stack<JvmFrame> newFrames = new Stack<>();
        newFrames.addAll(frames.stream().map(JvmFrame::copy).collect(Collectors.toList()));
        newContext.frames = newFrames;
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
        newContext.exceptionHandlerStack = new Stack<>();
        newContext.exceptionHandlerStack.addAll(
                exceptionHandlerStack.stream()
                        .map(JvmExceptionFrame::clone)
                        .collect(Collectors.toList()));
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

    public List<String> findDifferences(JvmContext ctx) {
        List<String> differences = new ArrayList<>();
        for (Entry<String, JvmObject> entry : heap.entrySet()) {
            if (!ctx.heap.containsKey(entry.getKey())) {
                differences.add("Missing object " + entry.getKey());
            }
            if (!entry.getValue().getFields().equals(getObject(entry.getKey()).fields)) {
                differences.add("Fields of " + entry.getKey() + " are different");
            }
            if (entry.getValue().getProtocol() != getObject(entry.getKey()).getProtocol()) {
                if (entry.getValue().getProtocol() != null
                        && !entry.getValue()
                                .getProtocol()
                                .equals(getObject(entry.getKey()).getProtocol())) {
                    differences.add(
                            "Protocol of object "
                                    + entry.getKey()
                                    + " should be "
                                    + entry.getValue().getProtocol().toString()
                                    + " but it is "
                                    + getObject(entry.getKey()).getProtocol().toString());
                }
            }
        }
        for (Entry<String, JvmObject> entry : ctx.heap.entrySet()) {
            if (!heap.containsKey(entry.getKey())) {
                differences.add("A new object " + entry.getKey() + " is present");
            }
        }
        if (!frames.equals(ctx.frames)) {
            differences.add("Different stacks");
        }

        if (!exceptionHandlerStack.equals(ctx.exceptionHandlerStack)) {
            differences.add("Different exception handler stacks");
        }
        return differences;
    }

    public boolean isInsideExceptionHandler() {
        return !exceptionHandlerStack.empty();
    }

    public void enterExceptionHandler(List<JvmExceptionHandler> handlers) {
        exceptionHandlerStack.push(new JvmExceptionFrame(handlers));
    }

    public void registerMethodCallForObject(JvmObject object) {}

    public void exitExceptionHandler() {
        exceptionHandlerStack.pop();
    }

    public void exitExceptionHandler(JvmExceptionHandler handler) {
        if (exceptionHandlerStack.empty()) {
            throw new CheckerException(
                    "Empty exception handler stack when attempting to exit a try statement");
        }
        if (!exceptionHandlerStack.pop().getHandlers().contains(handler)) {
            throw new CheckerException("Invalid exception handler exited");
        }
    }

    public int heapSize() {
        return heap.size();
    }
}
