package com.github.natanbc.idk.ast.variable;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

public class AstIdentifier implements AstNode {
    private final String name;
    
    public AstIdentifier(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitIdentifier(this);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AstIdentifier && ((AstIdentifier) obj).name.equals(name);
    }
    
    @Override
    public String toString() {
        return "Identifier(" + name + ")";
    }
}
