/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.examples.simplecall;

public class SwitchExample {

    public static void main(String[] args) {
        C1 c1 = new C1();
        switch (c1.question()) {
            case YES -> c1.Branch1();
            case NO -> c1.Branch2();
        }
    }
}
