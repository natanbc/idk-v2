package com.github.natanbc.idk.compiler;

import com.github.natanbc.idk.common.BinaryOperationType;
import com.github.natanbc.idk.common.UnaryOperationType;
import com.github.natanbc.idk.runtime.*;
import com.github.natanbc.idk.runtime.internal.FunctionState;
import com.github.natanbc.idk.runtime.internal.ReturnException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.Map;

import static java.lang.invoke.MethodType.methodType;

public class Intrinsics {
    public static final MethodHandles.Lookup PUBLIC_LOOKUP = MethodHandles.publicLookup();
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    
    public static final MethodHandle GET_GLOBALS_MAP = find(ExecutionContext.class, "getGlobals", methodType(Map.class));
    
    //not public because FunctionState is an internal class
    static final MethodHandle FUNCTION_STATE_FROM_MAP = findFunctionStateConstructor(methodType(Map.class, int.class));
    static final MethodHandle FUNCTION_STATE_FROM_PARENT = findFunctionStateConstructor(methodType(FunctionState.class, int.class));
    static final MethodHandle FILL_LOCALS_FROM_ARGS = find(FunctionState.class, "fillFromArgs", methodType(void.class, Value[].class, int.class, boolean.class));
    
    static final MethodHandle GET_LOCAL = find(FunctionState.class, "getLocal", methodType(Value.class, int.class));
    static final MethodHandle SET_LOCAL = find(FunctionState.class, "setLocal", methodType(Value.class, int.class, Value.class));
    
    static final MethodHandle GET_UPVALUE = find(FunctionState.class, "getUpvalue", methodType(Value.class, int.class, int.class));
    static final MethodHandle SET_UPVALUE = find(FunctionState.class, "setUpvalue", methodType(Value.class, int.class, int.class, Value.class));
    
    static final MethodHandle GET_GLOBAL = find(FunctionState.class, "getGlobal", methodType(Value.class, String.class));
    static final MethodHandle SET_GLOBAL = find(FunctionState.class, "setGlobal", methodType(Value.class, String.class, Value.class));
    
    static final MethodHandle NEW_RETURN_EXCEPTION = findConstructor(ReturnException.class, methodType(Value.class));
    static final MethodHandle GET_RETURN_FROM_EXCEPTION = find(ReturnException.class, "getValue", methodType(Value.class));
    
    static final MethodHandle NEW_FUNCTION = findConstructor(FunctionImpl.class, methodType(String.class, List.class, MethodHandle.class, FunctionState.class));
    
    public static final MethodHandle TYPE = valueMethod("type", methodType(String.class));
    
    public static final MethodHandle NEG = unaryOperator("neg");
    public static final MethodHandle NEGATE = unaryOperator("negate");
    
    public static final MethodHandle ADD = binaryOperator("add");
    public static final MethodHandle SUB = binaryOperator("sub");
    public static final MethodHandle MUL = binaryOperator("mul");
    public static final MethodHandle DIV = binaryOperator("div");
    public static final MethodHandle MOD = binaryOperator("mod");
    public static final MethodHandle POW = binaryOperator("pow");
    public static final MethodHandle EQ = binaryOperator("eq");
    public static final MethodHandle NEQ = binaryOperator("neq");
    public static final MethodHandle GREATER = binaryOperator("greater");
    public static final MethodHandle GREATER_EQ = binaryOperator("greaterEq");
    public static final MethodHandle SMALLER = binaryOperator("smaller");
    public static final MethodHandle SMALLER_EQ = binaryOperator("smallerEq");
    
    public static final MethodHandle GET = valueMethod("get", methodType(Value.class, Value.class));
    public static final MethodHandle SET = valueMethod("set", methodType(Value.class, Value.class, Value.class));
    public static final MethodHandle SIZE = valueMethod("size", methodType(long.class));
    public static final MethodHandle KEYS = valueMethod("keys", methodType(ArrayValue.class));
    
    public static final MethodHandle IS_ARRAY = typeCheck("Array");
    public static final MethodHandle AS_ARRAY = typeConversion(ArrayValue.class, "Array");
    public static final MethodHandle NEW_ARRAY_FROM_VALUES = findConstructor(ArrayValue.class, methodType(Value[].class));
    public static final MethodHandle NEW_EMPTY_ARRAY = findConstructor(ArrayValue.class, methodType(void.class));
    public static final MethodHandle ARRAY_RAW_GET = find(ArrayValue.class, "rawGet", methodType(Value.class, int.class));
    public static final MethodHandle ARRAY_RAW_SET = find(ArrayValue.class, "rawSet", methodType(Value.class, int.class, Value.class));
    
    public static final MethodHandle IS_BOOLEAN = typeCheck("Boolean");
    public static final MethodHandle AS_BOOLEAN = typeConversion(BooleanValue.class, "Boolean");
    public static final MethodHandle BOOLEAN_VALUE = valueGetter(BooleanValue.class, boolean.class);
    
    public static final MethodHandle IS_DOUBLE = typeCheck("Double");
    public static final MethodHandle AS_DOUBLE = typeConversion(DoubleValue.class, "Double");
    public static final MethodHandle DOUBLE_VALUE = valueGetter(DoubleValue.class, double.class);
    
    public static final MethodHandle IS_FUNCTION = typeCheck("Function");
    public static final MethodHandle AS_FUNCTION = typeConversion(Function.class, "Function");
    public static final MethodHandle FUNCTION_CALL = find(Function.class, "call", methodType(Value.class, ExecutionContext.class, Value[].class));
    
    public static final MethodHandle IS_LONG = typeCheck("Long");
    public static final MethodHandle AS_LONG = typeConversion(LongValue.class, "Long");
    public static final MethodHandle LONG_VALUE = valueGetter(LongValue.class, long.class);
    public static final MethodHandle NEW_LONG = findConstructor(LongValue.class, methodType(long.class));
    
