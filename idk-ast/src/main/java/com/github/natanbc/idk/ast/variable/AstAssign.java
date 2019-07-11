package com.github.natanbc.idk.ast.variable;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

public class AstAssign implements AstNode {
    private final AstNode target;
    private final AstNode value;
    
    public AstAssign(AstNode target, AstNode value) {
        this.target = target;
        this.value = value;
    }
    
    public AstNode getTarget() {
        return target;
    }
    
    public AstNode getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitAssign(this);
    }
    
    @Override
    public int hashCode() {
        return target.hashCode() ^ value.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AstAssign)) {
            return false;
        }
        var o = (AstAssign)obj;
        return ((AstAssign) obj).target.equals(target) && ((AstAssign) obj).value.equals(value);
    }
    
    @Override
    public String toString() {
        return "Assign(" + target + ", " + value + ")";
    }
}
