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

import JVM.Instructions.JvmInstruction;
import JVM.Instructions.JvmJUMP;
import JVM.Instructions.JvmLabel;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
public class JvmInstructionNode {
    private JvmInstruction instruction;
    private Set<JvmInstructionNode> children;

    public JvmInstructionNode() {

    }

    public JvmInstruction getInstruction() {
        return instruction;
    }

    public void setInstruction(JvmInstruction instruction) {
        this.instruction = instruction;
    }

    public Set<JvmInstructionNode> getChildren() {
        return children;
    }

    public void setChildren(Set<JvmInstructionNode> children) {
        this.children = children;
    }

    public Set<JvmInstructionNode> getNext() {
        return children;
    }

    public static JvmInstructionNode fromList(List<JvmInstruction> instructions) {
        Map<String, List<JvmInstructionNode>> forwardJumpTable = new HashMap<>();
        Map<String, JvmInstructionNode> labels = new HashMap<>();
        HashSet<JvmInstruction> seen = new HashSet<>();
        return fromListInternal(instructions, 0, forwardJumpTable, labels, seen);
    }

    private static JvmInstructionNode fromListInternal(List<JvmInstruction> instructions, int index, Map<String, List<JvmInstructionNode>> forwardJumpTable, Map<String, JvmInstructionNode> labels, HashSet<JvmInstruction> seen) {
        if (index >= instructions.size()) { return null; }
        JvmInstruction next = instructions.get(index);
        JvmInstructionNode ins = new JvmInstructionNode();
        Set<JvmInstructionNode> children = new HashSet<>();
        ins.setInstruction(next);
        ins.setChildren(children);
        if (next instanceof JvmJUMP) {
            JvmJUMP jmp = (JvmJUMP) next;
            if (labels.containsKey(jmp.getLabel().toString())) {
                // Backward jump
                children.add(labels.get(jmp.getLabel().toString()));
            } else {
                // Forward jump
                if (!forwardJumpTable.containsKey(jmp.getLabel().toString())) {
                    forwardJumpTable.put(jmp.getLabel().toString(), new ArrayList<>());
                }
                forwardJumpTable.get(jmp.getLabel().toString()).add(ins);
            }
            switch (jmp.getOpcode()) {
                case GOTO:
                case GOTO_W:
                    fromListInternal(instructions, index + 1, forwardJumpTable, labels, seen);
                    break;
                default:
                    children.add(fromListInternal(instructions, index + 1, forwardJumpTable, labels, seen));
            }
        } else {
            if (next instanceof JvmLabel) {
                JvmLabel label = (JvmLabel) next;
                if (forwardJumpTable.containsKey(label.getLabel())) {
                    // If any forward jumps to this label, update their references
                    for (JvmInstructionNode jvmInstructionNode : forwardJumpTable.get(label.getLabel())) {
                        jvmInstructionNode.getChildren().add(ins);
                    }
                }
            }
            JvmInstructionNode nextIns = fromListInternal(instructions, index + 1, forwardJumpTable, labels, seen);
            if (nextIns != null) {
                children.add(nextIns);
            }
        }
        return ins;
    }

    public String getGraph() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph instructions {\n");
        for (String node : getNodes(new HashSet<>())) {
            sb.append(node).append("\n");
        }
        for (String con : getConnections(new HashSet<>())) {
            sb.append(con).append("\n");
        }
        sb.append("\n}");
        return sb.toString();
    }

    private List<String> getConnections(HashSet<JvmInstructionNode> seen) {
        if (seen.contains(this)) return new ArrayList<>();
        seen.add(this);
        List<String> connections = new ArrayList<>();
        for (JvmInstructionNode child : children) {
            connections.add(this.getNodeName() + " -> " + child.getNodeName() + ";");
            connections.addAll(child.getConnections(seen));
        }
        return connections;
    }

    private String getNodeName() {
        return "node" + System.identityHashCode(this.instruction);
    }

    private List<String> getNodes(HashSet<JvmInstructionNode> seen) {
        if (seen.contains(this)) return new ArrayList<>();
        seen.add(this);
        List<String> nodes = new ArrayList<>();
        nodes.add(getNodeName() + "[label=\"" + this.instruction.toString().trim() + "\"];");
        for (JvmInstructionNode child : this.children) {
            nodes.addAll(child.getNodes(seen));
        }
        return nodes;
    }
}
