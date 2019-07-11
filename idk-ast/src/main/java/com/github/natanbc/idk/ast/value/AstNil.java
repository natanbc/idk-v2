package com.github.natanbc.idk.ast.value;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

public class AstNil implements AstNode {
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitNil(this);
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AstNil;
    }
    
    @Override
    public String toString() {
        return "Nil";
    }
}
