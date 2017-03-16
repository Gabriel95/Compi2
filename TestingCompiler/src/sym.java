public class sym
{
	public static final int EOF = 0;
	public static final int error = 1;
	public static final int DIV = 2;
	public static final int MUL = 3;
	public static final int num = 4;
	public static final int CLOSE = 5;
	public static final int PLUS = 6;
	public static final int MINUS = 7;
	public static final int OPEN = 8;
	public static final String[] terminalNames = new String[]
	{
		"$",
		"error",
		"DIV",
		"MUL",
		"num",
		"CLOSE",
		"PLUS",
		"MINUS",
		"OPEN"
	};
}