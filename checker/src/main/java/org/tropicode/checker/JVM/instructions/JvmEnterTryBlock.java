/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import java.util.List;
import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmExceptionHandler;

public class JvmEnterTryBlock extends JvmInstruction {

    private final List<JvmExceptionHandler> handlers;

    public JvmEnterTryBlock(List<JvmExceptionHandler> handlers) {
        this.handlers = handlers;
    }

    public List<JvmExceptionHandler> getHandlers() {
        return handlers;
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        ctx.enterExceptionHandler(handlers);
    }

    @Override
    public String toString() {
        return "Enter try-catch block";
    }
}
