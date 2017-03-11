package FileGeneration;
import Automata.GrammarLine;
import Semantic.Types.SymbolTable;
import Semantic.Types.Terminal;
import com.google.common.collect.RowSortedTable;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created by jpaz on 3/6/17.
 */
public class FileGenerationService {

    public static void generateSymClass()
    {
        int count = 2;
        String initial = "public class sym\n{\n\tpublic static final int EOF = 0;\n\tpublic static final int error = 1;\n";
        String terminals = "\tpublic static final String[] terminalNames = new String[]\n\t{\n\t\t\"$\",\n\t\t\"error\",\n";
        String variableDeclaration = "\tpublic static final int ";
        StringBuilder sb = new StringBuilder();
        sb.append(initial);

        for (Object o : SymbolTable.Instance().variables.entrySet())
        {
            Map.Entry pair = (Map.Entry) o;
            if(pair.getValue() instanceof Terminal)
            {
                sb.append(variableDeclaration + pair.getKey() + " = " + count + ";\n");
                count++;

            }
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
        writeToFile("sym.java",sb.toString());
    }

    public static void generateParser(RowSortedTable<String, String, String> table, List<GrammarLine> grammarLines)
    {
        String top = "";
        String bottom = "";
        StringBuilder sb = new StringBuilder();
        try
        {
            File file = new File("src//FileGeneration//ParserTopTemplate");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            top = new String(data, "UTF-8");

            file = new File("src//FileGeneration//ParserBottomTemplate");
            fis = new FileInputStream(file);
            data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            bottom = new String(data, "UTF-8");
        }
        catch(Exception e){
            System.out.print("error:" + e.getMessage());
        }
        sb.append(top);
        //Create table in constructor
        for (String row : table.rowKeySet())
        {
            for(String column : table.columnKeySet())
            {
                if(table.contains(row,column))
                {
                    String s = "\t\ttable.put(\"" + row + "\", \"" + column + "\", \"" + table.get(row,column) + "\");\n";
                    sb.append(s);
                }
            }
        }

        //Create Grammar in constructor
        for(GrammarLine grammarLine : grammarLines)
        {
            String s = "\t\tgrammarLines.add(new GrammarLine(\"" + grammarLine.Producer + "\",new ArrayList<>(Arrays.asList(";
            String suffix = "\"";
            for(String p : grammarLine.Productions)
            {
                s += suffix;
                suffix = "\", \"";
                s += p;
            }
            s += "\"))));\n";
            sb.append(s);
        }
        sb.append(bottom);
        writeToFile("parser.java",sb.toString());
    }

    private static void writeToFile(String fileName, String toWrite)
    {
        try {

            File file = new File(fileName);

            if (file.createNewFile()){
                System.out.println("File was created!");
            }else{
                file.delete();
                file.createNewFile();
                System.out.println("File was created!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);
            bw.write(toWrite);

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
