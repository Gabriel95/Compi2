import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jpaz on 3/6/17.
 */
public class TestMain {
    public static void main(String args[]){
        try {
            Lex lex = new Lex(new FileReader("src//input.txt"));
            try
            {
                parser p = new parser(lex);
                p.parse();
                System.out.println("Success!");
            } catch (Exception ex) {
                Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
