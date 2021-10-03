/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JvmExceptionFrame implements Cloneable {
    private List<JvmExceptionHandler> handlers;
    private List<JvmObject> taintedObjects = new ArrayList<>();

    public JvmExceptionFrame(List<JvmExceptionHandler> handlers) {
        this.handlers = handlers;
    }

    public JvmExceptionFrame(List<JvmExceptionHandler> handlers, List<JvmObject> taintedObjects) {
        this.handlers = handlers;
        this.taintedObjects = taintedObjects;
    }

    public List<JvmExceptionHandler> getHandlers() {
        return handlers;
    }

    public List<JvmObject> getTaintedObjects() {
        return taintedObjects;
    }

    @Override
    public JvmExceptionFrame clone() {
        try {
            JvmExceptionFrame clone = (JvmExceptionFrame) super.clone();
            clone.handlers = new ArrayList<>(handlers);
            clone.taintedObjects =
                    taintedObjects.stream().map(JvmObject::copy).collect(Collectors.toList());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JvmExceptionFrame that = (JvmExceptionFrame) o;

        if (!handlers.equals(that.handlers)) {
            return false;
        }
        return taintedObjects.equals(that.taintedObjects);
    }
}
