package com.github.natanbc.idk.ast.misc;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

public class AstRange implements AstNode {
    private final AstNode from;
    private final AstNode to;
    
    public AstRange(AstNode from, AstNode to) {
        this.from = from;
        this.to = to;
    }
    
    public AstNode getFrom() {
        return from;
    }
    
    public AstNode getTo() {
        return to;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitRange(this);
    }
    
    @Override
    public int hashCode() {
        return from.hashCode() ^ to.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AstRange)) {
            return false;
        }
        var o = (AstRange)obj;
        return o.from.equals(from) && o.to.equals(to);
    }
    
    @Override
    public String toString() {
        return "Range(" + from + ", " + to + ")";
    }
}
