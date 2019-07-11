package com.github.natanbc.idk.ir.misc;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrRoot implements IrNode {
    private final int localsCount;
    private final IrNode body;
    
    public IrRoot(int localsCount, IrNode body) {
        this.localsCount = localsCount;
        this.body = body;
    }
    
    public int getLocalsCount() {
        return localsCount;
    }
    
    public IrNode getBody() {
        return body;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitRoot(this);
    }
    
    @Override
    public int hashCode() {
        return localsCount ^ body.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IrRoot)) {
            return false;
        }
        var o = (IrRoot)obj;
        return o.localsCount == localsCount && o.body.equals(body);
    }
    
    @Override
    public String toString() {
        return "Root(" + localsCount + ", " + body + ")";
    }
}
