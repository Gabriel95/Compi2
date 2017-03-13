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
		table.put("0", "E", "2");
		table.put("0", "S", "1");
		table.put("0", "T", "3");
		table.put("0", "num", "d4");
		table.put("1", "$", "Accepted");
		table.put("2", "$", "r1");
		table.put("2", "PLUS", "d5");
		table.put("3", "$", "r3");
		table.put("3", "PLUS", "r3");
		table.put("4", "$", "r4");
		table.put("4", "PLUS", "r4");
		table.put("5", "T", "6");
		table.put("5", "num", "d4");
		table.put("6", "$", "r2");
		table.put("6", "PLUS", "r2");
		grammarLines.add(new GrammarLine("S",new ArrayList<>(Arrays.asList("E"))));
		grammarLines.add(new GrammarLine("E",new ArrayList<>(Arrays.asList("E", "PLUS", "T"))));
		grammarLines.add(new GrammarLine("E",new ArrayList<>(Arrays.asList("T"))));
		grammarLines.add(new GrammarLine("T",new ArrayList<>(Arrays.asList("num"))));
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
				Integer e;
				e = (Integer) stack.elementAt(stack.size() - 2);
				PopStack(magnitude);
				System.out.println(e);
				stack.push(RESULT);
				return;
			}
			case 2:
			{
				Integer e;
				Integer t;
				e = (Integer) stack.elementAt(stack.size() - 6);
				t = (Integer) stack.elementAt(stack.size() - 2);
				PopStack(magnitude);
				RESULT = e + t;
				stack.push(RESULT);
				return;
			}
			case 3:
			{
				Integer t;
				t = (Integer) stack.elementAt(stack.size() - 2);
				PopStack(magnitude);
				RESULT = t;
				stack.push(RESULT);
				return;
			}
			case 4:
			{
				Integer n;
				n = (Integer) stack.elementAt(stack.size() - 2);
				PopStack(magnitude);
				RESULT = n;
				stack.push(RESULT);
				return;
			}
			default:
				return;
		}
	}
}