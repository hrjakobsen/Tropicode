/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM;

public abstract class JvmValue implements Cloneable {

    public static final JvmValue UNKNOWN = new UnknownByte();
    public static final JvmValue UNKNOWN_REFERENCE = new UnknownReference();

    private static class UnknownByte extends JvmValue {

        @Override
        public String toString() {
            return "Unknown";
        }
    }

    public static class Reference extends JvmValue implements Cloneable {

        protected final String identifer;

        public Reference(String identifer) {
            this.identifer = identifer;
        }

        @Override
        public String toString() {
            return identifer.substring(0, 5);
        }

        public String getIdentifier() {
            return identifer;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    public static class UnknownReference extends Reference implements Cloneable {

        public UnknownReference() {
            super("UNKNOWN");
        }

        @Override
        public String toString() {
            return this.identifer;
        }

        @Override
        public String getIdentifier() {
            throw new UnsupportedOperationException("Trying to dereference unknown reference");
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
