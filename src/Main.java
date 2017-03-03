import Automata.AutomataNode;
import Automata.AutomataService;
import Automata.GrammarLine;
import Automata.GrammarService;
import Lexer.*;
import ParserPK.Parser;
import Semantic.Nodes.Statements.ProductionNode;
import Semantic.Nodes.Statements.RootNode;
import Semantic.Nodes.Statements.StatementNode;
import Table.TableService;
import com.google.common.collect.RowSortedTable;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String args[]) throws Exception {
        String cupFileContent = "";
        try
        {
            File file = new File("src//test2.txt");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            cupFileContent = new String(data, "UTF-8");
        }
        catch(Exception e){
            System.out.print("error:" + e.getMessage());
        }
        CupLexer lexer = new CupLexer(cupFileContent);

//        Token t = lexer.getNextToken();
//        while(t.type != TokenType.EOF){
//            System.out.println(t.lexeme + " " + t.type);
//            t = lexer.getNextToken();
//        }
        Parser parser = new Parser(lexer);

            StatementNode list = parser.Parse();
            list.EvaluateSemantic();
//            String n = new GsonBuilder().setPrettyPrinting().create().toJson(list);
            List<ProductionNode> f = new ArrayList<>();
            f.addAll(((RootNode)list).productionList);

            List<ProductionNode> f2 = new ArrayList<>(((RootNode)list).productionList);

            List<AutomataNode> automata = AutomataService.GetAutomata(f);
            List<GrammarLine> grammarLines = GrammarService.GetNonSimplifiedGrammarTable(f2);
            RowSortedTable<String, String, String> table =  TableService.GetTable(automata,grammarLines);
            String n = new GsonBuilder().setPrettyPrinting().create().toJson(table);
            System.out.println(n);
            System.out.println("SUCCESS!");


    }
}
