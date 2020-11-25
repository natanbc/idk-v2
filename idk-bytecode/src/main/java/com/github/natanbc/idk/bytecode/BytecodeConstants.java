package com.github.natanbc.idk.bytecode;

import com.github.natanbc.idk.common.BinaryOperationType;
import com.github.natanbc.idk.common.UnaryOperationType;

import static com.github.natanbc.idk.common.BinaryOperationType.*;
import static com.github.natanbc.idk.common.UnaryOperationType.*;

public class BytecodeConstants {
    public static final int MAGIC = 0xB173C0DE;
    public static final short FUNCTION_TAG = (short)0xC0DE;
    public static final byte CONSTANT_LONG = 1;
    public static final byte CONSTANT_DOUBLE = 2;
    public static final byte CONSTANT_STRING = 3;
    
    private static final BinaryOperationType[] BINARY_OPS = {
            ADD, SUB, MUL, DIV, MOD, POW, EQ, NEQ, GREATER,
            GREATER_EQ, SMALLER, SMALLER_EQ, AND, OR
    };
    private static final UnaryOperationType[] UNARY_OPS = {
            NEG, NEGATE
    };
    
    public static int binaryOpNumber(BinaryOperationType type) {
        return findNumber(BINARY_OPS, type);
    }
    
    public static int unaryOpNumber(UnaryOperationType type) {
        return findNumber(UNARY_OPS, type);
    }
    
    private static <E extends Enum<E>> int findNumber(E[] array, E value) {
        for(var i = 0; i < array.length; i++) {
            if(array[i] == value) return i;
        }
        throw new IllegalArgumentException("Unsupported type " + value.name());
    }
    
    public static BinaryOperationType binaryOp(byte number) {
        return findValue(BINARY_OPS, number);
    }
    
    public static UnaryOperationType unaryOp(byte number) {
        return findValue(UNARY_OPS, number);
    }
    
    private static <E extends Enum<E>> E findValue(E[] array, byte id) {
        int idx = id & 0xFF;
        if(idx >= array.length) {
            throw new IllegalArgumentException("Unknown type " + Integer.toHexString(idx));
        }
        return array[idx];
    }
}
