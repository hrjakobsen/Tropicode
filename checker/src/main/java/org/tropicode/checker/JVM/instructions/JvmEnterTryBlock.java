/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmExceptionHandler;

public class JvmEnterTryBlock extends JvmInstruction {

    private JvmExceptionHandler handler;

    public JvmEnterTryBlock(JvmExceptionHandler handler) {
        this.handler = handler;
    }

    public JvmExceptionHandler getHandler() {
        return handler;
    }

    public void setHandler(JvmExceptionHandler handler) {
        this.handler = handler;
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        // do nothing
    }

    @Override
    public String toString() {
        return "Enter try-catch block";
    }
}
