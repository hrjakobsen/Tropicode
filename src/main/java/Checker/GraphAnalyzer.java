/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package Checker;

import CFG.InstructionGraph;
import JVM.JvmContext;

public interface GraphAnalyzer {
    void checkGraph(InstructionGraph entry, JvmContext initial);
}
