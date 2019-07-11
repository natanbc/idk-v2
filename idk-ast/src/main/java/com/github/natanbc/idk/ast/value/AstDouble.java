package com.github.natanbc.idk.ast.value;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

public class AstDouble implements AstNode {
    private final double value;
    
    public AstDouble(double value) {
        this.value = value;
    }
    
    public double getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitDouble(this);
    }
    
    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AstDouble && ((AstDouble) obj).value == value;
    }
    
    @Override
    public String toString() {
        return "Double(" + value + ")";
    }
}
