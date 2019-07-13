package com.github.natanbc.idk.ir.convert;

import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.variable.IrGlobal;
import com.github.natanbc.idk.ir.variable.IrLocal;
import com.github.natanbc.idk.ir.variable.IrUpvalue;

import java.util.ArrayList;
import java.util.List;

class FunctionScope {
    private static final String UNUSED_NAME = null;
    
    private final List<String> declaredLocals = new ArrayList<>();
    private final FunctionScope parent;
    private final FunctionScope outer;
    
    private FunctionScope(FunctionScope outer, Void v) {
        this.parent = outer.parent;
        this.outer = outer;
        this.declaredLocals.addAll(outer.declaredLocals);
    }
    
    FunctionScope(FunctionScope parent) {
        this.parent = parent;
        this.outer = null;
    }
    
    FunctionScope innerScope() {
        return new FunctionScope(this, null);
    }
    
    int localsCount() {
        return declaredLocals.size();
    }
    
    IrLocal declareLocal(String name) {
        if(outer != null) {
            outer.declareLocal(UNUSED_NAME);
        }
        declaredLocals.add(name);
        return new IrLocal(declaredLocals.size() - 1);
    }
    
    IrNode find(String name) {
        var local = findLocal(name);
        if(local != null) {
            return local;
        }
        var upvalue = findUpvalue(name, 0);
        if(upvalue != null) {
            return upvalue;
        }
        return new IrGlobal(name);
    }
    
    private IrNode findLocal(String name) {
        var idx = declaredLocals.lastIndexOf(name);
        if(idx >= 0) {
            return new IrLocal(idx);
        }
        return null;
    }
    
    private IrNode findUpvalue(String name, int level) {
        var idx = declaredLocals.indexOf(name);
        if(idx >= 0) {
            return new IrUpvalue(level, idx);
        }
        if(parent == null) {
            return null;
        }
        return parent.findUpvalue(name, level + 1);
    }
}
