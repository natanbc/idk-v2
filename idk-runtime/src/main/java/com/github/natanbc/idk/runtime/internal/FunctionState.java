package com.github.natanbc.idk.runtime.internal;

import com.github.natanbc.idk.runtime.NilValue;
import com.github.natanbc.idk.runtime.Value;

import java.util.Arrays;
import java.util.Map;

public class FunctionState extends BasicExecutionContext {
    private final FunctionState parent;
    private final Value[] locals;
    
    public FunctionState(FunctionState parent, int localsCount) {
        super(parent.getGlobals());
        this.parent = parent;
        this.locals = new Value[localsCount];
        Arrays.fill(locals, NilValue.instance());
    }
    
    public FunctionState(Map<String, Value> globals, int localsCount) {
        super(globals);
        this.parent = null;
        this.locals = new Value[localsCount];
        Arrays.fill(locals, NilValue.instance());
    }
    
    public void fillFromArgs(Value[] args, int argCount) {
        System.arraycopy(args, 0, locals, 0, Math.min(argCount, args.length));
    }
    
    public Value getLocal(int index) {
        return locals[index];
    }
    
    public Value setLocal(int index, Value value) {
        return locals[index] = value;
    }
    
    public Value getUpvalue(int level, int index) {
        return getParent(level).getLocal(index);
    }
    
    public Value setUpvalue(int level, int index, Value value) {
        return getParent(level).setLocal(index, value);
    }
    
    private FunctionState getParent(int level) {
        FunctionState s = this;
        for(var i = 0; i < level; i++) {
            s = s.parent;
        }
        return s;
    }
}
