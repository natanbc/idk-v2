package com.github.natanbc.idk.parser;

import com.github.natanbc.pratt.*;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.io.Reader;

public class IdkLexer extends Lexer {
    public IdkLexer(@Nonnull CharacterStream stream) {
        super(stream);
    }
    
    public IdkLexer(@Nonnull Reader reader) {
        super(reader);
    }
    
    @CheckReturnValue
    @Nonnull
    @Override
    public Token parse() {
        int ch = read(true);
        switch(ch) {
            case -1: return new Token(TokenType.EOF, pos(), "<EOF>");
            
            case ';': return new Token(TokenType.SEMICOLON, pos(), ";");
            
            case '"': return readString(pos(), '"');
            case '\'': return readString(pos(), '\'');
            
            case '(': return new Token(TokenType.LEFT_PAREN, pos(), "(");
            case ')': return new Token(TokenType.RIGHT_PAREN, pos(), ")");
    
            case '[': return new Token(TokenType.LEFT_BRACKET, pos(), "[");
            case ']': return new Token(TokenType.RIGHT_BRACKET, pos(), "]");
    
            case '{': return new Token(TokenType.LEFT_BRACE, pos(), "{");
            case '}': return new Token(TokenType.RIGHT_BRACE, pos(), "}");
            
            case '+': return new Token(TokenType.PLUS, pos(), "+");
            case '-': return new Token(TokenType.MINUS, pos(), "-");
            case '*': return new Token(TokenType.ASTERISK, pos(), "*");
            case '/': return new Token(TokenType.SLASH, pos(), "/");
            case '%': return new Token(TokenType.PERCENT, pos(), "%");
            case '^': return new Token(TokenType.CARET, pos(), "^");
            
            case ',': return new Token(TokenType.COMMA, pos(), ",");
            case '.': {
                var p = pos();
                if(eat('.')) {
                    if(eat('.')) {
                        return new Token(TokenType.VARARGS, p, "...");
                    } else {
                        return new Token(TokenType.RANGE, p, "..");
                    }
                } else {
                    return new Token(TokenType.DOT, p, ".");
                }
            }
    
            case '=': {
                var p = pos();
                if(eat('=')) {
                    return new Token(TokenType.EQ, p, "==");
                } else {
                    return new Token(TokenType.ASSIGN, p, "=");
                }
            }
    
            case '!': {
                var p = pos();
                if(eat('=')) {
                    return new Token(TokenType.NEQ, p, "!=");
                } else {
                    return new Token(TokenType.NEGATION, p, "!");
                }
            }
    
            case '>': {
                var p = pos();
                if(eat('=')) {
                    return new Token(TokenType.GREATER_EQ, p, ">=");
                } else {
                    return new Token(TokenType.GREATER, p, ">");
                }
            }
    
            case '<': {
                var p = pos();
                if(eat('=')) {
                    return new Token(TokenType.SMALLER_EQ, p, "<=");
                } else {
                    return new Token(TokenType.SMALLER, p, "<");
                }
            }
    
            case '&': {
                var p = pos();
                if(eat('&')) {
                    return new Token(TokenType.AND, p, "&&");
                } else {
                    throw new IllegalArgumentException("Unexpected &:\n" + prettyContext(p, 5));
                }
            }
    
            case '|': {
                var p = pos();
                if(eat('|')) {
                    return new Token(TokenType.OR, p, "||");
                } else {
                    throw new IllegalArgumentException("Unexpected |:\n" + prettyContext(p, 5));
                }
            }
            
            default: {
                if(Character.isDigit(ch)) {
                    return readNumber(pos(), (char)ch);
                } else if(Character.isJavaIdentifierStart(ch)) {
                    return readName(pos(), ch);
                } else {
                    throw new IllegalArgumentException("Unexpected character '" + ((char)ch) + "':\n" + prettyContext(pos(), 5));
                }
            }
        }
    }
    
    @CheckReturnValue
    @Nonnull
    @Override
    public TokenKind eofKind() {
        return TokenType.EOF;
    }
    
