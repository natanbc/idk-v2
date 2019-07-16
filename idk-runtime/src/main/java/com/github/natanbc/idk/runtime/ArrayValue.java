package com.github.natanbc.idk.runtime;

import com.github.natanbc.idk.runtime.internal.SparseArray;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ArrayValue implements Value {
    private final SparseArray<Value> array = new SparseArray<>();
    
    //used for the key set on ObjectValue
    ArrayValue(Collection<? extends Value> values) {
        var i = 0;
        for(var v : values) {
            array.put(i++, Objects.requireNonNull(v));
        }
    }
    
    public ArrayValue(List<? extends Value> values) {
        this((Collection<? extends Value>)values);
    }
    
    public ArrayValue(Value[] array) {
        for(var i = 0; i < array.length; i++) {
            this.array.put(i, Objects.requireNonNull(array[i]));
        }
    }
    
    public ArrayValue() {}
    
    public Value rawGet(int index) {
        if(index < 0) {
            throw new RangeError("Negative index");
        }
        return array.get(index, NilValue.instance());
    }
    
    public Value rawSet(int index, Value value) {
        if(index < 0) {
            throw new RangeError("Negative index");
        }
        array.put(index, value);
        return value;
    }
    
    @Override
    public String type() {
        return "array";
    }
    
    @Override
    public StringValue tostring() {
        return StringValue.of("array 0x" + Integer.toHexString(System.identityHashCode(this)));
    }
    
    @Override
    public boolean isArray() {
        return true;
    }
    
    @Override
    public ArrayValue asArray() {
        return this;
    }
    
    @Override
    public Value get(Value key) {
        return array.get(index(key), NilValue.instance());
    }
    
    @Override
    public Value set(Value key, Value value) {
        array.put(index(key), value);
        return value;
    }
    
    @Override
    public long size() {
        return array.size();
    }
    
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }
    
    @Override
    public String toString() {
        return "Array(" + array + ")";
    }
    
    private int index(Value key) {
        if(!key.isLong()) {
            throw Helpers.typeError("Attempt to write to non int key " + key);
        }
        var k = key.asLong().getValue();
        if(k < 0) {
            throw new RangeError("Negative index");
        }
        if(k > Integer.MAX_VALUE) {
            throw new RangeError("Index too large");
        }
        return (int)k;
    }
}
