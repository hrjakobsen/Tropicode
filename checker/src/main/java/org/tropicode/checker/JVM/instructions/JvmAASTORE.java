/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.instructions;

import lombok.extern.log4j.Log4j2;
import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;
import org.tropicode.checker.JVM.JvmValue;

@Log4j2
public class JvmAASTORE extends JvmOperation {

    public JvmAASTORE() {
        super(JvmOpCode.AASTORE);
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        JvmValue.Reference obj = (JvmValue.Reference) ctx.pop();
        log.warn("Losing track of obj " + obj.getIdentifier());
    }
}
