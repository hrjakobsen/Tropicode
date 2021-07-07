/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package person;

import Annotations.Protocol;

@Protocol("{setFirstName; {setLastName; {greet; end}}  setLastName; {setFirstName; {greet; end}}}")
public class Person {

    private String firstName;
    private String lastName;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void greet() {
        System.out.println("Hello " + firstName + " " + lastName);
    }
}
