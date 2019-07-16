package com.github.natanbc.idk.runtime;

public class RangeError extends ThrownError {
    public RangeError(String message) {
        super(StringValue.of(message));
    }
}
