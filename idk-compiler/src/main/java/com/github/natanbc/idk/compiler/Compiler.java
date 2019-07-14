package com.github.natanbc.idk.compiler;

import com.github.natanbc.idk.ir.IrVisitor;
import com.github.natanbc.idk.ir.misc.*;
import com.github.natanbc.idk.ir.operation.IrBinaryOperation;
import com.github.natanbc.idk.ir.operation.IrUnaryOperation;
import com.github.natanbc.idk.ir.value.*;
import com.github.natanbc.idk.ir.variable.*;

import java.lang.invoke.MethodHandle;

public class Compiler implements IrVisitor<MethodHandle> {
    private static final Compiler INSTANCE = new Compiler();
    
    public static Compiler instance() {
        return INSTANCE;
    }
    
    @Override
    public MethodHandle visitRoot(IrRoot node) {
        return node.accept(ActualCompiler.instance());
    }
    
    @Override
    public MethodHandle visitBoolean(IrBoolean node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitDouble(IrDouble node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitLong(IrLong node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitNil(IrNil node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitString(IrString node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitArrayLiteral(IrArrayLiteral node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitObjectLiteral(IrObjectLiteral node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitUnaryOperation(IrUnaryOperation node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitBinaryOperation(IrBinaryOperation node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitRange(IrRange node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitLocal(IrLocal node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitUpvalue(IrUpvalue node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitGlobal(IrGlobal node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitAssign(IrAssign node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitMember(IrMember node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitBody(IrBody node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitCall(IrCall node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitFunction(IrFunction node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitIf(IrIf node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitWhile(IrWhile node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitFor(IrFor node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitReturn(IrReturn node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
    
    @Override
    public MethodHandle visitThrow(IrThrow node) {
        throw new UnsupportedOperationException("Only root nodes can be compiled");
    }
}
