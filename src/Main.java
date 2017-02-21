import Lexer.*;
import ParserPK.Parser;
import Semantic.Nodes.Expression.ProdPartNode;
import Semantic.Nodes.Statements.ProductionNode;
import Semantic.Nodes.Statements.RootNode;
import Semantic.Nodes.Statements.StatementNode;
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
            String n = new GsonBuilder().setPrettyPrinting().create().toJson(list);
            List<ProductionNode> f = new ArrayList<>();
            List<ProductionNode> ff = new ArrayList<>();
            f.addAll(((RootNode)list).productionList);
            ff.addAll(((RootNode)list).productionList);
            Map<String, List<String>> firstTable =  GrammarService.FirstTable(f);
            Map<String, List<String>> followTable = GrammarService.FollowTable(ff);
            System.out.println(n);
            System.out.println("SUCCESS!");


    }
}
