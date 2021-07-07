/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import JVM.JvmContext;
import JVM.JvmOpCode;
import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.Label;

@Log4j2
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
    public void evaluateInstruction(JvmContext ctx) {
        // Remove values used for jump check
        for (int i = 0; i < stackValues; i++) {
            ctx.pop();
        }
    }

    @Override
    public String toString() {
        return "    " + this.opcode.toString() + " " + this.label;
    }
}
