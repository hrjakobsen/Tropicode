/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.Checker.Extractor;

import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.tropicode.checker.JVM.Instructions.JvmAALOAD;
import org.tropicode.checker.JVM.Instructions.JvmAASTORE;
import org.tropicode.checker.JVM.Instructions.JvmALOADCONST;
import org.tropicode.checker.JVM.Instructions.JvmANEWARRAY;
import org.tropicode.checker.JVM.Instructions.JvmASTORECONST;
import org.tropicode.checker.JVM.Instructions.JvmBinaryOperation;
import org.tropicode.checker.JVM.Instructions.JvmCONST;
import org.tropicode.checker.JVM.Instructions.JvmDUP;
import org.tropicode.checker.JVM.Instructions.JvmINVOKE;
import org.tropicode.checker.JVM.Instructions.JvmInstanceFieldOperation;
import org.tropicode.checker.JVM.Instructions.JvmInstruction;
import org.tropicode.checker.JVM.Instructions.JvmJSR;
import org.tropicode.checker.JVM.Instructions.JvmJUMP;
import org.tropicode.checker.JVM.Instructions.JvmLDC;
import org.tropicode.checker.JVM.Instructions.JvmLOAD;
import org.tropicode.checker.JVM.Instructions.JvmLOOKUPSWITCH;
import org.tropicode.checker.JVM.Instructions.JvmLabel;
import org.tropicode.checker.JVM.Instructions.JvmNEW;
import org.tropicode.checker.JVM.Instructions.JvmNoEffectOperation;
import org.tropicode.checker.JVM.Instructions.JvmPOP;
import org.tropicode.checker.JVM.Instructions.JvmReturnOperation;
import org.tropicode.checker.JVM.Instructions.JvmSTORE;
import org.tropicode.checker.JVM.Instructions.JvmSWAP;
import org.tropicode.checker.JVM.Instructions.JvmStaticFieldOperation;
import org.tropicode.checker.JVM.Instructions.JvmTABLESWITCH;
import org.tropicode.checker.JVM.Instructions.JvmUnaryOperation;
import org.tropicode.checker.JVM.Instructions.JvmUnsupportedOperation;
import org.tropicode.checker.JVM.JvmExceptionHandler;
import org.tropicode.checker.JVM.JvmMethod;
import org.tropicode.checker.JVM.JvmOpCode;

@Log4j2
class CodeExtractorMethodVisitor extends MethodVisitor {

    private final Set<String> dependencies;
    JvmMethod method;
    private int lastLineNumber = -1;

    public CodeExtractorMethodVisitor(MethodVisitor mv, JvmMethod m, Set<String> dependencies) {
        super(Opcodes.ASM8, mv);
        this.method = m;
        this.dependencies = dependencies;
    }

    private void addOperation(JvmInstruction op) {
        method.getInstructions().add(op);
        op.setLineNumber(lastLineNumber);
    }

