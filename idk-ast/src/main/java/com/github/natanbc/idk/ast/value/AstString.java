package com.github.natanbc.idk.ast.value;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

public class AstString implements AstNode {
    private final String value;
    
    public AstString(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitString(this);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AstString && ((AstString) obj).value.equals(value);
    }
    
    @Override
    public String toString() {
        return "String(" + value + ")";
    }
}
