package com.github.natanbc.idk.ast.misc;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

public class AstIf implements AstNode {
    private final AstNode condition;
    private final AstNode ifBody;
    private final AstNode elseBody;
    
    public AstIf(AstNode condition, AstNode ifBody, AstNode elseBody) {
        this.condition = condition;
        this.ifBody = ifBody;
        this.elseBody = elseBody;
    }
    
    public AstNode getCondition() {
        return condition;
    }
    
    public AstNode getIfBody() {
        return ifBody;
    }
    
    public AstNode getElseBody() {
        return elseBody;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitIf(this);
    }
    
    @Override
    public int hashCode() {
        return condition.hashCode() ^ ifBody.hashCode() ^ elseBody.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AstIf)) {
            return false;
        }
        var o = (AstIf)obj;
        return o.condition.equals(condition) && o.ifBody.equals(ifBody) && o.elseBody.equals(elseBody);
    }
    
    @Override
    public String toString() {
        return "If(" + condition + ", " + ifBody + ", " + elseBody + ")";
    }
}
