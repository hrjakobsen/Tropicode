/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package Checker;

import CFG.InstructionGraph;
import CFG.Pair;
import Checker.Exceptions.CheckerException;
import JVM.Instructions.JvmInstruction;
import JVM.Instructions.JvmLabel;
import JVM.JvmContext;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class FlowAnalyzer implements GraphAnalyzer {
    Set<Pair<InstructionGraph, JvmContext>> cache = new HashSet<>();
    Queue<Pair<InstructionGraph, JvmContext>> queue = new ArrayDeque<>();
    Map<JvmInstruction, Integer> objectCountCache = new HashMap<>();

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
            JvmInstruction inst = next.getLeft().getBlock().getInstructions().get(0);
            // Hack to handle infinite checking when new objects are instantiated inside a loop body
            if (inst instanceof JvmLabel) {
                if (objectCountCache.containsKey(inst)) {
                    // Perform check to see if new objects has been introduced
                    if (ctx.heapSize() > objectCountCache.get(inst)) {
                        throw new CheckerException(
                                "Object instantiation in loop body is not allowed");
                    }
                } else {
                    objectCountCache.put(inst, ctx.heapSize());
                }
            }

            next.getLeft().getBlock().evaluate(ctx);
            for (InstructionGraph child : next.getLeft().getConnections()) {
                queue.add(new Pair<>(child, ctx));
            }
        }
    }
}
