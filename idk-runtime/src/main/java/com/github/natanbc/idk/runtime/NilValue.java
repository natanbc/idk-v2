package com.github.natanbc.idk.runtime;

public class NilValue implements Value {
    private static final NilValue INSTANCE = new NilValue();
    
    private final StringValue string = new StringValue("nil");
    
    private NilValue() {}
    
    public static NilValue instance() {
        return INSTANCE;
    }
    
    @Override
    public String type() {
        return "nil";
    }
    
    @Override
    public StringValue tostring() {
        return string;
    }
    
    @Override
    public boolean isNil() {
        return true;
    }
    
    @Override
    public NilValue asNil() {
        return this;
    }
    
    @Override
    public Value add(Value other) {
        if(other.isString()) {
            return new StringValue("nil" + other.asString().getValue());
        }
        throw Helpers.invalidArithmetic(this, other);
    }
    
    @Override
    public Value eq(Value other) {
        return BooleanValue.of(other == this);
    }
    
    @Override
    public Value neq(Value other) {
        return BooleanValue.of(other != this);
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }
    
    @Override
    public String toString() {
        return "Nil";
    }
}
