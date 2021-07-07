/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     Tropicode is a Java bytecode analyser used to verify object protocols.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package CFG;

import Checker.Exceptions.CheckerException;
import JVM.JvmContext;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GraphAnalyser {
    private InstructionGraph currentNode;

    public InstructionGraph getCurrentNode() {
        return currentNode;
    }

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
                            "Invalid context. Upon reaching an instruction that was visited earlier, there were the following differences in the expected contexts:\n"
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
