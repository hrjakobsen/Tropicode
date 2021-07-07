/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package Checker.Exceptions;

import JVM.Instructions.JvmInstruction;

public class UnsupportedInstructionException extends CheckerException {

    public UnsupportedInstructionException(JvmInstruction inst, String message) {
        super(message + " while executing " + inst + " at line " + inst.getLineNumber());
    }
}
