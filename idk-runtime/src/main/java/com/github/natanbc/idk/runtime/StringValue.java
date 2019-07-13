package com.github.natanbc.idk.runtime;

public class StringValue implements Value {
    private final String value;
    
    public StringValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String type() {
        return "string";
    }
    
    @Override
    public StringValue tostring() {
        return this;
    }
    
    @Override
    public boolean isString() {
        return true;
    }
    
    @Override
    public StringValue asString() {
        return this;
    }
    
    @Override
    public Value add(Value other) {
        return new StringValue(value + other.tostring().asString().getValue());
    }
    
    @Override
    public Value mul(Value other) {
        var v = other.asLong().getValue();
        if(v < 0 || v > Integer.MAX_VALUE) {
            throw new RangeError("Value out of range");
        }
        return new StringValue(value.repeat((int)v));
    }
    
    @Override
    public BooleanValue greater(Value other) {
        if(other.isString()) {
            return BooleanValue.of(value.compareTo(other.asString().getValue()) > 0);
        } else {
            throw Helpers.invalidComparison(this, other);
        }
    }
    
    @Override
    public BooleanValue greaterEq(Value other) {
        if(other.isString()) {
            return BooleanValue.of(value.compareTo(other.asString().getValue()) >= 0);
        } else {
            throw Helpers.invalidComparison(this, other);
        }
    }
    
    @Override
    public BooleanValue smaller(Value other) {
        if(other.isString()) {
            return BooleanValue.of(value.compareTo(other.asString().getValue()) < 0);
        } else {
            throw Helpers.invalidComparison(this, other);
        }
    }
    
    @Override
    public BooleanValue smallerEq(Value other) {
        if(other.isString()) {
            return BooleanValue.of(value.compareTo(other.asString().getValue()) <= 0);
        } else {
            throw Helpers.invalidComparison(this, other);
        }
    }
    
    @Override
    public Value get(Value key) {
        if(key.isLong()) {
            var idx = key.asLong().getValue();
            if(idx < 0) {
                throw new RangeError("Negative index");
            } else if(idx >= value.length()) {
                return NilValue.instance();
            } else {
                return new StringValue(String.valueOf(value.charAt((int)idx)));
            }
        } else {
            throw Helpers.typeError(this, "access non integer field of", null);
        }
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof StringValue && ((StringValue) obj).value.equals(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}
