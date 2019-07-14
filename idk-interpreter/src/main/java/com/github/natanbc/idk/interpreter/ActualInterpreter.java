package com.github.natanbc.idk.interpreter;

import com.github.natanbc.idk.common.BinaryOperationType;
import com.github.natanbc.idk.common.UnaryOperationType;
import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.IrVisitor;
import com.github.natanbc.idk.ir.misc.*;
import com.github.natanbc.idk.ir.operation.IrBinaryOperation;
import com.github.natanbc.idk.ir.operation.IrUnaryOperation;
import com.github.natanbc.idk.ir.value.*;
import com.github.natanbc.idk.ir.variable.*;
import com.github.natanbc.idk.runtime.*;
import com.github.natanbc.idk.runtime.internal.FunctionState;
import com.github.natanbc.idk.runtime.internal.ReturnException;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

class ActualInterpreter implements IrVisitor<Value> {
    private static final Map<UnaryOperationType, UnaryOperator<Value>> UNARY_OPERATORS = Map.of(
            UnaryOperationType.NEG, Value::neg,
            UnaryOperationType.NEGATE, Value::negate
    );
    
    private static final Map<BinaryOperationType, BinaryOperator<Value>> BINARY_OPERATORS = Map.ofEntries(
            Map.entry(BinaryOperationType.ADD, Value::add),
            Map.entry(BinaryOperationType.SUB, Value::sub),
            Map.entry(BinaryOperationType.MUL, Value::mul),
            Map.entry(BinaryOperationType.DIV, Value::div),
            Map.entry(BinaryOperationType.MOD, Value::mod),
            Map.entry(BinaryOperationType.POW, Value::pow),
            Map.entry(BinaryOperationType.EQ, Value::eq),
            Map.entry(BinaryOperationType.NEQ, Value::neq),
            Map.entry(BinaryOperationType.GREATER, Value::greater),
            Map.entry(BinaryOperationType.GREATER_EQ, Value::greaterEq),
            Map.entry(BinaryOperationType.SMALLER, Value::smaller),
            Map.entry(BinaryOperationType.SMALLER_EQ, Value::smallerEq)
    );
    
    private final FunctionState state;
    
    ActualInterpreter(FunctionState state) {
        this.state = state;
    }
    
    @Override
    public Value visitRoot(IrRoot node) {
        try {
            var state = new FunctionState(this.state.getGlobals(), node.getLocalsCount());
            return node.getBody().accept(new ActualInterpreter(state));
        } catch(ReturnException e) {
            return e.getValue();
        }
    }
    
    @Override
    public Value visitBoolean(IrBoolean node) {
        return BooleanValue.of(node.getValue());
    }
    
    @Override
    public Value visitDouble(IrDouble node) {
        return new DoubleValue(node.getValue());
    }
    
    @Override
    public Value visitLong(IrLong node) {
        return new LongValue(node.getValue());
    }
    
    @Override
    public Value visitNil(IrNil node) {
        return NilValue.instance();
    }
    
    @Override
    public Value visitString(IrString node) {
        return new StringValue(node.getValue());
    }
    
    @Override
    public Value visitArrayLiteral(IrArrayLiteral node) {
        var array = new ArrayValue();
        var i = 0;
        for(var n : node.getValues()) {
            array.rawSet(i, n.accept(this));
            i++;
        }
        return array;
    }
    
    @Override
    public Value visitObjectLiteral(IrObjectLiteral node) {
        var object = new ObjectValue();
        for(Map.Entry<IrNode, IrNode> entry : node.getEntries()) {
            object.set(entry.getKey().accept(this), entry.getValue().accept(this));
        }
        return object;
    }
    
    @Override
    public Value visitUnaryOperation(IrUnaryOperation node) {
        return UNARY_OPERATORS.get(node.getType()).apply(node.getTarget().accept(this));
    }
    
    @Override
    public Value visitBinaryOperation(IrBinaryOperation node) {
        return BINARY_OPERATORS.get(node.getType()).apply(
                node.getLhs().accept(this),
                node.getRhs().accept(this)
        );
    }
    
    @Override
    public Value visitRange(IrRange node) {
        return new RangeValue(
                node.getFrom().accept(this).asLong().getValue(),
                node.getTo().accept(this).asLong().getValue()
        );
    }
    
    @Override
    public Value visitLocal(IrLocal node) {
        return state.getLocal(node.getIndex());
    }
    
