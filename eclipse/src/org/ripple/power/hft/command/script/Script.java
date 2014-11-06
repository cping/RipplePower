package org.ripple.power.hft.command.script;

import org.address.collection.ArrayMap;

public final class Script {

	// maximum line length
	private static final int MAX_LINE_LENGTH = 8192;

	// general
	public static final int TT_WORD = 0x0001;
	public static final int TT_INTEGER = 0x0002;
	public static final int TT_DOUBLE = 0x0003;
	public static final int TT_STRING = 0x0004;
	public static final int TT_FUNC = 0x0005;
	public static final int TT_ARRAY = 0x0006;
	public static final int TT_NULL = 0x0007;
	public static final int TT_EOL = 0x0008;
	public static final int TT_EOF = 0x0009;
	public static final int TT_EQ = 0x0010;
	public static final int TT_DEFGLOBAL = 0x0011;
	public static final int TT_TYPEOF = 0x0012;

	// control struct
	public static final int TT_IF = 0x1001;
	public static final int TT_ELSE = 0x1002;
	public static final int TT_THEN = 0x1003; // :
	public static final int TT_ELSIF = 0x1004;
	public static final int TT_DEFFUNC = 0x1005;
	public static final int TT_WHILE = 0x1006;
	public static final int TT_RETURN = 0x1007;
	public static final int TT_END = 0x1008;

	// math calc
	public static final int TT_PLUS = 0x2001;
	public static final int TT_MINUS = 0x2002;
	public static final int TT_MULT = 0x2003;
	public static final int TT_DIV = 0x2004;
	public static final int TT_EXP = 0x2005;
	public static final int TT_MOD = 0x2006;

	// logic calc
	public static final int TT_LAND = 0x3001;
	public static final int TT_LOR = 0x3002;
	public static final int TT_LEQ = 0x3003;
	public static final int TT_LNEQ = 0x3004;
	public static final int TT_LGR = 0x3005;
	public static final int TT_LLS = 0x3006;
	public static final int TT_LGRE = 0x3007;
	public static final int TT_LLSE = 0x3008;
	public static final int TT_NOT = 0x3009;

	private static ArrayMap wordToken = new ArrayMap() {
		{
			put("if", new Integer(TT_IF));
			put("then", new Integer(TT_THEN));
			put("else", new Integer(TT_ELSE));
			put("elseif", new Integer(TT_ELSIF));
			put("while", new Integer(TT_WHILE));
			put("end", new Integer(TT_END));
			put("func", new Integer(TT_DEFFUNC));
			put("return", new Integer(TT_RETURN));
			put("global", new Integer(TT_DEFGLOBAL));
			put("typeof", new Integer(TT_TYPEOF));
			put("null", new Integer(TT_NULL));
		}
	};

	public int ttype;

	public Object value;

	private boolean pBack;
	private char[] pBuffer, line;
	private ScriptLoader lineLoader;
	private int length;
	private int c = 0;
	private static final int EOL = -1;
	private int pos = 0;

	public String toString() {
		return value + ":" + ttype;
	}

	public Script() {
		pBuffer = new char[MAX_LINE_LENGTH];
	}

	public Script(String firstLine) {
		this();
		setString(firstLine);
	}

	public Script(ScriptLoader lineLoader) {
		this();
		setLineLoader(lineLoader);
	}

	public void setString(String str) {
		checkLine(str);
		line = str.toCharArray();
		pos = 0;
		c = 0;

		ttype = 0;
		value = 0;
	}

	public void setLineLoader(ScriptLoader lineLoader) {
		this.lineLoader = lineLoader;
	}

	private int getChar() {
		if (line == null) {
			return EOL;
		}
		if (pos < line.length) {
			return line[pos++];
		} else {
			return EOL;
		}
	}

	private int peekChar(int offset) {
		int n;
		n = pos + offset - 1;
		if (n >= line.length) {
			return EOL;
		} else {
			return line[n];
		}
	}

	public int nextToken() {
		if (pBack) {
			pBack = false;
			return ttype;
		}
		if (ttype == TT_EOL) {
			if ((lineLoader != null)
					&& (lineLoader.getCurLine() < lineLoader.lineCount())) {
				setString(lineLoader.nextLine());
			} else {
				ttype = Script.TT_EOF;
				return TT_EOL;
			}
		}
		return nextT();
	}

	public void pushBack() {
		pBack = true;
	}

	String getLine() {
		return new String(line);
	}

