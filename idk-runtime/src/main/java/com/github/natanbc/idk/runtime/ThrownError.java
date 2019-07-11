package com.github.natanbc.idk.runtime;

public class ThrownError extends ExecutionError {
    private final Value value;
    
    public ThrownError(Value value) {
        super(value.tostring().getValue());
        this.value = value;
    }
    
    public Value getValue() {
        return value;
    }
}
