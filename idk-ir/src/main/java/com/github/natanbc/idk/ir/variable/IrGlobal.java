package com.github.natanbc.idk.ir.variable;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrGlobal implements IrNode {
    private final String name;
    
    public IrGlobal(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitGlobal(this);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IrGlobal && ((IrGlobal) obj).name.equals(name);
    }
    
    @Override
    public String toString() {
        return "Global(" + name + ")";
    }
}
