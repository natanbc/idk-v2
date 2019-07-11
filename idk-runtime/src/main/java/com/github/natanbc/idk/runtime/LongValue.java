package com.github.natanbc.idk.runtime;

public class LongValue implements Value {
    private final long value;
    
    public LongValue(long value) {
        this.value = value;
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
        return new StringValue(String.valueOf(value));
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
            return new StringValue(value + other.asString().getValue());
        } else if(other.isLong()) {
            return new LongValue(value + other.asLong().getValue());
        } else if(other.isDouble()) {
            return new DoubleValue(value + other.asDouble().getValue());
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value sub(Value other) {
        if(other.isLong()) {
            return new LongValue(value - other.asLong().getValue());
        } else if(other.isDouble()) {
            return new DoubleValue(value - other.asDouble().getValue());
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value mul(Value other) {
        if(other.isLong()) {
            return new LongValue(value * other.asLong().getValue());
        } else if(other.isDouble()) {
            return new DoubleValue(value * other.asDouble().getValue());
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value div(Value other) {
        if(other.isLong()) {
            return new LongValue(value / other.asLong().getValue());
        } else if(other.isDouble()) {
            return new DoubleValue(value / other.asDouble().getValue());
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value mod(Value other) {
        if(other.isLong()) {
            return new LongValue(value / other.asLong().getValue());
        } else if(other.isDouble()) {
            return new DoubleValue(value / other.asDouble().getValue());
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value pow(Value other) {
        if(other.isLong()) {
            return new LongValue((long)Math.pow(value, other.asLong().getValue()));
        } else if(other.isDouble()) {
            return new DoubleValue(Math.pow(value, other.asDouble().getValue()));
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value neg() {
        return new LongValue(-value);
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
        return "Long(" + value + ")";
    }
}
