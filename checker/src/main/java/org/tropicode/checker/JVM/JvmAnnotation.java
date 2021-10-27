/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM;

public class JvmAnnotation {
    final String descriptor;
    final boolean visible;
    final Object value;

    public JvmAnnotation(String descriptor, boolean visible, Object value) {
        this.descriptor = descriptor;
        this.visible = visible;
        this.value = value;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public boolean isVisible() {
        return visible;
    }

    public Object getValue() {
        return value;
    }
}
