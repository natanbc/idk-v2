package com.github.natanbc.idk.interpreter;

import com.github.natanbc.idk.bytecode.BytecodeConstants;
import com.github.natanbc.idk.bytecode.BytecodeReader;
import com.github.natanbc.idk.bytecode.ConditionType;
import com.github.natanbc.idk.bytecode.FunctionReader;
import com.github.natanbc.idk.bytecode.Opcode;
import com.github.natanbc.idk.bytecode.ValueType;
import com.github.natanbc.idk.common.BinaryOperationType;
import com.github.natanbc.idk.common.UnaryOperationType;
import com.github.natanbc.idk.runtime.ArrayValue;
import com.github.natanbc.idk.runtime.BooleanValue;
import com.github.natanbc.idk.runtime.DoubleValue;
import com.github.natanbc.idk.runtime.ExecutionContext;
import com.github.natanbc.idk.runtime.LongValue;
import com.github.natanbc.idk.runtime.NilValue;
import com.github.natanbc.idk.runtime.ObjectValue;
import com.github.natanbc.idk.runtime.RangeValue;
import com.github.natanbc.idk.runtime.StringValue;
import com.github.natanbc.idk.runtime.Value;
import com.github.natanbc.idk.runtime.internal.FunctionState;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class BytecodeInterpreter {
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
    
    private final BytecodeReader reader;
    private final Map<String, Value> globals;
    private final Function[] functions;
    private final Function entrypoint;
    
    public BytecodeInterpreter(byte[] code, Map<String, Value> globals) {
        this.reader = new BytecodeReader(code);
        this.globals = globals;
        this.functions = new Function[reader.functionCount()];
        for(var i = 0; i < functions.length; i++) {
            var fn = new Function(reader.readFunction());
            functions[fn.metadata.id() & 0xFFFF] = fn;
        }
        this.entrypoint = functions[reader.entrypoint() & 0xFFFF];
    }
    
    public Value run() {
        var f = new FunctionInterpreter(this, entrypoint, new FunctionState(globals, entrypoint.metadata.localsCount()));
        while(!f.isDone()) {
            f.step();
        }
        if(f.stack.isEmpty()) {
            return NilValue.instance();
        } else {
            return f.stack.pop();
        }
    }
    
    private static class FunctionInterpreter {
        private final ArrayDeque<Value> stack = new ArrayDeque<>();
        private final BytecodeInterpreter interpreter;
        private final Function function;
        private final FunctionState state;
        private int ip = 0;
        
        FunctionInterpreter(BytecodeInterpreter interpreter, Function function, FunctionState parent) {
            this.interpreter = interpreter;
            this.function = function;
            this.state = new FunctionState(parent, function.metadata.localsCount());
        }
        
        boolean isDone() {
            return ip >= function.code.capacity();
        }
        
        void step() {
            if(isDone()) {
                throw new IllegalStateException("Out of bounds execution");
            }
            var startIP = ip;
            var op = Opcode.fromValue(u8());
            switch(op) {
                case CONSTANT_NIL -> stack.push(NilValue.instance());
                case CONSTANT_BOOLEAN -> stack.push(BooleanValue.of(u8() != 0));
                case CONSTANT_LONG -> stack.push(LongValue.of(
                        interpreter.reader.constantLong(s16())
                ));
                case CONSTANT_DOUBLE -> stack.push(DoubleValue.of(
                        interpreter.reader.constantDouble(s16())
                ));
                case CONSTANT_STRING -> stack.push(StringValue.of(
                        interpreter.reader.constantString(s16())
                ));
                case CREATE_ARRAY -> {
                    var arr = new Value[u16()];
                    for(var idx = arr.length - 1; idx >= 0; idx--) {
                        arr[idx] = stack.pop();
                    }
                    stack.push(new ArrayValue(arr));
                }
                case CREATE_OBJECT -> {
                    var obj = new ObjectValue();
                    var n = u16();
                    for(var i = 0; i < n; i++) {
                        var v = stack.pop();
                        var k = stack.pop();
                        obj.set(k, v);
                    }
                    stack.push(obj);
                }
                case CREATE_RANGE -> {
                    var to = stack.pop();
                    var from = stack.pop();
                    stack.push(RangeValue.of(from.asLong().getValue(), to.asLong().getValue()));
                }
                case LOAD_LOCAL -> stack.push(state.getLocal(u16()));
                case STORE_LOCAL -> state.setLocal(u16(), stack.pop());
                case LOAD_UPVALUE -> stack.push(state.getUpvalue(u16(), u16()));
                case STORE_UPVALUE -> state.setUpvalue(u16(), u16(), stack.pop());
                case LOAD_GLOBAL -> stack.push(state.getGlobal(
                        interpreter.reader.constantString(s16())
                ));
                case STORE_GLOBAL -> state.setGlobal(
                        interpreter.reader.constantString(s16()),
                        stack.pop()
                );
                case LOAD_MEMBER -> {
                    var k = stack.pop();
                    var m = stack.pop();
                    stack.push(m.get(k));
                }
                case STORE_MEMBER -> {
                    var v = stack.pop();
                    var k = stack.pop();
                    var m = stack.pop();
                    m.set(k, v);
                }
                case CALL -> {
                    var argc = u16();
                    var args = new Value[argc];
                    for(var i = argc - 1; i >= 0; i--) {
                        args[i] = stack.pop();
                    }
                    stack.push(stack.pop().asFunction().call(state, args));
                }
                case RETURN -> ip = function.code.capacity();
                case BINARY_OPERATION -> {
                    /* short circuiting operators are handled by the compiler */
                    var type = BytecodeConstants.binaryOp(u8());
                    var rhs = stack.pop();
                    var lhs = stack.pop();
                    var operator = BINARY_OPERATORS.get(type);
                    if(operator != null) {
                        stack.push(operator.apply(lhs, rhs));
                        return;
                    }
                    throw new UnsupportedOperationException("Unimplemented operator " + type);
                }
                case UNARY_OPERATION -> {
                    var type = BytecodeConstants.unaryOp(u8());
                    var v = stack.pop();
                    var operator = UNARY_OPERATORS.get(type);
                    if(operator != null) {
                        stack.push(operator.apply(v));
                        return;
                    }
                    throw new UnsupportedOperationException("Unimplemented operator " + type);
                }
                case JUMP -> ip = u16();
                case JUMP_IF -> {
                    var cond = ConditionType.type(u8());
                    var pos = u16();
                    var v = stack.pop().asBoolean().getValue();
                    if(v == (cond == ConditionType.IF_TRUE)) {
                        ip = pos;
                    }
                }
                case LOAD_FUNCTION -> {
                    var m = interpreter.functions[u16()];
                    var annotations = m.metadata.annotations()
                            .stream().map(StringValue::of).collect(Collectors.toList());
                    var fn = new com.github.natanbc.idk.runtime.Function(m.metadata.name(), annotations) {
                        @Override
                        public Value call(ExecutionContext context, Value[] args) {
                            var fnInt = new FunctionInterpreter(interpreter, m, state);
                            fnInt.state.fillFromArgs(args, m.metadata.argumentCount(), m.metadata.varargs());
                            while(!fnInt.isDone()) {
                                fnInt.step();
                            }
                            if(fnInt.stack.isEmpty()) {
                                return NilValue.instance();
                            } else {
                                return fnInt.stack.pop();
                            }
                        }
                    };
                    stack.push(fn);
                }
                case POP -> stack.pop();
                case DUP -> stack.push(stack.peek());
                case THROW -> throw new UnsupportedOperationException("throw");
                case TEST_TYPE -> {
                    var t = ValueType.type(u8());
                    var v = stack.pop();
                    stack.push(BooleanValue.of(switch(t) {
                        case NIL -> v.isNil();
                        case BOOLEAN -> v.isBoolean();
                        case LONG -> v.isLong();
                        case DOUBLE -> v.isDouble();
                        case STRING -> v.isString();
                        case ARRAY -> v.isArray();
                        case OBJECT -> v.isObject();
                        case RANGE -> v.isRange();
                    }));
                }
                case SIZE -> stack.push(LongValue.of(stack.pop().size()));
                case SWAP2 -> {
                    var v1 = stack.pop();
                    var v2 = stack.pop();
                    stack.push(v1);
                    stack.push(v2);
                }
            }
        }
    
        private byte u8() {
            return function.code.get(advanceIP(1));
        }
    
        private short s16() {
            return function.code.getShort(advanceIP(2));
        }
    
        private int u16() {
            return s16() & 0xFFFF;
        }
        
        private int advanceIP(int n) {
            int i = ip;
            ip += n;
            return i;
        }
    }
    
    private static class Function {
        private final FunctionReader metadata;
        private final ByteBuffer code;
        
        Function(FunctionReader reader) {
            this.metadata = reader;
            var r = reader.reader();
            this.code = ByteBuffer.allocate(r.limit());
            for(var i = 0; i < code.capacity(); i++) {
                code.put(i, r.u8());
            }
        }
    }
}
