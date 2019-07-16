package com.github.natanbc.idk.parser;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.misc.*;
import com.github.natanbc.idk.ast.operation.AstBinaryOperation;
import com.github.natanbc.idk.ast.operation.AstUnaryOperation;
import com.github.natanbc.idk.ast.value.*;
import com.github.natanbc.idk.ast.variable.*;
import com.github.natanbc.idk.common.BinaryOperationType;
import com.github.natanbc.idk.common.UnaryOperationType;
import com.github.natanbc.pratt.*;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class IdkParselets {
    public static final Map<TokenType, InfixParselet<Void, AstNode>> INFIX_PARSELETS = Map.ofEntries(
            Map.entry(TokenType.PLUS, new BinaryOperatorParselet(Precedence.SUM, BinaryOperationType.ADD)),
            Map.entry(TokenType.MINUS, new BinaryOperatorParselet(Precedence.SUM, BinaryOperationType.SUB)),
            Map.entry(TokenType.ASTERISK, new BinaryOperatorParselet(Precedence.PRODUCT, BinaryOperationType.MUL)),
            Map.entry(TokenType.SLASH, new BinaryOperatorParselet(Precedence.PRODUCT, BinaryOperationType.DIV)),
            Map.entry(TokenType.PERCENT, new BinaryOperatorParselet(Precedence.PRODUCT, BinaryOperationType.MOD)),
            Map.entry(TokenType.CARET, new BinaryOperatorParselet(Precedence.EXPONENT, BinaryOperationType.POW)),
            Map.entry(TokenType.EQ, new BinaryOperatorParselet(Precedence.CONDITIONAL, BinaryOperationType.EQ)),
            Map.entry(TokenType.NEQ, new BinaryOperatorParselet(Precedence.CONDITIONAL, BinaryOperationType.NEQ)),
            Map.entry(TokenType.GREATER, new BinaryOperatorParselet(Precedence.CONDITIONAL, BinaryOperationType.GREATER)),
            Map.entry(TokenType.GREATER_EQ, new BinaryOperatorParselet(Precedence.CONDITIONAL, BinaryOperationType.GREATER_EQ)),
            Map.entry(TokenType.SMALLER, new BinaryOperatorParselet(Precedence.CONDITIONAL, BinaryOperationType.SMALLER)),
            Map.entry(TokenType.SMALLER_EQ, new BinaryOperatorParselet(Precedence.CONDITIONAL, BinaryOperationType.SMALLER_EQ)),
            Map.entry(TokenType.AND, new BinaryOperatorParselet(Precedence.CONJUNCTION, BinaryOperationType.AND)),
            Map.entry(TokenType.OR, new BinaryOperatorParselet(Precedence.DISJUNCTION, BinaryOperationType.OR)),
            Map.entry(TokenType.RANGE, new InfixParselet<>() {
                @Nonnegative
                @CheckReturnValue
                @Override
                public int precedence() {
                    return Precedence.RANGE;
                }
    
                @CheckReturnValue
                @Nonnull
                @Override
                public AstNode parse(Void context, @Nonnull Parser<Void, AstNode> parser, @Nonnull AstNode left, @Nonnull Token token) {
                    return new AstRange(left, parser.parseExpression(context, Precedence.RANGE));
                }
            }),
            Map.entry(TokenType.LEFT_PAREN, new InfixParselet<>() {
                @Nonnegative
                @CheckReturnValue
                @Override
                public int precedence() {
                    return Precedence.CALL;
                }
    
                @CheckReturnValue
                @Nonnull
                @Override
                public AstNode parse(Void context, @Nonnull Parser<Void, AstNode> parser, @Nonnull AstNode left, @Nonnull Token token) {
                    var l = new ArrayList<AstNode>();
                    if(!parser.matches(TokenType.RIGHT_PAREN)) {
                        do {
                            l.add(parser.parseExpression(context));
                        } while(parser.matches(TokenType.COMMA));
                        parser.expect(TokenType.RIGHT_PAREN);
                    }
                    return new AstCall(left, l);
                }
            }),
            Map.entry(TokenType.ASSIGN, new InfixParselet<>() {
                @Nonnegative
                @CheckReturnValue
                @Override
                public int precedence() {
                    return Precedence.ASSIGNMENT;
                }
    
                @CheckReturnValue
                @Nonnull
                @Override
                public AstNode parse(Void context, @Nonnull Parser<Void, AstNode> parser, @Nonnull AstNode left, @Nonnull Token token) {
                    var value = parser.parseExpression(context, Precedence.ASSIGNMENT);
                    if(left instanceof AstIdentifier || left instanceof AstGlobal || left instanceof AstLet
                            || left instanceof AstMember || left instanceof AstArrayLiteral || left instanceof AstObjectLiteral) {
                        return new AstAssign(left, value);
                    } else if(left instanceof AstAssign) {
                        return new AstAssign(((AstAssign) left).getTarget(), new AstAssign(((AstAssign) left).getValue(), value));
                    } else {
                        throw new SyntaxException("Invalid expression as assignment target:\n" + parser.lexer().prettyContextFor(token));
                    }
                }
            }),
            Map.entry(TokenType.DOT, new InfixParselet<>() {
                @Nonnegative
                @CheckReturnValue
                @Override
                public int precedence() {
                    return Precedence.MEMBER_ACCESS;
                }
    
                @CheckReturnValue
                @Nonnull
                @Override
                public AstNode parse(Void context, @Nonnull Parser<Void, AstNode> parser, @Nonnull AstNode left, @Nonnull Token token) {
                    return new AstMember(left, new AstString(parser.consume(TokenType.IDENTIFIER).value()));
                }
            }),
            Map.entry(TokenType.LEFT_BRACKET, new InfixParselet<>() {
                @Nonnegative
                @CheckReturnValue
                @Override
                public int precedence() {
                    return Precedence.MEMBER_ACCESS;
                }
    
                @CheckReturnValue
                @Nonnull
                @Override
                public AstNode parse(Void context, @Nonnull Parser<Void, AstNode> parser, @Nonnull AstNode left, @Nonnull Token token) {
                    var key = parser.parseExpression(context);
                    parser.expect(TokenType.RIGHT_BRACKET);
                    return new AstMember(left, key);
                }
            })
    );
    
    public static final Map<TokenType, PrefixParselet<Void, AstNode>> PREFIX_PARSELETS = Map.ofEntries(
            Map.entry(TokenType.MINUS, new PrefixOperatorParselet(n -> new AstUnaryOperation(UnaryOperationType.NEG, n))),
            Map.entry(TokenType.PLUS, new PrefixOperatorParselet(Function.identity())),
            Map.entry(TokenType.NEGATION, new PrefixOperatorParselet(n -> new AstUnaryOperation(UnaryOperationType.NEGATE, n))),
            Map.entry(TokenType.LET, (__1, parser, __2) -> new AstLet(parser.consume(TokenType.IDENTIFIER).value())),
            Map.entry(TokenType.GLOBAL, (__1, parser, __2) -> new AstGlobal(parser.consume(TokenType.IDENTIFIER).value())),
            //Map.entry(TokenType.VARARGS, (context, parser, __2) -> new AstUnpack(parser.parseExpression(context))),
            //Map.entry(TokenType.AWAIT, (context, parser, __2) -> new AstAwait(parser.parseExpression(context))),
            Map.entry(TokenType.RETURN, (context, parser, __2) -> new AstReturn(parser.parseExpression(context))),
            Map.entry(TokenType.THROW, (context, parser, __2) -> new AstThrow(parser.parseExpression(context))),
            Map.entry(TokenType.IDENTIFIER, (__1, __2, token) -> new AstIdentifier(token.value())),
            Map.entry(TokenType.LONG, (__1, __2, token) -> new AstLong(Long.parseLong(token.value()))),
            Map.entry(TokenType.DOUBLE, (__1, __2, token) -> new AstDouble(Double.parseDouble(token.value()))),
            Map.entry(TokenType.STRING, (__1, __2, token) -> new AstString(token.value())),
            Map.entry(TokenType.BOOLEAN, (__1, __2, token) -> new AstBoolean(Boolean.parseBoolean(token.value()))),
            Map.entry(TokenType.NIL, (__1, __2, __3) -> new AstNil()),
            Map.entry(TokenType.LEFT_PAREN, (context, parser, __1) -> {
                var n = parser.parseExpression(context);
                parser.expect(TokenType.RIGHT_PAREN);
                return n;
            }),
            Map.entry(TokenType.LEFT_BRACE, (context, parser, __1) -> {
                var l = new ArrayList<Map.Entry<AstNode, AstNode>>();
                if(!parser.matches(TokenType.RIGHT_BRACE)) {
                    do {
                        if(parser.matches(TokenType.LEFT_BRACKET)) {
                            var expr = parser.parseExpression(context);
                            parser.expect(TokenType.RIGHT_BRACKET);
                            parser.expect(TokenType.ASSIGN);
                            l.add(Map.entry(expr, parser.parseExpression(context)));
                        } else {
                            var k = parser.consume(TokenType.IDENTIFIER).value();
                            var next = parser.peek().kind();
                            if(next == TokenType.COMMA || next == TokenType.RIGHT_BRACE) {
                                l.add(Map.entry(new AstString(k), new AstIdentifier(k)));
                            } else {
                                parser.expect(TokenType.ASSIGN);
                                l.add(Map.entry(new AstString(k), parser.parseExpression(context)));
                            }
                        }
                    } while(parser.matches(TokenType.COMMA));
                    parser.expect(TokenType.RIGHT_BRACE);
                }
                return new AstObjectLiteral(l);
            }),
            Map.entry(TokenType.LEFT_BRACKET, (context, parser, __1) -> {
                var l = new ArrayList<AstNode>();
                if(!parser.matches(TokenType.RIGHT_BRACKET)) {
                    do {
                        l.add(parser.parseExpression(context));
                    } while(parser.matches(TokenType.COMMA));
                    parser.expect(TokenType.RIGHT_BRACKET);
                }
                return new AstArrayLiteral(l);
            }),
            Map.entry(TokenType.IF, (context, parser, __1) -> {
                parser.expect(TokenType.LEFT_PAREN);
                var cond = parser.parseExpression(context);
                parser.expect(TokenType.RIGHT_PAREN);
                var ifTrue = parseBody(context, parser);
                if(parser.matches(TokenType.ELSE)) {
                    return new AstIf(cond, ifTrue, parseBody(context, parser));
                } else {
                    return new AstIf(cond, ifTrue, new AstBody(Collections.emptyList()));
                }
            }),
            Map.entry(TokenType.WHILE, (context, parser, __1) -> {
                parser.expect(TokenType.LEFT_PAREN);
                var cond = parser.parseExpression(context);
                parser.expect(TokenType.RIGHT_PAREN);
                var body = parseBody(context, parser);
                if(parser.matches(TokenType.ELSE)) {
                    return new AstWhile(cond, body, parseBody(context, parser));
                } else {
                    return new AstWhile(cond, body, new AstBody(Collections.emptyList()));
                }
            }),
            Map.entry(TokenType.FOR, (context, parser, __1) -> {
                parser.expect(TokenType.LEFT_PAREN);
                var variable = parser.parseExpression(context);
                parser.expect(TokenType.IN);
                var value = parser.parseExpression(context);
                parser.expect(TokenType.RIGHT_PAREN);
                var body = parseBody(context, parser);
                if(parser.matches(TokenType.ELSE)) {
                    return new AstFor(variable, value, body, parseBody(context, parser));
                } else {
                    return new AstFor(variable, value, body, new AstBody(Collections.emptyList()));
                }
            }),
            Map.entry(TokenType.FUNCTION, new FunctionParselet(false)),
            Map.entry(TokenType.LOCAL, new LocalParselet())
    );
    
    private static AstNode parseBody(Void context, Parser<Void, AstNode> parser) {
        var leftBrace = parser.matches(TokenType.LEFT_BRACE);
        if(leftBrace && parser.matches(TokenType.RIGHT_BRACE)) {
            return new AstBody(Collections.emptyList());
        } else {
            var list = new ArrayList<AstNode>();
            var last = parser.lexer().pos();
            var needsSemicolon = false;
            do {
                if(parser.peek().position().line() == last.line() && needsSemicolon && !dropSemicolons(parser)) {
                    throw new SyntaxException("Semicolon required between multiple expressions on the same line:\n"
                            + parser.lexer().prettyContext(parser.peek().position(), 1)
                    );
                }
                last = parser.peek().position();
                list.add(parser.parseExpression(context));
                needsSemicolon = !dropSemicolons(parser);
            } while(leftBrace && !parser.matches(TokenType.RIGHT_BRACE));
            return new AstBody(list);
        }
    }
    
    private static class BinaryOperatorParselet implements InfixParselet<Void, AstNode> {
        private final int precedence;
        private final BinaryOperationType type;
    
        private BinaryOperatorParselet(int precedence, BinaryOperationType type) {
            this.precedence = precedence;
            this.type = type;
        }
    
        @Nonnegative
        @CheckReturnValue
        @Override
        public int precedence() {
            return precedence;
        }
    
        @CheckReturnValue
        @Nonnull
        @Override
        public AstNode parse(Void context, @Nonnull Parser<Void, AstNode> parser, @Nonnull AstNode left, @Nonnull Token token) {
            return new AstBinaryOperation(type, left, parser.parseExpression(context, precedence));
        }
    }
    
    private static class PrefixOperatorParselet implements PrefixParselet<Void, AstNode> {
        private final Function<AstNode, AstNode> nodeBuilder;
    
        private PrefixOperatorParselet(Function<AstNode, AstNode> nodeBuilder) {
            this.nodeBuilder = nodeBuilder;
        }
    
        @CheckReturnValue
        @Nonnull
        @Override
        public AstNode parse(Void context, @Nonnull Parser<Void, AstNode> parser, @Nonnull Token token) {
            return nodeBuilder.apply(parser.parseExpression(context, Precedence.PREFIX));
        }
    }
    
    private static class FunctionParselet implements PrefixParselet<Void, AstNode> {
        private final boolean local;
    
        private FunctionParselet(boolean local) {
            this.local = local;
        }
    
        @CheckReturnValue
        @Nonnull
        @Override
        public AstNode parse(Void context, @Nonnull Parser<Void, AstNode> parser, @Nonnull Token token) {
            var annotations = new ArrayList<String>();
            if(parser.matches(TokenType.LEFT_BRACKET)) {
                do {
                    annotations.add(parser.consume(TokenType.IDENTIFIER).value());
                } while(parser.matches(TokenType.COMMA));
                parser.expect(TokenType.RIGHT_BRACKET);
            }
            var name = parser.peek().kind() == TokenType.IDENTIFIER ? parser.consume(TokenType.IDENTIFIER).value() : null;
            parser.expect(TokenType.LEFT_PAREN);
            var args = new ArrayList<String>();
            var varargs = false;
            if(!parser.matches(TokenType.RIGHT_PAREN)) {
                do {
                    if(varargs) {
                        throw new SyntaxException("No arguments may be declared after a varargs one:\n" +
                                parser.lexer().prettyContextFor(parser.peek()));
                    }
                    if(parser.matches(TokenType.VARARGS)) {
                        varargs = true;
                    }
                    var argName = parser.consume(TokenType.IDENTIFIER);
                    if(args.contains(argName.value())) {
                        throw new SyntaxException("Argument " + argName.value() + " already exists:\n" +
                                parser.lexer().prettyContextFor(argName));
                    }
                    args.add(argName.value());
                } while(parser.matches(TokenType.COMMA));
                parser.expect(TokenType.RIGHT_PAREN);
            }
            var body = parseBody(context, parser);
    
            return new AstFunction(
                    local, name,
                    args,
                    body,
                    varargs,
                    annotations
            );
        }
    }
    
    private static class LocalParselet implements PrefixParselet<Void, AstNode> {
        private final FunctionParselet functionParselet = new FunctionParselet(true);
        
        @CheckReturnValue
        @Nonnull
        @Override
        public AstNode parse(Void context, @Nonnull Parser<Void, AstNode> parser, @Nonnull Token token) {
            return functionParselet.parse(context, parser, parser.consume(TokenType.FUNCTION));
        }
    }
    
    private static boolean dropSemicolons(Parser<Void, AstNode> parser) {
        var found = false;
        while(parser.matches(TokenType.SEMICOLON)) {
            found = true;
        }
        return found;
    }
}
