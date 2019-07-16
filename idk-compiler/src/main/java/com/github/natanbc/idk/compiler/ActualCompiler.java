package com.github.natanbc.idk.compiler;

import com.github.natanbc.idk.common.BinaryOperationType;
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
import com.headius.invokebinder.Binder;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.invoke.MethodType.methodType;

class ActualCompiler implements IrVisitor<MethodHandle> {
    private static final MethodType EXTERNAL_TYPE = MethodType.methodType(Value.class, ExecutionContext.class);
    private static final MethodType BASE_TYPE = MethodType.methodType(Value.class, FunctionState.class);
    
    private static final ActualCompiler INSTANCE = new ActualCompiler();
    
    static ActualCompiler instance() {
        return INSTANCE;
    }
    
    @Override
    public MethodHandle visitRoot(IrRoot node) {
        return Binder.from(EXTERNAL_TYPE)
                //-> [map, context]
                .fold(Intrinsics.GET_GLOBALS_MAP)
                //-> [state, map, context]
                .fold(Binder.from(methodType(FunctionState.class, Map.class))
                        .insert(1, node.getLocalsCount())
                        .invoke(Intrinsics.FUNCTION_STATE_FROM_MAP)
                )
                //-> [state]
                .drop(1, 2)
                // catch(ReturnException e) { return e.getValue(); }
                .catchException(ReturnException.class, Intrinsics.GET_RETURN_FROM_EXCEPTION)
                //-> [result]
                .invoke(node.getBody().accept(this));
    }
    
    @Override
    public MethodHandle visitBoolean(IrBoolean node) {
        return constant(BooleanValue.of(node.getValue()));
    }
    
    @Override
    public MethodHandle visitDouble(IrDouble node) {
        return constant(DoubleValue.of(node.getValue()));
    }
    
    @Override
    public MethodHandle visitLong(IrLong node) {
        return constant(LongValue.of(node.getValue()));
    }
    
    @Override
    public MethodHandle visitNil(IrNil node) {
        return constant(NilValue.instance());
    }
    
    @Override
    public MethodHandle visitString(IrString node) {
        return constant(StringValue.of(node.getValue()));
    }
    
    @Override
    public MethodHandle visitArrayLiteral(IrArrayLiteral node) {
        var b = base()
                //-> [array, state]
                .fold(Intrinsics.NEW_EMPTY_ARRAY)
                //-> [state, array]
                .permute(1, 0);
        var i = 0;
        for(var e : node.getValues()) {
            var v = e.accept(this);
            b = b
                    //-> [value, state, array]
                    .fold(v)
                    //-> [array, value, state]
                    .permute(2, 0, 1)
                    .foldVoid(
                            Binder.from(Value.class, ArrayValue.class, Value.class)
                                    //-> [array, value, index]
                                    .append(i)
                                    //-> [array, index, value]
                                    .permute(0, 2, 1)
                                    .invoke(Intrinsics.ARRAY_RAW_SET)
                    )
                    //-> [state, array]
                    .permute(2, 0);
            i++;
        }
        return b
                //-> [array]
                .drop(0)
                .cast(methodType(Value.class, Value.class))
                .identity();
    }
    
    @Override
    public MethodHandle visitObjectLiteral(IrObjectLiteral node) {
        var b = base()
                //-> [object, state]
                .fold(Intrinsics.NEW_OBJECT)
                //-> [state, object]
                .permute(1, 0);
        for(var e : node.getEntries()) {
            var k = e.getKey().accept(this);
            var v = e.getValue().accept(this);
            b = b
                    //-> [key, state, object]
                    .fold(k)
                    //-> [state, object, key]
                    .permute(1, 2, 0)
                    //-> [value, state, object, key]
                    .fold(v)
                    //-> [object, key, value, state]
                    .permute(2, 3, 0, 1)
                    .foldVoid(Intrinsics.SET.asType(methodType(Value.class, ObjectValue.class, Value.class, Value.class)))
                    //-> [state, object]
                    .permute(3, 0);
        }
        return b
                //-> [object]
                .drop(0)
                .cast(methodType(Value.class, Value.class))
                .identity();
    }
    
    @Override
    public MethodHandle visitUnaryOperation(IrUnaryOperation node) {
        return base()
                //-> [target, state]
                .fold(node.getTarget().accept(this))
                //-> [target]
                .drop(1)
                //-> [result]
                .invoke(Intrinsics.UNARY_OPERATIONS.get(node.getType()));
    }
    
