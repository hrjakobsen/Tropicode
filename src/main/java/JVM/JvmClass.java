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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JvmClass {
    List<JvmMethod> methods = new ArrayList<>();
    List<String> fields = new ArrayList<>();
    Map<String, JvmValue> staticFields = new HashMap<>();
    Typestate protocol = null;

    public List<JvmMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<JvmMethod> methods) {
        this.methods = methods;
    }

    public List<String> getFields() {
        return fields;
    }

    public Map<String, JvmValue> getStaticFields() {
        return staticFields;
    }

    @Override
    public String toString() {
        return "JvmClass{" +
                "methods=" + methods +
                '}';
    }

    public Typestate getProtocol() {
        return protocol;
    }

    public void setProtocol(Typestate protocol) {
        this.protocol = protocol;
    }
}
