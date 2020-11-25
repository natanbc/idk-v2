package com.github.natanbc.idk.bytecode;

import com.github.natanbc.idk.bytecode.util.ByteWriter;
import com.github.natanbc.idk.common.BinaryOperationType;
import com.github.natanbc.idk.common.UnaryOperationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionWriter {
    private final ByteWriter writer = new ByteWriter();
    private final Map<Label, List<Integer>> labelFixups = new HashMap<>();
    
    private final BytecodeWriter owner;
    private final short id;
    private final int localsCountOffset;
    private final int codeStart;
    private int localsCount;
    
    FunctionWriter(BytecodeWriter owner, short id, String name, int argumentCount,
                          int localsCount, boolean varargs, List<String> annotations) {
        this.owner = owner;
        this.id = id;
        writer.u16(id);
        writer.bool(name != null);
        if(name != null) {
            writer.u16(owner.constant(name));
        }
        writer.u16(argumentCount);
        localsCountOffset = writer.size();
        this.localsCount = localsCount;
        writer.u16(localsCount);
        writer.bool(varargs);
        writer.u16(annotations.size());
        for(var annotation : annotations) {
            writer.u16(owner.constant(annotation));
        }
        codeStart = writer.size();
    }
    
    public int newLocal() {
        int n = localsCount;
        writer.patchU16(localsCountOffset, n + 1);
        localsCount++;
        return n;
    }
    
    public short id() {
        return id;
    }
    
    public void loadNil() {
        writer.u8(Opcode.CONSTANT_NIL.value);
    }
    
    public void loadConstant(boolean b) {
        writer.u8(Opcode.CONSTANT_BOOLEAN.value);
        writer.bool(b);
    }
    
    public void loadConstant(long l) {
        op_u16(Opcode.CONSTANT_LONG, owner.constant(l));
    }
    
    public void loadConstant(double d) {
        op_u16(Opcode.CONSTANT_DOUBLE, owner.constant(d));
    }
    
    public void loadConstant(String s) {
        op_u16(Opcode.CONSTANT_STRING, owner.constant(s));
    }
    
    public void arrayLiteral(int size) {
        op_u16(Opcode.CREATE_ARRAY, size);
    }
    
    public void objectLiteral(int size) {
        op_u16(Opcode.CREATE_OBJECT, size);
    }
    
    public void createRange() {
        op(Opcode.CREATE_RANGE);
    }
    
    public void loadLocal(int idx) {
        op_u16(Opcode.LOAD_LOCAL, idx);
    }
    
    public void storeLocal(int idx) {
        op_u16(Opcode.STORE_LOCAL, idx);
    }
    
    public void loadUpvalue(int level, int idx) {
        op_u16_u16(Opcode.LOAD_UPVALUE, level, idx);
    }
    
    public void storeUpvalue(int level, int idx) {
        op_u16_u16(Opcode.STORE_UPVALUE, level, idx);
    }
    
    public void loadGlobal(String name) {
        op_u16(Opcode.LOAD_GLOBAL, owner.constant(name));
    }
    
    public void storeGlobal(String name) {
        op_u16(Opcode.STORE_GLOBAL, owner.constant(name));
    }
    
    public void loadMember() {
        op(Opcode.LOAD_MEMBER);
    }
    
    public void storeMember() {
        op(Opcode.STORE_MEMBER);
    }
    
    public void call(int argumentCount) {
        op_u16(Opcode.CALL, argumentCount);
    }
    
    public void ret() {
        op(Opcode.RETURN);
    }
    
    public void binaryOperation(BinaryOperationType type) {
        op_u8(Opcode.BINARY_OPERATION, BytecodeConstants.binaryOpNumber(type));
    }
    
    public void unaryOperation(UnaryOperationType type) {
        op_u8(Opcode.UNARY_OPERATION, BytecodeConstants.unaryOpNumber(type));
    }
    
    public void jump(Label target) {
        op(Opcode.JUMP);
        if(target.bound) {
            writer.u16(target.position);
        } else {
            labelFixups.computeIfAbsent(target, __ -> new ArrayList<>(1)).add(writer.size());
            writer.u16(0xFFFF);
        }
    }
    
    public void jumpIf(ConditionType type, Label target) {
        op(Opcode.JUMP_IF);
        writer.u8(type.value);
        if(target.bound) {
            writer.u16(target.position);
        } else {
            labelFixups.computeIfAbsent(target, __ -> new ArrayList<>(1)).add(writer.size());
            writer.u16(0xFFFF);
        }
    }
    
    public void loadFunction(short id) {
        op_u16(Opcode.LOAD_FUNCTION, id & 0xFFFF);
    }
    
    public void pop() {
        op(Opcode.POP);
    }
    
    public void dup() {
        op(Opcode.DUP);
    }
    
    public void exthrow() {
        op(Opcode.THROW);
    }
    
    public void testType(ValueType type) {
        op_u8(Opcode.TEST_TYPE, type.value);
    }
    
    public void size() {
        op(Opcode.SIZE);
    }
    
    public void swap2() {
        op(Opcode.SWAP2);
    }
    
    public void bind(Label label) {
        if(label.bound) {
            throw new IllegalArgumentException("Label already bound");
        }
        label.bound = true;
        label.position = writer.size() - codeStart;
    }
    
    public void instruction(Opcode opcode, Object... values) {
        writer.u8(opcode.value);
        if(opcode.argumentTypes.size() != values.length) {
            throw new IllegalArgumentException("Invalid number of arguments");
        }
        for(var i = 0; i < values.length; i++) {
            opcode.argumentTypes.get(i).tryWrite(this, values[i]);
        }
    }
    
    public ByteWriter writer() {
        return writer;
    }
    
    public void end() {
        labelFixups.forEach((label, positions) -> {
            if(!label.bound) {
                throw new IllegalStateException("Unbound labels remaining");
            }
            for(var pos : positions) {
                writer.patchU16(pos, label.position);
            }
        });
        owner.writeFunction(id, writer);
    }
    
    private void op(Opcode opcode) {
        writer.u8(opcode.value);
    }
    
    private void op_u8(Opcode opcode, int imm) {
        op(opcode);
        writer.u8(imm);
    }
    
    private void op_u16(Opcode opcode, int imm) {
        op(opcode);
        writer.u16(imm);
    }
    
    private void op_u16_u16(Opcode opcode, int imm1, int imm2) {
        op(opcode);
        writer.u16(imm1);
        writer.u16(imm2);
    }
}