    @Override
    public MethodHandle visitBinaryOperation(IrBinaryOperation node) {
        //special case for short circuiting
        if(node.getType() == BinaryOperationType.AND || node.getType() == BinaryOperationType.OR) {
            var lhsAsBoolean = Binder.from(methodType(boolean.class, FunctionState.class))
                    .filterReturn(Intrinsics.BOOLEAN_VALUE)
                    .filterReturn(Intrinsics.AS_BOOLEAN)
                    .invoke(node.getLhs().accept(this));
            var rhsAsBoolean = Binder.from(methodType(Value.class, FunctionState.class))
                    .filterReturn(Intrinsics.AS_BOOLEAN)
                    .invoke(node.getRhs().accept(this));
            if(node.getType() == BinaryOperationType.AND) {
                return base().branch(
                        lhsAsBoolean,
                        rhsAsBoolean,
                        constant(BooleanValue.of(false))
                );
            } else {
                return base().branch(
                        lhsAsBoolean,
                        constant(BooleanValue.of(true)),
                        rhsAsBoolean
                );
            }
        }
        return base()
                //-> [lhs, state]
                .fold(node.getLhs().accept(this))
                //-> [state, lhs]
                .permute(1, 0)
                //-> [rhs, state, lhs]
                .fold(node.getRhs().accept(this))
                //-> [lhs, rhs]
                .permute(2, 0)
                //-> [result]
                .invoke(Intrinsics.BINARY_OPERATIONS.get(node.getType()));
    }
    
    @Override
    public MethodHandle visitRange(IrRange node) {
        if(node.getFrom() instanceof IrLong && node.getTo() instanceof IrLong) {
            return constant(RangeValue.of(
                    ((IrLong) node.getFrom()).getValue(), ((IrLong) node.getTo()).getValue()
            ));
        }
        return base()
                //-> [from, state]
                .fold(Binder.from(methodType(long.class, FunctionState.class))
                        //-> [long]
                        .filterReturn(Intrinsics.LONG_VALUE)
                        //-> [LongValue]
                        .filterReturn(Intrinsics.AS_LONG)
                        //-> [Value]
                        .invoke(node.getFrom().accept(this))
                )
                //-> [state, from]
                .permute(1, 0)
                //-> [to, state, from]
                .fold(Binder.from(methodType(long.class, FunctionState.class))
                        //-> [long]
                        .filterReturn(Intrinsics.LONG_VALUE)
                        //-> [LongValue]
                        .filterReturn(Intrinsics.AS_LONG)
                        //-> [Value]
                        .invoke(node.getTo().accept(this))
                )
                //-> [from, to]
                .permute(2, 0)
                .cast(methodType(Value.class, long.class, long.class))
                //-> [range]
                .invoke(Intrinsics.NEW_RANGE);
    }
    
    @Override
    public MethodHandle visitLocal(IrLocal node) {
        return base().insert(1, node.getIndex()).invoke(Intrinsics.GET_LOCAL);
    }
    
    @Override
    public MethodHandle visitUpvalue(IrUpvalue node) {
        return base().insert(1, node.getLevel(), node.getIndex()).invoke(Intrinsics.GET_UPVALUE);
    }
    
    @Override
    public MethodHandle visitGlobal(IrGlobal node) {
        return base().insert(1, node.getName()).invoke(Intrinsics.GET_GLOBAL);
    }
    
    @Override
    public MethodHandle visitAssign(IrAssign node) {
        var target = node.getTarget();
        var value = node.getValue().accept(this);
        if(target instanceof IrLocal) {
            return base()
                    //-> [value, state]
                    .fold(value)
                    //-> [index, value, state]
                    .insert(0, ((IrLocal) target).getIndex())
                    //-> [state, index, value]
                    .permute(2, 0, 1)
                    //-> [value]
                    .invoke(Intrinsics.SET_LOCAL);
        } else if(target instanceof IrUpvalue) {
            var up = (IrUpvalue)target;
            return base()
                    //-> [value, state]
                    .fold(value)
                    //-> [level, index, value, state]
                    .insert(0, up.getLevel(), up.getIndex())
                    //-> [state, level, index, value]
                    .permute(3, 0, 1, 2)
                    //-> [value]
                    .invoke(Intrinsics.SET_UPVALUE);
        } else if(target instanceof IrGlobal) {
            return base()
                    //-> [value, state]
                    .fold(value)
                    //-> [name, value, state]
                    .insert(0, ((IrGlobal) target).getName())
                    //-> [state, name, value]
                    .permute(2, 0, 1)
                    //-> [value]
                    .invoke(Intrinsics.SET_GLOBAL);
        } else if(target instanceof IrMember) {
            var m = (IrMember)target;
            var t = m.getTarget().accept(this);
            var k = m.getKey().accept(this);
            return base()
                    //-> [target, state]
                    .fold(t)
                    //-> [state, target]
                    .permute(1, 0)
                    //-> [key, state, target]
                    .fold(k)
                    //-> [state, target, key]
                    .permute(1, 2, 0)
                    //-> [value, state, target, key]
                    .fold(value)
                    //-> [target, key, value]
                    .permute(2, 3, 0)
                    //-> [value]
                    .invoke(Intrinsics.SET);
        } else {
            return doThrow(TypeError.class, "Can't assign to " + target);
        }
    }
    
