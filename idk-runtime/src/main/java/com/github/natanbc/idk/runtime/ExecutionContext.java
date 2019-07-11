package com.github.natanbc.idk.runtime;

import com.github.natanbc.idk.runtime.internal.BasicExecutionContext;

import java.util.HashMap;
import java.util.Map;

public interface ExecutionContext {
    Value getGlobal(String name);
    
    Value setGlobal(String name, Value value);
    
    Map<String, Value> getGlobals();
    
    static ExecutionContext newEmptyContext() {
        return newFromMap(new HashMap<>());
    }
    
    static ExecutionContext newFromMap(Map<String, Value> map) {
        return new BasicExecutionContext(map);
    }
}
