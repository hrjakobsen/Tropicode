/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM;

import java.util.Objects;

public abstract class JvmValue implements Cloneable {

    public static final JvmValue UNKNOWN = new UnknownByte();
    public static final JvmValue UNKNOWN_REFERENCE = new UnknownReference();

    public boolean isUnknownReference() {
        return false;
    }

    public boolean isArrayReference() {
        return false;
    }

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

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Reference reference = (Reference) o;

            return Objects.equals(identifer, reference.identifer);
        }

        @Override
        public int hashCode() {
            return identifer != null ? identifer.hashCode() : 0;
        }
    }

    public static class ArrayReference extends Reference implements Cloneable {

        public ArrayReference(String identifer) {
            super(identifer);
        }

        @Override
        public boolean isArrayReference() {
            return true;
        }

        @Override
        public ArrayReference clone() {
            try {
                return (ArrayReference) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    public static class TaggedBoolean extends JvmValue implements Cloneable {
        private Reference objectReference;

        public TaggedBoolean(Reference objectReference) {
            this.objectReference = objectReference;
        }

        public Reference getObjectReference() {
            return objectReference;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TaggedBoolean that = (TaggedBoolean) o;

            return Objects.equals(objectReference, that.objectReference);
        }

        @Override
        public int hashCode() {
            return objectReference != null ? objectReference.hashCode() : 0;
        }
    }

    private static class UnknownReference extends Reference implements Cloneable {

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

        @Override
        public boolean isUnknownReference() {
            return true;
        }
    }
}
