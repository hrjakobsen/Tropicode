/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import JVM.JvmContext;
import JVM.JvmValue;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JvmKEYLOAD extends JvmInstruction {

    private final String key;

    public JvmKEYLOAD(String key) {
        super();
        this.key = key;
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        ctx.pop();
        String identifier = ctx.getKey(this.key);
        log.warn("Restoring unsafe access to " + identifier);
        ctx.push(new JvmValue.Reference(identifier));
    }

    @Override
    public String toString() {
        return "JvmKEYLOAD " + key;
    }
}
