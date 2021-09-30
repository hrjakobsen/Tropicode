/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM;

import org.tropicode.checker.checker.exceptions.CheckerException;

public class InvalidOpcodeException extends CheckerException {

    public InvalidOpcodeException(int opcode) {
        super("Invalid opcode: " + Integer.toHexString(opcode));
    }
}
