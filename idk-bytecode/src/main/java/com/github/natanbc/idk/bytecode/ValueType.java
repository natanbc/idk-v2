package com.github.natanbc.idk.bytecode;

import com.github.natanbc.idk.bytecode.util.Utils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ValueType {
    NIL(0),
    BOOLEAN(1),
    LONG(2),
    DOUBLE(3),
    STRING(4),
    ARRAY(5),
    OBJECT(6),
    RANGE(7);
    
    private static final Map<Integer, ValueType> MAP = Arrays.stream(values()).collect(
            Collectors.toUnmodifiableMap(
                    op -> op.value & 0xFF,
                    Function.identity(),
                    (a, b) -> { throw new IllegalStateException(
                            String.format("Duplicate value (%d) between %s and %s",
                                    a.value & 0xFF, a, b)
                    ); }
            )
    );
    public final byte value;
    
    ValueType(int value) {
        Utils.validateU8(value, "Value cannot be encoded in a single byte");
        this.value = (byte)value;
    }
    
    public static ValueType type(byte id) {
        var t = MAP.get(id & 0xFF);
        if(t == null) {
            throw new IllegalArgumentException("Unknown type " + Integer.toHexString(id & 0xFF));
        }
        return t;
    }
}
