package com.github.natanbc.idk.bytecode;

import com.github.natanbc.idk.bytecode.util.ByteWriter;
import com.github.natanbc.idk.bytecode.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class BytecodeWriter {
    private final ByteWriter writer = new ByteWriter();
    private final List<Object> constantPool = new ArrayList<>();
    private final BitSet writtenFunctions = new BitSet();
    private int nextFunctionId = 0;
    private int entrypoint;
    
    public ByteWriter writer() {
        return writer;
    }
    
    public FunctionWriter createFunction(String name, int argumentCount, int localsCount,
                                         boolean varargs, List<String> annotations) {
        Utils.validateU16(argumentCount, "Argument count out of bounds");
        Utils.validateU16(localsCount, "Locals count out of bounds");
        Utils.validateU16(annotations.size(), "Too many annotations");
        int id = nextFunctionId;
        Utils.validateInternalU16(id, "Too many functions");
        nextFunctionId++;
        /* expand bitset */
        writtenFunctions.set(id);
        writtenFunctions.clear(id);
        return new FunctionWriter(this, (short)id, name, argumentCount, localsCount, varargs, annotations);
    }
    
    public void setEntrypoint(short id) {
        if(!writtenFunctions.get(id & 0xFFFF)) {
            throw new IllegalStateException("No function with id " + id + " written");
        }
        entrypoint = id & 0xFFFF;
    }
    
    public short constant(String s) {
        Utils.validateConstantString(s);
        return checkConstant(s);
    }
    public short constant(long l) { return checkConstant(l); }
    public short constant(double d) { return checkConstant(d); }
    
    private short checkConstant(Object o) {
        int idx = constantPool.indexOf(o);
        if(idx == -1) {
            idx = constantPool.size();
            Utils.validateInternalU16(idx, "Too many constants");
            constantPool.add(o);
        }
        return (short)idx;
    }
    
    void writeFunction(short id, ByteWriter writer) {
        int idx = id & 0xFFFF;
        if(writtenFunctions.get(idx)) {
            throw new IllegalStateException("Function with id " + idx + " already written");
        }
        writtenFunctions.set(idx);
        this.writer.u16(BytecodeConstants.FUNCTION_TAG);
        this.writer.u32(writer.size());
        try {
            writer.write(this.writer.sink());
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public byte[] write() {
        try {
            var out = new ByteArrayOutputStream();
            write(out);
            return out.toByteArray();
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public void write(OutputStream out) throws IOException {
        try(var dos = new DataOutputStream(out)) {
            dos.writeInt(BytecodeConstants.MAGIC);
            dos.writeShort(constantPool.size());
            for(Object obj : constantPool) {
                if(obj instanceof Long) {
                    dos.writeByte(BytecodeConstants.CONSTANT_LONG);
                    dos.writeLong((Long)obj);
                } else if(obj instanceof Double) {
                    dos.writeByte(BytecodeConstants.CONSTANT_DOUBLE);
                    dos.writeDouble((Double)obj);
                } else if(obj instanceof String) {
                    dos.writeByte(BytecodeConstants.CONSTANT_STRING);
                    dos.writeUTF((String)obj);
                } else {
                    throw new IllegalStateException(
                            "Unable to serialize constant pool value "
                                    + (obj == null ? "null" : "of type " + obj.getClass().getName())
                    );
                }
            }
            dos.writeShort(entrypoint);
            dos.writeShort(nextFunctionId);
            writer.write(dos);
        }
    }
}
