import Automata.AutomataNode;
import Automata.AutomataService;
import Automata.GrammarLine;
import Automata.GrammarService;
import FileGeneration.FileGenerationService;
import Lexer.*;
import ParserPK.Parser;
import Semantic.Nodes.Statements.ProductionNode;
import Semantic.Nodes.Statements.RootNode;
import Semantic.Nodes.Statements.StatementNode;
import Semantic.Nodes.Statements.SymbolNode;
import Table.TableService;
import com.google.common.collect.RowSortedTable;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        catch(Exception e)
        {
            System.out.print("error:" + e.getMessage());
        }

        CupLexer lexer = new CupLexer(cupFileContent);
        Parser parser = new Parser(lexer);
        StatementNode list = parser.Parse();
        list.EvaluateSemantic();
        List<ProductionNode> f = new ArrayList<>(((RootNode)list).productionList);
        List<ProductionNode> f2 = new ArrayList<>(((RootNode)list).productionList);
        List<SymbolNode> symbolList = new ArrayList<>(((RootNode)list).symbolList);
        Map<String,String> TypeTable= GrammarService.GetTypeTable(symbolList);
        List<AutomataNode> automata = AutomataService.GetAutomata(f);
        List<GrammarLine> grammarLines = GrammarService.GetNonSimplifiedGrammarTable(f2,TypeTable);
        RowSortedTable<String, String, String> table =  TableService.GetTable(automata,grammarLines);
        String t = new GsonBuilder().setPrettyPrinting().create().toJson(table);
        FileGenerationService.generateSymClass();
        FileGenerationService.generateParser(table,grammarLines);

        //Print Grammar
//        for(int i = 0; i < grammarLines.size(); i++)
//        {
//            GrammarLine temp = grammarLines.get(i);
//            System.out.printf("%d. %s -> ",(i+1),temp.Producer);
//            for (String s : temp.Productions){
//                System.out.print(s + " ");
//            }
//            System.out.println("");
//        }
//        System.out.println(t);
//        System.out.println("SUCCESS!");

    }
}
