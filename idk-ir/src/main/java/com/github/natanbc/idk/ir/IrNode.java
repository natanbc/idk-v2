package com.github.natanbc.idk.ir;

public interface IrNode {
    <T> T accept(IrVisitor<T> visitor);
}
