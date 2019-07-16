package com.github.natanbc.idk.runtime;

import java.util.Collections;
import java.util.List;

public abstract class Function implements Value {
    private final String name;
    private final List<StringValue> annotationList;
    
    protected Function(String name, List<StringValue> annotationList) {
        this.name = name;
        this.annotationList = annotationList == null ? Collections.emptyList() : annotationList;
    }
    
    protected Function(List<StringValue> annotationList) {
        this(null, annotationList);
    }
    
    protected Function(String name) {
        this(name, null);
    }
    
    protected Function() {
        this(null, null);
    }
    
    public abstract Value call(ExecutionContext context, Value[] args);
    
    @Override
    public String type() {
        return "function";
    }
    
    @Override
    public StringValue tostring() {
        return StringValue.of(toString());
    }
    
    @Override
    public boolean isFunction() {
        return true;
    }
    
    @Override
    public Function asFunction() {
        return this;
    }
    
    @Override
    public Value get(Value key) {
        if(key.isString() && key.asString().getValue().equals("annotations")) {
            return new ArrayValue(annotationList);
        }
        return NilValue.instance();
    }
    
    @Override
    public Value set(Value key, Value value) {
        throw new ThrownError(StringValue.of("Functions are immutable"));
    }
    
    @Override
    public ArrayValue keys() {
        return new ArrayValue(Collections.singletonList(StringValue.of("annotations")));
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
        return "function " + (name == null ? "0x" + Integer.toHexString(hashCode()) : name);
    }
}
