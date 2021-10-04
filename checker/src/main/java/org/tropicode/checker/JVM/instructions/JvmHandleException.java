/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmValue;

public class JvmHandleException extends JvmInstruction {

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        // TODO: Clear required stack levels etc.
        ctx.exitExceptionHandler();
        // FIXME: For now add the exception reference to the stack as an unknown reference
        ctx.push(JvmValue.UNKNOWN_REFERENCE);
    }

    @Override
    public String toString() {
        return "Try-catch handler";
    }
}
