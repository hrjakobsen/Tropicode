/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     Tropicode is a Java bytecode analyser used to verify object protocols.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
