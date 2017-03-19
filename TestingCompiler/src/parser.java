import com.google.common.collect.RowSortedTable;
import com.google.common.collect.TreeBasedTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class parser {

    private class GrammarLine {
        public String Producer;
        public List<String> Productions;

        public GrammarLine(String producer, List<String> productions) {
            Producer = producer;
            Productions = productions;
        }
    }

    public class ParserException extends Exception
    {
        public ParserException(String message) {
            super(message);
        }
    }
    private RowSortedTable<String, String, String> table;
    private List<GrammarLine> grammarLines;
    private Stack stack;
    private Lex lexer;
    private boolean next = false;
    public parser(Lex lexer)
    {
        table = TreeBasedTable.create();
        grammarLines = new ArrayList<>();
        stack = new Stack();
        this.lexer = lexer;
		table.put("0", "$", "r2");
		table.put("0", "E", "3");
		table.put("0", "EQUATION", "2");
		table.put("0", "EQUATION_LIST", "1");
		table.put("0", "F", "5");
		table.put("0", "NUMBER", "d6");
		table.put("0", "OPEN", "d7");
		table.put("0", "T", "4");
		table.put("1", "$", "Accepted");
		table.put("10", "F", "5");
		table.put("10", "NUMBER", "d6");
		table.put("10", "OPEN", "d7");
		table.put("10", "T", "16");
		table.put("11", "F", "5");
		table.put("11", "NUMBER", "d6");
		table.put("11", "OPEN", "d7");
		table.put("11", "T", "17");
		table.put("12", "F", "18");
		table.put("12", "NUMBER", "d6");
		table.put("12", "OPEN", "d7");
		table.put("13", "F", "19");
		table.put("13", "NUMBER", "d6");
		table.put("13", "OPEN", "d7");
		table.put("14", "CLOSE", "d20");
		table.put("14", "MINUS", "d10");
		table.put("14", "PLUS", "d11");
		table.put("15", "MINUS", "d10");
		table.put("15", "PLUS", "d11");
		table.put("15", "SEMI", "d21");
		table.put("16", "CLOSE", "r5");
		table.put("16", "DIV", "d12");
		table.put("16", "EQUALS", "r5");
		table.put("16", "MINUS", "r5");
		table.put("16", "MUL", "d13");
		table.put("16", "PLUS", "r5");
		table.put("16", "SEMI", "r5");
		table.put("17", "CLOSE", "r6");
		table.put("17", "DIV", "d12");
		table.put("17", "EQUALS", "r6");
		table.put("17", "MINUS", "r6");
		table.put("17", "MUL", "d13");
		table.put("17", "PLUS", "r6");
		table.put("17", "SEMI", "r6");
		table.put("18", "CLOSE", "r8");
		table.put("18", "DIV", "r8");
		table.put("18", "EQUALS", "r8");
		table.put("18", "MINUS", "r8");
		table.put("18", "MUL", "r8");
		table.put("18", "PLUS", "r8");
		table.put("18", "SEMI", "r8");
		table.put("19", "CLOSE", "r9");
		table.put("19", "DIV", "r9");
		table.put("19", "EQUALS", "r9");
		table.put("19", "MINUS", "r9");
		table.put("19", "MUL", "r9");
		table.put("19", "PLUS", "r9");
		table.put("19", "SEMI", "r9");
		table.put("2", "$", "r2");
		table.put("2", "E", "3");
		table.put("2", "EQUATION", "2");
		table.put("2", "EQUATION_LIST", "8");
		table.put("2", "F", "5");
		table.put("2", "NUMBER", "d6");
		table.put("2", "OPEN", "d7");
		table.put("2", "T", "4");
		table.put("20", "CLOSE", "r11");
		table.put("20", "DIV", "r11");
		table.put("20", "EQUALS", "r11");
		table.put("20", "MINUS", "r11");
		table.put("20", "MUL", "r11");
		table.put("20", "PLUS", "r11");
		table.put("20", "SEMI", "r11");
		table.put("21", "$", "r3");
		table.put("21", "NUMBER", "r3");
		table.put("21", "OPEN", "r3");
		table.put("3", "EQUALS", "d9");
		table.put("3", "MINUS", "d10");
		table.put("3", "PLUS", "d11");
		table.put("4", "CLOSE", "r4");
		table.put("4", "DIV", "d12");
		table.put("4", "EQUALS", "r4");
		table.put("4", "MINUS", "r4");
		table.put("4", "MUL", "d13");
		table.put("4", "PLUS", "r4");
		table.put("4", "SEMI", "r4");
		table.put("5", "CLOSE", "r7");
		table.put("5", "DIV", "r7");
		table.put("5", "EQUALS", "r7");
		table.put("5", "MINUS", "r7");
		table.put("5", "MUL", "r7");
		table.put("5", "PLUS", "r7");
		table.put("5", "SEMI", "r7");
		table.put("6", "CLOSE", "r10");
		table.put("6", "DIV", "r10");
		table.put("6", "EQUALS", "r10");
		table.put("6", "MINUS", "r10");
		table.put("6", "MUL", "r10");
		table.put("6", "PLUS", "r10");
		table.put("6", "SEMI", "r10");
		table.put("7", "E", "14");
		table.put("7", "F", "5");
		table.put("7", "NUMBER", "d6");
		table.put("7", "OPEN", "d7");
		table.put("7", "T", "4");
		table.put("8", "$", "r1");
		table.put("9", "E", "15");
		table.put("9", "F", "5");
		table.put("9", "NUMBER", "d6");
		table.put("9", "OPEN", "d7");
		table.put("9", "T", "4");
		grammarLines.add(new GrammarLine("EQUATION_LIST",new ArrayList<>(Arrays.asList("EQUATION", "EQUATION_LIST"))));
		grammarLines.add(new GrammarLine("EQUATION_LIST",new ArrayList<>(Arrays.asList("ɛ"))));
		grammarLines.add(new GrammarLine("EQUATION",new ArrayList<>(Arrays.asList("E", "EQUALS", "E", "SEMI"))));
		grammarLines.add(new GrammarLine("E",new ArrayList<>(Arrays.asList("T"))));
		grammarLines.add(new GrammarLine("E",new ArrayList<>(Arrays.asList("E", "MINUS", "T"))));
		grammarLines.add(new GrammarLine("E",new ArrayList<>(Arrays.asList("E", "PLUS", "T"))));
		grammarLines.add(new GrammarLine("T",new ArrayList<>(Arrays.asList("F"))));
		grammarLines.add(new GrammarLine("T",new ArrayList<>(Arrays.asList("T", "DIV", "F"))));
		grammarLines.add(new GrammarLine("T",new ArrayList<>(Arrays.asList("T", "MUL", "F"))));
		grammarLines.add(new GrammarLine("F",new ArrayList<>(Arrays.asList("NUMBER"))));
		grammarLines.add(new GrammarLine("F",new ArrayList<>(Arrays.asList("OPEN", "E", "CLOSE"))));
 }

    public Object parse() throws ParserException {
        stack.push("0");
        try
        {
            Symbol s = lexer.yylex();
            while(s != null)
            {
                if(!table.contains(stack.peek(),sym.terminalNames[s.getType()]))
                    throw new ParserException("Unexpected token at line: " + s.getYyline() + " column: " + s.getYycolumn());
                String action = table.get(stack.peek(),sym.terminalNames[s.getType()]);
                doAction(action,s.getValue());
                if(stack.peek().equals("Accepted"))
                {
                    return stack.elementAt(stack.size()-2);
                }
                if(next)
                    s = lexer.yylex();
            }
            throw new ParserException("Expected EOF");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        throw new ParserException("Expected EOF");
    }

    private void doAction(String action, Object value) throws ParserException {
        char a = action.charAt(0);
        if(action.equals("Accepted"))
        {
            stack.push(action);
            next = true;
            return;
        }
        switch (a)
        {
            case 'd':
                stack.push(value);
                stack.push(action.replace("d",""));
                next = true;
                break;
            case 'r':
                int r = Integer.parseInt(action.replace("r",""));
                GrammarLine grammarLine = grammarLines.get(r - 1);
                int magnitude = 0;
                if(grammarLine.Productions.size() > 1)
                    magnitude = grammarLine.Productions.size();
                else if(!grammarLine.Productions.get(0).equals("ɛ"))
                    magnitude = 1;
                DoReduction(r, magnitude);
                if(!table.contains(stack.elementAt(stack.size()-2),grammarLine.Producer))
                    throw new ParserException("Unexpected token");
                stack.push(table.get(stack.elementAt(stack.size()-2),grammarLine.Producer));
                next = false;
                break;

        }
    }

    private void PopStack(int magnitude)
    {
        for(int i = 0; i < magnitude * 2; i++)
        {
            stack.pop();
        }
    }

    private void DoReduction(int r, int magnitude)
    {
        Object RESULT = null;
        switch (r)
        {
			case 1:
			{
				PopStack(magnitude);
				stack.push(RESULT);
				return;
			}
			case 2:
			{
				PopStack(magnitude);
				stack.push(RESULT);
				return;
			}
			case 3:
			{
				PopStack(magnitude);
				stack.push(RESULT);
				return;
			}
			case 4:
			{
				PopStack(magnitude);
				stack.push(RESULT);
				return;
			}
			case 5:
			{
				PopStack(magnitude);
				stack.push(RESULT);
				return;
			}
			case 6:
			{
				PopStack(magnitude);
				stack.push(RESULT);
				return;
			}
			case 7:
			{
				PopStack(magnitude);
				stack.push(RESULT);
				return;
			}
			case 8:
			{
				PopStack(magnitude);
				stack.push(RESULT);
				return;
			}
			case 9:
			{
				PopStack(magnitude);
				stack.push(RESULT);
				return;
			}
			case 10:
			{
				PopStack(magnitude);
				stack.push(RESULT);
				return;
			}
			case 11:
			{
				PopStack(magnitude);
				stack.push(RESULT);
				return;
			}
			default:
				return;
		}
	}
}