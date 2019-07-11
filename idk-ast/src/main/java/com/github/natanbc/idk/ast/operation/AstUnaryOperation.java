package com.github.natanbc.idk.ast.operation;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;
import com.github.natanbc.idk.common.UnaryOperationType;

public class AstUnaryOperation implements AstNode {
    private final UnaryOperationType type;
    private final AstNode target;
    
    public AstUnaryOperation(UnaryOperationType type, AstNode target) {
        this.type = type;
        this.target = target;
    }
    
    public UnaryOperationType getType() {
        return type;
    }
    
    public AstNode getTarget() {
        return target;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitUnaryOperation(this);
    }
    
    @Override
    public int hashCode() {
        return target.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AstUnaryOperation)) {
            return false;
        }
        var o = (AstUnaryOperation)obj;
        return o.type == type && o.target.equals(target);
    }
    
    @Override
    public String toString() {
        return type.getTitleCase() + "(" + target + ")";
    }
}
