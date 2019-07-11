package com.github.natanbc.idk.interpreter;

import com.github.natanbc.idk.runtime.Value;

class ReturnException extends RuntimeException {
    final Value ret;

    ReturnException(Value ret) {
        super(null, null, false, false);
        this.ret = ret;
    }
}
