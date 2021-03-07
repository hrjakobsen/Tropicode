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
        TypestateLexer lexer = new TypestateLexer(protocol);
        TypestateParser parser = new TypestateParser();
        Typestate parsedProtocol = parser.parse(lexer.getTokens());
        HashMap<String, Typestate> implicitConstructorCall = new HashMap<>();
        implicitConstructorCall.put("<init>", parsedProtocol);
        System.out.println("PARSED PROTOCOL: " + parsedProtocol.toString());
        return new Branch(implicitConstructorCall);
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

    static class Branch extends Typestate {
        private HashMap<String, Typestate> branches;

        Branch(HashMap<String, Typestate> branches) {
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
                sb.append(operation).append("; ").append(branches.get(operation).toString());
            }
            sb.append("}");
            return sb.toString();
        }

        @Override
        public Typestate deepCopy()  {
            return new Branch((HashMap<String, Typestate>) this.branches.clone());
        }


    }
    static class Choice extends Typestate {
        private HashMap<String, Typestate> choices;

        Choice(HashMap<String, Typestate> choice) {
            this.choices = choices;
        }

        @Override
        public boolean isAllowed(String action) {
            return choices.containsKey(action);
        }

        @Override
        public Typestate perform(String action) {
            return choices.getOrDefault(action, null);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder().append("<");
            for (String operation : choices.keySet()) {
                sb.append(operation).append(": ").append(choices.get(operation).toString());
            }
            sb.append(">");
            return sb.toString();
        }

        @Override
        public Typestate deepCopy() {
            return new Typestate.Branch((HashMap<String, Typestate>) this.choices.clone());
        }
    }

    static class Recursive extends Typestate {
        final Typestate next;
        final String identifier;

        Recursive(String identifier, Typestate next) {
            this.next = next;
            this.identifier = identifier;
        }

        @Override
        public Typestate deepCopy() {
            return new Recursive(identifier, next.deepCopy());
        }

        @Override
        public boolean isAllowed(String action) {
            return this.next.isAllowed(action);
        }

        @Override
        public Typestate perform(String action) {
            throw new IllegalArgumentException("Perform not implemented on recursive typestates");
        }
    }

    static class Variable extends Typestate {
        final String identifier;

        Variable(String identifier) {
            this.identifier = identifier;
        }


        @Override
        public Typestate deepCopy() {
            return new Variable(this.identifier);
        }

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
            return identifier;
        }
    }
}
