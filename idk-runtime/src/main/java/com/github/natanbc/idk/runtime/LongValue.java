package com.github.natanbc.idk.runtime;

public class LongValue implements Value {
    private final long value;
    
    private LongValue(long value) {
        this.value = value;
    }
    
    public static LongValue of(long value) {
        if(value >= 0 && value < 256) {
            return LongCache.CACHE[(int)value];
        }
        return new LongValue(value);
    }
    
    public long getValue() {
        return value;
    }
    
    @Override
    public String type() {
        return "long";
    }
    
    @Override
    public StringValue tostring() {
        return StringValue.of(String.valueOf(value));
    }
    
    @Override
    public boolean isLong() {
        return true;
    }
    
    @Override
    public LongValue asLong() {
        return this;
    }
    
    @Override
    public Value add(Value other) {
        if(other.isString()) {
            return StringValue.of(value + other.asString().getValue());
        } else if(other.isLong()) {
            return of(value + other.asLong().getValue());
        } else if(other.isDouble()) {
            return DoubleValue.of(value + other.asDouble().getValue());
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value sub(Value other) {
        if(other.isLong()) {
            return of(value - other.asLong().getValue());
        } else if(other.isDouble()) {
            return DoubleValue.of(value - other.asDouble().getValue());
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value mul(Value other) {
        if(other.isLong()) {
            return of(value * other.asLong().getValue());
        } else if(other.isDouble()) {
            return DoubleValue.of(value * other.asDouble().getValue());
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value div(Value other) {
        if(other.isLong()) {
            return of(value / other.asLong().getValue());
        } else if(other.isDouble()) {
            return DoubleValue.of(value / other.asDouble().getValue());
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value mod(Value other) {
        if(other.isLong()) {
            return of(value % other.asLong().getValue());
        } else if(other.isDouble()) {
            return DoubleValue.of(value % other.asDouble().getValue());
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value pow(Value other) {
        if(other.isLong()) {
            return of((long)Math.pow(value, other.asLong().getValue()));
        } else if(other.isDouble()) {
            return DoubleValue.of(Math.pow(value, other.asDouble().getValue()));
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value neg() {
        return of(-value);
    }
    
    @Override
    public Value eq(Value other) {
        return BooleanValue.of(
                (other.isLong() && other.asLong().value == value) || (other.isDouble() && other.asDouble().getValue() == value)
        );
    }
    
    @Override
    public Value greater(Value other) {
        if(other.isLong()) {
            return BooleanValue.of(value > other.asLong().getValue());
        } else if(other.isDouble()) {
            return BooleanValue.of(value > other.asDouble().getValue());
        } else {
            throw Helpers.invalidComparison(this, other);
        }
    }
    
    @Override
    public Value greaterEq(Value other) {
        if(other.isLong()) {
            return BooleanValue.of(value >= other.asLong().getValue());
        } else if(other.isDouble()) {
            return BooleanValue.of(value >= other.asDouble().getValue());
        } else {
            throw Helpers.invalidComparison(this, other);
        }
    }
    
    @Override
    public Value smaller(Value other) {
        if(other.isLong()) {
            return BooleanValue.of(value < other.asLong().getValue());
        } else if(other.isDouble()) {
            return BooleanValue.of(value < other.asDouble().getValue());
        } else {
            throw Helpers.invalidComparison(this, other);
        }
    }
    
    @Override
    public Value smallerEq(Value other) {
        if(other.isLong()) {
            return BooleanValue.of(value <= other.asLong().getValue());
        } else if(other.isDouble()) {
            return BooleanValue.of(value <= other.asDouble().getValue());
        } else {
            throw Helpers.invalidComparison(this, other);
        }
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof LongValue && ((LongValue) obj).value == value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
    private static class LongCache {
        static final LongValue[] CACHE;
        
        static {
            CACHE = new LongValue[256];
            for(int i = 0; i < 256; i++) {
                CACHE[i] = new LongValue(i);
            }
        }
    }
}