    @Override
    public MethodHandle visitMember(IrMember node) {
        return base()
                //-> [target, state]
                .fold(node.getTarget().accept(this))
                //-> [state, target]
                .permute(1, 0)
                //-> [key, state, target]
                .fold(node.getKey().accept(this))
                //-> [target, key]
                .permute(2, 0)
                //-> [value]
                .invoke(Intrinsics.GET);
    }
    
    @Override
    public MethodHandle visitBody(IrBody node) {
        var b = base()
                //-> [nil, state]
                .fold(constant(NilValue.instance()));
        for(var c : node.getChildren()) {
            //-> [value of c, state]
            //    replaces last value
            b = b.drop(0).fold(c.accept(this));
        }
        return b
                //-> [value]
                .drop(1)
                .identity();
    }
    
    @Override
    public MethodHandle visitCall(IrCall node) {
        var function = node.getTarget().accept(this);
        var arguments = toJavaArray(node.getArguments());
    
        return Binder.from(BASE_TYPE)
                //-> [function, state]
                .fold(Binder.from(Function.class, FunctionState.class)
                        .insert(0, MethodHandles.filterReturnValue(
                                function,
                                Intrinsics.AS_FUNCTION
                        ))
                        .invoker()
                )
                //-> [state, function]
                .permute(1, 0)
                //-> [args, state, function]
                .fold(Binder.from(Value[].class, FunctionState.class)
                        .insert(0, arguments)
                        .invoker()
                )
                //-> [function, state, args]
                .permute(2, 1, 0)
                .cast(methodType(Value.class, Function.class, ExecutionContext.class, Value[].class))
                //-> [result]
                .invoke(Intrinsics.FUNCTION_CALL);
    }
    
    @Override
    public MethodHandle visitFunction(IrFunction node) {
        var code = Binder.from(methodType(Value.class, FunctionState.class, Value[].class))
                //-> [state, parent, args]
                .fold(Binder.from(FunctionState.class, FunctionState.class)
                        //-> [parent, locals count]
                        .append(node.getLocalsCount())
                        //-> [state, parent, locals count]
                        .fold(Intrinsics.FUNCTION_STATE_FROM_PARENT)
                        //-> [state]
                        .drop(1, 2)
                        .identity()
                )
                //-> [state, args]
                .drop(1)
                //-> [state, args]
                .foldVoid(MethodHandles.insertArguments(
                        Intrinsics.FILL_LOCALS_FROM_ARGS,
                        2, node.getArgumentCount(), node.isVarargs()
                ))
                //-> [state]
                .drop(1)
                .invoke(node.getBody().accept(this));
    
        List<StringValue> annotationList = new ArrayList<>();
        for(String annotation : node.getAnnotations()) {
            annotationList.add(StringValue.of(annotation));
        }
        
        return base()
                //-> [state, name]
                .append(String.class, node.getName()) //name may be null so getting the type fails
                //-> [state, name, annotations, code]
                .append(annotationList, code)
                //-> [name, annotations, code, state]
                .permute(1, 2, 3, 0)
                .cast(methodType(Value.class, String.class, List.class, MethodHandle.class, FunctionState.class))
                .invoke(Intrinsics.NEW_FUNCTION);
    }
    
    @Override
    public MethodHandle visitIf(IrIf node) {
        return MethodHandles.guardWithTest(
                makePredicate(node.getCondition().accept(this)),
                node.getIfBody().accept(this),
                node.getElseBody().accept(this)
        );
    }
    
    @Override
    public MethodHandle visitWhile(IrWhile node) {
        var cond = makePredicate(node.getCondition().accept(this));
        var body = node.getBody().accept(this);
        var elseBody = node.getElseBody().accept(this);
        return MethodHandles.guardWithTest(
                cond,
                MethodHandles.doWhileLoop(
                        constant(NilValue.instance()),
                        MethodHandles.dropArguments(body, 0, Value.class),
                        MethodHandles.dropArguments(cond, 0, Value.class)
                ),
                elseBody
        );
    }
    
