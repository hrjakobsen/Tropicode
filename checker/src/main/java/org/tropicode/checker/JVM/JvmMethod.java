/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.tropicode.checker.JVM.instructions.JvmInstruction;
import org.tropicode.checker.cfg.InstructionGraph;

@Log4j2
public class JvmMethod {

    int access;
    String name;
    String descriptor;
    String signature;
    List<JvmInstruction> instructions;
    List<JvmExceptionHandler> exceptionHandlers;
    int numberOfLocalVariables = 0;

    public JvmMethod(int access, String name, String descriptor, String signature) {
        this(access, name, descriptor, signature, new ArrayList<>(), new ArrayList<>());
    }

    public boolean is(int perm) {
        return (access & perm) == perm;
    }

    public JvmMethod(
            int access,
            String name,
            String descriptor,
            String signature,
            List<JvmInstruction> instructions,
            List<JvmExceptionHandler> exceptionHandlers) {
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.instructions = instructions;
        this.exceptionHandlers = exceptionHandlers;
    }

    public int getNumberOfLocalVariables() {
        return numberOfLocalVariables;
    }

    public void setNumberOfLocalVariables(int numberOfLocalVariables) {
        this.numberOfLocalVariables = numberOfLocalVariables;
    }

    public List<JvmExceptionHandler> getExceptionHandlers() {
        return exceptionHandlers;
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public InstructionGraph getInstructionGraph() {
        return getInstructionGraph(0);
    }

    public InstructionGraph getInstructionGraph(int depth) {
        return InstructionGraph.fromList(this.instructions, depth);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public List<JvmInstruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<JvmInstruction> instructions) {
        this.instructions = instructions;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (JvmInstruction inst : this.getInstructions()) {
            str.append(inst.toString()).append("\n");
        }

        return "JvmMethod{"
                + "access="
                + access
                + ", name='"
                + name
                + '\''
                + ", descriptor='"
                + descriptor
                + '\''
                + ", signature='"
                + signature
                + '\''
                + ", instructions=\n"
                + str
                + '}';
    }

    public boolean hasReturnValue() {
        return !descriptor.endsWith("V");
    }

    public static final class AccessFlags {
        public static final int ACC_PUBLIC = 0x0001;
        public static final int ACC_PRIVATE = 0x0002;
        public static final int ACC_PROTECTED = 0x0004;
        public static final int ACC_STATIC = 0x0009;
        public static final int ACC_FINAL = 0x0010;
        public static final int ACC_SYNCHRONIZED = 0x0020;
        public static final int ACC_BRIDGE = 0x0040;
        public static final int ACC_VARARGS = 0x0080;
        public static final int ACC_NATIVE = 0x0100;
        public static final int ACC_ABSTRACT = 0x0400;
        public static final int ACC_STRICT = 0x0800;
        public static final int ACC_SYNTHETIC = 0x1000;
    }
}
