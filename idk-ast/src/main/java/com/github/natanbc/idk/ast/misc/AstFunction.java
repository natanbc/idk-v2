package com.github.natanbc.idk.ast.misc;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;

import java.util.List;
import java.util.Objects;

public class AstFunction implements AstNode {
    private final boolean local;
    private final String name;
    private final List<String> arguments;
    private final AstNode body;
    private final boolean varargs;
    private final List<String> annotations;
    
    public AstFunction(boolean local, String name, List<String> arguments, AstNode body, boolean varargs, List<String> annotations) {
        this.local = local;
        this.name = name;
        this.arguments = arguments;
        this.body = body;
        this.varargs = varargs;
        this.annotations = annotations;
    }
    
    public boolean isLocal() {
        return local;
    }
    
    public String getName() {
        return name;
    }
    
    public List<String> getArguments() {
        return arguments;
    }
    
    public AstNode getBody() {
        return body;
    }
    
    public boolean isVarargs() {
        return varargs;
    }
    
    public List<String> getAnnotations() {
        return annotations;
    }
    
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitFunction(this);
    }
    
    @Override
    public int hashCode() {
        return Boolean.hashCode(local) ^ Objects.hashCode(name) ^ arguments.hashCode()
                ^ body.hashCode() ^ Boolean.hashCode(varargs) ^ annotations.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AstFunction)) {
            return false;
        }
        var o = (AstFunction)obj;
        return o.local == local && Objects.equals(o.name, name) && o.arguments.equals(arguments)
                && o.body.equals(body) && o.varargs == varargs && o.annotations.equals(annotations);
    }
    
    @Override
    public String toString() {
        return "Function(" + (local ? "local" : "non local") + ", " + name + ", " + arguments + ", " + body + ", " + varargs + ", " + annotations + ")";
    }
}
