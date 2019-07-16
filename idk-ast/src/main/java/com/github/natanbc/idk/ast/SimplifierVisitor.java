package com.github.natanbc.idk.ast;

import com.github.natanbc.idk.ast.misc.*;
import com.github.natanbc.idk.ast.operation.AstBinaryOperation;
import com.github.natanbc.idk.ast.operation.AstUnaryOperation;
import com.github.natanbc.idk.ast.value.*;
import com.github.natanbc.idk.ast.variable.*;
import com.github.natanbc.idk.common.UnaryOperationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimplifierVisitor implements AstVisitor<AstNode> {
    private static final SimplifierVisitor SIMPLIFY_INTERNAL = new SimplifierVisitor(true);
    private static final SimplifierVisitor NO_SIMPLIFY_INTERNAL = new SimplifierVisitor(false);
    
    private final boolean simplifyInternal;
    
    private SimplifierVisitor(boolean simplifyInternal) {
        this.simplifyInternal = simplifyInternal;
    }
    
    public SimplifierVisitor() { this(true); }
    
    public static SimplifierVisitor instance() {
        return SIMPLIFY_INTERNAL;
    }
    
    @Override
    public AstNode visitBoolean(AstBoolean node) {
        return node;
    }
    
    @Override
    public AstNode visitDouble(AstDouble node) {
        return node;
    }
    
    @Override
    public AstNode visitLong(AstLong node) {
        return node;
    }
    
    @Override
    public AstNode visitNil(AstNil node) {
        return node;
    }
    
    @Override
    public AstNode visitString(AstString node) {
        return node;
    }
    
    @Override
    public AstNode visitArrayLiteral(AstArrayLiteral node) {
        return new AstArrayLiteral(
                simplify(node.getValues())
        );
    }
    
    @Override
    public AstNode visitObjectLiteral(AstObjectLiteral node) {
        var ret = new ArrayList<Map.Entry<AstNode, AstNode>>(node.getEntries().size());
        for(var n : node.getEntries()) {
            ret.add(Map.entry(
                    n.getKey().accept(SIMPLIFY_INTERNAL),
                    n.getValue().accept(SIMPLIFY_INTERNAL)
            ));
        }
        return new AstObjectLiteral(ret);
    }
    
    @Override
    public AstNode visitUnaryOperation(AstUnaryOperation node) {
        switch(node.getType()) {
            case NEG: {
                var target = node.getTarget();
//                if(target instanceof AstUnaryOperation && ((AstUnaryOperation) target).getType() == UnaryOperationType.NEG) {
//                    return ((AstUnaryOperation) target).getTarget().accept(SIMPLIFY_INTERNAL);
//                }
                if(target instanceof AstLong) {
                    return new AstLong(-((AstLong) target).getValue());
                }
                if(target instanceof AstDouble) {
                    return new AstDouble(-((AstDouble) target).getValue());
                }
                if(simplifyInternal) {
                    return new AstUnaryOperation(UnaryOperationType.NEG, target.accept(SIMPLIFY_INTERNAL)).accept(NO_SIMPLIFY_INTERNAL);
                } else {
                    return node;
                }
            }
            case NEGATE: {
                var target = node.getTarget();
//                if(target instanceof AstUnaryOperation && ((AstUnaryOperation) target).getType() == UnaryOperationType.NEGATE) {
//                    return ((AstUnaryOperation) target).getTarget().accept(SIMPLIFY_INTERNAL);
//                }
                if(target instanceof AstBoolean) {
                    return new AstBoolean(!((AstBoolean) target).getValue());
                }
                if(simplifyInternal) {
                    return new AstUnaryOperation(UnaryOperationType.NEG, target.accept(SIMPLIFY_INTERNAL)).accept(NO_SIMPLIFY_INTERNAL);
                } else {
                    return node;
                }
            }
            default: return node;
        }
    }
    
    //TODO
    @Override
    public AstNode visitBinaryOperation(AstBinaryOperation node) {
        return node;
    }
    
    @Override
    public AstNode visitRange(AstRange node) {
        return new AstRange(node.getFrom().accept(SIMPLIFY_INTERNAL), node.getTo().accept(SIMPLIFY_INTERNAL));
    }
    
    @Override
    public AstNode visitIdentifier(AstIdentifier node) {
        return node;
    }
    
    @Override
    public AstNode visitGlobal(AstGlobal node) {
        return node;
    }
    
    @Override
    public AstNode visitLet(AstLet node) {
        return node;
    }
    
    @Override
    public AstNode visitAssign(AstAssign node) {
        return new AstAssign(node.getTarget().accept(SIMPLIFY_INTERNAL), node.getValue().accept(SIMPLIFY_INTERNAL));
    }
    
    //TODO
    @Override
    public AstNode visitMember(AstMember node) {
        return node;
    }
    
    @Override
    public AstNode visitBody(AstBody node) {
        if(node.getChildren().isEmpty()) {
            return new AstNil(); //effectively the same, but faster to evaluate
        }
        if(node.getChildren().size() == 1) {
            return node.getChildren().get(0).accept(SIMPLIFY_INTERNAL);
        }
        if(simplifyInternal) {
            return new AstBody(simplify(node.getChildren())).accept(NO_SIMPLIFY_INTERNAL);
        } else {
            return node;
        }
    }
    
    @Override
    public AstNode visitCall(AstCall node) {
        return new AstCall(
                node.getTarget().accept(SIMPLIFY_INTERNAL),
                simplify(node.getArguments())
        );
    }
    
    @Override
    public AstNode visitFunction(AstFunction node) {
        return new AstFunction(
                node.getName(),
                node.getArguments(),
                node.getBody().accept(SIMPLIFY_INTERNAL),
                node.isVarargs(),
                node.getAnnotations()
        );
    }
    
    @Override
    public AstNode visitIf(AstIf node) {
        var cond = node.getCondition();
        if(cond instanceof AstBoolean) {
            if(((AstBoolean) cond).getValue()) {
                return node.getIfBody().accept(SIMPLIFY_INTERNAL);
            } else {
                return node.getElseBody().accept(SIMPLIFY_INTERNAL);
            }
        }
        if(simplifyInternal) {
            return new AstIf(
                    cond.accept(SIMPLIFY_INTERNAL),
                    node.getIfBody().accept(SIMPLIFY_INTERNAL),
                    node.getElseBody().accept(SIMPLIFY_INTERNAL)
            ).accept(NO_SIMPLIFY_INTERNAL);
        } else {
            return node;
        }
    }
    
    @Override
    public AstNode visitWhile(AstWhile node) {
        var cond = node.getCondition();
        if(cond instanceof AstBoolean && !((AstBoolean) cond).getValue()) {
            return node.getElseBody().accept(SIMPLIFY_INTERNAL);
        }
        if(simplifyInternal) {
            return new AstWhile(
                    cond.accept(SIMPLIFY_INTERNAL),
                    node.getBody().accept(SIMPLIFY_INTERNAL),
                    node.getElseBody().accept(SIMPLIFY_INTERNAL)
            ).accept(NO_SIMPLIFY_INTERNAL);
        } else {
            return node;
        }
    }
    
    @Override
    public AstNode visitFor(AstFor node) {
        return new AstFor(
                node.getVariable(),
                node.getValue().accept(SIMPLIFY_INTERNAL),
                node.getBody().accept(SIMPLIFY_INTERNAL),
                node.getElseBody().accept(SIMPLIFY_INTERNAL)
        );
    }
    
    @Override
    public AstNode visitReturn(AstReturn node) {
        return new AstReturn(node.getValue().accept(SIMPLIFY_INTERNAL));
    }
    
    @Override
    public AstNode visitThrow(AstThrow node) {
        return new AstThrow(node.getValue().accept(SIMPLIFY_INTERNAL));
    }
    
//    @Override
//    public AstNode visitAwait(AstAwait node) {
//        return new AstAwait(node.getValue().accept(SIMPLIFY_INTERNAL));
//    }

//    @Override
//    public AstNode visitUnpack(AstUnpack node) {
//        return new AstUnpack(node.getValue().accept(SIMPLIFY_INTERNAL));
//    }
    
    private static List<AstNode> simplify(List<AstNode> list) {
        var ret = new ArrayList<AstNode>(list.size());
        for(var n : list) {
            ret.add(n.accept(SIMPLIFY_INTERNAL));
        }
        return ret;
    }
}
