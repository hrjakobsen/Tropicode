/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.tropicode.checker.checker.Typestate;

public class JvmHeapSnapshot {

    Map<String, Typestate> snapshot = new HashMap<>();

    public JvmHeapSnapshot(Map<String, JvmObject> heap) {
        for (String objIdentifier : heap.keySet()) {
            if (heap.get(objIdentifier).getProtocol() != null) {
                snapshot.put(objIdentifier, heap.get(objIdentifier).getProtocol().deepCopy());
            }
        }
    }

    public boolean compareTo(Map<String, JvmObject> heap, List<String> errors) {
        boolean result = true;
        long numberOfObjectsWithProtocol =
                heap.values().stream().map(JvmObject::getProtocol).filter(Objects::nonNull).count();
        if (numberOfObjectsWithProtocol != snapshot.size()) {
            result = false;
            errors.add(
                    String.format(
                            "Expected %d values in the heap, instead got %d",
                            snapshot.size(), numberOfObjectsWithProtocol));
        }
        for (String objIdentifier : heap.keySet()) {
            if (heap.get(objIdentifier).getProtocol() == null) {
                continue;
            }
            if (!snapshot.containsKey(objIdentifier)) {
                errors.add("Missing key " + objIdentifier);
                result = false;
            }
            if (!snapshot.get(objIdentifier).equals(heap.get(objIdentifier).getProtocol())) {
                errors.add(
                        String.format(
                                "Mismatching typestates. Expected %s but got %s",
                                snapshot.get(objIdentifier).toString(),
                                heap.get(objIdentifier).getProtocol().toString()));
                result = false;
            }
        }
        return result;
    }
}
