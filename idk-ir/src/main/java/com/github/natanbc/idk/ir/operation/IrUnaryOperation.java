package com.github.natanbc.idk.ir.operation;

import com.github.natanbc.idk.common.UnaryOperationType;
import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrUnaryOperation implements IrNode {
    private final UnaryOperationType type;
    private final IrNode target;
    
    public IrUnaryOperation(UnaryOperationType type, IrNode target) {
        this.type = type;
        this.target = target;
    }
    
    public UnaryOperationType getType() {
        return type;
    }
    
    public IrNode getTarget() {
        return target;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitUnaryOperation(this);
    }
    
    @Override
    public int hashCode() {
        return target.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IrUnaryOperation)) {
            return false;
        }
        var o = (IrUnaryOperation)obj;
        return o.type == type && o.target.equals(target);
    }
    
    @Override
    public String toString() {
        return type.getTitleCase() + "(" + target + ")";
    }
}
