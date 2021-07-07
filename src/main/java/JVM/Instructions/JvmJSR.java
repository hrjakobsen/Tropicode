/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import Checker.Exceptions.UnsupportedOperationException;
import JVM.JvmContext;
import JVM.JvmOpCode;
import org.objectweb.asm.Label;

public class JvmJSR extends JvmOperation {

    private final Label label;

    public JvmJSR(Label label) {
        super(JvmOpCode.JSR);
        this.label = label;
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        throw new UnsupportedOperationException("JSR instruction not supported");
    }
}
