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
		table.put("0", "E", "1");
		table.put("0", "F", "3");
		table.put("0", "OPEN", "d4");
		table.put("0", "T", "2");
		table.put("0", "a", "d5");
		table.put("0", "b", "d6");
		table.put("0", "e", "d7");
		table.put("1", "$", "Accepted");
		table.put("1", "PLUS", "d8");
		table.put("10", "$", "r9");
		table.put("10", "CLOSE", "r9");
		table.put("10", "OPEN", "r9");
		table.put("10", "PLUS", "r9");
		table.put("10", "STAR", "r9");
		table.put("10", "a", "r9");
		table.put("10", "b", "r9");
		table.put("10", "e", "r9");
		table.put("11", "CLOSE", "d13");
		table.put("11", "PLUS", "d8");
		table.put("12", "$", "r1");
		table.put("12", "CLOSE", "r1");
		table.put("12", "F", "9");
		table.put("12", "OPEN", "d4");
		table.put("12", "PLUS", "r1");
		table.put("12", "a", "d5");
		table.put("12", "b", "d6");
		table.put("12", "e", "d7");
		table.put("13", "$", "r5");
		table.put("13", "CLOSE", "r5");
		table.put("13", "OPEN", "r5");
		table.put("13", "PLUS", "r5");
		table.put("13", "STAR", "r5");
		table.put("13", "a", "r5");
		table.put("13", "b", "r5");
		table.put("13", "e", "r5");
		table.put("2", "$", "r2");
		table.put("2", "CLOSE", "r2");
		table.put("2", "F", "9");
		table.put("2", "OPEN", "d4");
		table.put("2", "PLUS", "r2");
		table.put("2", "a", "d5");
		table.put("2", "b", "d6");
		table.put("2", "e", "d7");
		table.put("3", "$", "r4");
		table.put("3", "CLOSE", "r4");
		table.put("3", "OPEN", "r4");
		table.put("3", "PLUS", "r4");
		table.put("3", "STAR", "d10");
		table.put("3", "a", "r4");
		table.put("3", "b", "r4");
		table.put("3", "e", "r4");
		table.put("4", "E", "11");
		table.put("4", "F", "3");
		table.put("4", "OPEN", "d4");
		table.put("4", "T", "2");
		table.put("4", "a", "d5");
		table.put("4", "b", "d6");
		table.put("4", "e", "d7");
		table.put("5", "$", "r6");
		table.put("5", "CLOSE", "r6");
		table.put("5", "OPEN", "r6");
		table.put("5", "PLUS", "r6");
		table.put("5", "STAR", "r6");
		table.put("5", "a", "r6");
		table.put("5", "b", "r6");
		table.put("5", "e", "r6");
		table.put("6", "$", "r7");
		table.put("6", "CLOSE", "r7");
		table.put("6", "OPEN", "r7");
		table.put("6", "PLUS", "r7");
		table.put("6", "STAR", "r7");
		table.put("6", "a", "r7");
		table.put("6", "b", "r7");
		table.put("6", "e", "r7");
		table.put("7", "$", "r8");
		table.put("7", "CLOSE", "r8");
		table.put("7", "OPEN", "r8");
		table.put("7", "PLUS", "r8");
		table.put("7", "STAR", "r8");
		table.put("7", "a", "r8");
		table.put("7", "b", "r8");
		table.put("7", "e", "r8");
		table.put("8", "F", "3");
		table.put("8", "OPEN", "d4");
		table.put("8", "T", "12");
		table.put("8", "a", "d5");
		table.put("8", "b", "d6");
		table.put("8", "e", "d7");
		table.put("9", "$", "r3");
		table.put("9", "CLOSE", "r3");
		table.put("9", "OPEN", "r3");
		table.put("9", "PLUS", "r3");
		table.put("9", "STAR", "d10");
		table.put("9", "a", "r3");
		table.put("9", "b", "r3");
		table.put("9", "e", "r3");
		grammarLines.add(new GrammarLine("E",new ArrayList<>(Arrays.asList("E", "PLUS", "T"))));
		grammarLines.add(new GrammarLine("E",new ArrayList<>(Arrays.asList("T"))));
		grammarLines.add(new GrammarLine("T",new ArrayList<>(Arrays.asList("T", "F"))));
		grammarLines.add(new GrammarLine("T",new ArrayList<>(Arrays.asList("F"))));
		grammarLines.add(new GrammarLine("F",new ArrayList<>(Arrays.asList("OPEN", "E", "CLOSE"))));
		grammarLines.add(new GrammarLine("F",new ArrayList<>(Arrays.asList("a"))));
		grammarLines.add(new GrammarLine("F",new ArrayList<>(Arrays.asList("b"))));
		grammarLines.add(new GrammarLine("F",new ArrayList<>(Arrays.asList("e"))));
		grammarLines.add(new GrammarLine("F",new ArrayList<>(Arrays.asList("F", "STAR"))));
 }

    public void parse() throws ParserException {
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
                    break;
                if(next)
                    s = lexer.yylex();
            }
			if(!stack.peek().equals("Accepted"))
				throw new ParserException("Expected EOF?");

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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
                DoReduction(r, grammarLine.Productions.size());
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
			default:
				return;
		}
	}
}