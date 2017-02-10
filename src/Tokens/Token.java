package Tokens;

import Tokens.TokenType;

/**
 * Created by Gabriel Paz on 07-Feb-17.
 */
public class Token {
    public String lexeme;
    public TokenType type;
    public int Line;
    public int Column;
    public Token() {

    }

    public Token(String lexeme, TokenType type, int line, int column) {
        this.lexeme = lexeme;
        this.type = type;
        Line = line;
        Column = column;
    }
}
