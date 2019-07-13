package com.github.natanbc.idk.runtime;

public interface Value {
    String type();
    
    default StringValue tostring() {
        return new StringValue(toString());
    }
    
    default boolean isBoolean() {
        return false;
    }
    
    default BooleanValue asBoolean() {
        throw Helpers.typeError("not a boolean");
    }
    
    default boolean isDouble() {
        return false;
    }
    
    default DoubleValue asDouble() {
        throw Helpers.typeError("not a double");
    }
    
    default boolean isLong() {
        return false;
    }
    
    default LongValue asLong() {
        throw Helpers.typeError("not a long");
    }
    
    default boolean isNil() {
        return false;
    }
    
    default NilValue asNil() {
        throw Helpers.typeError("not a nil");
    }
    
    default boolean isString() {
        return false;
    }
    
    default StringValue asString() {
        throw Helpers.typeError("not a string");
    }
    
    default boolean isFunction() {
        return false;
    }
    
    default Function asFunction() {
        throw Helpers.typeError("not a function");
    }
    
    default boolean isObject() {
        return false;
    }
    
    default ObjectValue asObject() {
        throw Helpers.typeError("not an object");
    }
    
    default boolean isArray() {
        return false;
    }
    
    default ArrayValue asArray() {
        throw Helpers.typeError("not an array");
    }
    
    default boolean isRange() {
        return false;
    }
    
    default RangeValue asRange() {
        throw Helpers.typeError("not a range");
    }
    
    default Value add(Value other) {
        throw Helpers.invalidArithmetic(this, other);
    }
    
    default Value sub(Value other) {
        throw Helpers.invalidArithmetic(this, other);
    } 
    
    default Value mul(Value other) {
        throw Helpers.invalidArithmetic(this, other);
    }
    
    default Value div(Value other) {
        throw Helpers.invalidArithmetic(this, other);
    }
    
    default Value mod(Value other) {
        throw Helpers.invalidArithmetic(this, other);
    }
    
    default Value pow(Value other) {
        throw Helpers.invalidArithmetic(this, other);
    }
    
    default Value neg() {
        throw Helpers.typeError(this, "perform arithmetic", null);
    }
    
    default Value negate() {
        throw Helpers.typeError("negate");
    }
    
    default Value eq(Value other) {
        return BooleanValue.of(equals(other));
    }
    
    default Value neq(Value other) {
        return eq(other).negate();
    }
    
    default BooleanValue greater(Value other) {
        throw Helpers.typeError(this, "compare", other);
    }
    
    default BooleanValue greaterEq(Value other) {
        throw Helpers.typeError(this, "compare", other);
    }
    
    default BooleanValue smaller(Value other) {
        throw Helpers.typeError(this, "compare", other);
    }
    
    default BooleanValue smallerEq(Value other) {
        throw Helpers.typeError(this, "compare", other);
    }
    
    default Value get(Value key) {
        throw Helpers.typeError(this, "index", null);
    }
    
    default Value set(Value key, Value value) {
        throw Helpers.typeError(this, "index", null);
    }
    
    default long size() {
        throw Helpers.typeError(this, "get length of", null);
    }
    
    default ArrayValue keys() {
        throw Helpers.typeError(this, "get keys of", null);
    }
}
