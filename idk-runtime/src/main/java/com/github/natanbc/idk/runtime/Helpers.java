package com.github.natanbc.idk.runtime;

class Helpers {
    static RuntimeException typeError(String message) {
        throw new TypeError(message);
    }
    
    static RuntimeException typeError(Value self, String action, Value other) {
        if(other == null) {
            throw typeError("Attempt to " + action + " " + self.type());
        } else {
            throw typeError("Attempt to " + action + " " + self.type() + " and " + other.type());
        }
    }
    
    static RuntimeException invalidArithmetic(Value self, Value other) {
        throw typeError(self, "perform arithmetic between", other);
    }
    
    static RuntimeException invalidComparison(Value self, Value other) {
        throw typeError(self, "compare", other);
    }
}
