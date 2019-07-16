package com.github.natanbc.idk.ast.misc;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

public class AstFor implements AstNode {
    private final AstNode variable;
    private final AstNode value;
    private final AstNode body;
    private final AstNode elseBody;
    
    public AstFor(AstNode variable, AstNode value, AstNode body, AstNode elseBody) {
        this.variable = variable;
        this.value = value;
        this.body = body;
        this.elseBody = elseBody;
    }
    
    public AstNode getVariable() {
        return variable;
    }
    
    public AstNode getValue() {
        return value;
    }
    
    public AstNode getBody() {
        return body;
    }
    
    public AstNode getElseBody() {
        return elseBody;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitFor(this);
    }
    
    @Override
    public int hashCode() {
        return variable.hashCode() ^ value.hashCode() ^ body.hashCode() ^ elseBody.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AstFor)) {
            return false;
        }
        var o = (AstFor)obj;
        return o.variable.equals(variable) && o.value.equals(value) && o.body.equals(body) && o.elseBody.equals(elseBody);
    }
    
    @Override
    public String toString() {
        return "For(" + variable + ", " + value + ", " + body + ", " + elseBody + ")";
    }
}
