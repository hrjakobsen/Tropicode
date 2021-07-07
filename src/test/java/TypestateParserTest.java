/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import Checker.Typestate;
import Checker.Typestate.BooleanChoice;
import Checker.Typestate.Branch;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class TypestateParserTest {

    @Test
    void parsesEnd() {
        Typestate actualTypestate = Typestate.fromString("end");
        assertSame(actualTypestate, Typestate.END);
    }

    @Test
    void parsesSingleBranch() {
        Typestate actualTypestate = Typestate.fromString("{m1; end}");
        Typestate expectedTypestate =
                new Typestate.Branch(
                        new HashMap<>() {
                            {
                                put("m1", Typestate.END);
                            }
                        });
        assertEquals(actualTypestate, expectedTypestate);
    }

    @Test
    void parsesMultipleBranches() {
        Typestate actualTypestate = Typestate.fromString("{m1; end m2; end}");
        Typestate expectedTypestate =
                new Typestate.Branch(
                        new HashMap<>() {
                            {
                                put("m1", Typestate.END);
                                put("m2", Typestate.END);
                            }
                        });
        assertEquals(actualTypestate, expectedTypestate);
    }

    @Test
    void parsesNestedBranch() {
        Typestate actualTypestate = Typestate.fromString("{m1; {m2; end}}");
        Typestate expectedTypestate =
                new Typestate.Branch(
                        new HashMap<>() {
                            {
                                put(
                                        "m1",
                                        new Typestate.Branch(
                                                new HashMap<>() {
                                                    {
                                                        put("m2", Typestate.END);
                                                    }
                                                }));
                            }
                        });
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

    @Test
    void parsesParallelTypestate() {
        Typestate actualTypestate = Typestate.fromString("(end | X).end");
        Typestate expectedTypestate =
                new Typestate.Parallel(
                        new ArrayList<>() {
                            {
                                add(Typestate.END);
                                add(new Typestate.Variable("X"));
                            }
                        },
                        Typestate.END);
        assertEquals(actualTypestate, expectedTypestate);
    }

    @Test
    void parsesNestedParallelTypestate() {
        Typestate actualTypestate = Typestate.fromString("(end | (end | end))).end");
        Typestate expectedTypestate =
                new Typestate.Parallel(
                        new ArrayList<>() {
                            {
                                add(Typestate.END);
                                add(
                                        new Typestate.Parallel(
                                                new ArrayList<>() {
                                                    {
                                                        add(Typestate.END);
                                                        add(Typestate.END);
                                                    }
                                                },
                                                Typestate.END));
                            }
                        },
                        Typestate.END);
        assertEquals(actualTypestate, expectedTypestate);
    }

    @Test
    void parsesBooleanChoiceTypestate() {
        Typestate actualTypestate = Typestate.fromString("{m; [end, end]}");
        Typestate expectedTypestate =
                new Branch(
                        new HashMap<>() {
                            {
                                put("m", new BooleanChoice(Typestate.END, Typestate.END));
                            }
                        });

        assertEquals(actualTypestate, expectedTypestate);
    }
}
