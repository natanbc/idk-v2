package com.github.natanbc.idk.ast.value;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

public class AstLong implements AstNode {
    private final long value;
    
    public AstLong(long value) {
        this.value = value;
    }
    
    public long getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitLong(this);
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AstLong && ((AstLong) obj).value == value;
    }
    
    @Override
    public String toString() {
        return "Long(" + value + ")";
    }
}
