package com.github.natanbc.idk.runtime;

import java.util.HashMap;
import java.util.Map;

public class ObjectValue implements Value {
    private Map<Value, Value> map = new HashMap<>();
    private boolean cow;
    
    public ObjectValue(Map<Value, Value> map) {
        this.map.putAll(map);
    }
    
    public ObjectValue() {}
    
    public ObjectValue copyOnWrite() {
        cow = true;
        return this;
    }
    
    public Map<Value, Value> getMap() {
        return map;
    }
    
    @Override
    public String type() {
        return "object";
    }
    
    @Override
    public StringValue tostring() {
        return StringValue.of("object 0x" + Integer.toHexString(System.identityHashCode(this)));
    }
    
    @Override
    public boolean isObject() {
        return true;
    }
    
    @Override
    public ObjectValue asObject() {
        return this;
    }
    
    @Override
    public Value get(Value key) {
        return map.getOrDefault(key, NilValue.instance());
    }
    
    @Override
    public Value set(Value key, Value value) {
        if(cow) {
            map = new HashMap<>(map);
            cow = false;
        }
        map.put(key, value);
        return value;
    }
    
    @Override
    public long size() {
        return map.size();
    }
    
    @Override
    public ArrayValue keys() {
        return new ArrayValue(map.keySet());
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
        return "Object(" + map + ")";
    }
}
