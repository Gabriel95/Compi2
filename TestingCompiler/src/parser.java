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
		table.put("0", "F", "4");
		table.put("0", "OPEN", "d6");
		table.put("0", "S", "1");
		table.put("0", "T", "3");
		table.put("0", "num", "d5");
		table.put("1", "$", "Accepted");
		table.put("10", "F", "15");
		table.put("10", "OPEN", "d6");
		table.put("10", "num", "d5");
		table.put("11", "$GHOST1", "7");
		table.put("11", "CLOSE", "d16");
		table.put("11", "MINUS", "d8");
		table.put("11", "PLUS", "r10");
		table.put("12", "F", "4");
		table.put("12", "OPEN", "d6");
		table.put("12", "T", "17");
		table.put("12", "num", "d5");
		table.put("13", "$", "r3");
		table.put("13", "CLOSE", "r3");
		table.put("13", "DIV", "d10");
		table.put("13", "MINUS", "r3");
		table.put("13", "MUL", "d9");
		table.put("13", "PLUS", "r3");
		table.put("14", "$", "r5");
		table.put("14", "CLOSE", "r5");
		table.put("14", "DIV", "r5");
		table.put("14", "MINUS", "r5");
		table.put("14", "MUL", "r5");
		table.put("14", "PLUS", "r5");
		table.put("15", "$", "r6");
		table.put("15", "CLOSE", "r6");
		table.put("15", "DIV", "r6");
		table.put("15", "MINUS", "r6");
		table.put("15", "MUL", "r6");
		table.put("15", "PLUS", "r6");
		table.put("16", "$", "r9");
		table.put("16", "CLOSE", "r9");
		table.put("16", "DIV", "r9");
		table.put("16", "MINUS", "r9");
		table.put("16", "MUL", "r9");
		table.put("16", "PLUS", "r9");
		table.put("17", "$", "r2");
		table.put("17", "CLOSE", "r2");
		table.put("17", "DIV", "d10");
		table.put("17", "MINUS", "r2");
		table.put("17", "MUL", "d9");
		table.put("17", "PLUS", "r2");
		table.put("2", "$", "r1");
		table.put("2", "$GHOST1", "7");
		table.put("2", "MINUS", "d8");
		table.put("2", "PLUS", "r10");
		table.put("3", "$", "r4");
		table.put("3", "CLOSE", "r4");
		table.put("3", "DIV", "d10");
		table.put("3", "MINUS", "r4");
		table.put("3", "MUL", "d9");
		table.put("3", "PLUS", "r4");
		table.put("4", "$", "r7");
		table.put("4", "CLOSE", "r7");
		table.put("4", "DIV", "r7");
		table.put("4", "MINUS", "r7");
		table.put("4", "MUL", "r7");
		table.put("4", "PLUS", "r7");
		table.put("5", "$", "r8");
		table.put("5", "CLOSE", "r8");
		table.put("5", "DIV", "r8");
		table.put("5", "MINUS", "r8");
		table.put("5", "MUL", "r8");
		table.put("5", "PLUS", "r8");
		table.put("6", "E", "11");
		table.put("6", "F", "4");
		table.put("6", "OPEN", "d6");
		table.put("6", "T", "3");
		table.put("6", "num", "d5");
		table.put("7", "PLUS", "d12");
		table.put("8", "F", "4");
		table.put("8", "OPEN", "d6");
		table.put("8", "T", "13");
		table.put("8", "num", "d5");
		table.put("9", "F", "14");
		table.put("9", "OPEN", "d6");
		table.put("9", "num", "d5");
		grammarLines.add(new GrammarLine("S",new ArrayList<>(Arrays.asList("E"))));
		grammarLines.add(new GrammarLine("E",new ArrayList<>(Arrays.asList("E", "$GHOST1", "PLUS", "T"))));
		grammarLines.add(new GrammarLine("E",new ArrayList<>(Arrays.asList("E", "MINUS", "T"))));
		grammarLines.add(new GrammarLine("E",new ArrayList<>(Arrays.asList("T"))));
		grammarLines.add(new GrammarLine("T",new ArrayList<>(Arrays.asList("T", "MUL", "F"))));
		grammarLines.add(new GrammarLine("T",new ArrayList<>(Arrays.asList("T", "DIV", "F"))));
		grammarLines.add(new GrammarLine("T",new ArrayList<>(Arrays.asList("F"))));
		grammarLines.add(new GrammarLine("F",new ArrayList<>(Arrays.asList("num"))));
		grammarLines.add(new GrammarLine("F",new ArrayList<>(Arrays.asList("OPEN", "E", "CLOSE"))));
		grammarLines.add(new GrammarLine("$GHOST1",new ArrayList<>(Arrays.asList("ɛ"))));
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
				e = (Integer) stack.elementAt(stack.size() - 8);
				t = (Integer) stack.elementAt(stack.size() - 2);
				PopStack(magnitude);
				RESULT = e + t;
				stack.push(RESULT);
				return;
			}
			case 3:
			{
				Integer e;
				Integer t;
				e = (Integer) stack.elementAt(stack.size() - 6);
				t = (Integer) stack.elementAt(stack.size() - 2);
				PopStack(magnitude);
				RESULT = e - t;
				stack.push(RESULT);
				return;
			}
			case 4:
			{
				Integer t;
				t = (Integer) stack.elementAt(stack.size() - 2);
				PopStack(magnitude);
				RESULT = t;
				stack.push(RESULT);
				return;
			}
			case 5:
			{
				Integer t;
				Integer f;
				t = (Integer) stack.elementAt(stack.size() - 6);
				f = (Integer) stack.elementAt(stack.size() - 2);
				PopStack(magnitude);
				RESULT = t * f;
				stack.push(RESULT);
				return;
			}
			case 6:
			{
				Integer t;
				Integer f;
				t = (Integer) stack.elementAt(stack.size() - 6);
				f = (Integer) stack.elementAt(stack.size() - 2);
				PopStack(magnitude);
				RESULT = t / f;
				stack.push(RESULT);
				return;
			}
			case 7:
			{
				Integer f;
				f = (Integer) stack.elementAt(stack.size() - 2);
				PopStack(magnitude);
				RESULT = f;
				stack.push(RESULT);
				return;
			}
			case 8:
			{
				Integer n;
				n = (Integer) stack.elementAt(stack.size() - 2);
				PopStack(magnitude);
				RESULT = n;
				stack.push(RESULT);
				return;
			}
			case 9:
			{
				Integer e;
				e = (Integer) stack.elementAt(stack.size() - 4);
				PopStack(magnitude);
				RESULT = e;
				stack.push(RESULT);
				return;
			}
			case 10:
			{
				Integer e;
				e = (Integer) stack.elementAt(stack.size() - 2);
				PopStack(magnitude);
				System.out.println(e);
				stack.push(RESULT);
				return;
			}
			default:
				return;
		}
	}
}