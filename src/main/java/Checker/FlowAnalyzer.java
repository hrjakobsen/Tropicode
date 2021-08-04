/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package Checker;

import CFG.InstructionGraph;
import CFG.Pair;
import JVM.JvmContext;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class FlowAnalyzer implements GraphAnalyzer {
    Set<Pair<InstructionGraph, JvmContext>> cache = new HashSet<>();
    Queue<Pair<InstructionGraph, JvmContext>> queue = new ArrayDeque<>();

    @Override
    public void checkGraph(InstructionGraph entry, JvmContext initial) {
        queue.add(new Pair<>(entry, initial));

        while (!queue.isEmpty()) {
            Pair<InstructionGraph, JvmContext> next = queue.poll();
            if (cache.contains(next)) {
                continue;
            }
            cache.add(next);
            JvmContext ctx = next.getRight().copy();
            next.getLeft().getBlock().evaluate(ctx);
            for (InstructionGraph child : next.getLeft().getConnections()) {
                queue.add(new Pair<>(child, ctx));
            }
        }
    }
}
