package com.github.natanbc.idk.ast.value;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

import java.util.List;

public class AstArrayLiteral implements AstNode {
    private final List<AstNode> values;
    
    public AstArrayLiteral(List<AstNode> values) {
        this.values = values;
    }
    
    public List<AstNode> getValues() {
        return values;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitArrayLiteral(this);
    }
    
    @Override
    public int hashCode() {
        return values.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AstArrayLiteral && ((AstArrayLiteral) obj).values.equals(values);
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
