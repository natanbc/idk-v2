package com.github.natanbc.idk.bytecode;

import com.github.natanbc.idk.bytecode.util.ByteReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FunctionReader {
    private final BytecodeReader owner;
    private final ByteReader reader;
    
    private final short id;
    private final String name;
    private final int argumentCount;
    private final int localsCount;
    private final boolean varargs;
    private final List<String> annotations;
    
    FunctionReader(BytecodeReader owner, ByteReader reader) {
        this.owner = owner;
        this.reader = reader;
        
        this.id = reader.u16();
        if(reader.bool()) {
            this.name = owner.constantString(reader.u16());
        } else {
            this.name = null;
        }
        this.argumentCount = reader.u16() & 0xFFFF;
        this.localsCount = reader.u16() & 0xFFFF;
        this.varargs = reader.bool();
        var annotationCount = reader.u16() & 0xFFFF;
        var annotations = new ArrayList<String>(annotationCount);
        for(var i = 0; i < annotationCount; i++) {
            annotations.add(owner.constantString(reader.u16()));
        }
        this.annotations = Collections.unmodifiableList(annotations);
    }
    
    public ByteReader reader() {
        return reader;
    }
    
    public BytecodeReader owner() {
        return owner;
    }
    
    public short id() {
        return id;
    }
    
    public String name() {
        return name;
    }
    
    public int argumentCount() {
        return argumentCount;
    }
    
    public int localsCount() {
        return localsCount;
    }
    
    public boolean varargs() {
        return varargs;
    }
    
    public List<String> annotations() {
        return annotations;
    }
    
    public Opcode nextOpcode() {
        if(!reader.canRead()) return null;
        return Opcode.fromValue(reader.u8());
    }
    
    public Operation nextInstruction() {
        if(!reader.canRead()) return null;
        var op = nextOpcode();
        var args = new Object[op.argumentTypes.size()];
        for(var i = 0; i < args.length; i++) {
            args[i] = op.argumentTypes.get(i).tryRead(this);
        }
        return new Operation(op, args);
    }
    
    public static class Operation {
        private final Opcode opcode;
        private final Object[] args;
    
        Operation(Opcode opcode, Object[] args) {
            this.opcode = opcode;
            this.args = args;
        }
    
        public Opcode opcode() {
            return opcode;
        }
    
        public Object[] args() {
            return args;
        }
    
        @Override
        public String toString() {
            return opcode.name() + (args.length > 0 ? " " + Arrays.toString(args) : "");
        }
    }
}
