package com.github.natanbc.idk.ir.convert;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.AstVisitor;
import com.github.natanbc.idk.ast.misc.*;
import com.github.natanbc.idk.ast.operation.AstBinaryOperation;
import com.github.natanbc.idk.ast.operation.AstUnaryOperation;
import com.github.natanbc.idk.ast.value.*;
import com.github.natanbc.idk.ast.variable.*;
import com.github.natanbc.idk.common.BinaryOperationType;
import com.github.natanbc.idk.ir.IrNode;
import com.github.natanbc.idk.ir.misc.*;
import com.github.natanbc.idk.ir.operation.IrBinaryOperation;
import com.github.natanbc.idk.ir.operation.IrUnaryOperation;
import com.github.natanbc.idk.ir.value.*;
import com.github.natanbc.idk.ir.variable.IrAssign;
import com.github.natanbc.idk.ir.variable.IrGlobal;
import com.github.natanbc.idk.ir.variable.IrMember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

class ActualIrConverter implements AstVisitor<IrNode> {
    private final FunctionScope scope;
    
    ActualIrConverter(FunctionScope scope) {
        this.scope = scope;
    }
    
    @Override
    public IrNode visitBoolean(AstBoolean node) {
        return new IrBoolean(node.getValue());
    }
    
    @Override
    public IrNode visitDouble(AstDouble node) {
        return new IrDouble(node.getValue());
    }
    
    @Override
    public IrNode visitLong(AstLong node) {
        return new IrLong(node.getValue());
    }
    
    @Override
    public IrNode visitNil(AstNil node) {
        return new IrNil();
    }
    
    @Override
    public IrNode visitString(AstString node) {
        return new IrString(node.getValue());
    }
    
    @Override
    public IrNode visitArrayLiteral(AstArrayLiteral node) {
        var r = new ArrayList<IrNode>(node.getValues().size());
        for(var v : node.getValues()) {
            r.add(v.accept(innerScope()));
        }
        return new IrArrayLiteral(r);
    }
    
    @Override
    public IrNode visitObjectLiteral(AstObjectLiteral node) {
        var r = new ArrayList<Map.Entry<IrNode, IrNode>>(node.getEntries().size());
        for(var v : node.getEntries()) {
            r.add(Map.entry(
                    v.getKey().accept(innerScope()),
                    v.getValue().accept(innerScope())
            ));
        }
        return new IrObjectLiteral(r);
    }
    
    @Override
    public IrNode visitUnaryOperation(AstUnaryOperation node) {
        return new IrUnaryOperation(node.getType(), node.getTarget().accept(innerScope()));
    }
    
    @Override
    public IrNode visitBinaryOperation(AstBinaryOperation node) {
        return new IrBinaryOperation(node.getType(), node.getLhs().accept(innerScope()), node.getRhs().accept(innerScope()));
    }
    
    @Override
    public IrNode visitRange(AstRange node) {
        return new IrRange(node.getFrom().accept(innerScope()), node.getTo().accept(innerScope()));
    }
    
    @Override
    public IrNode visitIdentifier(AstIdentifier node) {
        return scope.find(node.getName());
    }
    
    @Override
    public IrNode visitGlobal(AstGlobal node) {
        return new IrGlobal(node.getName());
    }
    
    @Override
    public IrNode visitLet(AstLet node) {
        return new IrAssign(scope.declareLocal(node.getName()), new IrNil());
    }
    
    @Override
    public IrNode visitAssign(AstAssign node) {
        if(node.getTarget() instanceof AstLet) {
            var name = ((AstLet) node.getTarget()).getName();
            return new IrAssign(scope.declareLocal(name), node.getValue().accept(innerScope()));
        }
        if(node.getTarget() instanceof AstObjectLiteral) {
            var loadNode = scope.declareLocal(uniqueKey(node.getValue()));
            var b = new ArrayList<IrNode>();
            b.add(new IrAssign(loadNode, node.getValue().accept(innerScope())));
            for(var entry : ((AstObjectLiteral) node.getTarget()).getEntries()) {
                b.add(destructure(new IrMember(loadNode, entry.getKey().accept(innerScope())), entry.getValue()));
            }
            b.add(loadNode);
            return new IrBody(b);
        }
        if(node.getTarget() instanceof AstArrayLiteral) {
            var loadNode = scope.declareLocal(uniqueKey(node.getValue()));
            var b = new ArrayList<IrNode>();
            b.add(new IrAssign(loadNode, node.getValue().accept(innerScope())));
            var i = 0;
            for(var entry : ((AstArrayLiteral) node.getTarget()).getValues()) {
                b.add(destructure(new IrMember(loadNode, new IrLong(i)), entry));
                i++;
            }
            b.add(loadNode);
            return new IrBody(b);
        }
        return new IrAssign(node.getTarget().accept(innerScope()), node.getValue().accept(innerScope()));
    }
    
