/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmValue;

public class JvmKEYSET extends JvmInstruction {

    private final String key;

    public JvmKEYSET(String key) {
        super();
        this.key = key;
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        JvmValue.Reference ref = (JvmValue.Reference) ctx.pop();
        ctx.addKey(this.key, ref.getIdentifier());
        ctx.push(ref);
    }

    @Override
    public String toString() {
        return "JvmKEYSET " + key;
    }
}
