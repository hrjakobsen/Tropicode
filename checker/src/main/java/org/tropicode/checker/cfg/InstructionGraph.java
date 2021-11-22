/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.cfg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.Label;
import org.tropicode.checker.JVM.JvmClass;
import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmExceptionHandler;
import org.tropicode.checker.JVM.JvmMethod;
import org.tropicode.checker.JVM.JvmOpCode;
import org.tropicode.checker.JVM.instructions.JvmEnterTryBlock;
import org.tropicode.checker.JVM.instructions.JvmExitTryBlock;
import org.tropicode.checker.JVM.instructions.JvmHandleException;
import org.tropicode.checker.JVM.instructions.JvmINVOKE;
import org.tropicode.checker.JVM.instructions.JvmInstruction;
import org.tropicode.checker.JVM.instructions.JvmJUMP;
import org.tropicode.checker.JVM.instructions.JvmLOOKUPSWITCH;
import org.tropicode.checker.JVM.instructions.JvmLabel;
import org.tropicode.checker.JVM.instructions.JvmNEW;
import org.tropicode.checker.JVM.instructions.JvmReturnOperation;
import org.tropicode.checker.JVM.instructions.JvmStaticFieldOperation;
import org.tropicode.checker.JVM.instructions.JvmTABLESWITCH;

@Log4j2
public class InstructionGraph {

    private BasicBlock block;
    private List<InstructionGraph> connections = new ArrayList<>();

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    private int depth = 0;

    public InstructionGraph(BasicBlock entry) {
        this(entry, 0);
    }

    public InstructionGraph(BasicBlock entry, int depth) {
        this.block = entry;
        this.depth = depth;
    }

