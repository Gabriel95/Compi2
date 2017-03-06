import Automata.GrammarLine;
import Semantic.Types.CustomType;
import Semantic.Types.SymbolTable;
import Semantic.Types.Terminal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by jpaz on 3/6/17.
 */
public class FileGenerationService {

    public static void generateSymClass() {
        int count = 2;
        String initial = "public class sym\n{\n\tpublic static final int EOF = 0;\n\tpublic static final int error = 1;\n";
        String terminals = "\tpublic static final String[] terminalNames = new String[]\n\t{\n";
        String variableDeclaration = "\tpublic static final int ";
        StringBuilder sb = new StringBuilder();
        sb.append(initial);

        for (Object o : SymbolTable.Instance().variables.entrySet())
        {
            Map.Entry pair = (Map.Entry) o;
            if(pair.getValue() instanceof Terminal)
            {
                sb.append(variableDeclaration + pair.getKey() + " = " + count + ";\n");
            }
            count++;
        }
        sb.append(terminals);
        String prefix = "";
        for (Object o : SymbolTable.Instance().variables.entrySet())
        {
            Map.Entry pair = (Map.Entry) o;
            if(pair.getValue() instanceof Terminal)
            {
                sb.append(prefix);
                prefix = ",\n";
                sb.append("\t\t\"" + pair.getKey() + "\"");
            }
        }
        sb.append("\n\t};\n}");

        try {

            File file = new File("sym.java");

            if (file.createNewFile()){
//                System.out.println("File is created!");
            }else{
                file.delete();
                file.createNewFile();
//                System.out.println("File is created!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            fw = new FileWriter("sym.java");
            bw = new BufferedWriter(fw);
            bw.write(sb.toString());

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
    }
}
