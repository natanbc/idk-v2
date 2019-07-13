package com.github.natanbc.idk.ir.misc;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrRange implements IrNode {
    private final IrNode from;
    private final IrNode to;
    
    public IrRange(IrNode from, IrNode to) {
        this.from = from;
        this.to = to;
    }
    
    public IrNode getFrom() {
        return from;
    }
    
    public IrNode getTo() {
        return to;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitRange(this);
    }
    
    @Override
    public int hashCode() {
        return from.hashCode() ^ to.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IrRange)) {
            return false;
        }
        var o = (IrRange)obj;
        return o.from.equals(from) && o.to.equals(to);
    }
    
    @Override
    public String toString() {
        return "Range(" + from + ", " + to + ")";
    }
}
