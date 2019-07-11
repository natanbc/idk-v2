package com.github.natanbc.idk.ast;

public interface AstNode {
    <T> T accept(AstVisitor<T> visitor);
}
