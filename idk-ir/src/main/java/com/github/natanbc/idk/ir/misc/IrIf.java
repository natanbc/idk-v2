package com.github.natanbc.idk.ir.misc;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrIf implements IrNode {
    private final IrNode condition;
    private final IrNode ifBody;
    private final IrNode elseBody;
    
    public IrIf(IrNode condition, IrNode ifBody, IrNode elseBody) {
        this.condition = condition;
        this.ifBody = ifBody;
        this.elseBody = elseBody;
    }
    
    public IrNode getCondition() {
        return condition;
    }
    
    public IrNode getIfBody() {
        return ifBody;
    }
    
    public IrNode getElseBody() {
        return elseBody;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitIf(this);
    }
    
    @Override
    public int hashCode() {
        return condition.hashCode() ^ ifBody.hashCode() ^ elseBody.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IrIf)) {
            return false;
        }
        var o = (IrIf)obj;
        return o.condition.equals(condition) && o.ifBody.equals(ifBody) && o.elseBody.equals(elseBody);
    }
    
    @Override
    public String toString() {
        return "If(" + condition + ", " + ifBody + ", " + elseBody + ")";
    }
}
