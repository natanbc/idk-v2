package com.github.natanbc.idk.ast;

import com.github.natanbc.idk.ast.misc.*;
import com.github.natanbc.idk.ast.operation.AstBinaryOperation;
import com.github.natanbc.idk.ast.operation.AstUnaryOperation;
import com.github.natanbc.idk.ast.value.*;
import com.github.natanbc.idk.ast.variable.*;

public interface AstVisitor<T> {
    T visitBoolean(AstBoolean node);
    
    T visitDouble(AstDouble node);
    
    T visitLong(AstLong node);
    
    T visitNil(AstNil node);
    
    T visitString(AstString node);
    
    T visitArrayLiteral(AstArrayLiteral node);
    
    T visitObjectLiteral(AstObjectLiteral node);
    
    T visitUnaryOperation(AstUnaryOperation node);
    
    T visitBinaryOperation(AstBinaryOperation node);
    
    T visitIdentifier(AstIdentifier node);
    
    T visitGlobal(AstGlobal node);
    
    T visitLet(AstLet node);
    
    T visitAssign(AstAssign node);
    
    T visitMember(AstMember node);
    
    T visitBody(AstBody node);
    
    T visitCall(AstCall node);
    
    T visitFunction(AstFunction node);
    
    T visitIf(AstIf node);
    
    T visitWhile(AstWhile node);
    
    T visitReturn(AstReturn node);
    
    T visitThrow(AstThrow node);
    
    //T visitAwait(AstAwait node);
    
    //T visitUnpack(AstUnpack node);
}
