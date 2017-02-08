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
                case 0:
                    if(Character.isAlphabetic(currentChar))
                    {
                        lexeme += currentChar;
                        state = 1;
                        currentChar = getCurrentSymbol();
                        increasePointer();
                    }
                    break;
                case 1:
                    if(Character.isAlphabetic(currentChar) || Character.isDigit(currentChar) || currentChar == '_')
                    {
                        lexeme += currentChar;
                        currentChar = getCurrentSymbol();
                        increasePointer();

                    }
                    else
                    {
                        return new Token(lexeme,TokenType.ID);
                    }
                case 2:
                    System.out.println(currentChar);

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
