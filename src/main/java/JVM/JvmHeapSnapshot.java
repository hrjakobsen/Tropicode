/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     Tropicode is a Java bytecode analyser used to verify object protocols.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package JVM;

import Checker.Typestate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
