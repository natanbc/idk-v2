package com.github.natanbc.idk.ir.variable;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrAssign implements IrNode {
    private final IrNode target;
    private final IrNode value;
    
    public IrAssign(IrNode target, IrNode value) {
        this.target = target;
        this.value = value;
    }
    
    public IrNode getTarget() {
        return target;
    }
    
    public IrNode getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitAssign(this);
    }
    
    @Override
    public int hashCode() {
        return target.hashCode() ^ value.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IrAssign)) {
            return false;
        }
        var o = (IrAssign)obj;
        return ((IrAssign) obj).target.equals(target) && ((IrAssign) obj).value.equals(value);
    }
    
    @Override
    public String toString() {
        return "Assign(" + target + ", " + value + ")";
    }
}
