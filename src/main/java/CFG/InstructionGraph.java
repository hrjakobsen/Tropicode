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

package CFG;

import JVM.Instructions.JvmINVOKE;
import JVM.Instructions.JvmInstruction;
import JVM.Instructions.JvmJUMP;
import JVM.Instructions.JvmLabel;
import JVM.Instructions.JvmReturnOperation;
import JVM.JvmClass;
import JVM.JvmContex;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class InstructionGraph {

    private BasicBlock block;
    private List<InstructionGraph> connections = new ArrayList<>();

    public InstructionGraph(BasicBlock entry) {
        this.block = entry;
    }

    public static InstructionGraph fromList(List<JvmInstruction> instructions) {
        List<InstructionGraph> nodes = new ArrayList<>();
        InstructionGraph lastNode = new InstructionGraph(new BasicBlock());
        InstructionGraph returnNode = null;
        nodes.add(lastNode);
        Map<String, List<InstructionGraph>> forwardJumps = new HashMap<>();
        Map<String, InstructionGraph> jumpTable = new HashMap<>();
        int returns = 0;
        boolean shouldCoalesceReturns = false;

        for (JvmInstruction instruction : instructions) {
            if (instruction instanceof JvmLabel) {
                if (lastNode.getBlock().getInstructions().size() != 0) {
                    lastNode = new InstructionGraph(new BasicBlock());
                    nodes.add(lastNode);
                }
                jumpTable.put(((JvmLabel) instruction).getLabel(), lastNode);
            }
            lastNode.getBlock().insertInstruction(instruction);
            if (instruction instanceof JvmJUMP) {
                JvmJUMP jmpInstruction = (JvmJUMP) instruction;
                if (jumpTable.containsKey(jmpInstruction.getLabel().toString())) {
                    lastNode.getConnections()
                            .add(jumpTable.get(jmpInstruction.getLabel().toString()));
                } else {
                    if (!forwardJumps.containsKey(jmpInstruction.getLabel().toString())) {
                        forwardJumps.put(jmpInstruction.getLabel().toString(), new ArrayList<>());
                    }
                    forwardJumps.get(jmpInstruction.getLabel().toString()).add(lastNode);
                }
                lastNode = new InstructionGraph(new BasicBlock());
                nodes.add(lastNode);
            }
            if (instruction instanceof JvmINVOKE || instruction instanceof JvmReturnOperation) {
                lastNode = new InstructionGraph(new BasicBlock());
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
            if (instruction instanceof JvmJUMP) {
                switch (((JvmJUMP) instruction).getOpcode()) {
                    case GOTO:
                    case GOTO_W:
                        continue;
                    default:
                        break;
                }
            }
            if (shouldCoalesceReturns && instruction instanceof JvmReturnOperation) {
                if (returnNode == null) {
                    returnNode = new InstructionGraph(new BasicBlock(instruction));
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
        sb.append(getNodeName()).append("[shape=box,label=\"");
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

    public void explodeGraph(JvmContex ctx) {
        explodeGraphInternal(ctx, new HashSet<>());
    }

    private void explodeGraphInternal(JvmContex ctx, HashSet<InstructionGraph> seen) {
        if (seen.contains(this)) return;
        seen.add(this);
        if (block.getLastInstruction() instanceof JvmINVOKE) {
            JvmINVOKE call = (JvmINVOKE) block.getLastInstruction();
            JvmClass klass = ctx.getClasses().get(call.getOwner());
            if (klass != null) {
                InstructionGraph callNode =
                        klass.getMethods()
                                .get(call.getName() + call.getDescriptor())
                                .getInstructionGraph();
                callNode.setConnections(this.connections);
                this.connections =
                        new ArrayList<>() {
                            {
                                add(callNode);
                            }
                        };
            }
        }
        for (InstructionGraph child : getConnections()) {
            child.explodeGraphInternal(ctx, seen);
        }
    }
}
