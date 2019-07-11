package com.github.natanbc.idk.ast.misc;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

import java.util.List;

public class AstCall implements AstNode {
    private final AstNode target;
    private final List<AstNode> arguments;
    
    public AstCall(AstNode target, List<AstNode> arguments) {
        this.target = target;
        this.arguments = arguments;
    }
    
    public AstNode getTarget() {
        return target;
    }
    
    public List<AstNode> getArguments() {
        return arguments;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitCall(this);
    }
    
    @Override
    public int hashCode() {
        return target.hashCode() ^ arguments.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AstCall)) {
            return false;
        }
        var o = (AstCall)obj;
        return o.target.equals(target) && o.arguments.equals(arguments);
    }
    
    @Override
    public String toString() {
        var sb = new StringBuilder()
                .append("Call(")
                .append(target)
                .append(", (");
        for(var v : arguments) {
            sb.append(v).append(", ");
        }
        if(arguments.size() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString() + "))";
    }
}
