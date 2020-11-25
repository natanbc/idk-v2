package com.github.natanbc.idk.bytecode.convert;

import com.github.natanbc.idk.bytecode.BytecodeWriter;
import com.github.natanbc.idk.bytecode.ConditionType;
import com.github.natanbc.idk.bytecode.FunctionWriter;
import com.github.natanbc.idk.bytecode.Label;
import com.github.natanbc.idk.bytecode.ValueType;
import com.github.natanbc.idk.common.BinaryOperationType;
import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;
import com.github.natanbc.idk.ir.misc.IrBody;
import com.github.natanbc.idk.ir.misc.IrCall;
import com.github.natanbc.idk.ir.misc.IrFor;
import com.github.natanbc.idk.ir.misc.IrFunction;
import com.github.natanbc.idk.ir.misc.IrIf;
import com.github.natanbc.idk.ir.misc.IrRange;
import com.github.natanbc.idk.ir.misc.IrReturn;
import com.github.natanbc.idk.ir.misc.IrRoot;
import com.github.natanbc.idk.ir.misc.IrThrow;
import com.github.natanbc.idk.ir.misc.IrWhile;
import com.github.natanbc.idk.ir.operation.IrBinaryOperation;
import com.github.natanbc.idk.ir.operation.IrUnaryOperation;
import com.github.natanbc.idk.ir.value.IrArrayLiteral;
import com.github.natanbc.idk.ir.value.IrBoolean;
import com.github.natanbc.idk.ir.value.IrDouble;
import com.github.natanbc.idk.ir.value.IrLong;
import com.github.natanbc.idk.ir.value.IrNil;
import com.github.natanbc.idk.ir.value.IrObjectLiteral;
import com.github.natanbc.idk.ir.value.IrString;
import com.github.natanbc.idk.ir.variable.IrAssign;
import com.github.natanbc.idk.ir.variable.IrGlobal;
import com.github.natanbc.idk.ir.variable.IrLocal;
import com.github.natanbc.idk.ir.variable.IrMember;
import com.github.natanbc.idk.ir.variable.IrUpvalue;

class ActualBytecodeConverter implements IrVisitor<Boolean> {
    private final BytecodeWriter writer;
    private final FunctionWriter function;
    private boolean resultUsed;
    
    ActualBytecodeConverter(BytecodeWriter writer, FunctionWriter function) {
        this.writer = writer;
        this.function = function;
    }
    
    @Override
    public Boolean visitRoot(IrRoot node) {
        throw new UnsupportedOperationException("Multiple root nodes");
    }
    
    @Override
    public Boolean visitBoolean(IrBoolean node) {
        function.loadConstant(node.getValue());
        return true;
    }
    
    @Override
    public Boolean visitDouble(IrDouble node) {
        function.loadConstant(node.getValue());
        return true;
    }
    
    @Override
    public Boolean visitLong(IrLong node) {
        function.loadConstant(node.getValue());
        return true;
    }
    
    @Override
    public Boolean visitNil(IrNil node) {
        function.loadNil();
        return true;
    }
    
    @Override
    public Boolean visitString(IrString node) {
        function.loadConstant(node.getValue());
        return true;
    }
    
    @Override
    public Boolean visitArrayLiteral(IrArrayLiteral node) {
        node.getValues().forEach(this::compile);
        function.arrayLiteral(node.getValues().size());
        return true;
    }
    
    @Override
    public Boolean visitObjectLiteral(IrObjectLiteral node) {
        node.getEntries().forEach(pair -> {
            compile(pair.getKey());
            compile(pair.getValue());
        });
        function.objectLiteral(node.getEntries().size());
        return true;
    }
    
    @Override
    public Boolean visitUnaryOperation(IrUnaryOperation node) {
        compile(node.getTarget());
        function.unaryOperation(node.getType());
        return true;
    }
    
    @Override
    public Boolean visitBinaryOperation(IrBinaryOperation node) {
        switch(node.getType()) {
            case OR -> {
                compile(node.getLhs());
                var l = new Label();
                function.dup();
                function.jumpIf(ConditionType.IF_TRUE, l);
                compile(node.getRhs());
                function.binaryOperation(BinaryOperationType.OR);
                function.bind(l);
            }
            case AND -> {
                compile(node.getLhs());
                var l = new Label();
                function.dup();
                function.jumpIf(ConditionType.IF_FALSE, l);
                compile(node.getRhs());
                function.binaryOperation(BinaryOperationType.AND);
                function.bind(l);
            }
            default -> {
                compile(node.getLhs());
                compile(node.getRhs());
                function.binaryOperation(node.getType());
            }
        }
        return true;
    }
    
