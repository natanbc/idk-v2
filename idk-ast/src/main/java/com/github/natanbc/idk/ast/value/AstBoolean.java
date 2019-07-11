package com.github.natanbc.idk.ast.value;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

public class AstBoolean implements AstNode {
    private final boolean value;
    
    public AstBoolean(boolean value) {
        this.value = value;
    }
    
    public boolean getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitBoolean(this);
    }
    
    @Override
    public int hashCode() {
        return value ? 1 : 0;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AstBoolean && ((AstBoolean) obj).value == value;
    }
    
    @Override
    public String toString() {
        return "Boolean(" + value + ")";
    }
}