    @Override
    public MethodHandle visitFor(IrFor node) {
        var value = node.getValue().accept(this);
        var body = node.getBody().accept(this);
        var elseBody = node.getElseBody().accept(this);
        return base()
                .fold(value)
                .invoke(
                        MethodHandles.guardWithTest(
                                Intrinsics.IS_RANGE,
                                //ranges are always closed on both ends so the else body never runs
                                forRange(node.getVariableIndex(), body),
                                MethodHandles.guardWithTest(
                                        Intrinsics.IS_ARRAY,
                                        forArray(node.getVariableIndex(), body, elseBody),
                                        Binder.from(methodType(Value.class, Value.class, FunctionState.class))
                                                //-> [type, value, state]
                                                .fold(Intrinsics.TYPE)
                                                //-> [message, value, state]
                                                .filter(
                                                        0,
                                                        MethodHandles.insertArguments(
                                                                Intrinsics.STRING_CONCAT,
                                                                0,
                                                                "Bad type as for target: "
                                                        )
                                                )
                                                //-> [message]
                                                .drop(1, 2)
                                                .filterReturn(MethodHandles.throwException(Value.class, TypeError.class))
                                                .invokeConstructorQuiet(Intrinsics.PUBLIC_LOOKUP, TypeError.class)
                                )
                        )
                );
    }
    
    @Override
    public MethodHandle visitReturn(IrReturn node) {
        return base()
                //-> [value, state]
                .fold(node.getValue().accept(this))
                //-> [return, value, state]
                .fold(Intrinsics.NEW_RETURN_EXCEPTION)
                //-> [return]
                .drop(1, 2)
                .invoke(MethodHandles.throwException(Value.class, ReturnException.class));
    }
    
    @Override
    public MethodHandle visitThrow(IrThrow node) {
        return base()
                //-> [value, state]
                .fold(node.getValue().accept(this))
                //-> [error, value, state]
                .fold(Intrinsics.NEW_THROWN_ERROR)
                //-> [error]
                .drop(1, 2)
                .invoke(MethodHandles.throwException(Value.class, ThrownError.class));
    }
    
    private static MethodHandle forRange(int localIdx, MethodHandle body) {
        var loop = MethodHandles.loop(
                new MethodHandle[] {
                        Binder.from(methodType(long.class, RangeValue.class, FunctionState.class))
                                //-> [range]
                                .drop(1)
                                //-> [to, range]
                                .fold(Intrinsics.RANGE_TO)
                                //-> [range, to]
                                .permute(1, 0)
                                //-> [from, range, to]
                                .fold(Intrinsics.RANGE_FROM)
                                //-> [from, to]
                                .drop(1)
                                //-> [end]
                                .invoke(Intrinsics.RANGE_ITERATION_END),
                        null,
                        Binder.from(methodType(boolean.class, long.class, Value.class, long.class, RangeValue.class, FunctionState.class))
                                //-> [i, end]
                                .permute(2, 0)
                                //-> [done]
                                .invoke(Intrinsics.LONGS_DIFFER),
                        Binder.from(methodType(Value.class, long.class, Value.class))
                                .drop(0)
                                .identity()
                },
                new MethodHandle[] {
                        Binder.from(methodType(Value.class, RangeValue.class, FunctionState.class))
                                .drop(0, 2)
                                .constant(NilValue.instance()),
                        Binder.from(methodType(Value.class, long.class, Value.class, long.class, RangeValue.class, FunctionState.class))
                                //-> [i, state]
                                .permute(2, 4)
                                .foldVoid(Binder.from(Value.class, long.class, FunctionState.class)
                                        //-> [long, i, state]
                                        .fold(Intrinsics.NEW_LONG)
                                        //-> [long, i, state, localIdx]
                                        .append(localIdx)
                                        //-> [state, idx, long]
                                        .permute(2, 3, 0)
                                        .cast(methodType(Value.class, FunctionState.class, int.class, Value.class))
                                        .invoke(Intrinsics.SET_LOCAL)
                                )
                                .drop(0)
                                //-> [value]
                                .invoke(body)
                },
                new MethodHandle[] {
                        Binder.from(methodType(long.class, RangeValue.class, FunctionState.class))
                                .drop(1)
                                .invoke(Intrinsics.RANGE_FROM),
                        Binder.from(methodType(long.class, long.class, Value.class, long.class, RangeValue.class, FunctionState.class))
                                //-> [i, end]
                                .permute(2, 0)
                                //-> [step, i, end]
                                .fold(Intrinsics.CALCULATE_STEP)
                                //-> [step, i]
                                .drop(2)
                                //-> [next i]
                                .invoke(Intrinsics.ADD_LONGS)
                }
        );
        
        return Binder.from(methodType(Value.class, Value.class, FunctionState.class))
                //-> [range, value, state]
                .fold(Intrinsics.AS_RANGE)
                //-> [range, state]
                .drop(1)
                //-> [result]
                .invoke(loop);
    }
    
