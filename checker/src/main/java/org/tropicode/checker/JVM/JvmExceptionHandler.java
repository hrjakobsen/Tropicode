/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM;

import org.objectweb.asm.Label;

public class JvmExceptionHandler {
    private final Label from;
    private final Label to;
    private final Label target;
    private final String type;

    public JvmExceptionHandler(Label from, Label to, Label target, String type) {
        this.from = from;
        this.to = to;
        this.target = target;
        this.type = type;
    }

    public Label getFrom() {
        return from;
    }

    public Label getTo() {
        return to;
    }

    public Label getTarget() {
        return target;
    }

    public String getType() {
        return type;
    }
}
