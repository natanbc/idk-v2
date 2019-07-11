package com.github.natanbc.idk.ast.variable;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

public class AstMember implements AstNode {
    private final AstNode target;
    private final AstNode key;
    
    public AstMember(AstNode target, AstNode key) {
        this.target = target;
        this.key = key;
    }
    
    public AstNode getTarget() {
        return target;
    }
    
    public AstNode getKey() {
        return key;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitMember(this);
    }
    
    @Override
    public int hashCode() {
        return target.hashCode() ^ key.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AstMember)) {
            return false;
        }
        var o = (AstMember)obj;
        return o.target.equals(target) && o.key.equals(key);
    }
    
    @Override
    public String toString() {
        return "Member(" + target + ", " + key + ")";
    }
}
