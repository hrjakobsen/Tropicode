/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM.Instructions;

import org.objectweb.asm.Label;
import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmOpCode;

public class JvmLOOKUPSWITCH extends JvmOperation {
    private Label defaultLabel;
    private int[] keys;
    private Label[] labels;

    public JvmLOOKUPSWITCH(Label defaultLabel, int[] keys, Label[] labels) {
        super(JvmOpCode.LOOKUPSWITCH);
        this.defaultLabel = defaultLabel;
        this.keys = keys;
        this.labels = labels;
    }

    public Label getDefaultLabel() {
        return defaultLabel;
    }

    public int[] getKeys() {
        return keys;
    }

    public Label[] getLabels() {
        return labels;
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {}

    @Override
    public boolean shouldFallThrough() {
        return false;
    }
}
