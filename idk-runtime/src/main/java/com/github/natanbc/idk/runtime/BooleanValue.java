package com.github.natanbc.idk.runtime;

public class BooleanValue implements Value {
    private static final BooleanValue TRUE = new BooleanValue(true);
    private static final BooleanValue FALSE = new BooleanValue(false);
    
    private final boolean value;
    private final StringValue string;
    
    private BooleanValue(boolean value) {
        this.value = value;
        this.string = new StringValue(String.valueOf(value));
    }
    
    public static BooleanValue of(boolean value) {
        return value ? TRUE : FALSE;
    }
    
    public boolean getValue() {
        return value;
    }
    
    @Override
    public String type() {
        return "boolean";
    }
    
    @Override
    public StringValue tostring() {
        return string;
    }
    
    @Override
    public boolean isBoolean() {
        return true;
    }
    
    @Override
    public BooleanValue asBoolean() {
        return this;
    }
    
    @Override
    public Value add(Value other) {
        if(other.isString()) {
            return new StringValue(value + other.asString().getValue());
        }
        throw Helpers.invalidArithmetic(this, other);
    }
    
    @Override
    public Value eq(Value other) {
        return of(other == this);
    }
    
    @Override
    public Value neq(Value other) {
        return of(other != this);
    }
    
    @Override
    public Value negate() {
        return of(!value);
    }
    
    @Override
    public int hashCode() {
        return value ? 1 : 0;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }
    
    @Override
    public String toString() {
        return "Boolean(" + value + ")";
    }
}
