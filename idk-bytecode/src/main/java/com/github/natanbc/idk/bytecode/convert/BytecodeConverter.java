package com.github.natanbc.idk.bytecode.convert;

import com.github.natanbc.idk.bytecode.BytecodeWriter;
import com.github.natanbc.idk.ir.IrVisitor;
import com.github.natanbc.idk.ir.misc.IrBody;
import com.github.natanbc.idk.ir.misc.IrCall;
import com.github.natanbc.idk.ir.misc.IrFor;
import com.github.natanbc.idk.ir.misc.IrFunction;
import com.github.natanbc.idk.ir.misc.IrIf;
import com.github.natanbc.idk.ir.misc.IrRange;
import com.github.natanbc.idk.ir.misc.IrReturn;
import com.github.natanbc.idk.ir.misc.IrRoot;
import com.github.natanbc.idk.ir.misc.IrThrow;
import com.github.natanbc.idk.ir.misc.IrWhile;
import com.github.natanbc.idk.ir.operation.IrBinaryOperation;
import com.github.natanbc.idk.ir.operation.IrUnaryOperation;
import com.github.natanbc.idk.ir.value.IrArrayLiteral;
import com.github.natanbc.idk.ir.value.IrBoolean;
import com.github.natanbc.idk.ir.value.IrDouble;
import com.github.natanbc.idk.ir.value.IrLong;
import com.github.natanbc.idk.ir.value.IrNil;
import com.github.natanbc.idk.ir.value.IrObjectLiteral;
import com.github.natanbc.idk.ir.value.IrString;
import com.github.natanbc.idk.ir.variable.IrAssign;
import com.github.natanbc.idk.ir.variable.IrGlobal;
import com.github.natanbc.idk.ir.variable.IrLocal;
import com.github.natanbc.idk.ir.variable.IrMember;
import com.github.natanbc.idk.ir.variable.IrUpvalue;

import java.util.List;

public class BytecodeConverter implements IrVisitor<byte[]> {
    private static final BytecodeConverter INSTANCE = new BytecodeConverter();
    
    public static BytecodeConverter instance() {
        return INSTANCE;
    }
    
    @Override
    public byte[] visitRoot(IrRoot node) {
        var writer = new BytecodeWriter();
        var fw = writer.createFunction("$main", 0, node.getLocalsCount(), false, List.of());
        new ActualBytecodeConverter(writer, fw).compile(node.getBody());
        fw.end();
        writer.setEntrypoint(fw.id());
        return writer.write();
    }
    
    @Override
    public byte[] visitBoolean(IrBoolean node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitDouble(IrDouble node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitLong(IrLong node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitNil(IrNil node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitString(IrString node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitArrayLiteral(IrArrayLiteral node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitObjectLiteral(IrObjectLiteral node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitUnaryOperation(IrUnaryOperation node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitBinaryOperation(IrBinaryOperation node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitRange(IrRange node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitLocal(IrLocal node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitUpvalue(IrUpvalue node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitGlobal(IrGlobal node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitAssign(IrAssign node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitMember(IrMember node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitBody(IrBody node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitCall(IrCall node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitFunction(IrFunction node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitIf(IrIf node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitWhile(IrWhile node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitFor(IrFor node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitReturn(IrReturn node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
    
    @Override
    public byte[] visitThrow(IrThrow node) {
        throw new UnsupportedOperationException("Only root nodes can be converted");
    }
}
