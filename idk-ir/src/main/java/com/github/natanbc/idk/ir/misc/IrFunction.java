package com.github.natanbc.idk.ir.misc;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

import java.util.List;
import java.util.Objects;

public class IrFunction implements IrNode {
    private final String name;
    private final int argumentCount;
    private final int localsCount;
    private final IrNode body;
    private final boolean varargs;
    private final List<String> annotations;
    
    public IrFunction(String name, int argumentCount, int localsCount, IrNode body, boolean varargs, List<String> annotations) {
        this.name = name;
        this.argumentCount = argumentCount;
        this.localsCount = localsCount;
        this.body = body;
        this.varargs = varargs;
        this.annotations = annotations;
    }
    
    public String getName() {
        return name;
    }
    
    public int getArgumentCount() {
        return argumentCount;
    }
    
    public int getLocalsCount() {
        return localsCount;
    }
    
    public IrNode getBody() {
        return body;
    }
    
    public boolean isVarargs() {
        return varargs;
    }
    
    public List<String> getAnnotations() {
        return annotations;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitFunction(this);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(name) ^ argumentCount ^ localsCount ^ body.hashCode() ^ Boolean.hashCode(varargs) ^ annotations.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IrFunction)) {
            return false;
        }
        var o = (IrFunction)obj;
        return Objects.equals(o.name, name) && o.argumentCount == argumentCount && o.localsCount == localsCount
                && o.body.equals(body) && o.varargs == varargs && o.annotations.equals(annotations);
    }
    
    @Override
    public String toString() {
        return "Function(" + name + ", " + argumentCount + ", " + localsCount + ", " + body + ", " + varargs + ", " + annotations + ")";
    }
}
