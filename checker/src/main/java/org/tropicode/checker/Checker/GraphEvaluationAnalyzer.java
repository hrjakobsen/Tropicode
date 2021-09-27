/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.Checker;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.tropicode.checker.CFG.InstructionGraph;
import org.tropicode.checker.CFG.Pair;
import org.tropicode.checker.Checker.Exceptions.CheckerException;
import org.tropicode.checker.JVM.JvmContext;

@Log4j2
public class GraphEvaluationAnalyzer implements GraphAnalyzer {
    public void checkGraph(InstructionGraph entry, JvmContext initial) {
        Queue<Pair<InstructionGraph, JvmContext>> next = new ArrayDeque<>();
        next.add(new Pair<>(entry, initial));
        Map<InstructionGraph, JvmContext> snapshotMap = new HashMap<>();
        while (!next.isEmpty()) {
            Pair<InstructionGraph, JvmContext> toCheck = next.poll();
            InstructionGraph node = toCheck.getLeft();
            JvmContext ctx = toCheck.getRight();
            if (snapshotMap.containsKey(node)) {
                if (!snapshotMap.get(node).equals(ctx)) {
                    List<String> differences = snapshotMap.get(node).findDifferences(ctx);
                    throw new CheckerException(
                            "Invalid context at "
                                    + node.getBlock().getSourceLocation()
                                    + ". Upon reaching an instruction that was visited "
                                    + "earlier, there were the following differences in the "
                                    + "expected contexts:\n"
                                    + differences.stream()
                                            .map(s -> "  * " + s)
                                            .collect(Collectors.joining("\n")));
                } else {
                    // skip connections we've seen before
                    continue;
                }
            } else {
                snapshotMap.put(node, ctx);
            }
            JvmContext childCtx = ctx.copy();
            node.getBlock().evaluate(childCtx);
            for (InstructionGraph connection : node.getConnections()) {
                next.add(new Pair<>(connection, childCtx));
            }
        }
    }
}
