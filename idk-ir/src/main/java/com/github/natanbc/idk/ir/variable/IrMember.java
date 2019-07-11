package com.github.natanbc.idk.ir.variable;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrMember implements IrNode {
    private final IrNode target;
    private final IrNode key;
    
    public IrMember(IrNode target, IrNode key) {
        this.target = target;
        this.key = key;
    }
    
    public IrNode getTarget() {
        return target;
    }
    
    public IrNode getKey() {
        return key;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitMember(this);
    }
    
    @Override
    public int hashCode() {
        return target.hashCode() ^ key.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IrMember)) {
            return false;
        }
        var o = (IrMember)obj;
        return o.target.equals(target) && o.key.equals(key);
    }
    
    @Override
    public String toString() {
        return "Member(" + target + ", " + key + ")";
    }
}
