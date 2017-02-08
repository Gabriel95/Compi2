public class CupLexer {
    private String CodeContent;
    private int CurrentPointer;


    public CupLexer(String codeContent, int currentPointer)
    {
        CodeContent = codeContent;
        CurrentPointer = currentPointer;
    }

    public Token getNextToken()
    {
        String lexeme = "";
        char currentChar = getCurrentSymbol();
        increasePointer();
        int state = 0;
        while (true)
        {
            switch (state)
            {
                //initial node
                case 0:
                    if(Character.isAlphabetic(currentChar))
                    {
                        lexeme += currentChar;
                        state = 1;
                        currentChar = getCurrentSymbol();
                        increasePointer();
                    }
                    if(TokenDictionary.SymbolDictionary.containsKey(""+currentChar));
                    {
                        state = 2;
                    }
                    break;
                //ID
                case 1:
                    if(Character.isLetterOrDigit(currentChar) || currentChar == '_')
                    {
                        lexeme += currentChar;
                        currentChar = getCurrentSymbol();
                        increasePointer();

                    }
                    else
                    {
                        return new Token(lexeme,TokenType.ID);
                    }
                //dot
                case 2:
                    lexeme += currentChar;
                    return new Token (lexeme,TokenType.ID);
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