    @Override
    public Boolean visitRange(IrRange node) {
        compile(node.getFrom());
        compile(node.getTo());
        function.createRange();
        return true;
    }
    
    @Override
    public Boolean visitLocal(IrLocal node) {
        function.loadLocal(node.getIndex());
        return true;
    }
    
    @Override
    public Boolean visitUpvalue(IrUpvalue node) {
        function.loadUpvalue(node.getLevel(), node.getIndex());
        return true;
    }
    
    @Override
    public Boolean visitGlobal(IrGlobal node) {
        function.loadGlobal(node.getName());
        return true;
    }
    
    @Override
    public Boolean visitAssign(IrAssign node) {
        var target = node.getTarget();
        if(target instanceof IrLocal) {
            compile(node.getValue());
            if(resultUsed) function.dup();
            function.storeLocal(((IrLocal) target).getIndex());
        } else if(target instanceof IrUpvalue) {
            compile(node.getValue());
            var up = (IrUpvalue)target;
            if(resultUsed) function.dup();
            function.storeUpvalue(up.getLevel(), up.getIndex());
        } else if(target instanceof IrGlobal) {
            compile(node.getValue());
            if(resultUsed) function.dup();
            function.storeGlobal(((IrGlobal) target).getName());
        } else if(target instanceof IrMember) {
            var m = (IrMember)target;
            compile(m.getTarget());
            compile(m.getKey());
            compile(node.getValue());
            if(resultUsed) function.dup();
            function.storeMember();
        } else {
            throw new IllegalArgumentException("Can't assign to " + target);
        }
        return false;
    }
    
    @Override
    public Boolean visitMember(IrMember node) {
        compile(node.getTarget());
        compile(node.getKey());
        function.loadMember();
        return true;
    }
    
    @Override
    public Boolean visitBody(IrBody node) {
        if(node.getChildren().isEmpty() && resultUsed) {
            function.loadNil();
            return false;
        }
        for(var it = node.getChildren().iterator(); it.hasNext();) {
            var n = it.next();
            compile(n, resultUsed && !it.hasNext());
        }
        return false;
    }
    
    @Override
    public Boolean visitCall(IrCall node) {
        compile(node.getTarget());
        for(var argument : node.getArguments()) {
            compile(argument);
        }
        function.call(node.getArguments().size());
        return true;
    }
    
    @Override
    public Boolean visitFunction(IrFunction node) {
        var fw = writer.createFunction(
                node.getName(),
                node.getArgumentCount(),
                node.getLocalsCount(),
                node.isVarargs(),
                node.getAnnotations()
        );
        node.getBody().accept(new ActualBytecodeConverter(writer, fw));
        fw.end();
        function.loadFunction(fw.id());
        //IR already folds Function("name", ...) into Assign("name", Function("name", ...))
//        if(node.getName() != null) {
//            if(resultUsed) function.dup();
//            function.storeGlobal(node.getName());
//        }
        return true;
    }
    
    @Override
    public Boolean visitIf(IrIf node) {
        var elseLabel = new Label();
        var endLabel = new Label();
        
        compile(node.getCondition());
        function.jumpIf(ConditionType.IF_FALSE, elseLabel);
        //compile(node.getIfBody(), resultUsed);
        compile(node.getIfBody());
        function.jump(endLabel);
        function.bind(elseLabel);
        //compile(node.getElseBody(), resultUsed);
        compile(node.getElseBody());
        function.bind(endLabel);
        if(!resultUsed) function.pop();
        return false;
    }
    
    @Override
    public Boolean visitWhile(IrWhile node) {
        var loopStart = new Label();
        var loopDone = new Label();
        var end = new Label();
        
        var runElse = function.newLocal();
        function.loadConstant(true);
        function.storeLocal(runElse);
        
        if(resultUsed) function.loadNil();
        function.bind(loopStart);
        compile(node.getCondition());
        function.jumpIf(ConditionType.IF_FALSE, loopDone);
        if(resultUsed) function.pop();
        function.loadConstant(false);
        function.storeLocal(runElse);
        compile(node.getBody(), resultUsed);
        function.jump(loopStart);
        
        function.bind(loopDone);
        function.loadLocal(runElse);
        function.jumpIf(ConditionType.IF_FALSE, end);
        if(resultUsed) function.pop();
        compile(node.getElseBody(), resultUsed);
        function.bind(end);
        
        return true;
    }
    
