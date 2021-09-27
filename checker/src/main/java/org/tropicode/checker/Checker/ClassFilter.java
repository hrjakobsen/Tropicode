/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.Checker;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.SneakyThrows;

public class ClassFilter {
    Set<String> patterns = new HashSet<>();

    public boolean accepts(String className) {
        return !rejects(className);
    }

    public boolean rejects(String className) {
        for (String pattern : patterns) {
            if (className.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    public void addPattern(String pattern) {
        this.patterns.add(pattern);
    }

    public void addPatterns(Collection<String> patterns) {
        this.patterns.addAll(patterns);
    }

    @SneakyThrows
    public void addFile(Path path) {
        Files.lines(path, StandardCharsets.UTF_8).forEach(this::addPattern);
    }
}
