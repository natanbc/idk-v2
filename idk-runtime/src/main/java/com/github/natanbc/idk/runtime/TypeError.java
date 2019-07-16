package com.github.natanbc.idk.runtime;

public class TypeError extends ThrownError {
    public TypeError(String message) {
        super(StringValue.of(message));
    }
}
