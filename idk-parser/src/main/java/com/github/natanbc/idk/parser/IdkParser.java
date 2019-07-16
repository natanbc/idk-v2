package com.github.natanbc.idk.parser;

import com.github.natanbc.idk.ast.AstNode;
import com.github.natanbc.idk.ast.misc.AstBody;
import com.github.natanbc.pratt.Lexer;
import com.github.natanbc.pratt.Parser;

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
        var needsSemicolon = false;
        var last = lexer.pos();
        while(!matches(TokenType.EOF)) {
            if(peek().position().line() == last.line() && needsSemicolon && !dropSemicolons()) {
                throw new SyntaxException("Semicolon required between multiple expressions on the same line:\n"
                        + lexer.prettyContext(peek().position(), 1)
                );
            }
            last = peek().position();
            list.add(parseExpression(null));
            needsSemicolon = !dropSemicolons();
        }
        if(list.size() == 1) {
            return list.get(0);
        } else {
            return new AstBody(list);
        }
    }
    
    private boolean dropSemicolons() {
        var found = false;
        while(matches(TokenType.SEMICOLON)) {
            found = true;
        }
        return found;
    }
}
