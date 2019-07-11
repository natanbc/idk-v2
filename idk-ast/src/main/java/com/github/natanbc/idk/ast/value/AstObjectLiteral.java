package com.github.natanbc.idk.ast.value;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

import java.util.List;
import java.util.Map;

public class AstObjectLiteral implements AstNode {
    private final List<Map.Entry<AstNode, AstNode>> entries;
    
    public AstObjectLiteral(List<Map.Entry<AstNode, AstNode>> entries) {
        this.entries = entries;
    }
    
    public List<Map.Entry<AstNode, AstNode>> getEntries() {
        return entries;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitObjectLiteral(this);
    }
    
    @Override
    public int hashCode() {
        return entries.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AstObjectLiteral && ((AstObjectLiteral) obj).entries.equals(entries);
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
