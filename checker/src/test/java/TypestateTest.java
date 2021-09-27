/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.tropicode.checker.Checker.Typestate;

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
