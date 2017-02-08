package Tokens;

import Tokens.TokenType;

/**
 * Created by Gabriel Paz on 07-Feb-17.
 */
public class Token {
    public String lexeme;
    public TokenType type;

    public Token() {

    }

    public Token(String lexeme, TokenType type) {
        this.lexeme = lexeme;
        this.type = type;
    }
}
