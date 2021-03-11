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

public class Main {
    private C1 member = new C1();
    private static C1 staticmem;

    public static void main(String[] args) {
        C1 c = new C1();
        C1[] cs = new C1[] {c};
        int i = 0;
        c.question();
        if (i == 0) {
            c.Branch2();
            c.Branch1();
        } else {
            c.Branch1();
            c.Branch2();
        }
        c.stop();
    }
}