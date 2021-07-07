/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import Checker.Exceptions.CheckerException;
import JVM.JvmContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        if (ctx.hasSnapshot(this.label)) {
            // We have previously seen a forward jump to this label, now ensure that the context is
            // consistent
            List<String> errors = new ArrayList<>();
            if (!ctx.isEqualToSnapshot(this.label, errors)) {
                throw new CheckerException(
                        "Invalid state when performing jump. Errors:\n"
                                + errors.stream()
                                        .map((String s) -> "  * " + s)
                                        .collect(Collectors.joining("\n")));
            }
        } else {
            // First time we see this label, save a snapshot for later jumps back to the label
            ctx.takeSnapshot(this.label);
        }
    }
}
