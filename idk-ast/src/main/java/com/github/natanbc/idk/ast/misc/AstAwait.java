package com.github.natanbc.idk.ast.misc;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

public class AstAwait implements AstNode {
    private final AstNode value;
    
    public AstAwait(AstNode value) {
        this.value = value;
    }
    
    public AstNode getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        //return visitor.visitAwait(this);
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AstAwait && ((AstAwait) obj).value.equals(value);
    }
    
    @Override
    public String toString() {
        return "Await(" + value + ")";
    }
}
