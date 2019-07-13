package com.github.natanbc.idk.ir.convert;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;
import com.github.natanbc.idk.ast.misc.*;
import com.github.natanbc.idk.ast.operation.AstBinaryOperation;
import com.github.natanbc.idk.ast.operation.AstUnaryOperation;
import com.github.natanbc.idk.ast.value.*;
import com.github.natanbc.idk.ast.variable.*;
import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.misc.IrRoot;

public class IrConverter implements AstVisitor<IrNode> {
    private static final IrConverter INSTANCE = new IrConverter();
    
    public static IrConverter instance() {
        return INSTANCE;
    }
    
    @Override
    public IrNode visitBoolean(AstBoolean node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitDouble(AstDouble node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitLong(AstLong node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitNil(AstNil node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitString(AstString node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitArrayLiteral(AstArrayLiteral node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitObjectLiteral(AstObjectLiteral node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitUnaryOperation(AstUnaryOperation node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitBinaryOperation(AstBinaryOperation node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitRange(AstRange node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitIdentifier(AstIdentifier node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitGlobal(AstGlobal node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitLet(AstLet node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitAssign(AstAssign node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitMember(AstMember node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitBody(AstBody node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitCall(AstCall node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitFunction(AstFunction node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitIf(AstIf node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitWhile(AstWhile node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitFor(AstFor node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitReturn(AstReturn node) {
        return convertAndWrap(node);
    }
    
    @Override
    public IrNode visitThrow(AstThrow node) {
        return convertAndWrap(node);
    }
    
//    @Override
//    public IrNode visitUnpack(AstUnpack node) {
//        return convertAndWrap(node);
//    }
    
    private static IrNode convertAndWrap(AstNode node) {
        var scope = new FunctionScope(null);
        var ir = node.accept(new ActualIrConverter(scope));
        return new IrRoot(scope.localsCount(), ir);
    }
}
