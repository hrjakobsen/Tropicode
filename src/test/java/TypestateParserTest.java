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
import Checker.Typestate;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TypestateParserTest {

    @Test
    void parsesEnd() {
        Typestate actualTypestate = Typestate.fromString("end");
        assertSame(actualTypestate, Typestate.END);
    }

    @Test
    void parsesSingleBranch() {
        Typestate actualTypestate = Typestate.fromString("{m1; end}");
        Typestate expectedTypestate = new Typestate.Branch(new HashMap<>() {{ put("m1", Typestate.END); }});
        assertEquals(actualTypestate, expectedTypestate);
    }

    @Test
    void parsesMultipleBranches() {
        Typestate actualTypestate = Typestate.fromString("{m1; end m2; end}");
        Typestate expectedTypestate = new Typestate.Branch(new HashMap<>() {{ put("m1", Typestate.END); put("m2", Typestate.END); }});
        assertEquals(actualTypestate, expectedTypestate);
    }

    @Test
    void parsesNestedBranch() {
        Typestate actualTypestate = Typestate.fromString("{m1; {m2; end}}");
        Typestate expectedTypestate = new Typestate.Branch(new HashMap<>() {{ put("m1", new Typestate.Branch(new HashMap<>() {{ put("m2", Typestate.END); }})); }});
        assertEquals(actualTypestate, expectedTypestate);
    }

    @Test
    void parsesRecursive() {
        Typestate actualTypestate = Typestate.fromString("rec X. end");
        Typestate expectedTypestate = new Typestate.Recursive("X", Typestate.END);
        assertEquals(actualTypestate, expectedTypestate);
    }

    @Test
    void parsesRecursionVariable() {
        Typestate actualTypestate = Typestate.fromString("X");
        Typestate expectedTypestate = new Typestate.Variable("X");
        assertEquals(actualTypestate, expectedTypestate);
    }
}
