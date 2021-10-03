/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import org.tropicode.checker.JVM.JvmContext;

public class JvmHandleException extends JvmInstruction {

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        // TODO: Clear required stack levels etc.
        ctx.exitExceptionHandler();
    }

    @Override
    public String toString() {
        return "Try-catch handler";
    }
}