    public static InstructionGraph fromList(
            List<JvmInstruction> plainInstructions,
            List<JvmExceptionHandler> exceptionHandlers,
            int depth) {
        List<JvmInstruction> instructions =
                insertExceptionHandlers(plainInstructions, exceptionHandlers);
        if (instructions.size() == 0) instructions.add(new JvmReturnOperation(JvmOpCode.RETURN));
        List<InstructionGraph> nodes = new ArrayList<>();
        InstructionGraph lastNode = new InstructionGraph(new BasicBlock(), depth);
        InstructionGraph returnNode = null;
        nodes.add(lastNode);
        Map<String, List<InstructionGraph>> forwardJumps = new HashMap<>();
        Map<String, InstructionGraph> jumpTable = new HashMap<>();
        int returns = 0;
        boolean shouldCoalesceReturns = false;
        Stack<List<String>> exceptionHandlerStack = new Stack<>();

        for (JvmInstruction instruction : instructions) {
            if (instruction instanceof JvmLabel) {
                if (lastNode.getBlock().getInstructions().size() != 0) {
                    lastNode = new InstructionGraph(new BasicBlock(), depth);
                    nodes.add(lastNode);
                }
                jumpTable.put(((JvmLabel) instruction).getLabel(), lastNode);
            }

            if (instruction instanceof JvmEnterTryBlock) {
                exceptionHandlerStack.push(
                        ((JvmEnterTryBlock) instruction)
                                .getHandlers().stream()
                                        .map(JvmExceptionHandler::getTarget)
                                        .map(Object::toString)
                                        .collect(Collectors.toList()));
            }

            if (instruction instanceof JvmExitTryBlock) {
                for (String label : exceptionHandlerStack.pop()) {
                    if (!forwardJumps.containsKey(label)) {
                        forwardJumps.put(label, new ArrayList<>());
                    }
                    if (!forwardJumps.get(label).contains(lastNode)) {
                        forwardJumps.get(label).add(lastNode);
                    }
                }
            }

            if (instruction instanceof JvmEnterTryBlock
                    || instruction instanceof JvmExitTryBlock
                    || instruction instanceof JvmHandleException) {
                if (lastNode.getBlock().getInstructions().size() != 0) {
                    lastNode = new InstructionGraph(new BasicBlock(), depth);
                    nodes.add(lastNode);
                }
            }
            lastNode.getBlock().insertInstruction(instruction);

            if (instruction instanceof JvmJUMP) {
                JvmJUMP jmpInstruction = (JvmJUMP) instruction;
                addJumpsToLabels(
                        lastNode, forwardJumps, jumpTable, jmpInstruction.getLabel(), null);
                lastNode = new InstructionGraph(new BasicBlock(), depth);
                nodes.add(lastNode);
            }
            if (instruction instanceof JvmLOOKUPSWITCH) {
                JvmLOOKUPSWITCH lookupSwitch = (JvmLOOKUPSWITCH) instruction;
                addJumpsToLabels(
                        lastNode,
                        forwardJumps,
                        jumpTable,
                        lookupSwitch.getDefaultLabel(),
                        lookupSwitch.getLabels());
                lastNode = new InstructionGraph(new BasicBlock(), depth);
                nodes.add(lastNode);
            }

            if (instruction instanceof JvmTABLESWITCH) {
                JvmTABLESWITCH tableSwitch = (JvmTABLESWITCH) instruction;
                addJumpsToLabels(
                        lastNode,
                        forwardJumps,
                        jumpTable,
                        tableSwitch.getDefaultLabel(),
                        tableSwitch.getLabels());
                lastNode = new InstructionGraph(new BasicBlock(), depth);
                nodes.add(lastNode);
            }

            if (instruction instanceof JvmINVOKE
                    || instruction instanceof JvmReturnOperation
                    || instruction instanceof JvmStaticFieldOperation
                    || instruction instanceof JvmNEW) {
                lastNode = new InstructionGraph(new BasicBlock(), depth);
                nodes.add(lastNode);
            }
            if (instruction instanceof JvmReturnOperation) {
                if (++returns > 1) {
                    shouldCoalesceReturns = true;
                }
            }
        }

        for (Map.Entry<String, List<InstructionGraph>> entry : forwardJumps.entrySet()) {
            for (InstructionGraph node : entry.getValue()) {
                node.getConnections().add(jumpTable.get(entry.getKey()));
            }
        }

        for (int i = 0; i < nodes.size() - 1; i++) {
            JvmInstruction instruction = nodes.get(i).getBlock().getLastInstruction();
            if (!instruction.shouldFallThrough()) {
                continue;
            }
            if (shouldCoalesceReturns && instruction instanceof JvmReturnOperation) {
                if (returnNode == null) {
                    returnNode = new InstructionGraph(new BasicBlock(instruction), depth);
                }
                nodes.get(i)
                        .getBlock()
                        .getInstructions()
                        .remove(nodes.get(i).getBlock().getInstructions().size() - 1);
                nodes.get(i).getConnections().add(returnNode);
                continue;
            }
            if (!(i == nodes.size() - 2
                    && nodes.get(i + 1).getBlock().getInstructions().size() == 0)) {
                nodes.get(i).getConnections().add(nodes.get(i + 1));
            }
        }

        return nodes.get(0);
    }