	private int nextT() {
		int cPos = 0;
		boolean getNext;
		value = null;
		ttype = 0;

		while (ttype == 0) {
			getNext = true;
			switch (c) {
			case 0:
			case ' ':
			case '\t':
			case '\n':
			case '\r':
				break;
			case EOL:
				ttype = TT_EOL;
				break;

			case '#':
				pos = length;
				ttype = TT_EOL;
				break;

			case '"':
				c = getChar();
				while ((c != EOL) && (c != '"')) {
					if (c == '\\') {
						switch (peekChar(1)) {
						case 'n':
							pBuffer[cPos++] = '\n';
							getChar();
							break;
						case 't':
							pBuffer[cPos++] = '\t';
							getChar();
							break;
						case 'r':
							pBuffer[cPos++] = '\r';
							getChar();
							break;
						case '\"':
							pBuffer[cPos++] = '"';
							getChar();
							break;
						case '\\':
							pBuffer[cPos++] = '\\';
							getChar();
							break;
						}
					} else {
						pBuffer[cPos++] = (char) c;
					}
					c = getChar();
				}
				value = new String(pBuffer, 0, cPos);
				ttype = TT_STRING;
				break;

			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
			case 'G':
			case 'H':
			case 'I':
			case 'J':
			case 'K':
			case 'L':
			case 'M':
			case 'N':
			case 'O':
			case 'P':
			case 'Q':
			case 'R':
			case 'S':
			case 'T':
			case 'U':
			case 'V':
			case 'W':
			case 'X':
			case 'Y':
			case 'Z':
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'g':
			case 'h':
			case 'i':
			case 'j':
			case 'k':
			case 'l':
			case 'm':
			case 'n':
			case 'o':
			case 'p':
			case 'q':
			case 'r':
			case 's':
			case 't':
			case 'u':
			case 'v':
			case 'w':
			case 'x':
			case 'y':
			case 'z':
				while ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
						|| (c >= '0' && c <= '9') || c == '_' || c == '.') {
					pBuffer[cPos++] = (char) c;
					c = getChar();
				}
				getNext = false;
				value = new String(pBuffer, 0, cPos);
				Integer tt = (Integer) wordToken.get(value);
				if (tt != null) {
					ttype = tt.intValue();
				} else {
					if (c == '(') {
						ttype = TT_FUNC;
					} else if (c == '[') {
						ttype = TT_ARRAY;
					} else {
						ttype = TT_WORD;
					}
				}
				break;

			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9': {
				boolean isDouble = false;
				while ((c >= '0' && c <= '9') || c == '.') {
					if (c == '.') {
						isDouble = true;
					}
					pBuffer[cPos++] = (char) c;
					c = getChar();
				}
				getNext = false;
				String str = new String(pBuffer, 0, cPos);
				if (isDouble) {
					ttype = TT_DOUBLE;
					value = Double.valueOf(str);
				} else {
					ttype = TT_INTEGER;
					value = new Long(Long.parseLong(str));
				}
				break;
			}

			case '+':
				ttype = TT_PLUS;
				break;
			case '-':
				ttype = TT_MINUS;
				break;
			case '*':
				ttype = TT_MULT;
				break;
			case '/':
				ttype = TT_DIV;
				break;
			case '^':
				ttype = TT_EXP;
				break;
			case '%':
				ttype = TT_MOD;
				break;
			case ':':
				ttype = TT_THEN;
				break;
			case '>':
				if (peekChar(1) == '=') {
					getChar();
					ttype = TT_LGRE;
				} else {
					ttype = TT_LGR;
				}
				break;
			case '<':
				if (peekChar(1) == '=') {
					getChar();
					ttype = TT_LLSE;
				} else {
					ttype = TT_LLS;
				}
				break;
			case '=':
				if (peekChar(1) == '=') {
					getChar();
					ttype = TT_LEQ;
				} else {
					ttype = TT_EQ;
				}
				break;
			case '!':
				if (peekChar(1) == '=') {
					getChar();
					ttype = TT_LNEQ;
				} else {
					ttype = TT_NOT;
				}
				break;
			default:
				if ((c == '|') && (peekChar(1) == '|')) {
					getChar();
					ttype = TT_LOR;
				} else if ((c == '&') && (peekChar(1) == '&')) {
					getChar();
					ttype = TT_LAND;
				} else {
					ttype = c;
				}
			}
			if (getNext)
				c = getChar();
		}
		return ttype;
	}

	private void checkLine(String line) {
		boolean inQuotes = false;
		int brCount = 0;
		char chars[] = line.toCharArray();
		int n;

		if (chars != null) {
			for (n = 0; n < chars.length; n++) {
				switch (chars[n]) {
				case '#': {
					if (!inQuotes) {
						n = chars.length;
						break;
					}
				}
				case '"':
				case '\'': {
					if (inQuotes && chars[n - 1] != '\\') {
						inQuotes = false;
					} else if (n == 0 || chars[n - 1] != '\\') {
						inQuotes = true;
					}
					break;
				}
				case '(': {
					brCount++;
					break;
				}
				case ')': {
					brCount--;
					break;
				}
				}
			}
			if (inQuotes) {
				throw new ParseError("Mismatched quotes\n" + inQuotes
						+ new String(chars));
			}
			if (brCount != 0) {
				throw new ParseError("Mismatched brackets\n"
						+ new String(chars));
			}
		}

	}

}
