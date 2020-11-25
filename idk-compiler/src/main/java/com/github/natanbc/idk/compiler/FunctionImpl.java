package com.github.natanbc.idk.compiler;

import com.github.natanbc.idk.runtime.ExecutionContext;
import com.github.natanbc.idk.runtime.Function;
import com.github.natanbc.idk.runtime.StringValue;
import com.github.natanbc.idk.runtime.Value;
import com.github.natanbc.idk.runtime.internal.FunctionState;
import com.github.natanbc.idk.runtime.internal.ReturnException;

import java.lang.invoke.MethodHandle;
import java.util.List;

class FunctionImpl extends Function {
    private final MethodHandle target;
    private final FunctionState state;
    
    FunctionImpl(String name, List<StringValue> annotationList, MethodHandle target, FunctionState state) {
        super(name, annotationList);
        this.target = target;
        this.state = state;
    }
    
    @Override
    public Value call(Value[] args) {
        try {
            return (Value)target.invoke(state, args);
        } catch(ReturnException e) {
            return e.getValue();
        } catch(Throwable t) {
            throw throwUnchecked(t);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> Error throwUnchecked(Throwable exception) throws T {
        throw (T)exception;
    }
}
