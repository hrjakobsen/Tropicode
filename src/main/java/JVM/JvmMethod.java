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

import CFG.BasicBlock;
import CFG.InstructionGraph;
import CFG.JvmInstructionNode;
import JVM.Instructions.JvmInstruction;

import java.util.ArrayList;
import java.util.List;

public class JvmMethod {
    int access;
    String name;
    String descriptor;
    String signature;
    List<JvmInstruction> instructions;

    public JvmMethod(int access, String name, String descriptor, String signature) {
        this(access, name, descriptor, signature, new ArrayList<>());
    }

    public JvmMethod(int access, String name, String descriptor, String signature, List<JvmInstruction> instructions) {
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.instructions = instructions;
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public InstructionGraph getInstructionGraph() {
        return InstructionGraph.fromList(this.instructions);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public List<JvmInstruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<JvmInstruction> instructions) {
        this.instructions = instructions;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (JvmInstruction inst : this.getInstructions()) {
            str.append(inst.toString()).append("\n");
        }


        return "JvmMethod{" +
                "access=" + access +
                ", name='" + name + '\'' +
                ", descriptor='" + descriptor + '\'' +
                ", signature='" + signature + '\'' +
                ", instructions=\n" + str.toString() +
                '}';
    }
}
