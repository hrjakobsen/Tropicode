/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.examples.trycatch;

import org.tropicode.checker.annotations.Protocol;

public class Main {
    @Protocol("{doMethodCall; {nextMethod; {finalMethod; end}}}")
    private static class ClassWithProtocol {
        public void doMethodCall() {}

        public void nextMethod() {}

        public void finalMethod() {}

        public void cleanup() {}
    }

    public static void main(String[] args) {
        ClassWithProtocol obj = new ClassWithProtocol();
        try {
            obj.doMethodCall();
            int a = 1 / 0;
        } catch (ArithmeticException e) {
            System.out.println("Error");
        }
        obj.nextMethod();
    }
}
