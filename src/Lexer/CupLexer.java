package Lexer;
import Tokens.Token;
import Tokens.TokenDictionary;
import Tokens.TokenType;

public class CupLexer {
    private String CodeContent;
    private int CurrentPointer;
    private boolean isJava;
    private TokenDictionary dictionary;

    public CupLexer(String codeContent)
    {
        CodeContent = codeContent;
        CurrentPointer = 0;
        isJava = false;
        dictionary = new TokenDictionary();
    }

    public Token getNextToken() throws Exception {
        return isJava ? getNextJavaToken() : getNextCUPToken();
    }

    private Token getNextJavaToken(){
        String lexeme = "";
        char currentChar = getCurrentSymbol();
        while(!lexeme.endsWith(":}") && currentChar != '\0')
        {
            lexeme += currentChar;
            increasePointer();
            currentChar = getCurrentSymbol();
        }
        lexeme = lexeme.substring(0,lexeme.length()-2);
        isJava = false;
        return new Token(lexeme, TokenType.JAVACODE);

    }

    private Token getNextCUPToken() throws Exception {
        String lexeme = "";
        char currentChar = getCurrentSymbol();
        int state = 0;
        while (true)
        {
            switch (state)
            {
                //initial node
                case 0:
                    if(Character.isLetter(currentChar))
                    {
                        lexeme += currentChar;
                        increasePointer();
                        currentChar = getCurrentSymbol();
                        state = 1;

                    }
                    else if(dictionary.SymbolDictionary.containsKey(""+currentChar))
                    {
                        state = 2;
                    }
                    else if(Character.isWhitespace(currentChar))
                    {
                        increasePointer();
                        currentChar = getCurrentSymbol();
                    }
                    else if(currentChar == '\0')
                    {
                        return new Token(lexeme, TokenType.EOF);
                    }
                    else if(currentChar == '/'){
                        increasePointer();
                        currentChar = getCurrentSymbol();
                        if(currentChar == '*'){
                            increasePointer();
                            currentChar = getCurrentSymbol();
                            state = 3;
                        }
                        else
                        {
                          throw new LexerException("Unknown Token: /");
                        }
                    }
                    break;
                //IDs or Reserved Words
                case 1:
                    if(Character.isLetterOrDigit(currentChar) || currentChar == '_')
                    {
                        lexeme += currentChar;
                        increasePointer();
                        currentChar = getCurrentSymbol();

                    }
                    else
                    {
                        return new Token(lexeme,dictionary.ReservedWords.containsKey(lexeme) ? dictionary.ReservedWords.get(lexeme) : TokenType.ID);
                    }
                    break;
                //, . { } ( ) : ; |
                case 2:
                    //check for ::=
                    if(currentChar == ':')
                    {
                        lexeme += currentChar;
                        increasePointer();
                        currentChar = getCurrentSymbol();
                        if(currentChar == ':')
                        {
                            lexeme += currentChar;
                            increasePointer();
                            currentChar = getCurrentSymbol();
                            if(currentChar == '=')
                            {
                                lexeme += currentChar;
                                increasePointer();
                                return new Token(lexeme, TokenType.PRODUCTION);
                            }
                            else
                            {
                                throw new LexerException("Unknown Token: " + lexeme);
                            }
                        }
                        else
                        {
                            return new Token(lexeme, TokenType.COLON);
                        }
                    }

                    //check for {:
                    if(currentChar == '{'){
                        lexeme += currentChar;
                        increasePointer();
                        currentChar = getCurrentSymbol();
                        if(currentChar == ':')
                        {
                            increasePointer();
                            isJava = true;
                            return getNextJavaToken();
                        }
                        else
                        {
                            return new Token(lexeme, TokenType.OPEN_BRACE);
                        }
                    }
                    lexeme += currentChar;
                    increasePointer();
                    return new Token(lexeme, dictionary.SymbolDictionary.get(lexeme));
                case 3:
                    String acumm = "";
                    while(!acumm.endsWith("*/") && currentChar != '\0'){
                        acumm += currentChar;
                        increasePointer();
                        currentChar = getCurrentSymbol();
                    }
                    lexeme = "";
                    state = 0;
                    break;
            }
        }

    }
    private char getCurrentSymbol()
    {
        if(CurrentPointer < CodeContent.length())
        {
            char current = CodeContent.charAt(CurrentPointer);
            return current;
        }
        return '\0';
    }

    private void increasePointer(){
        CurrentPointer++;
    }
}
