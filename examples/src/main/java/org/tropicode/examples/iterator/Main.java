/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.examples.iterator;

import org.tropicode.checker.annotations.Protocol;
import org.tropicode.checker.annotations.Unrestricted;

public class Main {
    @Protocol("rec X.{hasNext; [{next; X}, end]}")
    private static class IntIterator {
        int index;
        int[] arr;

        public IntIterator(int[] arr) {
            this.arr = arr;
        }

        public boolean hasNext() {
            return index < arr.length;
        }

        public int next() {
            return arr[index++];
        }

        @Override
        @Unrestricted
        public String toString() {
            return "Iterator is at index " +  index;
        }
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4, 5};
        IntIterator i = new IntIterator(nums);
        while (i.hasNext()) {
            System.out.println(i.next());
        }
    }
}
