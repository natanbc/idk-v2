package com.github.natanbc.idk.ir.variable;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrLocal implements IrNode {
    private final int index;
    
    public IrLocal(int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitLocal(this);
    }
    
    @Override
    public int hashCode() {
        return index;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IrLocal && ((IrLocal) obj).index == index;
    }
    
    @Override
    public String toString() {
        return "Local(" + index + ")";
    }
}
