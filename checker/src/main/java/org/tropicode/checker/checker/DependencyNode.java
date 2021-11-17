/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DependencyNode {
    private final String className;
    private final List<DependencyNode> children = new ArrayList<>();

    public DependencyNode(String className) {
        // Classes can be specified with both / and . as separators
        this.className = className.replaceAll("\\.", "/");
    }

    public DependencyNode(String className, Collection<DependencyNode> children) {
        this(className);
        this.children.addAll(children);
    }

    public String getClassName() {
        return className;
    }

    public String getClassPath() {
        return className.replaceAll("/", ".");
    }

    public List<DependencyNode> getChildren() {
        return children;
    }

    public List<String> getDependencyList() {
        ArrayList<String> deps = new ArrayList<>();
        calculateDependencies(new HashSet<>(), deps);
        return deps;
    }

    private void calculateDependencies(Set<String> alreadyLoaded, List<String> dependencies) {
        for (DependencyNode child : this.children) {
            child.calculateDependencies(alreadyLoaded, dependencies);
            if (!alreadyLoaded.contains(child.getClassName())) {
                dependencies.add(child.getClassName());
                alreadyLoaded.add(child.getClassName());
            }
        }
    }
}
