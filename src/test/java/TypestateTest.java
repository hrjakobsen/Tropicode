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

import static org.junit.jupiter.api.Assertions.assertEquals;

import Checker.Typestate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TypestateTest {

    @Test
    void ParallelUsageHasLocalOperations() {
        Typestate typestate = Typestate.fromString("({m; end} | {n; end}).{g; end}");
        List<String> expectedAvailableMethods = Arrays.asList("m", "n");
        assertEquals(expectedAvailableMethods, typestate.getOperations());
    }

    @Test
    void ParallelUsageWithNoLocalOperationsUsesContinuation() {
        Typestate typestate = Typestate.fromString("(end | end).{g; end}");
        List<String> expectedAvailableMethods = Collections.singletonList("g");
        assertEquals(expectedAvailableMethods, typestate.getOperations());
    }

    @Test
    void ParallelUsageAdvancesLocally() {
        Typestate typestate = Typestate.fromString("({m; end} | {n; end}).{g; end}");
        Typestate expectedTypestate = Typestate.fromString("(end | {n; end}).{g; end}");
        Typestate actualTypestate = typestate.perform("m");
        assertEquals(expectedTypestate, actualTypestate);
    }

    @Test
    void ParallelUsageNoLocalProtocolsAdvancesGlobally() {
        Typestate typestate = Typestate.fromString("(end | end).{g; end}");
        Typestate expectedTypestate = Typestate.END;
        Typestate actualTypestate = typestate.perform("g");
        assertEquals(expectedTypestate, actualTypestate);
    }
}
