package com.github.natanbc.idk.ir.value;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrBoolean implements IrNode {
    private final boolean value;
    
    public IrBoolean(boolean value) {
        this.value = value;
    }
    
    public boolean getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitBoolean(this);
    }
    
    @Override
    public int hashCode() {
        return value ? 1 : 0;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IrBoolean && ((IrBoolean) obj).value == value;
    }
    
    @Override
    public String toString() {
        return "Boolean(" + value + ")";
    }
}
