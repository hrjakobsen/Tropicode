/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import Checker.Exceptions.UnsupportedOperationException;
import JVM.JvmContext;
import JVM.JvmOpCode;

public class JvmUnsupportedOperation extends JvmOperation {

    public JvmUnsupportedOperation(JvmOpCode opcode) {
        super(opcode);
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        throw new UnsupportedOperationException(opcode + " has not been implemented");
    }
}
