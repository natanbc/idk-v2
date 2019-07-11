package com.github.natanbc.idk.ir.value;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrDouble implements IrNode {
    private final double value;
    
    public IrDouble(double value) {
        this.value = value;
    }
    
    public double getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitDouble(this);
    }
    
    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IrDouble && ((IrDouble) obj).value == value;
    }
    
    @Override
    public String toString() {
        return "Double(" + value + ")";
    }
}
