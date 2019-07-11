package com.github.natanbc.idk.ast.misc;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

public class AstWhile implements AstNode {
    private final AstNode condition;
    private final AstNode body;
    private final AstNode elseBody;
    
    public AstWhile(AstNode condition, AstNode body, AstNode elseBody) {
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }
    
    public AstNode getCondition() {
        return condition;
    }
    
    public AstNode getBody() {
        return body;
    }
    
    public AstNode getElseBody() {
        return elseBody;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitWhile(this);
    }
    
    @Override
    public int hashCode() {
        return condition.hashCode() ^ body.hashCode() ^ elseBody.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AstWhile)) {
            return false;
        }
        var o = (AstWhile)obj;
        return o.condition.equals(condition) && o.body.equals(body) && o.elseBody.equals(elseBody);
    }
    
    @Override
    public String toString() {
        return "While(" + condition + ", " + body + ", " + elseBody + ")";
    }
}
