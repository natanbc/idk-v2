package com.github.natanbc.idk.bytecode.util;

import java.util.Objects;
import java.util.function.Consumer;

public class Utils {
    public static <T> void checkedWrite(Object value, Class<T> type, Consumer<T> write) {
        if(type.isInstance(value)) {
            write.accept(type.cast(value));
        } else {
            throw new IllegalArgumentException(
                    "Expected value of type " + type.getName() + ", got "
                    + (value == null ? "null" : value.getClass().getName())
            );
        }
    }
    
    public static void validateU8(int value, String message) {
        if(value != (value & 0xFF)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void validateU16(int value, String message) {
        if(value != (value & 0xFFFF)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static void validateInternalU16(int value, String message) {
        if(value != (value & 0xFFFF)) {
            throw new IllegalStateException(message);
        }
    }
    
    public static void validateConstantString(String s) {
        Objects.requireNonNull(s, "Constant pool values may not be null");
    
        /* copied from DataOutputStream */
        var str = s.length();
        var utf = utfLen(s);
    
        if(utf > 65535 || /* overflow */ utf < str) {
            throw new IllegalArgumentException(tooLongMsg(s, utf));
        }
    }
    
    public static int utfLen(String s) {
        var str = s.length();
        var utf = str; // optimized for ASCII
        for(var i = 0; i < str; i++) {
            int c = s.charAt(i);
            if(c >= 0x80 || c == 0) {
                utf += (c >= 0x800) ? 2 : 1;
            }
        }
        return utf;
    }
    
    private static String tooLongMsg(String s, int bits32) {
        int len = s.length();
        String head = s.substring(0, 8);
        String tail = s.substring(len - 8, len);
        // handle int overflow with max 3x expansion
        long actualLength = (long)len + Integer.toUnsignedLong(bits32 - len);
        return "encoded string (" + head + "..." + tail + ") too long: "
                       + actualLength + " bytes";
    }
}