    @Override
    public Boolean visitFor(IrFor node) {
        //optimization for known types
        if(node.getValue() instanceof IrRange) {
            var range = (IrRange) node.getValue();
            compile(range.getFrom());
            compile(range.getTo());
            /* stack must be [..., from, to] */
            compileRangeFor(node);
            return false;
        }
        var isRange = new Label();
        compile(node.getValue());
        // ..., val -> ..., val, val
        function.dup();
        // ..., val, val -> ..., val, <is range>
        function.testType(ValueType.RANGE);
        // ..., val, <is range> -> ..., val
        function.jumpIf(ConditionType.IF_TRUE, isRange);
        // ..., val -> ..., val, "not implemented"
        function.loadConstant("For not implemented for value ");
        // ..., val, "not implemented" -> ..., "not implemented", val
        function.swap2();
        // ..., "not implemented", val -> ..., "not implemented: val"
        function.binaryOperation(BinaryOperationType.ADD);
        // ..., "not implemented: val" -> ...
        function.exthrow();
        
        function.bind(isRange);
        // ..., range -> ..., range, range
        function.dup();
        // ..., range, range -> ..., range, range, "from"
        function.loadConstant("from");
        // ..., range, range, "from" -> ..., range, <from>
        function.loadMember();
        // ..., range, <from> -> ..., <from>, range
        function.swap2();
        // ..., <from>, range -> ..., <from>, range, "to"
        function.loadConstant("to");
        // ..., <from>, range -> ..., <from>, <to>
        function.loadMember();
        // actual for logic
        /* stack must be [..., from, to] */
        compileRangeFor(node);
        
        return false;
    }
    
    @Override
    public Boolean visitReturn(IrReturn node) {
        compile(node.getValue());
        function.ret();
        return true;
    }
    
    @Override
    public Boolean visitThrow(IrThrow node) {
        compile(node.getValue());
        function.exthrow();
        return true;
    }
    
    /* stack must have [from, to] at the top */
    private void compileRangeFor(IrFor node) {
        var from = function.newLocal();
        var to = function.newLocal();
        var step = function.newLocal();
        var end = function.newLocal();
        function.storeLocal(to);
        function.storeLocal(from);
        {
            /* var step = range.getFrom() > range.getTo() ? -1 : 1; */
            var gt = new Label();
            var cont = new Label();
            function.loadLocal(from);
            function.loadLocal(to);
            function.binaryOperation(BinaryOperationType.GREATER);
            function.jumpIf(ConditionType.IF_TRUE, gt);
            function.loadConstant(1);
            function.storeLocal(step);
            function.jump(cont);
        
            function.bind(gt);
            function.loadConstant(-1);
            function.storeLocal(step);
            function.bind(cont);
        }
        {
            /* var end = range.getTo() + step; */
            function.loadLocal(to);
            function.loadLocal(step);
            function.binaryOperation(BinaryOperationType.ADD);
            function.storeLocal(end);
        }
        {
                /*
                for(var l = range.getFrom(); l != end; l += step) {
                    state().setLocal(node.getVariableIndex(), LongValue.of(l));
                    ret = node.getBody().accept(this);
                }
                 */
            var loopVar = function.newLocal();
            function.loadLocal(from);
            function.storeLocal(loopVar);
        
            var loopStart = new Label();
            var loopEnd = new Label();
            function.bind(loopStart);
            function.loadLocal(loopVar);
            function.loadLocal(end);
            function.binaryOperation(BinaryOperationType.NEQ);
            function.jumpIf(ConditionType.IF_FALSE, loopEnd);
            function.loadLocal(loopVar);
            function.storeLocal(node.getVariableIndex());
            compile(node.getBody(), false);
            function.loadLocal(loopVar);
            function.loadLocal(step);
            function.binaryOperation(BinaryOperationType.ADD);
            function.storeLocal(loopVar);
            function.jump(loopStart);
            function.bind(loopEnd);
            if(resultUsed) function.loadNil();
        }
    }
    
    void compile(IrNode node) {
        compile(node, true);
    }
    
    private void compile(IrNode node, boolean resultUsed) {
        var old = this.resultUsed;
        this.resultUsed = resultUsed;
        try {
            if(node.accept(this) && !resultUsed) {
                function.pop();
            }
        } finally {
            this.resultUsed = old;
        }
    }
}
