package com.github.natanbc.idk.runtime.internal;

import com.github.natanbc.idk.runtime.Value;

public class ReturnException extends RuntimeException {
    private final Value value;
    
    public ReturnException(Value value) {
        super(null, null, false, false);
        this.value = value;
    }
    
    public Value getValue() {
        return value;
    }
}
