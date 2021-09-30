/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;

public class JvmUnsupportedOperation extends JvmOperation {

    public JvmUnsupportedOperation(JvmOpCode opcode) {
        super(opcode);
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        throw new UnsupportedOperationException(opcode + " has not been implemented");
    }
}
