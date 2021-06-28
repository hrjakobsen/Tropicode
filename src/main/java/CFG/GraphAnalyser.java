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
import JVM.JvmContex;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GraphAnalyser {
    private InstructionGraph currentNode;

    public InstructionGraph getCurrentNode() {
        return currentNode;
    }

    public JvmContex checkGraph(InstructionGraph node, JvmContex ctx) {
        this.currentNode = node;
        log.debug("Checking node " + currentNode.getBlock() + " in context " + ctx);
        JvmContex tmp = null;
        node.getBlock().evaluate(ctx, this);
        for (InstructionGraph next : node.getConnections()) {
            JvmContex nextCtx = checkGraph(next, ctx.copy());
            if (tmp == null) {
                tmp = nextCtx;
            }
            if (!tmp.equals(nextCtx)) {
                throw new CheckerException("Invalid context");
            }
        }
        return tmp == null ? ctx : tmp;
    }
}
