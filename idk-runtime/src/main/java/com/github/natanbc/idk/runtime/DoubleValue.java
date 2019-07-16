package com.github.natanbc.idk.runtime;

public class DoubleValue implements Value {
    private final double value;
    
    private DoubleValue(double value) {
        this.value = value;
    }
    
    public static DoubleValue of(double value) {
        return new DoubleValue(value);
    }
    
    public double getValue() {
        return value;
    }
    
    @Override
    public String type() {
        return "double";
    }
    
    @Override
    public StringValue tostring() {
        return StringValue.of(String.valueOf(value));
    }
    
    @Override
    public boolean isDouble() {
        return true;
    }
    
    @Override
    public DoubleValue asDouble() {
        return this;
    }
    
    @Override
    public Value add(Value other) {
        if(other.isString()) {
            return StringValue.of(value + other.asString().getValue());
        } else if(other.isLong()) {
            return of(value + other.asLong().getValue());
        } else if(other.isDouble()) {
            return of(value + other.asDouble().getValue());
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value sub(Value other) {
        if(other.isLong()) {
            return of(value - other.asLong().getValue());
        } else if(other.isDouble()) {
            return of(value - other.asDouble().getValue());
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value mul(Value other) {
        if(other.isLong()) {
            return of(value * other.asLong().getValue());
        } else if(other.isDouble()) {
            return of(value * other.asDouble().getValue());
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value div(Value other) {
        if(other.isLong()) {
            return of(value / other.asLong().getValue());
        } else if(other.isDouble()) {
            return of(value / other.asDouble().getValue());
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value mod(Value other) {
        if(other.isLong()) {
            return of(value % other.asLong().getValue());
        } else if(other.isDouble()) {
            return of(value % other.asDouble().getValue());
        } else {
            throw Helpers.invalidArithmetic(this, other);
        }
    }
    
    @Override
    public Value pow(Value other) {
        if(other.isLong()) {
            return of(Math.pow(value, other.asLong().getValue()));
        } else if(other.isDouble()) {
            return of(Math.pow(value, other.asDouble().getValue()));
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
                (other.isDouble() && other.asDouble().value == value) || (other.isLong() && other.asLong().getValue() == value)
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
        return Double.hashCode(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof DoubleValue && ((DoubleValue) obj).value == value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
