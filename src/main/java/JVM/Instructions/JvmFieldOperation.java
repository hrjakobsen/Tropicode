/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM.Instructions;

import JVM.JvmOpCode;

public abstract class JvmFieldOperation extends JvmOperation {

    protected final String owner;
    protected final String fieldName;

    public JvmFieldOperation(JvmOpCode opcode, String owner, String fieldName) {
        super(opcode);
        this.owner = owner;
        this.fieldName = fieldName;
    }

    public String getOwner() {
        return owner;
    }

    public String getFieldName() {
        return fieldName;
    }
}
