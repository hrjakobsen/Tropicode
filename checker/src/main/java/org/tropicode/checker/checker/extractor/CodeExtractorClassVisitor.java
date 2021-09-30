/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.checker.extractor;

import java.util.HashSet;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.tropicode.checker.JVM.JvmClass;
import org.tropicode.checker.JVM.JvmMethod;
import org.tropicode.checker.JVM.JvmValue;

@Log4j2
public class CodeExtractorClassVisitor extends ClassVisitor {

    JvmClass klass = new JvmClass();
    Set<String> classDependencies = new HashSet<>();

    public CodeExtractorClassVisitor() {
        super(Opcodes.ASM8);
    }

    public CodeExtractorClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM8, cv);
    }

    public JvmClass getJvmClass() {
        return klass;
    }

    public Set<String> getClassDependencies() {
        return classDependencies;
    }

    @Override
    public void visit(
            int version,
            int access,
            String name,
            String signature,
            String superName,
            String[] interfaces) {
        if (superName != null) {
            classDependencies.add(superName);
        }
        klass.setAccess_flags(access);
        klass.setName(name);
        klass.setInterfaces(interfaces);
        klass.setSuperName(superName);
        klass.setSignature(signature);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(
            int access, String name, String descriptor, String signature, String[] exceptions) {
        JvmMethod m = new JvmMethod(access, name, descriptor, signature);
        m.setNumberOfLocalVariables(countMethodArguments(descriptor));
        klass.getMethods().put(name + descriptor, m);
        return new CodeExtractorMethodVisitor(
                super.visitMethod(access, name, descriptor, signature, exceptions),
                m,
                classDependencies);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return new CodeExtractorAnnotationExtractor(
                super.visitAnnotation(desc, visible), desc, visible, klass);
    }

    @Override
    public FieldVisitor visitField(
            int access, String name, String descriptor, String signature, Object value) {
        boolean isStatic = (access & Opcodes.ACC_STATIC) > 0;
        if (isStatic) {
            if (descriptor.charAt(0) == 'L') {
                klass.getStaticFields().put(name, JvmValue.UNKNOWN_REFERENCE);
            } else {
                klass.getStaticFields().put(name, JvmValue.UNKNOWN);
            }
        } else {
            klass.getFields().add(name);
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    public static int countMethodArguments(String descriptor) {
        if (descriptor.charAt(0) != '(') {
            throw new IllegalArgumentException("Descriptor must start with (");
        }
        return parseTypeArguments(descriptor.substring(1));
    }

    private static int parseTypeArguments(String descriptor) {
        if (descriptor.charAt(0) == ')') {
            return 0;
        }
        return switch (descriptor.charAt(0)) {
            case 'B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z' -> 1 + parseTypeArguments(
                descriptor.substring(1));
            case '[' -> parseTypeArguments(descriptor.substring(1));
            case 'L' -> 1 + parseTypeArguments(descriptor.substring(descriptor.indexOf(';') + 1));
            default -> throw new IllegalStateException(descriptor);
        };
    }
}
