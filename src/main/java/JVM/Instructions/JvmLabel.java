/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import JVM.JvmContext;

public class JvmLabel extends JvmInstruction {

    String label;

    public JvmLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label + ":";
    }

    public String getLabel() {
        return label;
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        // do nothing
    }
}
