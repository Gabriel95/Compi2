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
        if(currentToken.type != TokenType.EOF)
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
        production_list();
    }

    private void production_list() throws Exception {
        production();
        production_list_prime();
    }

    private void production_list_prime() throws Exception {
        if(currentToken.type == TokenType.ID){
            production();
            production_list_prime();
        }
        else
        {
            //Epsilon
        }
    }

    private void production() throws Exception{
        nt_id();
        if(currentToken.type != TokenType.PRODUCTION)
            throw new ParserException("Expected: ::=");
        ConsumeToken();
        rhs_list();
        if(currentToken.type != TokenType.SEMICOLON)
            throw new ParserException("Expected: SEMICOLON");
        ConsumeToken();
    }

    private void rhs_list() throws Exception {
        rhs();
        if(currentToken.type == TokenType.PIPE)
        {
            ConsumeToken();
            rhs_list();
        }
    }

    private void rhs() throws Exception {
        prod_part_list();
        //TODO %
    }

    private void prod_part_list() throws Exception {
        if(currentToken.type == TokenType.ID || currentToken.type == TokenType.JAVACODE){
            prod_part();
            prod_part_list();
        }
        else
        {

        }
    }

    private void prod_part() throws Exception {
        if(currentToken.type == TokenType.ID)
        {
            symbol_id();
            opt_label();
        }
        else if(currentToken.type == TokenType.JAVACODE)
        {
            ConsumeToken();
        }

    }

    private void opt_label() throws Exception {
        if(currentToken.type == TokenType.COLON)
        {
            ConsumeToken();
            label_id();
        }
        else
        {

        }
    }

    private void label_id() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Expected: ID");
        ConsumeToken();
    }

    private void symbol_id() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Expected: ID");
        ConsumeToken();
    }

    private void start_spec() throws Exception {
        if(currentToken.type == TokenType.RW_START)
        {
            ConsumeToken();
            if(currentToken.type != TokenType.RW_WITH)
                throw new ParserException("Expected: with");
            ConsumeToken();
            nt_id();
            if(currentToken.type != TokenType.SEMICOLON)
                throw new ParserException("Expected: SEMICOLON");
            ConsumeToken();
        }
        else
        {

        }
    }

    private void nt_id() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Expected: ID");
        ConsumeToken();
    }

    private void presedence_list() {
        //TODO
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
            term_declaration();
        }
        else if(currentToken.type == TokenType.RW_NON){
            ConsumeToken();
            if(currentToken.type != TokenType.RW_TERMINAL)
                throw new ParserException("Expected: key word Terminal");
            ConsumeToken();
            non_term_declaration();
        }
    }

    private void non_term_declaration() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Expected: ID");
        ConsumeToken();
        if(currentToken.type == TokenType.SEMICOLON)
            ConsumeToken();
        else
            non_term_declaration_prime();
    }

    private void non_term_declaration_prime() throws Exception {
        if(currentToken.type == TokenType.DOT)
        {
            ConsumeToken();
            multiPart_Id();
            declares_non_term();
        }
        else if (currentToken.type == TokenType.COMMA)
        {
            ConsumeToken();
            declares_non_term();
        }
        else if(currentToken.type == TokenType.ID){
            declares_non_term();
        }
        else
        {

        }
    }

    private void term_declaration() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Expected: ID");
        ConsumeToken();
        if(currentToken.type == TokenType.SEMICOLON)
            ConsumeToken();
        else term_declaration_prime();
    }

    private void term_declaration_prime() throws Exception {
        if(currentToken.type == TokenType.DOT)
        {
            ConsumeToken();
            multiPart_Id();
            declares_term();
        }
        else if (currentToken.type == TokenType.COMMA)
        {
            ConsumeToken();
            declares_term();
        }
        else if(currentToken.type == TokenType.ID){
            declares_term();
        }
        else
        {

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
        multiPart_Id_prime();
    }

    private void multiPart_Id_prime() throws Exception {
        if(currentToken.type == TokenType.DOT)
        {
            ConsumeToken();
            if(currentToken.type != TokenType.ID)
                throw new ParserException("Expected: ID");
            ConsumeToken();
            multiPart_Id_prime();
        }
        else
        {

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
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Expected: ID");
        ConsumeToken();
        import_id_prime();
    }

    private void import_id_prime() throws Exception {
        if(currentToken.type == TokenType.DOT){
            ConsumeToken();
            id_or_star();
        }
        else{
            //Epsilon
        }
    }

    private void id_or_star() throws Exception {
        if(currentToken.type == TokenType.ID)
        {
            ConsumeToken();
            import_id_prime();
        }else if(currentToken.type == TokenType.ASTERISKS){
            ConsumeToken();
        }else
        {
            throw new ParserException("Expected * or ID");
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
