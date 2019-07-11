package com.github.natanbc.idk.ir.misc;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrUnpack implements IrNode {
    private final IrNode value;
    
    public IrUnpack(IrNode value) {
        this.value = value;
    }
    
    public IrNode getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        //return visitor.visitUnpack(this);
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IrUnpack && ((IrUnpack) obj).value.equals(value);
    }
    
    @Override
    public String toString() {
        return "Unpack(" + value + ")";
    }
}
