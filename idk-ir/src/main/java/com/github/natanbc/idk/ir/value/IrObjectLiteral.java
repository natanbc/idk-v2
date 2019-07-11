package com.github.natanbc.idk.ir.value;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

import java.util.List;
import java.util.Map;

public class IrObjectLiteral implements IrNode {
    private final List<Map.Entry<IrNode, IrNode>> entries;
    
    public IrObjectLiteral(List<Map.Entry<IrNode, IrNode>> entries) {
        this.entries = entries;
    }
    
    public List<Map.Entry<IrNode, IrNode>> getEntries() {
        return entries;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitObjectLiteral(this);
    }
    
    @Override
    public int hashCode() {
        return entries.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IrObjectLiteral && ((IrObjectLiteral) obj).entries.equals(entries);
    }
    
    @Override
    public String toString() {
        var sb = new StringBuilder()
                .append("ObjectLiteral(");
        for(var entry : entries) {
            sb
                    .append('(')
                    .append(entry.getKey())
                    .append(", ")
                    .append(entry.getValue())
                    .append("), ");
        }
        if(entries.size() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString() + ")";
    }
}
