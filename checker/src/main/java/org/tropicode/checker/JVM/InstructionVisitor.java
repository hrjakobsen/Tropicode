/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM;

import org.tropicode.checker.JVM.instructions.JvmBinaryOperation;
import org.tropicode.checker.JVM.instructions.JvmCONST;
import org.tropicode.checker.JVM.instructions.JvmDUP;
import org.tropicode.checker.JVM.instructions.JvmINVOKE;
import org.tropicode.checker.JVM.instructions.JvmInstruction;
import org.tropicode.checker.JVM.instructions.JvmJSR;
import org.tropicode.checker.JVM.instructions.JvmJUMP;
import org.tropicode.checker.JVM.instructions.JvmLDC;
import org.tropicode.checker.JVM.instructions.JvmLOAD;
import org.tropicode.checker.JVM.instructions.JvmLabel;
import org.tropicode.checker.JVM.instructions.JvmNEW;
import org.tropicode.checker.JVM.instructions.JvmNoEffectOperation;
import org.tropicode.checker.JVM.instructions.JvmPOP;
import org.tropicode.checker.JVM.instructions.JvmReturnOperation;
import org.tropicode.checker.JVM.instructions.JvmSTORE;
import org.tropicode.checker.JVM.instructions.JvmUnsupportedOperation;

public abstract class InstructionVisitor<T> {

    public T visit(JvmInstruction inst) {
        if (inst instanceof JvmBinaryOperation) {
            return visitJvmBinaryOperation((JvmBinaryOperation) inst);
        } else if (inst instanceof JvmCONST) {
            return visitJvmCONST((JvmCONST) inst);
        } else if (inst instanceof JvmDUP) {
            return visitJvmDUP((JvmDUP) inst);
        } else if (inst instanceof JvmINVOKE) {
            return visitJvmINVOKE((JvmINVOKE) inst);
        } else if (inst instanceof JvmJSR) {
            return visitJvmJSR((JvmJSR) inst);
        } else if (inst instanceof JvmJUMP) {
            return visitJvmJUMP((JvmJUMP) inst);
        } else if (inst instanceof JvmLabel) {
            return visitJvmLabel((JvmLabel) inst);
        } else if (inst instanceof JvmLDC) {
            return visitJvmLDC((JvmLDC) inst);
        } else if (inst instanceof JvmLOAD) {
            return visitJvmLOAD((JvmLOAD) inst);
        } else if (inst instanceof JvmPOP) {
            return visitJvmPOP((JvmPOP) inst);
        } else if (inst instanceof JvmReturnOperation) {
            return visitJvmReturnOperation((JvmReturnOperation) inst);
        } else if (inst instanceof JvmSTORE) {
            return visitJvmSTORE((JvmSTORE) inst);
        } else if (inst instanceof JvmNoEffectOperation) {
            return visitJvmNoEffectOperation((JvmNoEffectOperation) inst);
        } else if (inst instanceof JvmUnsupportedOperation) {
            return visitJvmUnsupportedOperation((JvmUnsupportedOperation) inst);
        } else if (inst instanceof JvmNEW) {
            return visitJvmNEW((JvmNEW) inst);
        } else {
            throw new IllegalArgumentException("Unsupported class in visitor");
        }
    }

    public abstract T visitJvmBinaryOperation(JvmBinaryOperation inst);

    public abstract T visitJvmCONST(JvmCONST inst);

    public abstract T visitJvmDUP(JvmDUP inst);

    public abstract T visitJvmINVOKE(JvmINVOKE inst);

    public abstract T visitJvmJSR(JvmJSR inst);

    public abstract T visitJvmJUMP(JvmJUMP inst);

    public abstract T visitJvmLabel(JvmLabel inst);

    public abstract T visitJvmLDC(JvmLDC inst);

    public abstract T visitJvmLOAD(JvmLOAD inst);

    public abstract T visitJvmNEW(JvmNEW inst);

    public abstract T visitJvmPOP(JvmPOP inst);

    public abstract T visitJvmReturnOperation(JvmReturnOperation inst);

    public abstract T visitJvmSTORE(JvmSTORE inst);

    public abstract T visitJvmNoEffectOperation(JvmNoEffectOperation inst);

    public abstract T visitJvmUnsupportedOperation(JvmUnsupportedOperation inst);
}
