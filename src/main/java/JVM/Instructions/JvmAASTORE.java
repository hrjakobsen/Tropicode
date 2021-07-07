/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import JVM.JvmContext;
import JVM.JvmOpCode;
import JVM.JvmValue;
import lombok.extern.log4j.Log4j2;

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
