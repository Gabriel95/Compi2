package ParserPK;
import Lexer.CupLexer;
import Tokens.Token;
import Tokens.TokenType;

import java.util.ArrayList;
import java.util.List;

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

    public StatementNode Parse() throws Exception
    {
        StatementNode node = Code();
        if(currentToken.type != TokenType.EOF)
            throw new ParserException("Expected End Of File");
        return node;
    }

    private StatementNode Code() throws Exception {
        if(currentToken.type == TokenType.RW_PACKAGE || currentToken.type == TokenType.RW_IMPORT || currentToken.type == TokenType.RW_ACTION ||
                currentToken.type == TokenType.RW_PARSER || currentToken.type == TokenType.RW_INIT || currentToken.type == TokenType.RW_SCAN
                || currentToken.type == TokenType.RW_TERMINAL || currentToken.type == TokenType.RW_NON)
        {
            return java_cup_spec();
        }else{
            return new RootNode();
        }
    }

    private RootNode java_cup_spec() throws Exception {
        RootNode toReturn = new RootNode();
        toReturn.packageNode = package_spec();
        toReturn.importList = import_list();
        toReturn.codeParts = code_parts();
        toReturn.symbolList = symbol_list();
//        presedence_list();
//        start_spec();
//        production_list();
        return toReturn;
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

    private List<SymbolNode> symbol_list() throws Exception{
        SymbolNode symbolNode = symbol();
        if(currentToken.type == TokenType.RW_TERMINAL || currentToken.type == TokenType.RW_NON)
        {
            List<SymbolNode> toReturn = symbol_list();
            toReturn.add(0,symbolNode);
            return toReturn;
        }
        List<SymbolNode> toReturn = new ArrayList<>();
        toReturn.add(symbolNode);
        return toReturn;
    }

    private SymbolNode symbol() throws Exception{
        if(currentToken.type == TokenType.RW_TERMINAL)
        {
            ConsumeToken();
            TerminalSymbolNode terminalSymbolNode = term_declaration();
            return terminalSymbolNode;
        }
        else //if(currentToken.type == TokenType.RW_NON)
        {
            ConsumeToken();
            if(currentToken.type != TokenType.RW_TERMINAL)
                throw new ParserException("Expected: key word Terminal");
            ConsumeToken();
            symbolNode.declarationNode = non_term_declaration();
            return symbolNode;
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

    private TerminalSymbolNode term_declaration() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Expected: ID");
        String name = currentToken.lexeme;
        ConsumeToken();

        if(currentToken.type == TokenType.SEMICOLON)
        {
            TerminalSymbolNode terminalSymbolNode = new TerminalSymbolNode();
            ConsumeToken();
            terminalSymbolNode.className = null;
            TerminalIdNode declarationNode = new TerminalIdNode(); /*extends IdNode*/
            declarationNode.Name = name;
            List<TerminalIdNode> listDeclarationNode = new ArrayList<>();
            listDeclarationNode.add(declarationNode);
            terminalSymbolNode.declarationList = listDeclarationNode;
            return terminalSymbolNode;
        }
        else
        {
            term_declaration_prime(name);
        }
    }

    private TerminalSymbolNode term_declaration_prime(String name) throws Exception {
        if(currentToken.type == TokenType.DOT)
        {
            ConsumeToken();
            TerminalSymbolNode terminalSymbolNode = new TerminalSymbolNode();
            DeclarationTypeNode declarationTypeNode = new DeclarationTypeNode();
            declarationTypeNode.Name = name;
            DotAccessorNode dotAccessorNode = new DotAccessorNode();
            dotAccessorNode.IdNode = multiPart_Id();
            declarationTypeNode.accessor = dotAccessorNode;
            terminalSymbolNode.className = declarationTypeNode;
            terminalSymbolNode.declarationList = declares_term();
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

    private List<TerminalIdNode> declares_term() throws Exception {
        List<TerminalIdNode> terminalIdNodeList = term_name_list();
        if(currentToken.type != TokenType.SEMICOLON)
            throw new ParserException("Expected: Semicolon");
        ConsumeToken();
        return terminalIdNodeList;
    }

    private List<TerminalIdNode> term_name_list() throws Exception {
        TerminalIdNode terminalIdNode = new_term_id();
        List<TerminalIdNode> terminalIdNodeList = new ArrayList<>();
        if(currentToken.type == TokenType.COMMA){
            ConsumeToken();
            terminalIdNodeList = term_name_list();
            terminalIdNodeList.add(0,terminalIdNode);
            return terminalIdNodeList;
        }
        terminalIdNodeList.add(terminalIdNode);
        return terminalIdNodeList;
    }

    private TerminalIdNode new_term_id() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Expected: ID");
        TerminalIdNode terminalIdNode = new TerminalIdNode();
        terminalIdNode.Name = currentToken.lexeme;
        ConsumeToken();
        return terminalIdNode;
    }

    private void type_id() throws Exception {

    }

    private PackageNode package_spec() throws Exception {
        if(currentToken.type == TokenType.RW_PACKAGE)
        {
            ConsumeToken();
            IdNode idNode = multiPart_Id();
            if(currentToken.type != TokenType.SEMICOLON)
                throw new ParserException("Expected: Semicolon");
            ConsumeToken();
            PackageNode packageNode = new PackageNode();
            packageNode.IdNode = idNode;
            return packageNode;
        }
        else
        {
            return new PackageNode();
        }
    }

    private IdNode multiPart_Id() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Expected: ID");
        NormalIdNode idNode = new NormalIdNode();
        idNode.Name = currentToken.lexeme;
        ConsumeToken();
        idNode.accessor = multiPart_Id_prime();
        return idNode;
    }

    private AccessorNode multiPart_Id_prime() throws Exception {
        if(currentToken.type == TokenType.DOT)
        {
            ConsumeToken();
            DotAccessorNode dotAccessorNode = new DotAccessorNode();
            if(currentToken.type != TokenType.ID)
                throw new ParserException("Expected: ID");
            NormalIdNode idNode = new NormalIdNode();
            idNode.Name = currentToken.lexeme;
            ConsumeToken();
            idNode.accessor = multiPart_Id_prime();
            dotAccessorNode.IdNode = idNode;
            return dotAccessorNode;
        }
        else
        {
            return null;
        }
    }

    private List<ImportIdNode> import_list() throws Exception {
        if(currentToken.type == TokenType.RW_IMPORT){
            ConsumeToken();
            ImportIdNode importIdNode = import_id();
            if(currentToken.type != TokenType.SEMICOLON)
                throw new ParserException("Expected: ;");
            ConsumeToken();
            List<ImportIdNode> listToReturn = import_list();
            listToReturn.add(0,importIdNode);
            return listToReturn;
        }
        else
        {
            return new ArrayList<>();
        }
    }

    private ImportIdNode import_id() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Expected: ID");
        ImportIdNode importIdNode = new ImportIdNode();
        importIdNode.Name = currentToken.lexeme;
        ConsumeToken();
        importIdNode.accessor = import_id_prime();
        return importIdNode;
    }

    private AccessorNode import_id_prime() throws Exception {
        if(currentToken.type == TokenType.DOT){
            ConsumeToken();
            DotAccessorNode dotAccessorNode = new DotAccessorNode();
            dotAccessorNode.IdNode = id_or_star();
            return dotAccessorNode;
        }
        else{
            return null;
        }
    }

    private IdNode id_or_star() throws Exception {
        if(currentToken.type == TokenType.ID)
        {
            NormalIdNode normalIdNode = new NormalIdNode();
            normalIdNode.Name = currentToken.lexeme;
            ConsumeToken();
            normalIdNode.accessor = import_id_prime();
            return normalIdNode;
        }
        else if(currentToken.type == TokenType.ASTERISKS)
        {
            ConsumeToken();
            return new StarId();
        }
        else
        {
            throw new ParserException("Expected * or ID");
        }
    }

    private List<CodePartNode> code_parts() throws Exception {
        if( currentToken.type == TokenType.RW_ACTION || currentToken.type == TokenType.RW_PARSER
                || currentToken.type == TokenType.RW_INIT || currentToken.type == TokenType.RW_SCAN)
        {
            CodePartNode codePartNode = code_part();
            List<CodePartNode> listToReturn = code_parts();
            listToReturn.add(0,codePartNode);
            return listToReturn;
        }
        else
        {
            return new ArrayList<>();
        }
    }

    private CodePartNode code_part() throws Exception {
        if(currentToken.type == TokenType.RW_ACTION)
            return action_code_part();
        else if(currentToken.type == TokenType.RW_PARSER)
            return parser_code_part();
        else if(currentToken.type == TokenType.RW_INIT)
            return init_code();
        else //(currentToken.type == TokenType.RW_SCAN)
            return scan_code();
    }

    private ScanCodeNode scan_code() throws Exception {
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
        JavaCodeNode javaCodeNode = new JavaCodeNode();
        javaCodeNode.Code = currentToken.lexeme;
        ConsumeToken();
        opt_semi();
        ScanCodeNode scanCodeNode = new ScanCodeNode();
        scanCodeNode.javaCodeNode = javaCodeNode;
        return scanCodeNode;
    }

    private InitCodeNode init_code() throws Exception {
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
        JavaCodeNode javaCodeNode = new JavaCodeNode();
        javaCodeNode.Code = currentToken.lexeme;
        ConsumeToken();
        opt_semi();
        InitCodeNode initCodeNode = new InitCodeNode();
        initCodeNode.javaCodeNode = javaCodeNode;
        return initCodeNode;
    }

    private ParserCodePartNode parser_code_part() throws Exception {
        if(currentToken.type != TokenType.RW_PARSER)
            throw new ParserException("Expected: parser");
        ConsumeToken();
        if(currentToken.type != TokenType.RW_CODE)
            throw new ParserException("Expected: code");
        ConsumeToken();
        if(currentToken.type != TokenType.JAVACODE)
            throw new ParserException("Expected: JavaCode");
        JavaCodeNode javaCodeNode = new JavaCodeNode();
        javaCodeNode.Code = currentToken.lexeme;
        ConsumeToken();
        opt_semi();
        ParserCodePartNode parserCodePartNode = new ParserCodePartNode();
        parserCodePartNode.javaCodeNode = javaCodeNode;
        return parserCodePartNode;
    }

    private ActionCodePartNode action_code_part() throws Exception {
        if(currentToken.type != TokenType.RW_ACTION)
            throw new ParserException("Expected: action");
        ConsumeToken();
        if(currentToken.type != TokenType.RW_CODE)
            throw new ParserException("Expected: code");
        ConsumeToken();
        if(currentToken.type != TokenType.JAVACODE)
            throw new ParserException("Expected: JavaCode");
        JavaCodeNode javaCodeNode = new JavaCodeNode();
        javaCodeNode.Code = currentToken.lexeme;
        ConsumeToken();
        opt_semi();
        ActionCodePartNode actionCodePartNode = new ActionCodePartNode();
        actionCodePartNode.javaCodeNode = javaCodeNode;
        return actionCodePartNode;
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
