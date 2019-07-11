package com.github.natanbc.idk.ir.value;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

import java.util.List;

public class IrArrayLiteral implements IrNode {
    private final List<IrNode> values;
    
    public IrArrayLiteral(List<IrNode> values) {
        this.values = values;
    }
    
    public List<IrNode> getValues() {
        return values;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitArrayLiteral(this);
    }
    
    @Override
    public int hashCode() {
        return values.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IrArrayLiteral && ((IrArrayLiteral) obj).values.equals(values);
    }
    
    @Override
    public String toString() {
        var sb = new StringBuilder()
                .append("ArrayLiteral(");
        for(var v : values) {
            sb.append(v).append(", ");
        }
        if(values.size() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString() + ")";
    }
}
