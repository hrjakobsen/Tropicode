/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.examples.person;

public class Main {

    public static void main(String[] args) {
        Person p = new Person();
        p.setFirstName("John");
        p.greet();
    }
}