    public static final MethodHandle IS_NIL = typeCheck("Nil");
    public static final MethodHandle AS_NIL = typeConversion(NilValue.class, "Nil");
    
    public static final MethodHandle IS_OBJECT = typeCheck("Object");
    public static final MethodHandle AS_OBJECT = typeConversion(ObjectValue.class, "Object");
    public static final MethodHandle NEW_OBJECT = findConstructor(ObjectValue.class, methodType(void.class));
    
    public static final MethodHandle IS_RANGE = typeCheck("Range");
    public static final MethodHandle AS_RANGE = typeConversion(RangeValue.class, "Range");
    public static final MethodHandle RANGE_FROM = find(RangeValue.class, "getFrom", methodType(long.class));
    public static final MethodHandle RANGE_TO = find(RangeValue.class, "getTo", methodType(long.class));
    public static final MethodHandle NEW_RANGE = findConstructor(RangeValue.class, methodType(long.class, long.class));
    
    public static final MethodHandle IS_STRING = typeCheck("String");
    public static final MethodHandle AS_STRING = typeConversion(StringValue.class, "String");
    public static final MethodHandle STRING_VALUE = valueGetter(StringValue.class, String.class);
    
    public static final MethodHandle NEW_THROWN_ERROR = findConstructor(ThrownError.class, methodType(Value.class));
    
    public static final MethodHandle CALCULATE_STEP = findStatic(Intrinsics.class, "step", methodType(long.class, long.class, long.class));
    public static final MethodHandle RANGE_ITERATION_DONE = findStatic(Intrinsics.class, "done", methodType(boolean.class, long.class, long.class));
    public static final MethodHandle RANGE_ITERATION_END = findStatic(Intrinsics.class, "end", methodType(long.class, long.class, long.class));
    public static final MethodHandle ADD_LONGS = findStatic(Intrinsics.class, "add", methodType(long.class, long.class, long.class));
    
    public static final MethodHandle STRING_CONCAT = find(String.class, "concat", methodType(String.class, String.class));
    
    public static final MethodHandle NEW_VALUE_JAVA_ARRAY = MethodHandles.arrayConstructor(Value[].class);
    public static final MethodHandle VALUE_JAVA_ARRAY_SETTER = MethodHandles.arrayElementSetter(Value[].class);
    
    public static final Map<UnaryOperationType, MethodHandle> UNARY_OPERATIONS = Map.of(
            UnaryOperationType.NEG, NEG,
            UnaryOperationType.NEGATE, NEGATE
    );
    
    public static final Map<BinaryOperationType, MethodHandle> BINARY_OPERATIONS = Map.ofEntries(
            Map.entry(BinaryOperationType.ADD, ADD),
            Map.entry(BinaryOperationType.SUB, SUB),
            Map.entry(BinaryOperationType.MUL, MUL),
            Map.entry(BinaryOperationType.DIV, DIV),
            Map.entry(BinaryOperationType.MOD, MOD),
            Map.entry(BinaryOperationType.POW, POW),
            Map.entry(BinaryOperationType.EQ, EQ),
            Map.entry(BinaryOperationType.NEQ, NEQ),
            Map.entry(BinaryOperationType.GREATER, GREATER),
            Map.entry(BinaryOperationType.GREATER_EQ, GREATER_EQ),
            Map.entry(BinaryOperationType.SMALLER, SMALLER),
            Map.entry(BinaryOperationType.SMALLER_EQ, SMALLER_EQ)
    );
    
    private static MethodHandle unaryOperator(String name) {
        return valueMethod(name, methodType(Value.class));
    }
    
    private static MethodHandle binaryOperator(String name) {
        return valueMethod(name, methodType(Value.class, Value.class));
    }
    
    private static MethodHandle valueGetter(Class<?> owner, Class<?> type) {
        return find(owner,"getValue", methodType(type));
    }
    
    private static MethodHandle typeCheck(String name) {
        return valueMethod("is" + name, methodType(boolean.class));
    }
    
    private static MethodHandle typeConversion(Class<? extends Value> resultType, String name) {
        return valueMethod("as" + name, methodType(resultType));
    }
    
    private static MethodHandle valueMethod(String name, MethodType type) {
        return find(Value.class, name, type);
    }
    
    private static MethodHandle find(Class<?> owner, String name, MethodType type) {
        try {
            return LOOKUP.findVirtual(owner, name, type);
        } catch(Exception e) {
            throw new AssertionError(e);
        }
    }
    
    private static MethodHandle findStatic(Class<?> owner, String name, MethodType type) {
        try {
            return LOOKUP.findStatic(owner, name, type);
        } catch(Exception e) {
            throw new AssertionError(e);
        }
    }
    
    private static MethodHandle findFunctionStateConstructor(MethodType type) {
        return findConstructor(FunctionState.class, type);
    }
    
    private static MethodHandle findConstructor(Class<?> owner, MethodType type) {
        if(type.returnType() != void.class) {
            type = type
                    .insertParameterTypes(0, type.returnType())
                    .changeReturnType(void.class);
        }
        try {
            return LOOKUP.findConstructor(owner, type);
        } catch(Exception e) {
            throw new AssertionError(e);
        }
    }
    
    private static long end(long from, long to) {
        if(from > to) {
            return to - 1;
        } else {
            return to + 1;
        }
    }
    
    private static long step(long from, long to) {
        return from > to ? -1 : +1;
    }
    
    private static boolean done(long i, long to) {
        return i != to;
    }
    
    private static long add(long a, long b) {
        return a + b;
    }
}
