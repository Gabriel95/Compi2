public class sym
{
	public static final int EOF = 0;
	public static final int error = 1;
	public static final int NUMBER = 2;
	public static final int EQUALS = 3;
	public static final int MUL = 4;
	public static final int SEMI = 5;
	public static final int CLOSE = 6;
	public static final int MINUS = 7;
	public static final int OPEN = 8;
	public static final int DIV = 9;
	public static final int ID = 10;
	public static final int PLUS = 11;
	public static final String[] terminalNames = new String[]
	{
		"$",
		"error",
		"NUMBER",
		"EQUALS",
		"MUL",
		"SEMI",
		"CLOSE",
		"MINUS",
		"OPEN",
		"DIV",
		"ID",
		"PLUS"
	};
}