    private Token readString(Position pos, char quote) {
        var sb = new StringBuilder();
        var c = 0;
        while(true) {
            c = read(false);
            if(c == -1) {
                throw new IllegalArgumentException("Unclosed string:\n" + prettyContext(pos(), 5));
            }
            switch(c) {
                case '\r':
                case '\n':
                    throw new IllegalArgumentException("Unclosed string:\n" + prettyContext(pos(), 5));
                case '\\': {
                    var escape = read(false);
                    var escapePos = pos();
                    switch(escape) {
                        case '\\': sb.append('\\'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case 'u': {
                            var sb2 = new StringBuilder();
                            for(int i = 0; i < 4; i++) {
                                var hex = read(false);
                                if(hex == -1) {
                                    throw new IllegalArgumentException("Unfinished escape:\n" + prettyContext(escapePos, 5));
                                }
                                sb2.append((char)hex);
                            }
                            try {
                                sb.append((char)Integer.parseInt(sb2.toString(), 16));
                            } catch(NumberFormatException ignored) {
                                throw new IllegalArgumentException("Invalid escape " + sb2 + ":\n" + prettyContext(escapePos, 5));
                            }
                            break;
                        }
                        default: {
                            if(escape == quote) {
                                sb.append(quote);
                            } else {
                                throw new IllegalArgumentException("Invalid escape: \\" + (char)escape + ":\n" + prettyContext(pos(), 5));
                            }
                            break;
                        }
                    }
                    break;
                }
                default:
                    if(c == quote) {
                        return new Token(TokenType.STRING, pos, sb.toString());
                    } else {
                        sb.append((char)c);
                    }
            }
        }
    }
    
    private Token readName(Position pos, int ch) {
        var sb = new StringBuilder();
        do {
            sb.append((char)ch);
            ch = read(false);
        } while(Character.isJavaIdentifierPart(ch));
        back();
        
        var res = sb.toString();
        switch(res) {
            case "true": return new Token(TokenType.BOOLEAN, pos, "true");
            case "false": return new Token(TokenType.BOOLEAN, pos, "false");
            case "nil": return new Token(TokenType.NIL, pos, "nil");
            case "if": return new Token(TokenType.IF, pos, "if");
            case "while": return new Token(TokenType.WHILE, pos, "while");
            case "else": return new Token(TokenType.ELSE, pos, "else");
            case "fn": return new Token(TokenType.FUNCTION, pos, "fn");
            case "return": return new Token(TokenType.RETURN, pos, "return");
            case "let": return new Token(TokenType.LET, pos, "let");
            case "global": return new Token(TokenType.GLOBAL, pos, "global");
            case "throw": return new Token(TokenType.THROW, pos, "throw");
            //case "async": return new Token(TokenType.ASYNC, pos, "async");
            //case "await": return new Token(TokenType.AWAIT, pos, "await");
            case "for": return new Token(TokenType.FOR, pos, "for");
            case "in": return new Token(TokenType.IN, pos, "in");
            
            default: return new Token(TokenType.IDENTIFIER, pos, res);
        }
    }
    
    private Token readNumber(Position pos, char start) {
        var sb = new StringBuilder().append(start);
        var point = false;
        while(true) {
            var c = read(false);
            if(Character.isDigit(c)) {
                sb.append((char)c);
            } else {
                switch(c) {
                    case '.': {
                        if(point) {
                            back();
                            return new Token(TokenType.DOUBLE, pos, sb.toString());
                        }
                        if(!Character.isDigit(peek(false))) {
                            backTo(pos().line(), pos().column() - 1);
                            return new Token(TokenType.LONG, pos, sb.toString());
                        }
                        point = true;
                        sb.append('.');
                        break;
                    }
                    case '_': break;
                    default: {
                        back();
                        if(point) {
                            return new Token(TokenType.DOUBLE, pos, sb.toString());
                        } else {
                            return new Token(TokenType.LONG, pos, sb.toString());
                        }
                    }
                }
            }
        }
    }
    
    private boolean eat(char c) {
        if(match(c)) {
            //noinspection ResultOfMethodCallIgnored
            realStream.read(false);
            return true;
        } else {
            return false;
        }
    }
}
