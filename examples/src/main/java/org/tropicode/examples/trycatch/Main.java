/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.examples.trycatch;

import org.tropicode.checker.annotations.Protocol;

public class Main {
    @Protocol(
            "(try {doMethodCall; {nextMethod; {finalMethod; end}}} except {cleanup; end}); "
                    + "{after; end}")
    private static class ClassWithProtocol {
        public void doMethodCall() {
            System.out.println("doMethodCall");
        }

        public void nextMethod() {
            System.out.println("nextMethod");
        }

        public void finalMethod() {
            System.out.println("finalMethod");
        }

        public void cleanup() {
            System.out.println("cleanup");
        }

        public void after() {
            System.out.println("after");
        }
    }

    public static void main(String[] args) {
        ClassWithProtocol obj = new ClassWithProtocol();
        try {
            obj.doMethodCall();
            obj.nextMethod();
            obj.finalMethod();
            int a = 1 / 0;
        } catch (ArithmeticException e) {
            obj.cleanup();
            System.out.println("Error");
        }
        obj.after();
    }
}
