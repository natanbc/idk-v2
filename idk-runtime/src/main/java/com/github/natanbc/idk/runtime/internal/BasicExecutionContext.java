package com.github.natanbc.idk.runtime.internal;

import com.github.natanbc.idk.runtime.ExecutionContext;
import com.github.natanbc.idk.runtime.NilValue;
import com.github.natanbc.idk.runtime.Value;

import java.util.Map;

public class BasicExecutionContext implements ExecutionContext {
    protected final Map<String, Value> globals;
    
    public BasicExecutionContext(Map<String, Value> globals) {
        this.globals = globals;
    }
    
    @Override
    public Value getGlobal(String name) {
        return globals.getOrDefault(name, NilValue.instance());
    }
    
    @Override
    public Value setGlobal(String name, Value value) {
        globals.put(name, value);
        return value;
    }
    
    @Override
    public Map<String, Value> getGlobals() {
        return globals;
    }
}
