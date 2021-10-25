/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM;

import java.util.HashMap;
import java.util.Map;
import org.tropicode.checker.checker.Typestate;

public class JvmObject {

    private final String type;
    private final String identifier;
    private boolean tainted = false;

    Map<String, JvmValue> fields = new HashMap<>();
    private Typestate protocol;

    public JvmObject(String type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public boolean isTainted() {
        return tainted;
    }

    public void setTainted(boolean tainted) {
        this.tainted = tainted;
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
        newObj.setTainted(this.isTainted());
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JvmObject)) return false;
        JvmObject other = (JvmObject) obj;
        // catches null == null
        boolean protocolEquality = this.protocol == other.protocol;
        if (this.protocol != null) {
            protocolEquality = this.protocol.equals(other.protocol);
        }
        boolean fieldEquality = this.fields.equals(other.fields);
        return protocolEquality && fieldEquality;
    }

    public String getIdentifier() {
        return identifier;
    }
}
