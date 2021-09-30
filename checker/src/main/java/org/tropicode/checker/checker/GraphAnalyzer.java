/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.checker;

import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.cfg.InstructionGraph;

public interface GraphAnalyzer {
    void checkGraph(InstructionGraph entry, JvmContext initial);
}
