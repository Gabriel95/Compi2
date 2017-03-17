package ParserPK;
import Automata.LabelFactory;
import Lexer.CupLexer;
import Semantic.Nodes.*;
import Semantic.Nodes.Expression.*;
import Semantic.Nodes.Statements.StatementNode;
import Semantic.Nodes.Statements.*;
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
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: End Of File");
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
        toReturn.productionList = production_list();
        return RemoveEmbeddedActions(toReturn);
    }

    private RootNode RemoveEmbeddedActions(RootNode toReturn) {
        List<ProductionNode> toAdd = new ArrayList<>();
        for(ProductionNode prod : toReturn.productionList)
        {
            for(RhsNode rhsNode : prod.RhsTokenList)
            {
                for (int i = 0; i < rhsNode.prodPartList.size()-1; i++)
                {
                    if(rhsNode.prodPartList.get(i).JavaCodeNode!=null)
                    {
                        SymbolNode nonTerm = new NonTerminalSymbolNode();
                        NonTerminalIdNode idNonTerm = new NonTerminalIdNode();
                        String sName = LabelFactory.createLabel("$GHOST");
                        idNonTerm.Name = sName;
                        nonTerm.declarationList = new ArrayList<>();
                        nonTerm.declarationList.add(idNonTerm);
                        toReturn.symbolList.add(nonTerm);

                        ProductionNode newP = new ProductionNode();
                        newP.ntID = idNonTerm;

                        GhostRhsNode newRHS = new GhostRhsNode();
                        newRHS.ghostList = new ArrayList<>();
                        ProdPartNode eProd = new ProdPartNode();
                        eProd.symbolIdNode = new SymbolIdNode();
                        eProd.symbolIdNode.Name = "ɛ";
                        newRHS.prodPartList = new ArrayList<>();
                        newRHS.prodPartList.add(eProd);
                        newRHS.prodPartList.add(rhsNode.prodPartList.get(i));
                        rhsNode.prodPartList.remove(i);
                        for(int j = 0; j < i; j++)
                        {
                            newRHS.ghostList.add(rhsNode.prodPartList.get(j));
                        }
                        ProdPartNode ghostPart = new ProdPartNode();
                        ghostPart.symbolIdNode = new SymbolIdNode();
                        ghostPart.symbolIdNode.Name = sName;
                        rhsNode.prodPartList.add(i,ghostPart);
                        newP.RhsTokenList = new ArrayList<>();
                        newP.RhsTokenList.add(newRHS);
                        toAdd.add(newP);
                    }
                }
            }
        }
        toReturn.productionList.addAll(toAdd);
        return toReturn;
    }

    private List<ProductionNode> production_list() throws Exception {
        ProductionNode product = production();
        List<ProductionNode> production_list = production_list_prime();
        production_list.add(0,product);
        return production_list;
    }

    private List<ProductionNode> production_list_prime() throws Exception {
        if(currentToken.type == TokenType.ID){
            ProductionNode product = production();
            List<ProductionNode> production_list = production_list_prime();
            production_list.add(0,product);
            return production_list;
        }
        else
        {
            return new ArrayList<>();
        }
    }

    private ProductionNode production() throws Exception{
        NonTerminalIdNode nonTerminalIdNode = nt_id();
        if(currentToken.type != TokenType.PRODUCTION)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: ::=");
        ConsumeToken();
        List<RhsNode> rhsTokenList = rhs_list();
        if(currentToken.type != TokenType.SEMICOLON)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: SEMICOLON");
        ConsumeToken();
        ProductionNode product = new ProductionNode();
        product.RhsTokenList = rhsTokenList;
        product.ntID = nonTerminalIdNode;
        return product;
    }

    private List<RhsNode> rhs_list() throws Exception {
        RhsNode rhsNode = rhs();
        List<RhsNode> rhsNodeList = new ArrayList<>();
        if(currentToken.type == TokenType.PIPE)
        {
            ConsumeToken();
            rhsNodeList = rhs_list();
        }
        rhsNodeList.add(0,rhsNode);
        return rhsNodeList;
    }

    private RhsNode rhs() throws Exception {
        RhsNode rhsNode = new RhsNode();
        rhsNode.prodPartList = prod_part_list();
        return rhsNode;
        //TODO %
    }

    private List<ProdPartNode> prod_part_list() throws Exception {
        if(currentToken.type == TokenType.ID || currentToken.type == TokenType.JAVACODE){
            ProdPartNode prodPartNode = prod_part();
            List<ProdPartNode> prodPartNodeList = prod_part_list();
            prodPartNodeList.add(0,prodPartNode);
            return prodPartNodeList;
        }
        else
        {
            return new ArrayList<>();
        }
    }

    private ProdPartNode prod_part() throws Exception {

        ProdPartNode prodPartNode = new ProdPartNode();
        if(currentToken.type == TokenType.ID)
        {
            SymbolIdNode symbolId = symbol_id();
            symbolId.label = opt_label();
            prodPartNode.symbolIdNode = symbolId;
        }
        else if(currentToken.type == TokenType.JAVACODE)
        {
            JavaCodeNode javaCodeNode = new JavaCodeNode();
            javaCodeNode.Code = currentToken.lexeme;
            prodPartNode.JavaCodeNode = javaCodeNode;
            ConsumeToken();
        }
        else{
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: ID or JavaCode");
        }
        return prodPartNode;
    }

    private LabelIdNode opt_label() throws Exception {
        if(currentToken.type == TokenType.COLON)
        {
            ConsumeToken();
            return label_id();
        }
        else
        {
            return null;
        }
    }

    private LabelIdNode label_id() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: ID");
        LabelIdNode labelIdNode = new LabelIdNode();
        labelIdNode.Name = currentToken.lexeme;
        ConsumeToken();
        return labelIdNode;
    }

    private SymbolIdNode symbol_id() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: ID");
        SymbolIdNode symbolIdNode = new SymbolIdNode();
        symbolIdNode.Name = currentToken.lexeme;
        ConsumeToken();
        return symbolIdNode;
    }

    private void start_spec() throws Exception {
        if(currentToken.type == TokenType.RW_START)
        {
            ConsumeToken();
            if(currentToken.type != TokenType.RW_WITH)
                throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: with");
            ConsumeToken();
            nt_id();
            if(currentToken.type != TokenType.SEMICOLON)
                throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: SEMICOLON");
            ConsumeToken();
        }
        else
        {

        }
    }

    private NonTerminalIdNode nt_id() throws Exception {
//        if(currentToken.type != TokenType.ID)
//            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: ID");
//        NonTerminalIdNode nonTerminalIdNode = new NonTerminalIdNode();
//        nonTerminalIdNode.Name = currentToken.lexeme;
//        ConsumeToken();
//        return nonTerminalIdNode;
        return new_non_term_id();
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
            return term_declaration();
        }
        else //if(currentToken.type == TokenType.RW_NON)
        {
            ConsumeToken();
            if(currentToken.type != TokenType.RW_TERMINAL)
                throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: key word Terminal");
            ConsumeToken();
            NonTerminalSymbolNode nonTerminalSymbolNode;
            nonTerminalSymbolNode = non_term_declaration();
            return nonTerminalSymbolNode;
        }
    }

    private NonTerminalSymbolNode non_term_declaration() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: ID");
        String name = currentToken.lexeme;
        ConsumeToken();
        if(currentToken.type == TokenType.SEMICOLON)
        {
            NonTerminalSymbolNode nonTerminalSymbolNode = new NonTerminalSymbolNode();
            ConsumeToken();
            nonTerminalSymbolNode.className = null;
            NonTerminalIdNode nonTerminalIdNode = new NonTerminalIdNode(); /*extends IdNode*/
            nonTerminalIdNode.Name = name;
            List<IdNode> listDeclarationNode = new ArrayList<>();
            listDeclarationNode.add(nonTerminalIdNode);
            nonTerminalSymbolNode.declarationList = listDeclarationNode;
            return nonTerminalSymbolNode;
        }
        else
            return non_term_declaration_prime(name);
    }

    private NonTerminalSymbolNode non_term_declaration_prime(String name) throws Exception {
       NonTerminalSymbolNode nonTerminalSymbolNode = new NonTerminalSymbolNode();
        if(currentToken.type == TokenType.DOT)
        {
            ConsumeToken();
            DeclarationTypeNode declarationTypeNode = new DeclarationTypeNode();
            declarationTypeNode.Name = name;
            DotAccessorNode dotAccessorNode = new DotAccessorNode();
            dotAccessorNode.IdNode = multiPart_Id();
            declarationTypeNode.accessor = dotAccessorNode;
            nonTerminalSymbolNode.className = declarationTypeNode;
            nonTerminalSymbolNode.declarationList = declares_non_term();
            return nonTerminalSymbolNode;
        }
        else if (currentToken.type == TokenType.COMMA)
        {
            ConsumeToken();
            NonTerminalIdNode nonTerminalIdNode = new NonTerminalIdNode();
            nonTerminalIdNode.Name = name;
            nonTerminalSymbolNode.className = null;
            nonTerminalSymbolNode.declarationList = declares_non_term();
            nonTerminalSymbolNode.declarationList.add(0,nonTerminalIdNode);
            return nonTerminalSymbolNode;
        }
        else if(currentToken.type == TokenType.ID){
            DeclarationTypeNode declarationTypeNode = new DeclarationTypeNode();
            declarationTypeNode.Name = name;
            nonTerminalSymbolNode.className = declarationTypeNode;
            nonTerminalSymbolNode.declarationList = declares_non_term();
            return nonTerminalSymbolNode;
        }
        else
        {
            //TODO?
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: \';\' , \'.\' , \',\' or ID");
        }
    }

    private TerminalSymbolNode term_declaration() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: ID");
        String name = currentToken.lexeme;
        ConsumeToken();

        if(currentToken.type == TokenType.SEMICOLON)
        {
            TerminalSymbolNode terminalSymbolNode = new TerminalSymbolNode();
            ConsumeToken();
            terminalSymbolNode.className = null;
            TerminalIdNode declarationNode = new TerminalIdNode(); /*extends IdNode*/
            declarationNode.Name = name;
            List<IdNode> listDeclarationNode = new ArrayList<>();
            listDeclarationNode.add(declarationNode);
            terminalSymbolNode.declarationList = listDeclarationNode;
            return terminalSymbolNode;
        }
        else
        {
            return term_declaration_prime(name);
        }
    }

    private TerminalSymbolNode term_declaration_prime(String name) throws Exception {
        TerminalSymbolNode terminalSymbolNode = new TerminalSymbolNode();
        if(currentToken.type == TokenType.DOT)
        {
            ConsumeToken();
            DeclarationTypeNode declarationTypeNode = new DeclarationTypeNode();
            declarationTypeNode.Name = name;
            DotAccessorNode dotAccessorNode = new DotAccessorNode();
            dotAccessorNode.IdNode = multiPart_Id();
            declarationTypeNode.accessor = dotAccessorNode;
            terminalSymbolNode.className = declarationTypeNode;
            terminalSymbolNode.declarationList = declares_term();
            return terminalSymbolNode;
        }
        else if (currentToken.type == TokenType.COMMA)
        {
            ConsumeToken();
            TerminalIdNode terminalIdNode = new TerminalIdNode();
            terminalIdNode.Name = name;
            terminalSymbolNode.className = null;
            terminalSymbolNode.declarationList = declares_term();
            terminalSymbolNode.declarationList.add(0,terminalIdNode);
            return terminalSymbolNode;
        }
        else if(currentToken.type == TokenType.ID){
            DeclarationTypeNode declarationTypeNode = new DeclarationTypeNode();
            declarationTypeNode.Name = name;
            terminalSymbolNode.className = declarationTypeNode;
            terminalSymbolNode.declarationList = declares_term();
            return terminalSymbolNode;
        }
        else
        {
            //TODO?
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: \';\' , \'.\' , \',\' or ID");
        }
    }

    private List<IdNode> declares_non_term() throws Exception{
        List<IdNode> nonTerminalIdNodeList = non_term_name_list();
        if(currentToken.type != TokenType.SEMICOLON)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: Semicolon");
        ConsumeToken();
        return nonTerminalIdNodeList;
    }

    private List<IdNode> non_term_name_list() throws Exception{
        NonTerminalIdNode nonTerminalIdNode = new_non_term_id();
        List<IdNode> nonTerminalIdNodeList = new ArrayList<>();
        if(currentToken.type == TokenType.COMMA){
            ConsumeToken();
            nonTerminalIdNodeList = non_term_name_list();
            nonTerminalIdNodeList.add(0,nonTerminalIdNode);
            return nonTerminalIdNodeList;
        }
        nonTerminalIdNodeList.add(nonTerminalIdNode);
        return nonTerminalIdNodeList;
    }

    private NonTerminalIdNode new_non_term_id() throws Exception {
        if(currentToken.type != TokenType.ID)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: ID");
        NonTerminalIdNode nonTerminalIdNode = new NonTerminalIdNode();
        nonTerminalIdNode.Name = currentToken.lexeme;
        ConsumeToken();
        return nonTerminalIdNode;
    }

    private List<IdNode> declares_term() throws Exception {
        List<IdNode> terminalIdNodeList = term_name_list();
        if(currentToken.type != TokenType.SEMICOLON)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: Semicolon");
        ConsumeToken();
        return terminalIdNodeList;
    }

    private List<IdNode> term_name_list() throws Exception {
        TerminalIdNode terminalIdNode = new_term_id();
        List<IdNode> terminalIdNodeList = new ArrayList<>();
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
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: ID");
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
                throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: Semicolon");
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
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: ID");
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
                throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: ID");
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
                throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: ;");
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
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: ID");
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
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: * or ID");
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
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: scan");
        ConsumeToken();
        if(currentToken.type != TokenType.RW_WITH)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: with");
        ConsumeToken();
        if(currentToken.type != TokenType.RW_CODE)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: code");
        ConsumeToken();
        if(currentToken.type != TokenType.JAVACODE)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: JavaCode");
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
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: parser");
        ConsumeToken();
        if(currentToken.type != TokenType.RW_WITH)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: with");
        ConsumeToken();
        if(currentToken.type != TokenType.RW_CODE)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: code");
        ConsumeToken();
        if(currentToken.type != TokenType.JAVACODE)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: JavaCode");
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
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: parser");
        ConsumeToken();
        if(currentToken.type != TokenType.RW_CODE)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: code");
        ConsumeToken();
        if(currentToken.type != TokenType.JAVACODE)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: JavaCode");
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
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: action");
        ConsumeToken();
        if(currentToken.type != TokenType.RW_CODE)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: code");
        ConsumeToken();
        if(currentToken.type != TokenType.JAVACODE)
            throw new ParserException("Error at Line " + currentToken.Line + " Column " + currentToken.Column + " Expected: JavaCode");
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
