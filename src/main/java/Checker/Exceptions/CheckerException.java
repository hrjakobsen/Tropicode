/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
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
