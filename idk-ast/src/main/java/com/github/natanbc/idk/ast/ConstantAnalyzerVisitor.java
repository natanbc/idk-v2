package com.github.natanbc.idk.ast;

import com.github.natanbc.idk.ast.misc.*;
import com.github.natanbc.idk.ast.operation.AstBinaryOperation;
import com.github.natanbc.idk.ast.operation.AstUnaryOperation;
import com.github.natanbc.idk.ast.value.*;
import com.github.natanbc.idk.ast.variable.*;

public class ConstantAnalyzerVisitor implements AstVisitor<Boolean> {
    private static final ConstantAnalyzerVisitor INSTANCE = new ConstantAnalyzerVisitor();
    
    public static AstVisitor<Boolean> instance() {
        return INSTANCE;
    }
    
    @Override
    public Boolean visitBoolean(AstBoolean node) {
        return true;
    }
    
    @Override
    public Boolean visitDouble(AstDouble node) {
        return true;
    }
    
    @Override
    public Boolean visitLong(AstLong node) {
        return true;
    }
    
    @Override
    public Boolean visitNil(AstNil node) {
        return true;
    }
    
    @Override
    public Boolean visitString(AstString node) {
        return true;
    }
    
    @Override
    public Boolean visitArrayLiteral(AstArrayLiteral node) {
        for(var n : node.getValues()) {
            if(!n.accept(this)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Boolean visitObjectLiteral(AstObjectLiteral node) {
        for(var n : node.getEntries()) {
            if(!n.getKey().accept(this) || !n.getValue().accept(this)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Boolean visitUnaryOperation(AstUnaryOperation node) {
        return node.getTarget().accept(this);
    }
    
    @Override
    public Boolean visitBinaryOperation(AstBinaryOperation node) {
        return node.getLhs().accept(this) && node.getRhs().accept(this);
    }
    
    @Override
    public Boolean visitRange(AstRange node) {
        return node.getFrom().accept(this) && node.getTo().accept(this);
    }
    
    @Override
    public Boolean visitIdentifier(AstIdentifier node) {
        return true;
    }
    
    @Override
    public Boolean visitGlobal(AstGlobal node) {
        return true;
    }
    
    @Override
    public Boolean visitLet(AstLet node) {
        return false;
    }
    
    @Override
    public Boolean visitAssign(AstAssign node) {
        return false;
    }
    
    @Override
    public Boolean visitMember(AstMember node) {
        return node.getTarget().accept(this) && node.getKey().accept(this);
    }
    
    @Override
    public Boolean visitBody(AstBody node) {
        for(var n : node.getChildren()) {
            if(!n.accept(this)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Boolean visitCall(AstCall node) {
        return false;
    }
    
    @Override
    public Boolean visitFunction(AstFunction node) {
        return node.getName() == null;
    }
    
    @Override
    public Boolean visitIf(AstIf node) {
        return node.getCondition().accept(this) && node.getIfBody().accept(this) && node.getElseBody().accept(this);
    }
    
    @Override
    public Boolean visitWhile(AstWhile node) {
        return node.getCondition().accept(this) && node.getBody().accept(this) && node.getElseBody().accept(this);
    }
    
    @Override
    public Boolean visitFor(AstFor node) {
        return node.getValue().accept(this) && node.getBody().accept(this);
    }
    
    @Override
    public Boolean visitReturn(AstReturn node) {
        return false;
    }
    
    @Override
    public Boolean visitThrow(AstThrow node) {
        return false;
    }
    
//    @Override
//    public Boolean visitAwait(AstAwait node) {
//        return node.getValue().accept(this);
//    }

//    @Override
//    public Boolean visitUnpack(AstUnpack node) {
//        return node.getValue().accept(this);
//    }
}
