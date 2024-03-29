/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.checker;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.tropicode.checker.JVM.JvmContext;
import org.tropicode.checker.JVM.JvmObject;
import org.tropicode.checker.JVM.instructions.JvmInstruction;
import org.tropicode.checker.JVM.instructions.JvmJUMP;
import org.tropicode.checker.JVM.instructions.JvmLabel;
import org.tropicode.checker.cfg.InstructionGraph;
import org.tropicode.checker.cfg.Pair;
import org.tropicode.checker.checker.exceptions.CheckerException;

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
            boolean found = false;
            for (Pair<InstructionGraph, JvmContext> instructionGraphJvmContextPair : cache) {
                if (instructionGraphJvmContextPair.getLeft().equals(next.getLeft())
                        && instructionGraphJvmContextPair.getRight().equals(next.getRight())) {
                    found = true;
                    break;
                }
            }
            if (found) {
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

            if (next.getLeft().getBlock().getLastInstruction() instanceof JvmJUMP jumpInstruction
                && jumpInstruction.isConditional()
                && ctx.getConditional() != null
                && ctx.getObject(ctx.getConditional().getIdentifier()).getProtocol() != null) {
                JvmContext trueCtx = ctx.copy();
                JvmObject objTrue = trueCtx.getObject(trueCtx.getConditional().getIdentifier());
                objTrue.setProtocol(objTrue.getProtocol().perform("true"));

                JvmContext falseCtx = ctx.copy();
                JvmObject objFalse = falseCtx.getObject(falseCtx.getConditional().getIdentifier());
                objFalse.setProtocol(objFalse.getProtocol().perform("false"));

                queue.add(new Pair<>(next.getLeft().getConnections().get(1), trueCtx));
                queue.add(new Pair<>(next.getLeft().getConnections().get(0), falseCtx));

            } else {
                for (InstructionGraph child : next.getLeft().getConnections()) {
                    queue.add(new Pair<>(child, ctx));
                }
            }

        }
    }
}
