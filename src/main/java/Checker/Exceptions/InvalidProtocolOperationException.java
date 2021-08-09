/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package Checker.Exceptions;

import Checker.Typestate;

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
