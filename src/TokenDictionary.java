import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gabriel Paz on 07-Feb-17.
 */
public class TokenDictionary {
    public static Map<String, TokenType> SymbolDictionary ;
    static {
        SymbolDictionary.put(".",TokenType.DOT);
    }
}
