package com.github.natanbc.idk.ast.misc;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

import java.util.List;

public class AstBody implements AstNode {
    private final List<AstNode> children;
    
    public AstBody(List<AstNode> children) {
        this.children = children;
    }
    
    public List<AstNode> getChildren() {
        return children;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitBody(this);
    }
    
    @Override
    public int hashCode() {
        return children.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AstBody && ((AstBody) obj).children.equals(children);
    }
    
    @Override
    public String toString() {
        var sb = new StringBuilder()
                .append("Body(");
        for(var v : children) {
            sb.append(v).append(", ");
        }
        if(children.size() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString() + ")";
    }
}
