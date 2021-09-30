/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.examples.simplecall;

import org.tropicode.checker.annotations.Protocol;

@Protocol("{question; {Branch1; {Branch2; {stop; end}} " + "Branch2; {Branch1; {stop; end}}}}")
public class C1 {

    public String test;

    public Answer question() {
        System.out.println("Question asked");
        return Answer.YES;
    }

    public void Branch1() {
        System.out.println("Branch 1 chosen");
    }

    public void Branch2() {
        System.out.println("Branch 2 chosen");
    }

    public void stop() {
        System.out.println("Stopped");
    }
}
