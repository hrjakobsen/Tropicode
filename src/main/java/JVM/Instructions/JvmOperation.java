/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import JVM.JvmOpCode;

public abstract class JvmOperation extends JvmInstruction {

    JvmOpCode opcode;

    public JvmOperation(JvmOpCode opcode) {
        this.opcode = opcode;
    }

    @Override
    public String toString() {
        return "    " + opcode.toString();
    }

    public JvmOpCode getOpcode() {
        return opcode;
    }
}