    public JvmMethod getMethod() {
        return method;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        this.lastLineNumber = line;
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitInsn(int opcode) {
        JvmOpCode jvmop = JvmOpCode.getFromOpcode(opcode);
        switch (jvmop) {
            case NOP:
                addOperation(new JvmNoEffectOperation(jvmop));
                break;
            case ACONST_NULL:
            case ICONST_M1:
            case ICONST_0:
            case ICONST_1:
            case ICONST_2:
            case ICONST_3:
            case ICONST_4:
            case ICONST_5:
            case LCONST_0:
            case LCONST_1:
            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
            case DCONST_0:
            case DCONST_1:
                addOperation(new JvmCONST(jvmop));
                break;
            case IADD:
            case LADD:
            case FADD:
            case DADD:
            case ISUB:
            case LSUB:
            case FSUB:
            case DSUB:
            case IMUL:
            case LMUL:
            case FMUL:
            case DMUL:
            case IDIV:
            case LDIV:
            case FDIV:
            case DDIV:
            case IREM:
            case LREM:
            case FREM:
            case DREM:
            case IAND:
            case LAND:
            case IOR:
            case LOR:
            case IXOR:
            case LXOR:
            case LCMP:
            case FCMPL:
            case FCMPG:
            case DCMPL:
            case DCMPG:
                addOperation(new JvmBinaryOperation(jvmop));
                break;
            case IALOAD:
            case LALOAD:
            case FALOAD:
            case DALOAD:
            case BALOAD:
            case CALOAD:
            case SALOAD:
                addOperation(new JvmALOADCONST(jvmop));
                break;
            case AALOAD:
                addOperation(new JvmAALOAD());
                break;
            case IASTORE:
            case LASTORE:
            case FASTORE:
            case DASTORE:
            case BASTORE:
            case CASTORE:
            case SASTORE:
                addOperation(new JvmASTORECONST(jvmop));
                break;
            case AASTORE:
                addOperation(new JvmAASTORE());
                break;
            case SWAP:
                addOperation(new JvmSWAP());
                break;
            case DUP_X1:
            case DUP_X2:
            case DUP2:
            case DUP2_X1:
            case DUP2_X2:
                addOperation(new JvmDUP(jvmop));
                break;
            case INEG:
            case LNEG:
            case FNEG:
            case DNEG:
            case ISHL:
            case LSHL:
            case ISHR:
            case LSHR:
            case IUSHR:
            case LUSHR:
            case I2L:
            case I2F:
            case I2D:
            case L2I:
            case L2F:
            case L2D:
            case F2I:
            case F2L:
            case F2D:
            case D2I:
            case D2L:
            case D2F:
            case I2B:
            case I2C:
            case I2S:
                addOperation(new JvmUnaryOperation(jvmop));
                break;
            case ARRAYLENGTH:
                addOperation(new JvmCONST(jvmop));
            case ATHROW:
            case MONITORENTER:
            case MONITOREXIT:
                addOperation(new JvmUnsupportedOperation(jvmop));
                break;
            case DUP:
                addOperation(new JvmDUP(JvmOpCode.DUP));
                break;
            case POP:
            case POP2:
                addOperation(new JvmPOP());
                break;
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
            case RETURN:
                addOperation(new JvmReturnOperation(jvmop));
                break;
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        JvmOpCode jvmop = JvmOpCode.getFromOpcode(opcode);
        switch (jvmop) {
            case BIPUSH:
            case SIPUSH:
                addOperation(new JvmCONST(jvmop));
                break;
            case NEWARRAY:
                addOperation(new JvmUnsupportedOperation(jvmop));
                break;
        }
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        JvmOpCode jvmop = JvmOpCode.getFromOpcode(opcode);
        method.setNumberOfLocalVariables(Math.max(var + 1, method.getNumberOfLocalVariables()));
        switch (jvmop) {
            case ILOAD:
            case LLOAD:
            case FLOAD:
            case DLOAD:
            case ALOAD:
                addOperation(new JvmLOAD(opcode, var));
                break;
            case ISTORE:
            case LSTORE:
            case FSTORE:
            case DSTORE:
            case ASTORE:
                addOperation(new JvmSTORE(opcode, var));
                break;
            case RET:
                addOperation(new JvmUnsupportedOperation(jvmop));
                break;
        }
        super.visitVarInsn(opcode, var);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        JvmOpCode jvmop = JvmOpCode.getFromOpcode(opcode);
        switch (jvmop) {
            case ANEWARRAY:
                addOperation(new JvmANEWARRAY(type));
                break;
            case CHECKCAST:
                addOperation(new JvmNoEffectOperation(jvmop));
                break;
            case INSTANCEOF:
                addOperation(new JvmUnaryOperation(jvmop));
                break;
            case NEW:
                addOperation(new JvmNEW(type));
                break;
        }
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        dependencies.add(owner);
        JvmOpCode jvmop = JvmOpCode.getFromOpcode(opcode);
        switch (jvmop) {
            case GETSTATIC:
            case PUTSTATIC:
                addOperation(new JvmStaticFieldOperation(jvmop, owner, name));
                break;
            case GETFIELD:
            case PUTFIELD:
                addOperation(new JvmInstanceFieldOperation(jvmop, owner, name));
                break;
            default:
                throw new IllegalStateException();
        }
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitMethodInsn(
            int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (!owner.startsWith("[")) {
            // TODO: Verify that this always work, and that the class in the array will be found
            // elsewhere
            dependencies.add(owner);
        }
        JvmOpCode jvmop = JvmOpCode.getFromOpcode(opcode);
        addOperation(new JvmINVOKE(jvmop, owner, name, descriptor, isInterface));
        if (this.mv != null) {
            this.mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }

    @Override
    public void visitInvokeDynamicInsn(
            String name,
            String descriptor,
            Handle bootstrapMethodHandle,
            Object... bootstrapMethodArguments) {
        addOperation(new JvmUnsupportedOperation(JvmOpCode.INVOKEDYNAMIC));
        super.visitInvokeDynamicInsn(
                name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        JvmOpCode jvmop = JvmOpCode.getFromOpcode(opcode);
        switch (jvmop) {
            case JSR:
                addOperation(new JvmJSR(label));
                break;
            case GOTO:
                addOperation(new JvmJUMP(jvmop, label, 0));
                break;
            case IFNULL:
            case IFNONNULL:
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
                addOperation(new JvmJUMP(jvmop, label, 1));
                break;
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
                addOperation(new JvmJUMP(jvmop, label, 2));
                break;
        }
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(Label label) {
        addOperation(new JvmLabel(label.toString()));
        super.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(Object value) {
        addOperation(new JvmLDC(value));
        super.visitLdcInsn(value);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        addOperation(new JvmNoEffectOperation(JvmOpCode.IINC));
        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        addOperation(new JvmTABLESWITCH(dflt, labels));
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        addOperation(new JvmLOOKUPSWITCH(dflt, keys, labels));
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        addOperation(new JvmUnsupportedOperation(JvmOpCode.MULTIANEWARRAY));
        super.visitMultiANewArrayInsn(descriptor, numDimensions);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(
            int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return new CodeExtractorTypeAnnotationExtractor(
                super.visitTypeAnnotation(typeRef, typePath, descriptor, visible),
                typeRef,
                typePath,
                descriptor,
                visible,
                method);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        method.getExceptionHandlers().add(new JvmExceptionHandler(start, end, handler, type));
        super.visitTryCatchBlock(start, end, handler, type);
    }

    public Set<String> getDependencies() {
        return dependencies;
    }
}
