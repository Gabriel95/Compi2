public class sym
{
	public static final int EOF = 0;
	public static final int error = 1;
	public static final int MUL = 2;
	public static final int num = 3;
	public static final int CLOSE = 4;
	public static final int MINUS = 5;
	public static final int OPEN = 6;
	public static final int DIV = 7;
	public static final int ɛ = 8;
	public static final int PLUS = 9;
	public static final String[] terminalNames = new String[]
	{
		"$",
		"error",
		"MUL",
		"num",
		"CLOSE",
		"MINUS",
		"OPEN",
		"DIV",
		"ɛ",
		"PLUS"
	};
}