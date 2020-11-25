package com.github.natanbc.idk.bytecode;

import com.github.natanbc.idk.bytecode.util.Utils;

public enum ConditionType {
    IF_TRUE(0), IF_FALSE(1);
    
    public final byte value;
    
    ConditionType(int value) {
        Utils.validateU8(value, "Value cannot be encoded in a single byte");
        this.value = (byte)value;
    }
    
    public static ConditionType type(byte id) {
        if(id == 0) return IF_TRUE;
        if(id == 1) return IF_FALSE;
        throw new IllegalArgumentException("Unknown type " + Integer.toHexString(id & 0xFF));
    }
}
