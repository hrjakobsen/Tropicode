/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     SPDX-License-Identifier: BSD-3-Clause
 */

package org.tropicode.checker.JVM;

public enum JvmOpCode {
    AALOAD(0x32),
    AASTORE(0x53),
    ACONST_NULL(0x1),
    ALOAD(0x19),
    ALOAD_0(0x2a),
    ALOAD_1(0x2b),
    ALOAD_2(0x2c),
    ALOAD_3(0x2d),
    ANEWARRAY(0xbd),
    ARETURN(0xb0),
    ARRAYLENGTH(0xbe),
    ASTORE(0x3a),
    ASTORE_0(0x4b),
    ASTORE_1(0x4c),
    ASTORE_2(0x4d),
    ASTORE_3(0x4e),
    ATHROW(0xbf),
    BALOAD(0x33),
    BASTORE(0x54),
    BIPUSH(0x10),
    BREAKPOINT(0xca),
    CALOAD(0x34),
    CASTORE(0x55),
    CHECKCAST(0xc0),
    D2F(0x90),
    D2I(0x8e),
    D2L(0x8f),
    DADD(0x63),
    DALOAD(0x31),
    DASTORE(0x52),
    DCMPG(0x98),
    DCMPL(0x97),
    DCONST_0(0x0e),
    DCONST_1(0x0f),
    DDIV(0x6f),
    DLOAD(0x18),
    DLOAD_0(0x26),
    DLOAD_1(0x27),
    DLOAD_2(0x28),
    DLOAD_3(0x29),
    DMUL(0x6b),
    DNEG(0x77),
    DREM(0x73),
    DRETURN(0xaf),
    DSTORE(0x39),
    DSTORE_0(0x47),
    DSTORE_1(0x48),
    DSTORE_2(0x49),
    DSTORE_3(0x4a),
    DSUB(0x67),
    DUP(0x59),
    DUP_X1(0x5a),
    DUP_X2(0x5b),
    DUP2(0x5c),
    DUP2_X1(0x5d),
    DUP2_X2(0x5e),
    F2D(0x8d),
    F2I(0x8b),
    F2L(0x8c),
    FADD(0x62),
    FALOAD(0x30),
    FASTORE(0x51),
    FCMPG(0x96),
    FCMPL(0x95),
    FCONST_0(0x0b),
    FCONST_1(0x0c),
    FCONST_2(0x0d),
    FDIV(0x6e),
    FLOAD(0x17),
    FLOAD_0(0x22),
    FLOAD_1(0x23),
    FLOAD_2(0x24),
    FLOAD_3(0x25),
    FMUL(0x6a),
    FNEG(0x76),
    FREM(0x72),
    FRETURN(0xae),
    FSTORE(0x38),
    FSTORE_0(0x43),
    FSTORE_1(0x44),
    FSTORE_2(0x45),
    FSTORE_3(0x46),
    FSUB(0x66),
    GETFIELD(0xb4),
    GETSTATIC(0xb2),
    GOTO(0xa7),
    GOTO_W(0xc8),
    I2B(0x91),
    I2C(0x92),
    I2D(0x87),
    I2F(0x86),
    I2L(0x85),
    I2S(0x93),
    IADD(0x60),
    IALOAD(0x2e),
    IAND(0x7e),
    IASTORE(0x4f),
    ICONST_M1(0x2),
    ICONST_0(0x3),
    ICONST_1(0x4),
    ICONST_2(0x5),
    ICONST_3(0x6),
    ICONST_4(0x7),
    ICONST_5(0x8),
    IDIV(0x6c),
    IF_ACMPEQ(0xa5),
    IF_ACMPNE(0xa6),
    IF_ICMPEQ(0x9f),
    IF_ICMPGE(0xa2),
    IF_ICMPGT(0xa3),
    IF_ICMPLE(0xa4),
    IF_ICMPLT(0xa1),
    IF_ICMPNE(0xa0),
    IFEQ(0x99),
    IFGE(0x9c),
    IFGT(0x9d),
    IFLE(0x9e),
    IFLT(0x9b),
    IFNE(0x9a),
    IFNONNULL(0xc7),
    IFNULL(0xc6),
    IINC(0x84),
    ILOAD(0x15),
    ILOAD_0(0x1a),
    ILOAD_1(0x1b),
    ILOAD_2(0x1c),
    ILOAD_3(0x1d),
    IMPDEP1(0xfe),
    IMPDEP2(0xff),
    IMUL(0x68),
    INEG(0x74),
    INSTANCEOF(0xc1),
    INVOKEDYNAMIC(0xba),
    INVOKEINTERFACE(0xb9),
    INVOKESPECIAL(0xb7),
    INVOKESTATIC(0xb8),
    INVOKEVIRTUAL(0xb6),
    IOR(0x80),
    IREM(0x70),
    IRETURN(0xac),
    ISHL(0x78),
    ISHR(0x7a),
    ISTORE(0x36),
    ISTORE_0(0x3b),
    ISTORE_1(0x3c),
    ISTORE_2(0x3d),
    ISTORE_3(0x3e),
    ISUB(0x64),
    IUSHR(0x7c),
    IXOR(0x82),
    JSR(0xa8),
    JSR_W(0xc9),
    L2D(0x8a),
    L2F(0x89),
    L2I(0x88),
    LADD(0x61),
    LALOAD(0x2f),
    LAND(0x7f),
    LASTORE(0x50),
    LCMP(0x94),
    LCONST_0(0x9),
    LCONST_1(0x0a),
    LDC(0x12),
    LDC_W(0x13),
    LDC2_W(0x14),
    LDIV(0x6d),
    LLOAD(0x16),
    LLOAD_0(0x1e),
    LLOAD_1(0x1f),
    LLOAD_2(0x20),
    LLOAD_3(0x21),
    LMUL(0x69),
    LNEG(0x75),
    LOOKUPSWITCH(0xab),
    LOR(0x81),
    LREM(0x71),
    LRETURN(0xad),
    LSHL(0x79),
    LSHR(0x7b),
    LSTORE(0x37),
    LSTORE_0(0x3f),
    LSTORE_1(0x40),
    LSTORE_2(0x41),
    LSTORE_3(0x42),
    LSUB(0x65),
    LUSHR(0x7d),
    LXOR(0x83),
    MONITORENTER(0xc2),
    MONITOREXIT(0xc3),
    MULTIANEWARRAY(0xc5),
    NEW(0xbb),
    NEWARRAY(0xbc),
    NOP(0x0),
    POP(0x57),
    POP2(0x58),
    PUTFIELD(0xb5),
    PUTSTATIC(0xb3),
    RET(0xa9),
    RETURN(0xb1),
    SALOAD(0x35),
    SASTORE(0x56),
    SIPUSH(0x11),
    SWAP(0x5f),
    TABLESWITCH(0xaa),
    WIDE(0xc4);

    private final int opcode;

    JvmOpCode(int opcode) {
        this.opcode = opcode;
    }

    public static JvmOpCode getFromOpcode(int opcode) {
        for (JvmOpCode op : values()) {
            if (op.opcode == opcode) {
                return op;
            }
        }
        throw new InvalidOpcodeException(opcode);
    }
}
