import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gabriel Paz on 07-Feb-17.
 */
public class TokenDictionary {
    public  Map<String, TokenType> SymbolDictionary;
    public  Map<String, TokenType> ReservedWords;
    public TokenDictionary() {
        SymbolDictionary = new HashMap<>();
        ReservedWords = new HashMap<>();

        SymbolDictionary.put(".",TokenType.DOT);
        SymbolDictionary.put(";",TokenType.SEMICOLON);
        SymbolDictionary.put("{",TokenType.OPEN_BRACE);
        SymbolDictionary.put("}",TokenType.CLOSE_BRACE);
        SymbolDictionary.put("(",TokenType.OPEN_PAREN);
        SymbolDictionary.put(")",TokenType.CLOSE_PAREN);
        SymbolDictionary.put("|",TokenType.PIPE);
        SymbolDictionary.put(",",TokenType.COMMA);
        SymbolDictionary.put(":",TokenType.COLON);
        SymbolDictionary.put("*",TokenType.ASTERISKS);

        ReservedWords.put("import",TokenType.RW_IMPORT);
        ReservedWords.put("parser",TokenType.RW_PARSER);
        ReservedWords.put("code",TokenType.RW_CODE);
        ReservedWords.put("terminal",TokenType.RW_TERMINAL);
        ReservedWords.put("non",TokenType.RW_NON);
        ReservedWords.put("Integer",TokenType.RW_INTEGER);
        ReservedWords.put("Object",TokenType.RW_OBJECT);
        ReservedWords.put("String",TokenType.RW_STRING);
        ReservedWords.put("precedence",TokenType.RW_PRECEDENCE);
        ReservedWords.put("left",TokenType.RW_LEFT);
        ReservedWords.put("right",TokenType.RW_RIGHT);
        ReservedWords.put("init",TokenType.RW_INIT);
        ReservedWords.put("with",TokenType.RW_WITH);
        ReservedWords.put("scan",TokenType.RW_SCAN);
        ReservedWords.put("nonassoc",TokenType.RW_NONASSOC);
        ReservedWords.put("package",TokenType.RW_PACKAGE);
        ReservedWords.put("action",TokenType.RW_ACTION);
    }
}
