/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
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

import Checker.Exceptions.CheckerException;
import JVM.JvmContex;

public class JvmLabel extends JvmInstruction {
    String label;
    public JvmLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label + ":";
    }

    public String getLabel() {
        return label;
    }

    @Override
    public void evaluateInstruction(JvmContex ctx) {
        if (ctx.hasSnapshot(this.label)) {
            // We have previously seen a forward jump to this label, now ensure that the context is consistent
            if (!ctx.compareToSnapshot(this.label)) {
                throw new CheckerException("Invalid snapshot upon reached label");
            }
        } else {
            // First time we see this label, save a snapshot for later jumps back to the label
            ctx.takeSnapshot(this.label);
        }
    }
}
