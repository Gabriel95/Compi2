import Lexer.CupLexer;
import ParserPK.Parser;
import ParserPK.StatementNode;
import Tokens.Token;
import Tokens.TokenType;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class Main {
    public static void main(String args[]) throws Exception {
        String cupFileContent = "";
        try{
            File file = new File("src//test.cup");
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
        try {
            StatementNode list = parser.Parse();
            //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(list));
            System.out.println("SUCCESS!");
        }
        catch (Exception e)
        {
            System.out.print(e.getMessage());
        }

    }
}
