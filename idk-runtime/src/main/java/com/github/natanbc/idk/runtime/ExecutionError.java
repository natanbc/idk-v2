package com.github.natanbc.idk.runtime;

public abstract class ExecutionError extends RuntimeException {
    public ExecutionError(String message) {
        super(message);
    }
}
