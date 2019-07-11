package com.github.natanbc.idk.ir.misc;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

import java.util.List;

public class IrBody implements IrNode {
    private final List<IrNode> children;
    
    public IrBody(List<IrNode> children) {
        this.children = children;
    }
    
    public List<IrNode> getChildren() {
        return children;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitBody(this);
    }
    
    @Override
    public int hashCode() {
        return children.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IrBody && ((IrBody) obj).children.equals(children);
    }
    
    @Override
    public String toString() {
        var sb = new StringBuilder()
                .append("Body(");
        for(var v : children) {
            sb.append(v).append(", ");
        }
        if(children.size() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString() + ")";
    }
}
