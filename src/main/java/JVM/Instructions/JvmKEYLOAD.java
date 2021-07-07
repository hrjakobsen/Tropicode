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

package JVM.Instructions;

import JVM.JvmContext;
import JVM.JvmValue;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JvmKEYLOAD extends JvmInstruction {

    private final String key;

    public JvmKEYLOAD(String key) {
        super();
        this.key = key;
    }

    @Override
    public void evaluateInstruction(JvmContext ctx) {
        ctx.pop();
        String identifier = ctx.getKey(this.key);
        log.warn("Restoring unsafe access to " + identifier);
        ctx.push(new JvmValue.Reference(identifier));
    }

    @Override
    public String toString() {
        return "JvmKEYLOAD " + key;
    }
}