    @Override
    public Value visitUpvalue(IrUpvalue node) {
        return state.getUpvalue(node.getLevel(), node.getIndex());
    }
    
    @Override
    public Value visitGlobal(IrGlobal node) {
        return state.getGlobal(node.getName());
    }
    
    @Override
    public Value visitAssign(IrAssign node) {
        var target = node.getTarget();
        if(target instanceof IrLocal) {
            return state.setLocal(((IrLocal) target).getIndex(), node.getValue().accept(this));
        } else if(target instanceof IrUpvalue) {
            var up = (IrUpvalue)target;
            return state.setUpvalue(up.getLevel(), up.getIndex(), node.getValue().accept(this));
        } else if(target instanceof IrGlobal) {
            return state.setGlobal(((IrGlobal) target).getName(), node.getValue().accept(this));
        } else if(target instanceof IrMember) {
            var m = (IrMember)target;
            return m.getTarget().accept(this).set(m.getKey().accept(this), node.getValue().accept(this));
        } else {
            throw new TypeError("Can't assign to " + target);
        }
    }
    
    @Override
    public Value visitMember(IrMember node) {
        return node.getTarget().accept(this).get(node.getKey().accept(this));
    }
    
    @Override
    public Value visitBody(IrBody node) {
        Value ret = NilValue.instance();
        for(var n : node.getChildren()) {
            ret = n.accept(this);
        }
        return ret;
    }
    
    @Override
    public Value visitCall(IrCall node) {
        var target = node.getTarget().accept(this);
        var args = new Value[node.getArguments().size()];
        var i = 0;
        for(var arg : node.getArguments()) {
            args[i++] = arg.accept(this);
        }
        return target.asFunction().call(state, args);
    }
    
    @Override
    public Value visitFunction(IrFunction node) {
        var annotationList = new ArrayList<StringValue>();
        for(String annotation : node.getAnnotations()) {
            annotationList.add(new StringValue(annotation));
        }
        var fn = new Function(node.getName(), annotationList) {
            @Override
            public Value call(ExecutionContext context, Value[] args) {
                var s = new FunctionState(state, node.getLocalsCount());
                s.fillFromArgs(args, node.getArgumentCount(), node.isVarargs());
                try {
                    return node.getBody().accept(new ActualInterpreter(s));
                } catch(ReturnException e) {
                    return e.getValue();
                }
            }
        };
        if(node.getName() != null) {
            state.setGlobal(node.getName(), fn);
        }
        return fn;
    }
    
    @Override
    public Value visitIf(IrIf node) {
        if(node.getCondition().accept(this).asBoolean().getValue()) {
            return node.getIfBody().accept(this);
        } else {
            return node.getElseBody().accept(this);
        }
    }
    
    @Override
    public Value visitWhile(IrWhile node) {
        Value ret = NilValue.instance();
        var executed = false;
        while(node.getCondition().accept(this).asBoolean().getValue()) {
            executed = true;
            ret = node.getBody().accept(this);
        }
        if(!executed) {
            return node.getElseBody().accept(this);
        }
        return ret;
    }
    
    @Override
    public Value visitFor(IrFor node) {
        var value = node.getValue().accept(this);
        if(value.isRange()) {
            var range = value.asRange();
            var step = range.getFrom() > range.getTo() ? -1 : 1;
            Value ret = NilValue.instance();
            var end = range.getTo() + step;
            for(var l = range.getFrom(); l != end; l += step) {
                state.setLocal(node.getVariableIndex(), new LongValue(l));
                ret = node.getBody().accept(this);
            }
            return ret;
        }
        if(value.isArray()) {
            var array = value.asArray();
            Value ret = NilValue.instance();
            for(var i = 0; i < array.size(); i++) {
                state.setLocal(node.getVariableIndex(), array.rawGet(i));
                ret = node.getBody().accept(this);
            }
            return ret;
        }
        throw new TypeError("Bad type as for target: " + value.type());
    }
    
    @Override
    public Value visitReturn(IrReturn node) {
        throw new ReturnException(node.getValue().accept(this));
    }
    
    @Override
    public Value visitThrow(IrThrow node) {
        throw new ThrownError(node.getValue().accept(this));
    }
    
//    @Override
//    public Value visitUnpack(IrUnpack node) {
//        var target = node.getValue().accept(this);
//        return target.asArray().get(new LongValue(0));
//    }
}
