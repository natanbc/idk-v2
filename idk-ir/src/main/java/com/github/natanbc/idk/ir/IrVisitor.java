package com.github.natanbc.idk.ir;

import com.github.natanbc.idk.ir.misc.*;
import com.github.natanbc.idk.ir.operation.IrBinaryOperation;
import com.github.natanbc.idk.ir.operation.IrUnaryOperation;
import com.github.natanbc.idk.ir.value.*;
import com.github.natanbc.idk.ir.variable.*;

public interface IrVisitor<T> {
    T visitRoot(IrRoot node);
    
    T visitBoolean(IrBoolean node);
    
    T visitDouble(IrDouble node);
    
    T visitLong(IrLong node);
    
    T visitNil(IrNil node);
    
    T visitString(IrString node);
    
    T visitArrayLiteral(IrArrayLiteral node);
    
    T visitObjectLiteral(IrObjectLiteral node);
    
    T visitUnaryOperation(IrUnaryOperation node);
    
    T visitBinaryOperation(IrBinaryOperation node);
    
    T visitRange(IrRange node);
    
    T visitLocal(IrLocal node);
    
    T visitUpvalue(IrUpvalue node);
    
    T visitGlobal(IrGlobal node);
    
    T visitAssign(IrAssign node);
    
    T visitMember(IrMember node);
    
    T visitBody(IrBody node);
    
    T visitCall(IrCall node);
    
    T visitFunction(IrFunction node);
    
    T visitIf(IrIf node);
    
    T visitWhile(IrWhile node);
    
    T visitFor(IrFor node);
    
    T visitReturn(IrReturn node);
    
    T visitThrow(IrThrow node);
    
    //T visitUnpack(IrUnpack node);
}
