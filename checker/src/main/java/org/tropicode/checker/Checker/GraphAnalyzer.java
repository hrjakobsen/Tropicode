/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.Checker;

import org.tropicode.checker.CFG.InstructionGraph;
import org.tropicode.checker.JVM.JvmContext;

public interface GraphAnalyzer {
    void checkGraph(InstructionGraph entry, JvmContext initial);
}
