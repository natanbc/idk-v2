package com.github.natanbc.idk.ir.misc;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;

import java.util.List;

public class IrCall implements IrNode {
    private final IrNode target;
    private final List<IrNode> arguments;
    
    public IrCall(IrNode target, List<IrNode> arguments) {
        this.target = target;
        this.arguments = arguments;
    }
    
    public IrNode getTarget() {
        return target;
    }
    
    public List<IrNode> getArguments() {
        return arguments;
    }
    
    @Override
    public <T> T accept(IrVisitor<T> visitor) {
        return visitor.visitCall(this);
    }
    
    @Override
    public int hashCode() {
        return target.hashCode() ^ arguments.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IrCall)) {
            return false;
        }
        var o = (IrCall)obj;
        return o.target.equals(target) && o.arguments.equals(arguments);
    }
    
    @Override
    public String toString() {
        var sb = new StringBuilder()
                .append("Call(")
                .append(target)
                .append(", (");
        for(var v : arguments) {
            sb.append(v).append(", ");
        }
        if(arguments.size() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString() + "))";
    }
}
