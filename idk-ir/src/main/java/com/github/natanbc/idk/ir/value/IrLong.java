package com.github.natanbc.idk.ir.value;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrLong implements IrNode {
    private final long value;
    
    public IrLong(long value) {
        this.value = value;
    }
    
    public long getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitLong(this);
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IrLong && ((IrLong) obj).value == value;
    }
    
    @Override
    public String toString() {
        return "Long(" + value + ")";
    }
}
