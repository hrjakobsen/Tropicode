/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.checker.exceptions;

import org.tropicode.checker.checker.Typestate;

public class InvalidProtocolOperationException extends CheckerException {

    public InvalidProtocolOperationException(Typestate typestate, String operation) {
        super(
                "Protocol Violation: '"
                        + operation
                        + "' invoked with protocol "
                        + typestate
                        + ". The available operations are: {"
                        + String.join(",", typestate.getOperations())
                        + "}");
    }
}
