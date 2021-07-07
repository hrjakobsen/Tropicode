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

import JVM.Instructions.JvmInstruction;
import JVM.JvmContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BasicBlock {

    List<JvmInstruction> instructions = new ArrayList<>();

    public BasicBlock() {}

    public BasicBlock(JvmInstruction... instructions) {
        this.instructions = Arrays.asList(instructions);
    }

    public String getSourceLocation() {
        int maxLineNumber = -1;
        int minLineNumber = Integer.MAX_VALUE;
        for (JvmInstruction instruction : instructions) {
            if (instruction.getLineNumber() != -1) {
                maxLineNumber = Math.max(instruction.getLineNumber(), maxLineNumber);
                minLineNumber = Math.min(instruction.getLineNumber(), minLineNumber);
            }
        }
        if (maxLineNumber != -1) {
            if (maxLineNumber == minLineNumber) {
                return String.format("Line %d", maxLineNumber);
            } else {
                return String.format("Lines %d-%d", minLineNumber, maxLineNumber);
            }
        }
        return "Unknown location";
    }

    public List<JvmInstruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<JvmInstruction> instructions) {
        this.instructions = instructions;
    }

    public void insertInstruction(JvmInstruction instruction) {
        this.instructions.add(instruction);
    }

    public JvmInstruction getLastInstruction() {
        if (this.instructions.size() == 0) {
            throw new IllegalStateException();
        }
        return this.instructions.get(this.instructions.size() - 1);
    }

    public void evaluate(JvmContext ctx) {
        for (JvmInstruction instruction : this.getInstructions()) {
            instruction.evaluateInstruction(ctx);
        }
    }

    @Override
    public String toString() {
        return String.format(
                "BasicBlock{%s, (%d total)}",
                this.instructions.size() > 0 ? this.instructions.get(0).toString().trim() : "",
                this.instructions.size());
    }
}
