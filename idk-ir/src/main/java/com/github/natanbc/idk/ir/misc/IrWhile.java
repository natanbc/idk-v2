package com.github.natanbc.idk.ir.misc;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrWhile implements IrNode {
    private final IrNode condition;
    private final IrNode body;
    private final IrNode elseBody;
    
    public IrWhile(IrNode condition, IrNode body, IrNode elseBody) {
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }
    
    public IrNode getCondition() {
        return condition;
    }
    
    public IrNode getBody() {
        return body;
    }
    
    public IrNode getElseBody() {
        return elseBody;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitWhile(this);
    }
    
    @Override
    public int hashCode() {
        return condition.hashCode() ^ body.hashCode() ^ elseBody.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IrWhile)) {
            return false;
        }
        var o = (IrWhile)obj;
        return o.condition.equals(condition) && o.body.equals(body) && o.elseBody.equals(elseBody);
    }
    
    @Override
    public String toString() {
        return "While(" + condition + ", " + body + ", " + elseBody + ")";
    }
}
