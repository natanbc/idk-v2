package com.github.natanbc.idk.runtime;

import com.github.natanbc.idk.runtime.internal.SparseArray;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ArrayValue implements Value, Iterable<Value> {
    private SparseArray<Value> array;
    private boolean cow;
    
    public ArrayValue(Collection<? extends Value> values) {
        this.array = new SparseArray<>(values.size());
        var i = 0;
        for(var v : values) {
            array.put(i++, Objects.requireNonNull(v));
        }
    }
    
    public ArrayValue(List<? extends Value> values) {
        this((Collection<? extends Value>)values);
    }
    
    public ArrayValue(Value[] array) {
        this.array = new SparseArray<>(array.length);
        for(var i = 0; i < array.length; i++) {
            this.array.put(i, Objects.requireNonNull(array[i]));
        }
    }
    
    public ArrayValue() {
        this.array = new SparseArray<>();
    }
    
    public ArrayValue copyOnWrite() {
        cow = true;
        return this;
    }
    
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
        checkWrite();
        array.put(index, value);
        return value;
    }
    
    @Override
    public Iterator<Value> iterator() {
        return array.iterator();
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
        var i = index(key);
        checkWrite();
        array.put(i, value);
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
    
    private void checkWrite() {
        if(cow) {
            array = array.copy();
            cow = false;
        }
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
