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

import Checker.Exceptions.UnsupportedOperationException;

import java.util.UUID;

public abstract class JvmValue {
    private static class UnknownByte extends JvmValue {
        @Override
        public String toString() {
            return "Unknown";
        }
    }

    public static class Reference extends JvmValue {

        protected final String identifer;

        public Reference(String identifer) {
            this.identifer = identifer;
        }

        @Override
        public String toString() {
            return identifer.substring(0, 5);
        }

        public String getIdentifer() {
            return identifer;
        }
    }

    public static class UnknownReference extends Reference {
        public UnknownReference() {
            super("UNKNOWN");
        }

        @Override
        public String toString() {
            return this.identifer;
        }

        @Override
        public String getIdentifer() {
            throw new UnsupportedOperationException("Trying to dereference unknown reference");
        }
    }

    public static final JvmValue UNKNOWN = new UnknownByte();
    public static final JvmValue UNKNOWN_REFERENCE = new UnknownReference();
}
