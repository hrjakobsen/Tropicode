/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.checker;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.tropicode.checker.JVM.JvmClass;

@Log4j2
public class ProtocolResolver {

    final List<Path> protocolsDirectories = new ArrayList<>();

    public ProtocolResolver(String protocolsDirectories, boolean ignoreDefaultDirectory) {
        if (protocolsDirectories != null) {
            String[] directories = protocolsDirectories.split(":");
            for (String directory : directories) {
                this.protocolsDirectories.add(Path.of(directory));
            }
        }
        if (!ignoreDefaultDirectory) {
            this.protocolsDirectories.add(
                    Path.of(
                            System.getProperty("user.home")
                                    + File.separator
                                    + ".tropicode"
                                    + File.separator
                                    + "protocols"));
        }
    }

    public Optional<Typestate> resolve(String className) {
        // TODO: Implement looking in the ClassPath as well
        if (this.protocolsDirectories.isEmpty()) {
            return Optional.empty();
        }

        for (Path directory : this.protocolsDirectories) {
            Path pathToProtocolFile = directory.resolve(className + ".protocol");
            if (Files.exists(pathToProtocolFile)) {
                log.debug(
                        "Found protocol for {} in: {}",
                        className,
                        pathToProtocolFile.toAbsolutePath());
                return Typestate.fromFile(pathToProtocolFile);
            }
        }

        return Optional.empty();
    }

    public Optional<Typestate> resolve(JvmClass klass) {
        if (klass.getAnnotations().containsKey("Lorg/tropicode/checker/annotations/Protocol;")) {
            // We should use the Protocol annotation
            return Optional.of(
                    Typestate.getInitialObjectProtocol(
                            klass.getAnnotations()
                                    .get("Lorg/tropicode/checker/annotations/Protocol;")
                                    .getValue()
                                    .toString()));
        } else {
            return resolve(klass.getName());
        }
    }
}