    private static List<JvmInstruction> insertExceptionHandlers(
            List<JvmInstruction> instructions, List<JvmExceptionHandler> exceptionHandlers) {
        List<JvmInstruction> instructionsWithHandlers = new ArrayList<>();
        for (JvmInstruction instruction : instructions) {
            if (instruction instanceof JvmLabel) {
                // Check if label is exit point of an exception
                Stack<JvmExceptionHandler> added =
                        exceptionHandlers.stream()
                                .filter(
                                        ex ->
                                                ex.getTo()
                                                        .toString()
                                                        .equals(
                                                                ((JvmLabel) instruction)
                                                                        .getLabel()))
                                .collect(Collectors.toCollection(Stack::new));
                while (!added.isEmpty()) {
                    JvmExceptionHandler next = added.peek();
                    List<JvmExceptionHandler> matches =
                            added.stream()
                                    .filter(
                                            ex ->
                                                    ex.getFrom().equals(next.getFrom())
                                                            && ex.getTo().equals(next.getTo()))
                                    .collect(Collectors.toList());
                    instructionsWithHandlers.add(new JvmExitTryBlock(matches));
                    added.removeAll(matches);
                }

                instructionsWithHandlers.add(instruction);

                // Check if label is exception handler
                boolean exceptionHandler =
                        exceptionHandlers.stream()
                                .anyMatch(
                                        ex ->
                                                ex.getTarget()
                                                        .toString()
                                                        .equals(
                                                                ((JvmLabel) instruction)
                                                                        .getLabel()));
                if (exceptionHandler) {
                    instructionsWithHandlers.add(new JvmHandleException());
                }

                // Check if label is entry point of an exception

                added =
                        exceptionHandlers.stream()
                                .filter(
                                        ex ->
                                                ex.getFrom()
                                                        .toString()
                                                        .equals(
                                                                ((JvmLabel) instruction)
                                                                        .getLabel()))
                                .collect(Collectors.toCollection(Stack::new));
                while (!added.isEmpty()) {
                    JvmExceptionHandler next = added.peek();
                    List<JvmExceptionHandler> matches =
                            added.stream()
                                    .filter(
                                            ex ->
                                                    ex.getFrom().equals(next.getFrom())
                                                            && ex.getTo().equals(next.getTo()))
                                    .collect(Collectors.toList());
                    instructionsWithHandlers.add(new JvmEnterTryBlock(matches));
                    added.removeAll(matches);
                }
            } else {
                instructionsWithHandlers.add(instruction);
            }
        }
        return instructionsWithHandlers;
    }

    private static void addJumpsToLabels(
            InstructionGraph node,
            Map<String, List<InstructionGraph>> forwardJumps,
            Map<String, InstructionGraph> jumpTable,
            Label defaultLabel,
            Label[] moreLabels) {
        ArrayList<Label> labels = new ArrayList<>();
        if (defaultLabel != null) {
            labels.add(defaultLabel);
        }
        if (moreLabels != null) {
            labels.addAll(List.of(moreLabels));
        }

        for (Label label : labels) {
            if (jumpTable.containsKey(label.toString())) {
                if (!node.getConnections().contains(jumpTable.get(label.toString()))) {
                    node.getConnections().add(jumpTable.get(label.toString()));
                }
            } else {
                if (!forwardJumps.containsKey(label.toString())) {
                    forwardJumps.put(label.toString(), new ArrayList<>());
                }
                if (!forwardJumps.get(label.toString()).contains(node)) {
                    forwardJumps.get(label.toString()).add(node);
                }
            }
        }
    }

    public List<InstructionGraph> getConnections() {
        return connections;
    }

    public void setConnections(List<InstructionGraph> connections) {
        this.connections = connections;
    }

    public BasicBlock getBlock() {
        return block;
    }

    public void setBlock(BasicBlock block) {
        this.block = block;
    }

    public String getDotGraph() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph instructions {\n");
        appendNodes(new HashSet<>(), sb);
        appendConnections(new HashSet<>(), sb);
        sb.append("\n}");
        return sb.toString();
    }

    public void appendNodes(Set<InstructionGraph> seen, StringBuilder sb) {
        if (seen.contains(this)) {
            return;
        }
        seen.add(this);
        String hex = Integer.toHexString(Math.max(0, Math.min(255, 255 - this.depth * 20)));
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        sb.append(getNodeName())
                .append(
                        String.format(
                                "[fillcolor=\"#%s%s%s\",style=filled,shape=box,label=\"",
                                hex, hex, hex));
        appendNodeLabel(sb);
        sb.append("\"];\n");
        for (InstructionGraph next : this.getConnections()) {
            next.appendNodes(seen, sb);
        }
    }

    private String getNodeName() {
        return String.valueOf(System.identityHashCode(this));
    }

