/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
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
import java.util.Map;

public class JvmHeapSnapshot {
    Map<String, Typestate> snapshot = new HashMap<>();
    public JvmHeapSnapshot(Map<String, JvmObject> heap) {
        for (String objIdentifier : heap.keySet()) {
            snapshot.put(objIdentifier, heap.get(objIdentifier).getProtocol().deepCopy());
        }
    }

    public boolean compareTo(Map<String, JvmObject> heap) {
        if (heap.size() != snapshot.size()) {
            return false;
        }
        for (String objIdentifier : heap.keySet()) {
            if (!snapshot.containsKey(objIdentifier)) {
                System.out.println("Missing key" + objIdentifier);
                return false;
            }
            if (!snapshot.get(objIdentifier).equals(heap.get(objIdentifier).getProtocol())) {
                System.out.println("Different typestates:" + snapshot.get(objIdentifier).toString() + " != " + heap.get(objIdentifier).getProtocol().toString());
                return false;
            }
        }
        return true;
    }
}
