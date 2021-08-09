/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import JVM.JvmContext;
import JVM.JvmOpCode;
import org.objectweb.asm.Label;

public class JvmTABLESWITCH extends JvmOperation {
    private Label defaultLabel;
    private Label[] labels;

    @Override
    public void evaluateInstruction(JvmContext ctx) {}

    public JvmTABLESWITCH(Label defaultLabel, Label[] labels) {
        super(JvmOpCode.TABLESWITCH);
        this.defaultLabel = defaultLabel;
        this.labels = labels;
    }

    public Label getDefaultLabel() {
        return defaultLabel;
    }

    public Label[] getLabels() {
        return labels;
    }

    @Override
    public boolean shouldFallThrough() {
        return false;
    }
}
