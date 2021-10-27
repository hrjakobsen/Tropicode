/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.tropicode.checker.checker.Typestate;

@Log4j2
public class JvmClass {

    int access_flags;
    String name;
    String superName;
    String[] interfaces;
    String signature;
    Map<String, JvmMethod> methods = new HashMap<>();
    List<String> fields = new ArrayList<>();
    Map<String, JvmValue> staticFields = new HashMap<>();
    Typestate protocol = null;
    Map<String, JvmAnnotation> annotations = new HashMap<>();

    public Map<String, JvmAnnotation> getAnnotations() {
        return annotations;
    }

    public boolean is(int access_flag) {
        return (this.access_flags & access_flag) == access_flag;
    }

    public int getAccess_flags() {
        return access_flags;
    }

    public void setAccess_flags(int access_flags) {
        this.access_flags = access_flags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuperName() {
        return superName;
    }

    public void setSuperName(String superName) {
        this.superName = superName;
    }

    public String[] getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(String[] interfaces) {
        this.interfaces = interfaces;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Map<String, JvmMethod> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, JvmMethod> methods) {
        this.methods = methods;
    }

    public List<String> getFields() {
        return fields;
    }

    public Map<String, JvmValue> getStaticFields() {
        return staticFields;
    }

    @Override
    public String toString() {
        return "JvmClass " + this.name;
    }

    public void dump() {
        log.info("JvmClass" + this.name + "{" + "methods=" + methods.toString() + '}');
    }

    public Typestate getProtocol() {
        return protocol;
    }

    public void setProtocol(Typestate protocol) {
        this.protocol = protocol;
    }

    public boolean hasStaticConstructor() {
        return this.methods.containsKey("<clinit>()V");
    }

    public static final class AccessFlags {
        public static final int ACC_PUBLIC = 0x0001;
        public static final int ACC_FINAL = 0x0010;
        public static final int ACC_SUPER = 0x0020;
        public static final int ACC_INTERFACE = 0x0200;
        public static final int ACC_ABSTRACT = 0x0400;
        public static final int ACC_SYNTHETIC = 0x1000;
        public static final int ACC_ANNOTATION = 0x2000;
        public static final int ACC_ENUM = 0x4000;
    }
}
