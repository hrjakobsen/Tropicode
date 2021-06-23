/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package simplecall;

import Annotations.Protocol;

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
