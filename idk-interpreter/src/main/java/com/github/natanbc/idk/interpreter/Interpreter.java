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

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class Interpreter implements IrVisitor<Value> {
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
    
    private final ArrayDeque<FunctionState> states = new ArrayDeque<>();
    private final List<InterpreterHooks> hooks = new ArrayList<>();
    private final Map<String, Value> globals;
    
    public Interpreter(ExecutionContext context) {
        this(context.getGlobals());
    }
    
    public Interpreter(Map<String, Value> globals) {
        this.globals = globals;
    }
    
    public Interpreter addHook(InterpreterHooks hook) {
        hooks.add(Objects.requireNonNull(hook));
        return this;
    }
    
    @Override
    public Value visitRoot(IrRoot node) {
        var replaced = findReplacement(node);
        if(replaced.isPresent()) {
            return replaced.get();
        }
        var state = new FunctionState(globals, node.getLocalsCount());
        states.push(state);
        try {
            return filterValue(node, node.getBody().accept(this));
        } catch(ReturnException e) {
            return filterValue(node, e.getValue());
        } finally {
            if(states.pop() != state) {
                //noinspection ThrowFromFinallyBlock
                throw new IllegalStateException("State stack corrupted");
            }
        }
    }
    
    @Override
    public Value visitBoolean(IrBoolean node) {
        checkValid();
        var replaced = findReplacement(node);
        return replaced.orElseGet(() -> filterValue(node, BooleanValue.of(node.getValue())));
    }
    
    @Override
    public Value visitDouble(IrDouble node) {
        checkValid();
        var replaced = findReplacement(node);
        return replaced.orElseGet(() -> filterValue(node, DoubleValue.of(node.getValue())));
    }
    
    @Override
    public Value visitLong(IrLong node) {
        checkValid();
        var replaced = findReplacement(node);
        return replaced.orElseGet(() -> filterValue(node, LongValue.of(node.getValue())));
    }
    
    @Override
    public Value visitNil(IrNil node) {
        checkValid();
        var replaced = findReplacement(node);
        return replaced.orElseGet(() -> filterValue(node, NilValue.instance()));
    }
    
    @Override
    public Value visitString(IrString node) {
        checkValid();
        var replaced = findReplacement(node);
        return replaced.orElseGet(() -> filterValue(node, StringValue.of(node.getValue())));
    }
    
    @Override
    public Value visitArrayLiteral(IrArrayLiteral node) {
        checkValid();
        var replaced = findReplacement(node);
        if(replaced.isPresent()) {
            return replaced.get();
        }
        var array = new ArrayValue();
        var i = 0;
        for(var n : node.getValues()) {
            array.rawSet(i, n.accept(this));
            i++;
        }
        return filterValue(node, array);
    }
    
    @Override
    public Value visitObjectLiteral(IrObjectLiteral node) {
        checkValid();
        var replaced = findReplacement(node);
        if(replaced.isPresent()) {
            return replaced.get();
        }
        var object = new ObjectValue();
        for(Map.Entry<IrNode, IrNode> entry : node.getEntries()) {
            object.set(entry.getKey().accept(this), entry.getValue().accept(this));
        }
        return filterValue(node, object);
    }
    
    @Override
    public Value visitUnaryOperation(IrUnaryOperation node) {
        checkValid();
        var replaced = findReplacement(node);
        return replaced.orElseGet(() -> filterValue(node, UNARY_OPERATORS.get(node.getType()).apply(node.getTarget().accept(this))));
    }
    
    @Override
    public Value visitBinaryOperation(IrBinaryOperation node) {
        checkValid();
        var replaced = findReplacement(node);
        return replaced.orElseGet(() -> filterValue(node, BINARY_OPERATORS.get(node.getType()).apply(
                node.getLhs().accept(this),
                node.getRhs().accept(this)
        )));
    }
    
    @Override
    public Value visitRange(IrRange node) {
        checkValid();
        var replaced = findReplacement(node);
        return replaced.orElseGet(() -> filterValue(node, RangeValue.of(
                node.getFrom().accept(this).asLong().getValue(),
                node.getTo().accept(this).asLong().getValue()
        )));
    }
    
    @Override
    public Value visitLocal(IrLocal node) {
        checkValid();
        var replaced = findReplacement(node);
        return replaced.orElseGet(() -> filterValue(node, state().getLocal(node.getIndex())));
    }
    
    @Override
    public Value visitUpvalue(IrUpvalue node) {
        checkValid();
        var replaced = findReplacement(node);
        return replaced.orElseGet(() -> filterValue(node, state().getUpvalue(node.getLevel(), node.getIndex())));
    }
    
    @Override
    public Value visitGlobal(IrGlobal node) {
        checkValid();
        var replaced = findReplacement(node);
        return replaced.orElseGet(() -> filterValue(node, state().getGlobal(node.getName())));
    }
    
    @Override
    public Value visitAssign(IrAssign node) {
        checkValid();
        var replaced = findReplacement(node);
        if(replaced.isPresent()) {
            return replaced.get();
        }
        var target = node.getTarget();
        if(target instanceof IrLocal) {
            return filterValue(node, state().setLocal(((IrLocal) target).getIndex(), node.getValue().accept(this)));
        } else if(target instanceof IrUpvalue) {
            var up = (IrUpvalue)target;
            return filterValue(node, state().setUpvalue(up.getLevel(), up.getIndex(), node.getValue().accept(this)));
        } else if(target instanceof IrGlobal) {
            return state().setGlobal(((IrGlobal) target).getName(), node.getValue().accept(this));
        } else if(target instanceof IrMember) {
            var m = (IrMember)target;
            return filterValue(node, m.getTarget().accept(this).set(m.getKey().accept(this), node.getValue().accept(this)));
        } else {
            throw new TypeError("Can't assign to " + target);
        }
    }
    
    @Override
    public Value visitMember(IrMember node) {
        checkValid();
        var replaced = findReplacement(node);
        return replaced.orElseGet(() -> filterValue(node, node.getTarget().accept(this).get(node.getKey().accept(this))));
    }
    
    @Override
    public Value visitBody(IrBody node) {
        checkValid();
        var replaced = findReplacement(node);
        if(replaced.isPresent()) {
            return replaced.get();
        }
        Value ret = NilValue.instance();
        for(var n : node.getChildren()) {
            ret = n.accept(this);
        }
        return filterValue(node, ret);
    }
    
    @Override
    public Value visitCall(IrCall node) {
        checkValid();
        var replaced = findReplacement(node);
        if(replaced.isPresent()) {
            return replaced.get();
        }
        var target = node.getTarget().accept(this);
        var args = new Value[node.getArguments().size()];
        var i = 0;
        for(var arg : node.getArguments()) {
            args[i++] = arg.accept(this);
        }
        return filterValue(node, target.asFunction().call(state(), args));
    }
    
    @Override
    public Value visitFunction(IrFunction node) {
        checkValid();
        var replaced = findReplacement(node);
        if(replaced.isPresent()) {
            return replaced.get();
        }
        var annotationList = new ArrayList<StringValue>();
        for(String annotation : node.getAnnotations()) {
            annotationList.add(StringValue.of(annotation));
        }
        var fn = new Function(node.getName(), annotationList) {
            @Override
            public Value call(ExecutionContext context, Value[] args) {
                var s = new FunctionState(state(), node.getLocalsCount());
                s.fillFromArgs(args, node.getArgumentCount(), node.isVarargs());
                states.push(s);
                try {
                    return node.getBody().accept(Interpreter.this);
                } catch(ReturnException e) {
                    return e.getValue();
                } finally {
                    if(states.pop() != s) {
                        //noinspection ThrowFromFinallyBlock
                        throw new IllegalStateException("State stack corrupted");
                    }
                }
            }
        };
        if(node.getName() != null) {
            state().setGlobal(node.getName(), fn);
        }
        return filterValue(node, fn);
    }
    
    @Override
    public Value visitIf(IrIf node) {
        checkValid();
        var replaced = findReplacement(node);
        if(replaced.isPresent()) {
            return replaced.get();
        }
        if(node.getCondition().accept(this).asBoolean().getValue()) {
            return filterValue(node, node.getIfBody().accept(this));
        } else {
            return filterValue(node, node.getElseBody().accept(this));
        }
    }
    
    @Override
    public Value visitWhile(IrWhile node) {
        checkValid();
        var replaced = findReplacement(node);
        if(replaced.isPresent()) {
            return replaced.get();
        }
        Value ret = NilValue.instance();
        var executed = false;
        while(node.getCondition().accept(this).asBoolean().getValue()) {
            executed = true;
            ret = node.getBody().accept(this);
        }
        if(!executed) {
            return filterValue(node, node.getElseBody().accept(this));
        }
        return filterValue(node, ret);
    }
    
    @Override
    public Value visitFor(IrFor node) {
        checkValid();
        var replaced = findReplacement(node);
        if(replaced.isPresent()) {
            return replaced.get();
        }
        var value = node.getValue().accept(this);
        //ranges are always closed on both ends so the else body never runs
        if(value.isRange()) {
            var range = value.asRange();
            var step = range.getFrom() > range.getTo() ? -1 : 1;
            Value ret = NilValue.instance();
            var end = range.getTo() + step;
            for(var l = range.getFrom(); l != end; l += step) {
                state().setLocal(node.getVariableIndex(), LongValue.of(l));
                ret = node.getBody().accept(this);
            }
            return filterValue(node, ret);
        }
        if(value.isArray()) {
            var array = value.asArray();
            if(array.size() == 0) {
                return filterValue(node, node.getElseBody().accept(this));
            }
            Value ret = NilValue.instance();
            for(var i = 0; i < array.size(); i++) {
                state().setLocal(node.getVariableIndex(), array.rawGet(i));
                ret = node.getBody().accept(this);
            }
            return filterValue(node, ret);
        }
        throw new TypeError("Bad type as for target: " + value.type());
    }
    
    @Override
    public Value visitReturn(IrReturn node) {
        checkValid();
        var replaced = findReplacement(node);
        if(replaced.isPresent()) {
            return replaced.get();
        }
        throw new ReturnException(
                findReplacement(node)
                        .orElseGet(() -> node.getValue().accept(this))
        );
    }
    
    @Override
    public Value visitThrow(IrThrow node) {
        checkValid();
        var replaced = findReplacement(node);
        if(replaced.isPresent()) {
            return replaced.get();
        }
        throw new ThrownError(
                findReplacement(node)
                        .orElseGet(() -> node.getValue().accept(this))
        );
    }

//    @Override
//    public Value visitUnpack(IrUnpack node) {
//        var target = node.getValue().accept(this);
//        return target.asArray().get(new LongValue(0));
//    }
    
    private Optional<Value> findReplacement(IrNode node) {
        for(var hook : hooks) {
            var r = hook.replaceExecution(this, state(), node);
            if(r.isPresent()) {
                return r;
            }
        }
        return Optional.empty();
    }
    
    private Value filterValue(IrNode node, Value result) {
        for(var hook : hooks) {
            result = hook.filterResult(this, state(), node, result);
        }
        return result;
    }
    
    private FunctionState state() {
        return states.peek();
    }
    
    private void checkValid() {
        if(states.isEmpty()) {
            throw new UnsupportedOperationException("Only root nodes can be executed");
        }
    }
}
