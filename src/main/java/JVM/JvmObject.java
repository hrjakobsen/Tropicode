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
import java.util.Map;

public class JvmObject {

    private final String type;
    private final String identifier;
    Map<String, JvmValue> fields = new HashMap<>();
    private Typestate protocol;

    public JvmObject(String type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public Map<String, JvmValue> getFields() {
        return fields;
    }

    public Typestate getProtocol() {
        return protocol;
    }

    public void setProtocol(Typestate protocol) {
        this.protocol = protocol;
    }

    public JvmObject copy() {
        JvmObject newObj = new JvmObject(this.type, this.identifier);
        if (protocol != null) {
            newObj.setProtocol(protocol.deepCopy());
        }
        Map<String, JvmValue> newFields = new HashMap<>();
        for (String s : fields.keySet()) {
            newFields.put(s, fields.get(s));
        }
        newObj.fields = newFields;

        return newObj;
    }
}
