package FileGeneration;
import Automata.GrammarLine;
import Automata.Label;
import Semantic.Nodes.Expression.ImportIdNode;
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

    public static void generateParser(RowSortedTable<String, String, String> table, List<GrammarLine> grammarLines, List<ImportIdNode> imports)
    {
        String top = "";
        String bottom = "";
        StringBuilder sb = new StringBuilder();
        for (ImportIdNode importIdNode : imports)
        {
            sb.append("import " + importIdNode.getName() + "\n");
        }
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

        //Creating Reduction Cases
        for(int i = 0; i < grammarLines.size(); i++)
        {
            String s = "\n\t\t\tcase " + (i + 1) + ":\n\t\t\t{";
            for (Label lable : grammarLines.get(i).labelList)
            {
                s = s + "\n\t\t\t\t" + lable.Type + " " + lable.Name + ";" ;
            }

            for (Label lable : grammarLines.get(i).labelList)
            {
                s = s + "\n\t\t\t\t" + lable.Name + " = ";
                if(!lable.Type.equals("Object"))
                {
                    s = s + "(" + lable.Type + ") ";
                }
                s = s + "stack.elementAt(stack.size() - " + (2 * (grammarLines.get(i).Productions.size() - lable.position)) + ");";
            }
            s = s + "\n\t\t\t\tPopStack(magnitude);";

            for(String javaCode : grammarLines.get(i).javaCodeList)
            {
                s = s + "\n\t\t\t\t" + javaCode;
            }
            s = s + "\n\t\t\t\tstack.push(RESULT);\n\t\t\t\treturn;\n\t\t\t}";
            sb.append(s);
        }
        sb.append("\n\t\t\tdefault:\n\t\t\t\treturn;\n\t\t}\n\t}\n}");
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