    private void appendNodeLabel(StringBuilder sb) {
        for (JvmInstruction instruction : this.getBlock().getInstructions()) {
            if (instruction.getLineNumber() != -1) {
                sb.append("L\"").append(instruction.getLineNumber()).append("\": \"");
            }
            sb.append(instruction.toString().trim()).append("\n");
        }
    }

    private void appendConnections(HashSet<InstructionGraph> seen, StringBuilder sb) {
        if (seen.contains(this)) {
            return;
        }
        seen.add(this);

        for (InstructionGraph connection : this.getConnections()) {
            sb.append(getNodeName()).append(" -> ").append(connection.getNodeName()).append(";\n");
            connection.appendConnections(seen, sb);
        }
    }

    public void dump() {
        dump_internal(new HashSet<>());
    }

    private void dump_internal(HashSet<InstructionGraph> seen) {
        if (seen.contains(this)) {
            return;
        }
        seen.add(this);
        for (JvmInstruction instruction : this.getBlock().getInstructions()) {
            log.debug(instruction.toString());
        }

        for (InstructionGraph connection : this.getConnections()) {
            connection.dump_internal(seen);
        }
    }

    public void show() {
        try {
            Path tempFile = Files.createTempFile(null, null);
            Files.writeString(tempFile, this.getDotGraph());
            Runtime.getRuntime().exec("xdot " + tempFile.toAbsolutePath());
        } catch (IOException ex) {
            this.dump();
        }
    }

    public void explodeGraph(JvmContext ctx) {
        explodeGraphInternal(
                this,
                ctx,
                new HashSet<>(),
                new HashSet<>(),
                new Stack<>() {
                    {
                        push(null);
                    }
                });
    }

    private static void explodeGraphInternal(
            InstructionGraph entry,
            JvmContext ctx,
            Set<InstructionGraph> seen,
            Set<JvmClass> initialised,
            Stack<JvmMethod> expandedMethods) {
        Stack<InstructionGraph> next =
                new Stack<>() {
                    {
                        add(entry);
                    }
                };

        while (!next.isEmpty()) {
            InstructionGraph current = next.pop();
            if (seen.contains(current)) continue;
            seen.add(current);

            if (current.block.getLastInstruction() instanceof JvmINVOKE) {
                JvmINVOKE call = (JvmINVOKE) current.block.getLastInstruction();
                JvmClass klass = ctx.getClasses().get(call.getOwner());
                if (klass != null) {
                    call.setExpanded(true);
                    JvmMethod method =
                            klass.getMethods().get(call.getName() + call.getDescriptor());
                    if (expandedMethods.contains(method)) {
                        throw new IllegalStateException(
                                "Recursion encountered in method " + method.getName());
                    }
                    expandedMethods.push(method);
                    InstructionGraph callNode = method.getInstructionGraph(current.depth + 1);
                    callNode.setDepth(current.getDepth() + 1);
                    callNode.insertFinalConnections(current.connections, new HashSet<>());
                    current.connections =
                            new ArrayList<>() {
                                {
                                    add(callNode);
                                }
                            };
                }
            }

            if (current.block.getLastInstruction() instanceof JvmReturnOperation) {
                expandedMethods.pop();
            }

            for (InstructionGraph child : current.getConnections()) {
                next.push(child);
            }
        }
    }

    private void insertFinalConnections(
            List<InstructionGraph> connections, HashSet<InstructionGraph> seen) {
        if (seen.contains(this)) {
            return;
        }
        seen.add(this);
        if (this.connections.size() == 0) {
            this.connections = connections;
        } else {
            this.connections.forEach(n -> n.insertFinalConnections(connections, seen));
        }
    }

    @Override
    public String toString() {
        return block.toString();
    }

    public InstructionGraph prepend(InstructionGraph staticInitializers) {
        if (staticInitializers == null) {
            return this;
        }
        List<InstructionGraph> previousStart = new ArrayList<>();
        previousStart.add(this);
        staticInitializers.insertFinalConnections(previousStart, new HashSet<>());
        return staticInitializers;
    }
}
