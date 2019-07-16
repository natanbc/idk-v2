package com.github.natanbc.idk.interpreter;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;
import com.github.natanbc.idk.runtime.ExecutionContext;
import com.github.natanbc.idk.runtime.Value;

import java.util.Optional;

public interface InterpreterHooks {
    default Optional<Value> replaceExecution(IrVisitor<Value> interpreter, ExecutionContext context, IrNode node) {
        return Optional.empty();
    }
    
    default Value filterResult(IrVisitor<Value> interpreter, ExecutionContext context, IrNode node, Value result) {
        return result;
    }
}
