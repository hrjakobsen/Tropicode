/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package JVM;

import JVM.Instructions.JvmBinaryOperation;
import JVM.Instructions.JvmCONST;
import JVM.Instructions.JvmDUP;
import JVM.Instructions.JvmINVOKE;
import JVM.Instructions.JvmInstruction;
import JVM.Instructions.JvmJSR;
import JVM.Instructions.JvmJUMP;
import JVM.Instructions.JvmLDC;
import JVM.Instructions.JvmLOAD;
import JVM.Instructions.JvmLabel;
import JVM.Instructions.JvmNEW;
import JVM.Instructions.JvmNoEffectOperation;
import JVM.Instructions.JvmPOP;
import JVM.Instructions.JvmReturnOperation;
import JVM.Instructions.JvmSTORE;
import JVM.Instructions.JvmUnsupportedOperation;

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