    @Override
    public IrNode visitMember(AstMember node) {
        return new IrMember(node.getTarget().accept(innerScope()), node.getKey().accept(innerScope()));
    }
    
    @Override
    public IrNode visitBody(AstBody node) {
        var r = new ArrayList<IrNode>(node.getChildren().size());
        var s = innerScope();
        for(var v : node.getChildren()) {
            r.add(v.accept(s));
        }
        return new IrBody(r);
    }
    
    @Override
    public IrNode visitCall(AstCall node) {
        var t = node.getTarget().accept(innerScope());
        var r = new ArrayList<IrNode>(node.getArguments().size());
        for(var v : node.getArguments()) {
            r.add(v.accept(innerScope()));
        }
        return new IrCall(t, r);
    }
    
    @Override
    public IrNode visitFunction(AstFunction node) {
        var newScope = new FunctionScope(scope);
        for(String argument : node.getArguments()) {
            newScope.declareLocal(argument);
        }
        var ir = node.getBody().accept(new ActualIrConverter(newScope));
        return new IrFunction(
                node.getName(),
                node.getArguments().size(),
                newScope.localsCount(),
                ir,
                node.isVarargs(),
                node.getAnnotations()
        );
    }
    
    @Override
    public IrNode visitIf(AstIf node) {
        return new IrIf(node.getCondition().accept(innerScope()), node.getIfBody().accept(innerScope()), node.getElseBody().accept(innerScope()));
    }
    
    @Override
    public IrNode visitWhile(AstWhile node) {
        return new IrWhile(node.getCondition().accept(innerScope()), node.getBody().accept(innerScope()), node.getElseBody().accept(innerScope()));
    }
    
    @Override
    public IrNode visitFor(AstFor node) {
        var s = innerScope();
        return new IrFor(
                s.scope.declareLocal(node.getVariableName()).getIndex(),
                node.getValue().accept(innerScope()),
                node.getBody().accept(s)
        );
    }
    
    @Override
    public IrNode visitReturn(AstReturn node) {
        return new IrReturn(node.getValue().accept(innerScope()));
    }
    
    @Override
    public IrNode visitThrow(AstThrow node) {
        return new IrThrow(node.getValue().accept(innerScope()));
    }
    
//    @Override
//    public IrNode visitUnpack(AstUnpack node) {
//        return new IrUnpack(node.getValue().accept(this));
//    }
    
    private IrNode destructure(IrNode value, AstNode destination) {
        if(destination instanceof AstIdentifier) {
            var name = ((AstIdentifier) destination).getName();
            if(name.equals("_")) {
                return new IrNil();
            } else {
                return new IrAssign(scope.declareLocal(name), value);
            }
        }
        if(destination instanceof AstObjectLiteral) {
            var loadNode = scope.declareLocal(uniqueKey(destination));
            var b = new ArrayList<IrNode>();
            b.add(new IrAssign(loadNode, value));
            for(var entry : ((AstObjectLiteral) destination).getEntries()) {
                b.add(destructure(new IrMember(loadNode, entry.getKey().accept(innerScope())), entry.getValue()));
            }
            b.add(loadNode);
            return new IrBody(b);
        }
        if(destination instanceof AstArrayLiteral) {
            var loadNode = scope.declareLocal(uniqueKey(destination));
            var b = new ArrayList<IrNode>();
            b.add(new IrAssign(loadNode, value));
            var i = 0;
            for(var entry : ((AstArrayLiteral) destination).getValues()) {
                b.add(destructure(new IrMember(loadNode, new IrLong(i)), entry));
                i++;
            }
            b.add(loadNode);
            return new IrBody(b);
        }
        return new IrIf(
                new IrBinaryOperation(BinaryOperationType.NEQ, destination.accept(innerScope()), value),
                new IrThrow(new IrString("Pattern " + destination + " didn't match")),
                new IrBody(Collections.emptyList())
        );
    }
    
    private ActualIrConverter innerScope() {
        return new ActualIrConverter(scope.innerScope());
    }
    
    private static String uniqueKey(Object identifier) {
        return "0_uniq_" + identifier.getClass().getSimpleName() + "@" + System.identityHashCode(identifier);
    }
}
