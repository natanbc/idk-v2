package com.github.natanbc.idk.interpreter;

import com.github.natanbc.idk.ir.IrVisitor;
import com.github.natanbc.idk.ir.misc.*;
import com.github.natanbc.idk.ir.operation.IrBinaryOperation;
import com.github.natanbc.idk.ir.operation.IrUnaryOperation;
import com.github.natanbc.idk.ir.value.*;
import com.github.natanbc.idk.ir.variable.*;
import com.github.natanbc.idk.runtime.ExecutionContext;
import com.github.natanbc.idk.runtime.internal.FunctionState;
import com.github.natanbc.idk.runtime.Value;

import java.util.Map;

public class Interpreter implements IrVisitor<Value> {
    private final Map<String, Value> globals;
    
    public Interpreter(ExecutionContext context) {
        this(context.getGlobals());
    }
    
    public Interpreter(Map<String, Value> globals) {
        this.globals = globals;
    }
    
    @Override
    public Value visitRoot(IrRoot node) {
        try {
            var state = new FunctionState(globals, node.getLocalsCount());
            return node.getBody().accept(new ActualInterpreter(state));
        } catch(ReturnException e) {
            return e.ret;
        }
    }
    
    @Override
    public Value visitBoolean(IrBoolean node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitDouble(IrDouble node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitLong(IrLong node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitNil(IrNil node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitString(IrString node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitArrayLiteral(IrArrayLiteral node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitObjectLiteral(IrObjectLiteral node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitUnaryOperation(IrUnaryOperation node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitBinaryOperation(IrBinaryOperation node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitLocal(IrLocal node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitUpvalue(IrUpvalue node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitGlobal(IrGlobal node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitAssign(IrAssign node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitMember(IrMember node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitBody(IrBody node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitCall(IrCall node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitFunction(IrFunction node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitIf(IrIf node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitWhile(IrWhile node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitReturn(IrReturn node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
    @Override
    public Value visitThrow(IrThrow node) {
        throw new UnsupportedOperationException("Only root nodes can be directly executed");
    }
    
//    @Override
//    public Value visitUnpack(IrUnpack node) {
//        throw new UnsupportedOperationException("Only root nodes can be directly executed");
//    }
}