    private static MethodHandle forArray(int localIdx, MethodHandle body, MethodHandle elseBody) {
        var loop = MethodHandles.countedLoop(
                Binder.from(methodType(int.class, long.class, ArrayValue.class, FunctionState.class))
                        //-> [size]
                        .drop(1, 2)
                        .cast(methodType(int.class, long.class))
                        .identity(),
                Binder.from(methodType(Value.class, long.class, ArrayValue.class, FunctionState.class))
                        .dropAll()
                        .constant(NilValue.instance()),
                Binder.from(methodType(Value.class, Value.class, int.class, long.class, ArrayValue.class, FunctionState.class))
                        //-> [array, index, state]
                        .permute(3, 1, 4)
                        //-> [value, array, index, state]
                        .fold(Intrinsics.ARRAY_RAW_GET)
                        //-> [state, value]
                        .permute(3, 0)
                        .foldVoid(Binder.from(methodType(Value.class, FunctionState.class, Value.class))
                                //-> [state, value, index]
                                .append(localIdx)
                                //-> [state, index, value]
                                .permute(0, 2, 1)
                                //-> [value]
                                .invoke(Intrinsics.SET_LOCAL)
                        )
                        //-> [state]
                        .drop(1)
                        //-> [value]
                        .invoke(body)
        );
        
        return Binder.from(methodType(Value.class, Value.class, FunctionState.class))
                //-> [array, value, state]
                .fold(Intrinsics.AS_ARRAY)
                //-> [value, array, state]
                .permute(1, 0, 2)
                //-> [size, value, array, state]
                .fold(Intrinsics.SIZE)
                //-> [size, array, state]
                .drop(1)
                //-> [result]
                .branch(
                        Binder.from(methodType(boolean.class, long.class, ArrayValue.class, FunctionState.class))
                                //-> [size]
                                .drop(1, 2)
                                //-> [size, 0]
                                .append(0L)
                                //-> [not empty]
                                .invoke(Intrinsics.LONGS_DIFFER),
                        loop,
                        Binder.from(methodType(Value.class, long.class, ArrayValue.class, FunctionState.class))
                                //-> [state]
                                .drop(0, 2)
                                //-> [result]
                                .invoke(elseBody)
                );
    }
    
    private MethodHandle toJavaArray(List<IrNode> values) {
        var b = base()
                //-> [array, state]
                .fold(MethodHandles.insertArguments(
                        Intrinsics.NEW_VALUE_JAVA_ARRAY,
                        0, values.size()
                ))
                //-> [state, array]
                .permute(1, 0);
        var i = 0;
        for(var e : values) {
            var v = e.accept(this);
            b = b
                    //-> [value, state, array]
                    .fold(v)
                    //-> [array, value, state]
                    .permute(2, 0, 1)
                    .foldVoid(
                            Binder.from(void.class, Value[].class, Value.class)
                                    //-> [array, value, index]
                                    .append(i)
                                    //-> [array, index, value]
                                    .permute(0, 2, 1)
                                    .invoke(Intrinsics.VALUE_JAVA_ARRAY_SETTER)
                    )
                    //-> [state, array]
                    .permute(2, 0);
            i++;
        }
        return b
                //-> [array]
                .drop(0)
                .cast(methodType(Value[].class, Value[].class))
                .identity();
    }
    
    private static MethodHandle constant(Value value) {
        return base().drop(0).invoke(MethodHandles.constant(Value.class, value));
    }
    
    private static Binder base() {
        return Binder.from(BASE_TYPE);
    }
    
    private static MethodHandle makePredicate(MethodHandle target) {
        return Binder.from(methodType(boolean.class, FunctionState.class))
                //-> [value, state]
                .fold(target)
                //-> [boolean value, value, state]
                .fold(Intrinsics.AS_BOOLEAN)
                //-> [boolean, boolean value, value, state]
                .fold(Intrinsics.BOOLEAN_VALUE)
                .drop(1, 3)
                .identity();
    }
    
    private static MethodHandle doThrow(Class<? extends Throwable> exception, String message) {
        return base()
                //-> []
                .drop(0)
                //-> [message]
                .insert(0, message)
                //throws
                .filterReturn(MethodHandles.throwException(Value.class, exception))
                //-> [exception]
                .invokeConstructorQuiet(Intrinsics.PUBLIC_LOOKUP, exception);
    }
}
