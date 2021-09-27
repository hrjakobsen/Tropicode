/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.examples.simplecall;

public class Main {

    public static void main(String[] args) {
        C1 c = new C1();
        c.question();
        int in = 48; // System.in.read(); // 0 = ASCII(48)
        if (in == 48) {
            c.Branch2();
            c.Branch1();
        } else {
            c.Branch1();
            c.Branch2();
        }
        c.stop();
    }
}
