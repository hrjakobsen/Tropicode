/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.tropicode.checker.checker.exceptions.CheckerException;

@Log4j2
public class MethodDescriptorExtractor {
    private List<String> argumentTypes;
    private String returnType;
    private int index = 0;
    private char[] chars;

    public MethodDescriptorExtractor() {}

    public MethodDescriptorExtractor(String descriptor) {
        parseDescriptor(descriptor);
    }

    public void parseDescriptor(String descriptor) {
        if (descriptor.length() < 2 || descriptor.charAt(0) != '(') {
            throw new CheckerException("Invalid parameter string: " + descriptor);
        }
        log.debug("Parsing descriptor: " + descriptor);
        chars = descriptor.toCharArray();
        index = 0;
        argumentTypes = parseArguments();
        returnType = parseReturnType();
    }

    private String parseReturnType() {
        return parseType();
    }

    private List<String> parseArguments() {
        if (chars[index] != '(') {
            throw new CheckerException(
                    "Expected '(' when parsing arguments. Got: '" + chars[index] + "'");
        }
        // skip '('
        index++;
        List<String> args = new ArrayList<>();
        while (chars[index] != ')') {
            args.add(parseType());
        }
        // skip ')'
        index++;
        return args;
    }

    private String parseType() {
        switch (chars[index]) {
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'Z':
            case 'V':
                return String.valueOf(chars[index++]);
            case 'L':
                return parseUntil(';');
            case '[':
                index++;
                return parseType();
            default:
                throw new CheckerException(
                        "Invalid type descriptor "
                                + String.valueOf(chars)
                                + ". Unable to parse type starting with '"
                                + chars[index]
                                + "'.");
        }
    }

    private String parseUntil(char delimiter) {
        StringBuilder str = new StringBuilder();
        do {
            if (index >= chars.length) {
                throw new CheckerException(
                        "Invalid method descriptor. Could not find the character '"
                                + delimiter
                                + "'.");
            }
            str.append(chars[index]);
        } while (chars[index++] != delimiter);
        return str.toString();
    }

    public List<String> getArgumentTypes() {
        return argumentTypes;
    }

    public String getReturnType() {
        return returnType;
    }

    public boolean hasReturnValue() {
        return !getReturnType().equals("V");
    }

    public boolean returnsObject() {
        return hasReturnValue() && isObjectDescriptor(returnType);
    }

    public boolean returnsBaseType() {
        return hasReturnValue() && !isObjectDescriptor(returnType);
    }

    private static boolean isObjectDescriptor(String typeDescriptor) {
        return typeDescriptor.startsWith("[") || typeDescriptor.startsWith("L");
    }
}
