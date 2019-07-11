package com.github.natanbc.idk.ir.variable;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

public class IrUpvalue implements IrNode {
    private final int level;
    private final int index;
    
    public IrUpvalue(int level, int index) {
        this.level = level;
        this.index = index;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getIndex() {
        return index;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitUpvalue(this);
    }
    
    @Override
    public int hashCode() {
        return level ^ index;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IrUpvalue)) {
            return false;
        }
        var o = (IrUpvalue)obj;
        return o.level == level && o.index == index;
    }
    
    @Override
    public String toString() {
        return "Upvalue(" + level + ", " + index + ")";
    }
}
