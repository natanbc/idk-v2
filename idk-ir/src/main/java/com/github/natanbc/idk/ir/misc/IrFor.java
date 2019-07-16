package com.github.natanbc.idk.ir.misc;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrFor implements IrNode {
    private final int variableIndex;
    private final IrNode value;
    private final IrNode body;
    private final IrNode elseBody;
    
    public IrFor(int variableIndex, IrNode value, IrNode body, IrNode elseBody) {
        this.variableIndex = variableIndex;
        this.value = value;
        this.body = body;
        this.elseBody = elseBody;
    }
    
    public int getVariableIndex() {
        return variableIndex;
    }
    
    public IrNode getValue() {
        return value;
    }
    
    public IrNode getBody() {
        return body;
    }
    
    public IrNode getElseBody() {
        return elseBody;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitFor(this);
    }
    
    @Override
    public int hashCode() {
        return variableIndex ^ value.hashCode() ^ body.hashCode() ^ elseBody.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IrFor)) {
            return false;
        }
        var o = (IrFor)obj;
        return o.variableIndex == variableIndex && o.value.equals(value) && o.body.equals(body) && o.elseBody.equals(elseBody);
    }
    
    @Override
    public String toString() {
        return "For(" + variableIndex + ", " + value + ", " + body + ", " + elseBody + ")";
    }
}
