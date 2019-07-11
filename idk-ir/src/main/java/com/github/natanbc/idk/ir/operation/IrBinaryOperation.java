package com.github.natanbc.idk.ir.operation;

import com.github.natanbc.idk.common.BinaryOperationType;
import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrBinaryOperation implements IrNode {
    private final BinaryOperationType type;
    private final IrNode lhs;
    private final IrNode rhs;
    
    public IrBinaryOperation(BinaryOperationType type, IrNode lhs, IrNode rhs) {
        this.type = type;
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public BinaryOperationType getType() {
        return type;
    }
    
    public IrNode getLhs() {
        return lhs;
    }
    
    public IrNode getRhs() {
        return rhs;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitBinaryOperation(this);
    }
    
    @Override
    public int hashCode() {
        return type.hashCode() ^ lhs.hashCode() ^ rhs.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IrBinaryOperation)) {
            return false;
        }
        var o = (IrBinaryOperation)obj;
        return o.type == type && o.lhs.equals(lhs) && o.rhs.equals(rhs);
    }
    
    @Override
    public String toString() {
        return type.getTitleCase() + "(" + lhs + ", " + rhs + ")";
    }
}
