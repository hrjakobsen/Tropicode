/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.checker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.tropicode.checker.checker.exceptions.CheckerException;

public abstract class Typestate {

    public static Typestate END = new End();
    public static Typestate INVALID = new Invalid();

    public static Typestate getInitialObjectProtocol(String protocol) {
        Typestate parsedProtocol = fromString(protocol);
        HashMap<String, Typestate> implicitConstructorCall = new HashMap<>();
        implicitConstructorCall.put("<init>", parsedProtocol);
        return new Branch(implicitConstructorCall);
    }

    public static Typestate fromString(String protocol) {
        TypestateLexer lexer = new TypestateLexer(protocol);
        TypestateParser parser = new TypestateParser();
        return parser.parse(lexer.getTokens());
    }

    public abstract Typestate deepCopy();

    public abstract boolean isAllowed(String action);

    public abstract Typestate perform(String action);

    protected abstract Typestate unfoldRecursive(String identifier, Typestate ts);

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Typestate)) {
            return false;
        }
        return other.toString().equals(this.toString());
    }

    public abstract List<String> getOperations();

    private static class End extends Typestate {

        @Override
        public boolean isAllowed(String action) {
            return false;
        }

        @Override
        public Typestate perform(String action) {
            throw new CheckerException("Tried to perform an operation on an ended protocol");
        }

        @Override
        protected Typestate unfoldRecursive(String identifier, Typestate ts) {
            return this;
        }

        @Override
        public List<String> getOperations() {
            return new ArrayList<>();
        }

        @Override
        public String toString() {
            return "end";
        }

        @Override
        public Typestate deepCopy() {
            return this;
        }
    }

    public static class Branch extends Typestate {

        private final HashMap<String, Typestate> branches;

        public Branch(HashMap<String, Typestate> branches) {
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
        protected Typestate unfoldRecursive(String identifier, Typestate ts) {
            Branch copy = (Branch) this.deepCopy();
            copy.branches.replaceAll(
                    (a, v) -> copy.branches.get(a).unfoldRecursive(identifier, ts));
            return copy;
        }

        @Override
        public List<String> getOperations() {
            return new ArrayList<>(branches.keySet());
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
        public Typestate deepCopy() {
            HashMap<String, Typestate> newBranches = new HashMap<>();
            for (String key : this.branches.keySet()) {
                newBranches.put(key, this.branches.get(key).deepCopy());
            }
            return new Branch(newBranches);
        }
    }

    static class Choice extends Typestate {

        private final HashMap<String, Typestate> choices;

        Choice(HashMap<String, Typestate> choices) {
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
        protected Typestate unfoldRecursive(String identifier, Typestate ts) {
            Choice copy = (Choice) this.deepCopy();
            copy.choices.replaceAll((a, v) -> copy.choices.get(a).unfoldRecursive(identifier, ts));
            return copy;
        }

        @Override
        public List<String> getOperations() {
            return new ArrayList<>(choices.keySet());
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
            HashMap<String, Typestate> newChoices = new HashMap<>();
            for (String key : this.choices.keySet()) {
                newChoices.put(key, this.choices.get(key).deepCopy());
            }
            return new Choice(newChoices);
        }
    }

    public static class Recursive extends Typestate {

        final Typestate next;
        final String identifier;

        public Recursive(String identifier, Typestate next) {
            this.next = next;
            this.identifier = identifier;
        }

        @Override
        public Typestate deepCopy() {
            return new Recursive(identifier, next.deepCopy());
        }

        @Override
        public boolean isAllowed(String action) {
            return this.next.unfoldRecursive(this.identifier, this).isAllowed(action);
        }

        @Override
        public Typestate perform(String action) {
            return this.next.unfoldRecursive(this.identifier, this).perform(action);
        }

        @Override
        protected Typestate unfoldRecursive(String identifier, Typestate ts) {
            return next.unfoldRecursive(identifier, ts);
        }

        @Override
        public List<String> getOperations() {
            return next.getOperations();
        }

        @Override
        public String toString() {
            return "rec " + identifier + ". " + next.toString();
        }
    }

    public static class Variable extends Typestate {

        final String identifier;

        public Variable(String identifier) {
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
        protected Typestate unfoldRecursive(String identifier, Typestate ts) {
            if (identifier.equals(this.identifier)) {
                return ts.deepCopy();
            } else {
                return this;
            }
        }

        @Override
        public List<String> getOperations() {
            return new ArrayList<>();
        }

        @Override
        public String toString() {
            return identifier;
        }
    }

    public static class Parallel extends Typestate {

        private final List<Typestate> locals;
        private Typestate continuation;

        public Parallel(List<Typestate> locals, Typestate continuation) {
            this.locals = locals;
            this.continuation = continuation;
        }

        @Override
        public Typestate deepCopy() {
            List<Typestate> locals_copy =
                    this.locals.stream().map(Typestate::deepCopy).collect(Collectors.toList());
            Typestate continuation_copy = this.continuation.deepCopy();
            return new Parallel(locals_copy, continuation_copy);
        }

        @Override
        public boolean isAllowed(String action) {
            for (Typestate local : this.locals) {
                if (local.isAllowed(action)) {
                    return true;
                }
            }
            if (hasFinishedLocalProtocols()) {
                return continuation.isAllowed(action);
            }
            return false;
        }

        public boolean hasFinishedLocalProtocols() {
            for (Typestate local : this.locals) {
                if (!local.equals(Typestate.END)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public Typestate perform(String action) {
            if (this.hasFinishedLocalProtocols()) {
                return continuation.perform(action);
            }
            for (int i = 0; i < this.locals.size(); i++) {
                if (this.locals.get(i).isAllowed(action)) {
                    this.locals.set(i, this.locals.get(i).perform(action));
                    if (this.hasFinishedLocalProtocols()) {
                        return continuation;
                    }
                    return this;
                }
            }
            return null;
        }

        @Override
        protected Typestate unfoldRecursive(String identifier, Typestate ts) {
            Parallel copy = (Parallel) this.deepCopy();
            for (int i = 0; i < copy.locals.size(); i++) {
                copy.locals.set(i, copy.locals.get(i).unfoldRecursive(identifier, ts));
            }
            copy.continuation = copy.continuation.unfoldRecursive(identifier, ts);
            return copy;
        }

        @Override
        public List<String> getOperations() {
            if (hasFinishedLocalProtocols()) {
                return continuation.getOperations();
            }
            List<String> operations = new ArrayList<>();
            this.locals.forEach(t -> operations.addAll(t.getOperations()));
            return operations;
        }

        @Override
        public String toString() {
            return "("
                    + this.locals.stream().map(Object::toString).collect(Collectors.joining(" | "))
                    + ")"
                    + "."
                    + this.continuation.toString();
        }
    }

    public static final class BooleanChoice extends Typestate {
        private final Typestate trueBranch;
        private final Typestate falseBranch;

        public BooleanChoice(Typestate trueBranch, Typestate falseBranch) {
            this.trueBranch = trueBranch;
            this.falseBranch = falseBranch;
        }

        @Override
        public Typestate deepCopy() {
            return new BooleanChoice(this.trueBranch.deepCopy(), this.falseBranch.deepCopy());
        }

        @Override
        public boolean isAllowed(String action) {
            return action.equals("true") || action.equals("false");
        }

        @Override
        public Typestate perform(String action) {
            if (action.equals("true")) {
                return this.trueBranch;
            } else if (action.equals("false")) {
                return this.falseBranch;
            }
            throw new IllegalStateException();
        }

        @Override
        protected Typestate unfoldRecursive(String identifier, Typestate ts) {
            return new BooleanChoice(
                    this.trueBranch.unfoldRecursive(identifier, ts),
                    this.falseBranch.unfoldRecursive(identifier, ts));
        }

        @Override
        public List<String> getOperations() {
            return Arrays.asList("true", "false");
        }

        @Override
        public String toString() {
            return String.format(
                    "[%s, %s]", this.trueBranch.toString(), this.falseBranch.toString());
        }
    }

    public static final class ExceptionPath extends Typestate {
        private final Typestate intended;
        private final Typestate continuation;

        public ExceptionPath(Typestate intended, Typestate continuation) {
            this.intended = intended;
            this.continuation = continuation;
        }

        @Override
        public Typestate deepCopy() {
            return new ExceptionPath(this.intended.deepCopy(), this.continuation.deepCopy());
        }

        @Override
        public boolean isAllowed(String action) {
            if (action.equals("$EXCEPTION")) {
                return true;
            }
            return this.intended.isAllowed(action);
        }

        @Override
        public Typestate perform(String action) {
            if (action.equals("$EXCEPTION")) {
                return this.continuation;
            } else {
                return new ExceptionPath(this.intended.perform(action), this.continuation);
            }
        }

        @Override
        protected Typestate unfoldRecursive(String identifier, Typestate ts) {
            return new ExceptionPath(
                    this.intended.unfoldRecursive(identifier, ts),
                    this.continuation.unfoldRecursive(identifier, ts));
        }

        @Override
        public List<String> getOperations() {
            List<String> ops = this.intended.getOperations();
            ops.add("$EXCEPTION");
            return ops;
        }

        @Override
        public String toString() {
            return String.format(
                    "try %s except %s", this.intended.toString(), this.continuation.toString());
        }
    }

    private static final class Invalid extends Typestate {

        @Override
        public String toString() {
            return "Invalid";
        }

        @Override
        public Typestate deepCopy() {
            return this;
        }

        @Override
        public boolean isAllowed(String action) {
            return false;
        }

        @Override
        public Typestate perform(String action) {
            return this;
        }

        @Override
        protected Typestate unfoldRecursive(String identifier, Typestate ts) {
            return this;
        }

        @Override
        public boolean equals(Object other) {
            return this == other;
        }

        @Override
        public List<String> getOperations() {
            return new ArrayList<>();
        }
    }
}
