package ParserPK;
import Lexer.CupLexer;
import Tokens.Token;
import Tokens.TokenType;

/**
 * Created by jpaz on 2/8/17.
 */
public class Parser {

    private CupLexer lexer;
    public Token currentToken ;

    public Parser(CupLexer lexer) throws Exception {
        this.lexer = lexer;
        currentToken = this.lexer.getNextToken();
    }

    public void Parse() throws Exception {
        Code();
        if(currentToken.type != TokenType.EOF);
            throw new ParserException("Expected End Of File");
    }

    private void Code() throws Exception {
        if(currentToken.type == TokenType.RW_PACKAGE || currentToken.type == TokenType.RW_IMPORT || currentToken.type == TokenType.RW_ACTION ||
                currentToken.type == TokenType.RW_PARSER || currentToken.type == TokenType.RW_INIT || currentToken.type == TokenType.RW_SCAN
                || currentToken.type == TokenType.RW_TERMINAL || currentToken.type == TokenType.RW_NON)
        {
            java_cup_spec();
        }else{

        }
    }

    private void java_cup_spec() throws Exception {
        package_spec();
        import_list();
        code_parts();
        symbol_list();
        presedence_list();
        start_spec();
    }

    private void symbol_list() throws Exception{
        symbol();
        if(currentToken.type == TokenType.RW_TERMINAL || currentToken.type == TokenType.RW_NON)
        {
            symbol_list();
        }
    }

    private void symbol() throws Exception{
        if(currentToken.type == TokenType.RW_TERMINAL)
        {
            ConsumeToken();
            type_id();
            declares_term();
        }
        else if(currentToken.type == TokenType.RW_NON){
            ConsumeToken();
            if(currentToken.type != TokenType.RW_TERMINAL)
                throw new ParserException("Expected: key word Terminal");
            type_id();
            declares_non_term();
        }
    }

    private void declares_non_term() throws Exception{
        non_term_name_list();
        if(currentToken.type != TokenType.SEMICOLON)
            throw new ParserException("Expected: Semicolon");
        ConsumeToken();
    }

    private void non_term_name_list() throws Exception{
        new_non_term_id();
        if(currentToken.type == TokenType.COMMA){
            ConsumeToken();
            non_term_name_list();
        }
    }

    private void new_non_term_id() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Expected: ID");
        ConsumeToken();
    }

    private void declares_term() throws Exception {
        term_name_list();
        if(currentToken.type != TokenType.SEMICOLON)
            throw new ParserException("Expected: Semicolon");
        ConsumeToken();
    }

    private void term_name_list() throws Exception {
        new_term_id();
        if(currentToken.type == TokenType.COMMA){
            ConsumeToken();
            term_name_list();
        }
    }

    private void new_term_id() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Expected: ID");
        ConsumeToken();
    }

    private void type_id() throws Exception {
        multiPart_Id();
    }

    private void package_spec() throws Exception {
        if(currentToken.type == TokenType.RW_PACKAGE)
        {
            ConsumeToken();
            multiPart_Id();
            if(currentToken.type != TokenType.SEMICOLON)
                throw new ParserException("Expected: Semicolon");
            ConsumeToken();

        }
        else
        {

        }
    }

    private void multiPart_Id() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Expected: ID");
        ConsumeToken();
        if(currentToken.type == TokenType.DOT){
            ConsumeToken();
            multiPart_Id();
        }
    }

    private void import_list() throws Exception {
        if(currentToken.type == TokenType.RW_IMPORT){
            ConsumeToken();
            import_id();
            if(currentToken.type != TokenType.SEMICOLON)
                throw new ParserException("Expected: ;");
            ConsumeToken();
        }
        else
        {

        }
    }

    private void import_id() throws Exception {
        multiPart_Id();
        if(currentToken.type == TokenType.DOT)
        {
            ConsumeToken();
            if(currentToken.type != TokenType.ASTERISKS)
                throw new ParserException("Expected: *");
            ConsumeToken();
        }

    }

    private void code_parts() throws Exception {
        if( currentToken.type == TokenType.RW_ACTION || currentToken.type == TokenType.RW_PARSER
                || currentToken.type == TokenType.RW_INIT || currentToken.type == TokenType.RW_SCAN)
        {
            code_part();
            code_parts();
        }
        else
        {

        }
    }

    private void code_part() throws Exception {
        if(currentToken.type == TokenType.RW_ACTION)
            action_code_part();
        else if(currentToken.type == TokenType.RW_PARSER)
            parser_code_part();
        else if(currentToken.type == TokenType.RW_INIT)
            init_code();
        else if(currentToken.type == TokenType.RW_SCAN)
            scan_code();
    }

    private void scan_code() throws Exception {
        if(currentToken.type != TokenType.RW_SCAN)
            throw new ParserException("Expected: scan");
        ConsumeToken();
        if(currentToken.type != TokenType.RW_WITH)
            throw new ParserException("Expected: with");
        ConsumeToken();
        if(currentToken.type != TokenType.RW_CODE)
            throw new ParserException("Expected: code");
        ConsumeToken();
        if(currentToken.type != TokenType.JAVACODE)
            throw new ParserException("Expected: JavaCode");
        ConsumeToken();
        opt_semi();
    }

    private void init_code() throws Exception {
        if(currentToken.type != TokenType.RW_INIT)
            throw new ParserException("Expected: parser");
        ConsumeToken();
        if(currentToken.type != TokenType.RW_WITH)
            throw new ParserException("Expected: with");
        ConsumeToken();
        if(currentToken.type != TokenType.RW_CODE)
            throw new ParserException("Expected: code");
        ConsumeToken();
        if(currentToken.type != TokenType.JAVACODE)
            throw new ParserException("Expected: JavaCode");
        ConsumeToken();
        opt_semi();
    }

    private void parser_code_part() throws Exception {
        if(currentToken.type != TokenType.RW_PARSER)
            throw new ParserException("Expected: parser");
        ConsumeToken();
        if(currentToken.type != TokenType.RW_CODE)
            throw new ParserException("Expected: code");
        ConsumeToken();
        if(currentToken.type != TokenType.JAVACODE)
            throw new ParserException("Expected: JavaCode");
        ConsumeToken();
        opt_semi();
    }

    private void action_code_part() throws Exception {
        if(currentToken.type != TokenType.RW_ACTION)
            throw new ParserException("Expected: action");
        ConsumeToken();
        if(currentToken.type != TokenType.RW_CODE)
            throw new ParserException("Expected: code");
        ConsumeToken();
        if(currentToken.type != TokenType.JAVACODE)
            throw new ParserException("Expected: JavaCode");
        ConsumeToken();
        opt_semi();

    }

    private void opt_semi() throws Exception {
        if(currentToken.type == TokenType.SEMICOLON){
            ConsumeToken();
        }else{

        }
    }

    private void ConsumeToken() throws Exception {
        currentToken = lexer.getNextToken();
    }

}
