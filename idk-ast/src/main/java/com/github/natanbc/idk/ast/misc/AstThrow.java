package com.github.natanbc.idk.ast.misc;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

public class AstThrow implements AstNode {
    private final AstNode value;
    
    public AstThrow(AstNode value) {
        this.value = value;
    }
    
    public AstNode getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitThrow(this);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AstThrow && ((AstThrow) obj).value.equals(value);
    }
    
    @Override
    public String toString() {
        return "Throw(" + value + ")";
    }
}
