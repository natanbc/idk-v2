package com.github.natanbc.idk.bytecode;

import com.github.natanbc.idk.bytecode.util.ByteReader;

import java.util.ArrayList;
import java.util.List;

public class BytecodeReader {
    private final ByteReader reader;
    private final List<Object> constantPool;
    private final short entrypoint;
    private final int functionCount;
    private int readFunctions;
    
    public BytecodeReader(ByteReader reader) {
        this.reader = reader;
        var magic = reader.u32();
        if(magic != BytecodeConstants.MAGIC) {
            throw new IllegalArgumentException("Invalid magic 0x" + Integer.toHexString(magic));
        }
        int cpEntries = reader.u16() & 0xFFFF;
        constantPool = new ArrayList<>(cpEntries);
        for(var i = 0; i < cpEntries; i++) {
            var tag = reader.u8();
            switch(tag) {
                case BytecodeConstants.CONSTANT_LONG -> constantPool.add(reader.u64());
                case BytecodeConstants.CONSTANT_DOUBLE -> constantPool.add(reader.f64());
                case BytecodeConstants.CONSTANT_STRING -> constantPool.add(reader.utf());
                default -> throw new IllegalArgumentException("Invalid constant pool tag 0x" + Integer.toHexString(tag & 0xFF));
            }
        }
        entrypoint = reader.u16();
        functionCount = reader.u16() & 0xFFFF;
    }
    
    public BytecodeReader(byte[] data) {
        this(new ByteReader(data));
    }
    
    public ByteReader reader() {
        return reader;
    }
    
    public int functionCount() {
        return functionCount;
    }
    
    private <T> T constant(Class<T> type, short pos) {
        var c = constantPool.get(pos & 0xFFFF);
        if(type.isInstance(c)) {
            return type.cast(c);
        } else {
            throw new IllegalArgumentException("Constant " + (pos & 0xFFFF) + " is not a " + type.getSimpleName());
        }
    }
    
    public String constantString(short pos) {
        return constant(String.class, pos);
    }
    
    public long constantLong(short pos) {
        return constant(Long.class, pos);
    }
    
    public double constantDouble(short pos) {
        return constant(Double.class, pos);
    }
    
    public short entrypoint() {
        return entrypoint;
    }
    
    public FunctionReader readFunction() {
        if(readFunctions >= functionCount) {
            return null;
        }
        readFunctions++;
        short tag = reader.u16();
        if(tag != BytecodeConstants.FUNCTION_TAG) {
            throw new IllegalArgumentException("Invalid tag!");
        }
        int size = reader.u32();
        return new FunctionReader(this, reader.subReader(size));
    }
}
