package com.github.natanbc.idk.ast.operation;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;
import com.github.natanbc.idk.common.BinaryOperationType;

public class AstBinaryOperation implements AstNode {
    private final BinaryOperationType type;
    private final AstNode lhs;
    private final AstNode rhs;
    
    public AstBinaryOperation(BinaryOperationType type, AstNode lhs, AstNode rhs) {
        this.type = type;
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public BinaryOperationType getType() {
        return type;
    }
    
    public AstNode getLhs() {
        return lhs;
    }
    
    public AstNode getRhs() {
        return rhs;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitBinaryOperation(this);
    }
    
    @Override
    public int hashCode() {
        return type.hashCode() ^ lhs.hashCode() ^ rhs.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AstBinaryOperation)) {
            return false;
        }
        var o = (AstBinaryOperation)obj;
        return o.type == type && o.lhs.equals(lhs) && o.rhs.equals(rhs);
    }
    
    @Override
    public String toString() {
        return type.getTitleCase() + "(" + lhs + ", " + rhs + ")";
    }
}
