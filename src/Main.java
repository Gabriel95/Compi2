import java.io.File;
import java.io.FileInputStream;

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

        Token t = lexer.getNextToken();
        while(t.type != TokenType.EOF)
        {
            System.out.println(t.lexeme + " " + t.type);
            t = lexer.getNextToken();
        }


    }
}
