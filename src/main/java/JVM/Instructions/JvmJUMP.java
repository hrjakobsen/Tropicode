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

package JVM.Instructions;

import Checker.Exceptions.CheckerException;
import JVM.JvmContex;
import JVM.JvmOpCode;
import org.objectweb.asm.Label;

public class JvmJUMP extends JvmOperation {

    private final Label label;
    private final int stackValues;

    public JvmJUMP(JvmOpCode opcode, Label s, int i) {
        super(opcode);
        label = s;
        stackValues = i;
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public void evaluateInstruction(JvmContex ctx) {
        for (int i = 0; i < stackValues; i++) {
            ctx.pop();
        }
        if (ctx.hasSnapshot(this.label.toString())) {
            if (!ctx.compareToSnapshot(this.label.toString())) {
                throw new CheckerException("Snapshot not equals to current heap");
            }
        } else {
            System.out.println("Jump into the future label " + this.label.toString());
        }
    }
}
