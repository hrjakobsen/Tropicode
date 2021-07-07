/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package gettersetter;

public class Main {

    public static void main(String[] args) {
        DataWrapper wrapper = new DataWrapper();
        wrapper.setA("isA");
        wrapper.setB("isB");
        System.out.println(wrapper.getA());
        // Illegal call to setA (should be to setC)
        wrapper.setA("isC");
    }
}
