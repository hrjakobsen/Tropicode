/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     Tropicode is a Java bytecode analyser used to verify object protocols.
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

package Checker.Exceptions;

public class CheckerException extends RuntimeException {

    public CheckerException(String message) {
        super(message);
    }

    public static String centerText(int width, String text) {
        int len = text.length();
        int leftPadding = (width - len) / 2;
        int rightPadding = width - len - leftPadding;
        return String.format(
                "%" + leftPadding + "s" + "%s" + "%" + rightPadding + "s", "", text, "");
    }

    @Override
    public String toString() {
        final String line =
                "================================================================================\n";
        final String title = centerText(80, this.getClass().getName()) + "\n";
        StringBuilder sb = new StringBuilder(line).append(title);
        String str = this.getMessage();
        sb.append(str.replaceAll("(.{80})", "$1\n"));
        sb.append("\n" + line);
        return sb.toString();
    }
}
