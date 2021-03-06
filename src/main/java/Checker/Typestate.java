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

package Checker;

import java.util.*;

public abstract class Typestate implements Cloneable {

    public static Typestate fromString(String protocol) {
        String[] methods = protocol.split(";");
        LinkedList ll = new LinkedList(Arrays.asList(methods));
        return fromStringParts(ll);


    }
    private static Typestate fromStringParts(List<String> methods) {
        if (methods.isEmpty()) {
            return Typestate.END;
        } else {
            HashMap<String, Typestate> rem = new HashMap<>();
            rem.put(methods.remove(0), fromStringParts(methods));
            return new Branch(rem);
        }
    }

    public abstract Typestate deepCopy();

    public abstract boolean isAllowed(String action);
    public abstract Typestate perform(String action);

    public static Typestate END = new End();

    private static class End extends Typestate {

        @Override
        public boolean isAllowed(String action) {
            return false;
        }

        @Override
        public Typestate perform(String action) {
            return null;
        }

        @Override
        public String toString() {
            return "end";
        }

        @Override
        public Typestate deepCopy()  {
            return this;
        }
    }

    private static class Branch extends Typestate {
        private HashMap<String, Typestate> branches;

        private Branch(HashMap<String, Typestate> branches) {
            this.branches = branches;
        }

        @Override
        public boolean isAllowed(String action) {
            return branches.containsKey(action);
        }

        @Override
        public Typestate perform(String action) {
            return branches.getOrDefault(action, null);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder().append("{");
            for (String operation : branches.keySet()) {
                sb.append(operation).append(": ").append(branches.get(operation).toString());
            }
            sb.append("}");
            return sb.toString();
        }

        @Override
        public Typestate deepCopy()  {
            return new Branch((HashMap<String, Typestate>) this.branches.clone());
        }
    }
}
