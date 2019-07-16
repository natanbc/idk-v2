package com.github.natanbc.idk.parser;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.misc.AstBody;
import com.github.natanbc.pratt.Lexer;
import com.github.natanbc.pratt.Parser;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

public class IdkParser extends Parser<Void, AstNode> {
    public IdkParser(@Nonnull Lexer lexer) {
        super(lexer);
        IdkParselets.INFIX_PARSELETS.forEach(this::register);
        IdkParselets.PREFIX_PARSELETS.forEach(this::register);
    }
    
    public IdkParser(@Nonnull Reader source) {
        this(new IdkLexer(source));
    }
    
    public IdkParser(@Nonnull String source) {
        this(new StringReader(source));
    }
    
    public AstNode parse() {
        var list = new ArrayList<AstNode>();
        while(!matches(TokenType.EOF)) {
            dropSemicolons();
            list.add(parseExpression(null));
            dropSemicolons();
        }
        if(list.size() == 1) {
            return list.get(0);
        } else {
            return new AstBody(list);
        }
    }
    
    private void dropSemicolons() {
        while(true) {
            if(!matches(TokenType.SEMICOLON)) {
                break;
            }
        }
    }
}
