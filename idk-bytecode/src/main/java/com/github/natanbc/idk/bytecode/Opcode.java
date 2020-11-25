package com.github.natanbc.idk.bytecode;

import com.github.natanbc.idk.bytecode.util.Utils;
import com.github.natanbc.idk.common.BinaryOperationType;
import com.github.natanbc.idk.common.UnaryOperationType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Opcode {
    //... -> ..., nil
    CONSTANT_NIL(0),
    //... -> ..., <value: boolean>
    CONSTANT_BOOLEAN(1, ArgumentType.BOOLEAN /* value */),
    //... -> ..., <value: long>
    CONSTANT_LONG(2, ArgumentType.LONG /* value */),
    //... -> ..., <value: double>
    CONSTANT_DOUBLE(3, ArgumentType.DOUBLE /* value */),
    //... -> ..., <value: string>
    CONSTANT_STRING(4, ArgumentType.STRING /* value */),
    //..., <count elements: any> -> ..., <value: array>
    CREATE_ARRAY(5, ArgumentType.SHORT /* count */),
    //..., <count * 2 kv pairs: any> -> ..., <value: object>
    CREATE_OBJECT(6, ArgumentType.SHORT /* count */),
    //..., <from: long>, <to: long> -> ..., <value: range>
    CREATE_RANGE(7),
    //... -> ..., <value: any>
    LOAD_LOCAL(8, ArgumentType.SHORT /* idx */),
    //..., <value: any> -> ...
    STORE_LOCAL(9, ArgumentType.SHORT /* idx */),
    //... -> ..., <value: any>
    LOAD_UPVALUE(10, ArgumentType.SHORT /* level */, ArgumentType.SHORT /* idx */),
    //..., <value: any> -> ...
    STORE_UPVALUE(11, ArgumentType.SHORT /* level */, ArgumentType.SHORT /* idx */),
    //... -> ..., <value: any>
    LOAD_GLOBAL(12, ArgumentType.STRING /* name */),
    //..., <value: any> -> ...
    STORE_GLOBAL(13, ArgumentType.STRING /* name */),
    //..., <target: object|array>, <key: any/long> -> ..., <value: any>
    LOAD_MEMBER(14),
    //..., <target: object|array>, <key: any/long>, <value: any> -> ...
    STORE_MEMBER(15),
    //..., <target: function>, <arg count values: any> -> ..., <return value: any>
    CALL(16, ArgumentType.SHORT /* arg count */),
    //..., <return value: any> -> ...
    RETURN(17),
    //..., <lhs: any>, <rhs: any> -> ..., <res: any>
    BINARY_OPERATION(18, ArgumentType.BINARY_OPERATION /* type */),
    //..., <val: any> -> ..., <res: any>
    UNARY_OPERATION(19, ArgumentType.UNARY_OPERATION /* type */),
    //... -> ...
    JUMP(20, ArgumentType.SHORT /* position */),
    //..., <test value: boolean> -> ...
    JUMP_IF(21, ArgumentType.CONDITION_TYPE /* condition */, ArgumentType.SHORT /* position */),
    //... -> ..., <value: function>
    LOAD_FUNCTION(22, ArgumentType.SHORT /* function number */),
    //..., <val: any> -> ...
    POP(23),
    //..., <val: any> -> ..., <val: any>, <val: any>
    DUP(24),
    //..., <error: any> -> ...
    THROW(25),
    //..., <value: any> -> ..., <is type: boolean>
    TEST_TYPE(26, ArgumentType.VALUE_TYPE),
    //..., <value: array|object> -> ..., <size: long>
    SIZE(27),
    //..., <v1: any>, <v2: any> -> ..., <v2>, <v1>
    SWAP2(28);
    
    private static final Map<Integer, Opcode> MAP = Arrays.stream(values()).collect(
            Collectors.toUnmodifiableMap(
                    op -> op.value & 0xFF,
                    Function.identity(),
                    (a, b) -> { throw new IllegalStateException(
                            String.format("Duplicate value (%d) between %s and %s",
                                    a.value & 0xFF, a, b)
                    ); }
            )
    );
    public final byte value;
    public final List<ArgumentType> argumentTypes;
    public final int argumentBytes;
    
    Opcode(int value, ArgumentType... args) {
        Utils.validateU8(value, "Value cannot be encoded in a single byte");
        this.value = (byte)value;
        this.argumentTypes = List.of(args);
        this.argumentBytes = argumentTypes.stream().mapToInt(t -> t.byteSize).sum();
    }
    
    public static Opcode fromValue(byte value) {
        var v = MAP.get(value & 0xFF);
        if(v == null) {
            throw new IllegalStateException("Unknown operation 0x" + Integer.toHexString(value & 0xFF));
        }
        return v;
    }
    
    public enum ArgumentType {
        BOOLEAN(1, false) {
            @Override
            public void tryWrite(FunctionWriter output, Object value) {
                Utils.checkedWrite(value, Boolean.class, output::loadConstant);
            }
    
            @Override
            public Object tryRead(FunctionReader reader) {
                return reader.reader().bool();
            }
        },
        BYTE(1, false) {
            @Override
            public void tryWrite(FunctionWriter output, Object value) {
                Utils.checkedWrite(value, Byte.class, b -> output.writer().u8(b));
            }
    
            @Override
            public Object tryRead(FunctionReader reader) {
                return reader.reader().u8();
            }
        },
        SHORT(2, false) {
            @Override
            public void tryWrite(FunctionWriter output, Object value) {
                Utils.checkedWrite(value, Short.class, b -> output.writer().u16(b));
            }
    
            @Override
            public Object tryRead(FunctionReader reader) {
                return reader.reader().u16();
            }
        },
        LONG(2, true) {
            @Override
            public void tryWrite(FunctionWriter output, Object value) {
                Utils.checkedWrite(value, Long.class, output::loadConstant);
            }
    
            @Override
            public Object tryRead(FunctionReader reader) {
                return reader.owner().constantLong(reader.reader().u16());
            }
        },
        DOUBLE(2, true) {
            @Override
            public void tryWrite(FunctionWriter output, Object value) {
                Utils.checkedWrite(value, Double.class, output::loadConstant);
            }
    
            @Override
            public Object tryRead(FunctionReader reader) {
                return reader.owner().constantDouble(reader.reader().u16());
            }
        },
        STRING(2, true) {
            @Override
            public void tryWrite(FunctionWriter output, Object value) {
                Utils.checkedWrite(value, String.class, output::loadConstant);
            }
    
            @Override
            public Object tryRead(FunctionReader reader) {
                return reader.owner().constantString(reader.reader().u16());
            }
        },
        BINARY_OPERATION(1, false) {
            @Override
            public void tryWrite(FunctionWriter output, Object value) {
                Utils.checkedWrite(value, BinaryOperationType.class, op -> output.writer().u8(
                        BytecodeConstants.binaryOpNumber(op)
                ));
            }
        
            @Override
            public Object tryRead(FunctionReader reader) {
                return BytecodeConstants.binaryOp(reader.reader().u8());
            }
        },
        UNARY_OPERATION(1, false) {
            @Override
            public void tryWrite(FunctionWriter output, Object value) {
                Utils.checkedWrite(value, UnaryOperationType.class, op -> output.writer().u8(
                        BytecodeConstants.unaryOpNumber(op)
                ));
            }
        
            @Override
            public Object tryRead(FunctionReader reader) {
                return BytecodeConstants.unaryOp(reader.reader().u8());
            }
        },
        CONDITION_TYPE(1, false) {
            @Override
            public void tryWrite(FunctionWriter output, Object value) {
                Utils.checkedWrite(value, ConditionType.class, type -> output.writer().u8(
                        type.value
                ));
            }
        
            @Override
            public Object tryRead(FunctionReader reader) {
                return ConditionType.type(reader.reader().u8());
            }
        },
        VALUE_TYPE(1, false) {
            @Override
            public void tryWrite(FunctionWriter output, Object value) {
                Utils.checkedWrite(value, ValueType.class, type -> output.writer().u8(
                        type.value
                ));
            }
    
            @Override
            public Object tryRead(FunctionReader reader) {
                return ValueType.type(reader.reader().u8());
            }
        };
        
        public final int byteSize;
        public final boolean isConstantPool;
    
        ArgumentType(int byteSize, boolean isConstantPool) {
            this.byteSize = byteSize;
            this.isConstantPool = isConstantPool;
        }
        
        public abstract void tryWrite(FunctionWriter output, Object value);
        public abstract Object tryRead(FunctionReader reader);
    }
}
