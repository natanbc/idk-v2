package com.github.natanbc.idk.ir.value;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrNil implements IrNode {
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitNil(this);
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IrNil;
    }
    
    @Override
    public String toString() {
        return "Nil";
    }
}
