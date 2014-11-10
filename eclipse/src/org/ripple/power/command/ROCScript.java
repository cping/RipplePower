package org.ripple.power.command;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.address.collection.Array;
import org.address.collection.ArrayMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.Updateable;
import org.ripple.power.utils.ReflectorUtils;
import org.ripple.power.utils.StringUtils;

public class ROCScript {

	private DMacros macros_executer = null;

	private ArrayList<IMacros> macros_listeners = new ArrayList<IMacros>(10);

	private final IScriptLog scriptLog;

	private static HashMap<String, Long> waitTimes = new HashMap<String, Long>();
	static {
		waitTimes.put("mesc", LSystem.MSEC);
		waitTimes.put("second", LSystem.SECOND);
		waitTimes.put("minute", LSystem.MINUTE);
		waitTimes.put("hour", LSystem.HOUR);
		waitTimes.put("day", LSystem.DAY);
	}

	private void handleError(int error) throws ScriptException {
		String[] errors = new String[UNKNOWN + 1];
		errors[SYNTAX] = "Syntax Error";
		errors[UNBALPARENS] = "(... or ...)";
		errors[DIVBYZERO] = "1/0";
		errors[EQUALEXPECTED] = "Equal Expected";
		errors[UNKOWN] = "For vars that have no value: assignments,loops";
		errors[NOTABOOL] = "Not a boolean";
		errors[NOTANUMB] = "Not a number";
		errors[NOTASTR] = " Not a string";
		errors[DUPFUNCTION] = "Two functions with same name";
		errors[ENDEXPECTED] = "Reaches end of script without end";
		errors[THENEXPECTED] = "No then after if";
		errors[DOEXPECTED] = "No then after if";
		errors[MISSQUOTE] = "Strings missing a quote";
		errors[UNKFUNCTION] = "Unknown function";
		errors[INVALIDEXP] = "Invalid Expression";
		errors[UNEXPITEM] = "Unexpeced Item";
		errors[FILENOTFOUND] = "Can't find file";
		errors[INPUTIOERROR] = "Input that fails";
		errors[EXPERR] = "For if, while and for";
		errors[FILEIOERROR] = "Can't load file";
		errors[UNKNOWN] = "Unknown error";
		throw new ScriptException(errors[error] + ": " + textIdx
				+ "\nLine number: " + textLine + "\nItem: " + item
				+ "\nItem Type: " + itemType + "\ncommType: " + commType);
	}

	private final static int MAX_TEXT_SIZE = 65535;

	// 参数类型
	private final int NONE = 0; // 不存在
	private final int DELIMITER = 1; // 任意特殊符号
	private final int VARIABLE = 2; // 已经存在的变量
	private final int COMMAND = 3; // 接下来的命令
	private final int EOL = 4; // 结束一行
	private final int EOP = 5; // 结束全部文本

	// 判定参数类型
	private final int STRING = 6;
	private final int NUMBER = 7;
	private final int BOOLEAN = 8;
	private final int FUNCT = 9;

	// 脚本指令
	// 条件
	private final int UNKNCOM = 0;
	private final int PRINT = 1;
	private final int INPUT = 2;
	private final int RETURN = 3;
	private final int THEN = 4;
	private final int END = 5;
	private final int BEGIN = 6;
	private final int ELSE = 7;

	// 分支
	private final int IF = 8;
	private final int FOR = 9;
	private final int WHILE = 10;
	private final int FUNCTION = 11;
	// 延迟
	private final int WAIT = 12;

	// 错误
	private final int SYNTAX = 0;
	private final int UNBALPARENS = 1;
	private final int DIVBYZERO = 2;
	private final int EQUALEXPECTED = 3;
	private final int UNKOWN = 4;
	private final int NOTABOOL = 5;
	private final int NOTANUMB = 6;
	private final int NOTASTR = 7;
	private final int DUPFUNCTION = 8;
	private final int ENDEXPECTED = 9;
	private final int THENEXPECTED = 10;
	private final int MISSQUOTE = 11;
	private final int DOEXPECTED = 12;
	private final int UNKFUNCTION = 13;
	private final int INVALIDEXP = 14;
	private final int UNEXPITEM = 15;
	private final int TOOMANYPARAMS = 16;

	private final int FILENOTFOUND = 17;
	private final int INPUTIOERROR = 18;
	private final int EXPERR = 19;
	private final int FILEIOERROR = 20;

	// 宏
	private final int MACROS = 21;
	// 未知区域
	private final int UNKNOWN = 22;

	// 宏指令设置
	private boolean waitMacros = true;
	private boolean initNextMacros = true;

	// 脚本线程
	private Thread commandThread;

	// 保存脚本用
	private Array<Command> commands;
	// 变量
	private Array<ArrayMap> vars;
	// 函数
	private ArrayMap functs;

	private char[] contexts;
	private int textIdx;
	private int textLine;

	private String item;
	private int itemType;
	private int commType;
	private int macroType = -1;
	// <=
	private final char LE = 0;
	// >=
	private final char GE = 1;
	// ==
	private final char EQ = 4;

	private final char rOps[] = { LE, GE, '<', '>', EQ };

	private String relops = new String(rOps);

	private final char AND = 0;
	private final char OR = 1;
	private final char NOT = 2;
	private final char XOR = 3;
	private final char XAND = 4;

	private final char selectOpsId[] = { AND, OR, NOT, XOR, XAND };
	private final String selectOps[] = { "and", "or", "not", "xor", "xand" };

	private String[] commTable = { "", "print", "input", "return", "then",
			"end", "begin", "else", "if", "for", "while", "function", "wait" };

	private String[] macros = { "{", "}" };

	private boolean debug = true;

	private final boolean DEBUG_E = false;

	@SuppressWarnings("serial")
	public class ScriptException extends Exception {

		String errStr;

		public ScriptException(String str) {
			this.errStr = str;
		}

		public String toString() {
			return errStr;
		}
	}

	class Command {
		int loc, comm = 0, line;

		public String toString() {
			return commTable[comm];
		}
	}

	class ForLoop extends Command {
		String vName;
		int expLoc, itLoc;

		public ForLoop(String n, int exp, int it, int lo, int lin) {
			comm = FOR;
			vName = n;
			expLoc = exp;
			itLoc = it;
			loc = lo;
			line = lin;
		}

		public ForLoop() {
			comm = FOR;
		}
	}

	class WhileLoop extends Command {
		int expLoc, line;

		public WhileLoop(int exp, int lo, int lin) {
			comm = WHILE;
			expLoc = exp;
			loc = lo;
			line = lin;
		}

		public WhileLoop() {
			comm = WHILE;
		}
	}

	class Function extends Command {
		int backLoc;
		ArrayList<String> lists;

		public Function(int l, int bLoc, ArrayList<String> pars) {
			comm = FUNCTION;
			backLoc = bLoc;
			loc = l;
			lists = pars;
		}

		public Function() {
			comm = FUNCTION;
		}
	}

	class IfStat extends Command {
		boolean done;

		public IfStat(boolean d) {
			comm = IF;
			done = d;
		}
	}

	/**
	 * 构建脚本
	 * 
	 * @param script
	 * @param useFile
	 * @throws ScriptException
	 */
	public ROCScript(String script, boolean useFile) throws ScriptException {
		this(new DefScriptLog(), script, useFile);
	}

	/**
	 * 构建脚本
	 * 
	 * @param log
	 * @param script
	 * @param useFile
	 * @throws ScriptException
	 */
	public ROCScript(IScriptLog log, String script, boolean useFile)
			throws ScriptException {
		scriptLog = log;
		char[] charlist = new char[MAX_TEXT_SIZE];
		int size = 0;
		// 文件名导入
		if (useFile) {
			size = fileToChars(charlist, script);
		} else {
			// 直接加载内容
			script = script.trim();
			charlist = script.toCharArray();
			size = charlist.length;
		}
		if (size > MAX_TEXT_SIZE) {
			size = MAX_TEXT_SIZE;
		}
		if (size != -1) {
			contexts = new char[size];
			System.arraycopy(charlist, 0, contexts, 0, size);
		}
	}

	private void debug(String s) {
		if (debug) {
			if (commands != null) {
				for (int i = 0; i < commands.size(); i++)
					scriptLog.line("\t");
			}
			scriptLog.err("> " + s);
			scriptLog.newline();
		}
	}

	private void debug(String[] strs) {
		if (debug) {
			String str = "";
			if (commands != null) {
				for (int i = 0; i < commands.size(); i++)
					str += "\t";
			}
			for (String s : strs) {
				scriptLog.err(str + s);
			}
			scriptLog.newline();
		}
	}

	public int fileToChars(char[] p, String fileName) throws ScriptException {
		debug("Loading file...");
		int size = 0;

		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			size = br.read(p, 0, MAX_TEXT_SIZE);
			fr.close();
		} catch (FileNotFoundException exc) {
			handleError(FILENOTFOUND);
		} catch (IOException exc) {
			handleError(FILEIOERROR);
		}
		// 当读取到文件尾部时，后退一位
		if (p[size - 1] == (char) 26) {
			size--;
		}
		return size;
	}

	/**
	 * 指定脚本程序
	 * 
	 * @param d
	 * @throws ScriptException
	 */
	public void call(boolean d) {
		synchronized (ROCScript.class) {
			debug("Running script...");
			debug = d;

			// 初始化寄存器
			vars = new Array<ArrayMap>();
			vars.add(new ArrayMap());
			functs = new ArrayMap();

			commands = new Array<Command>();

			textIdx = 0;
			textLine = 1;

			Updateable update = new Updateable() {

				@Override
				public void action(Object o) {
					// 开始执行命令
					try {
						running();
					} catch (ScriptException e) {
						e.printStackTrace();
					}
				}
			};
			if (commandThread == null) {
				commandThread = LSystem.postThread(update);
			} else {
				try {
					commandThread.interrupt();
					commandThread = null;
				} catch (Exception ex) {

				}
				commandThread = LSystem.postThread(update);
			}
		}
	}

	/**
	 * 执行脚本命令
	 * 
	 * @return
	 * @throws ScriptException
	 */
	private Object running() throws ScriptException {
		debug("Starting script...");
		while (nextItem()) {
			switch (itemType) {
			// 变量
			case VARIABLE:
				assignVar();
				break;
			// 函数
			case FUNCT:
				execFunct();
				nextItem();
				break;
			// 具体表达式变量
			case COMMAND:
				switch (commType) {
				case PRINT:
					print();
					break;
				case INPUT:
					input();
					break;
				case IF:
					execIf();
					break;
				case FOR:
					execFor();
					break;
				case END:
					if (endCommand(false)) {
						return null;
					}
					break;
				case WHILE:
					execWhile();
					break;
				case RETURN:
					debug("Returning");
					nextItem();
					Object o = analysis();
					endCommand(true);
					return o;
				case FUNCTION:
					newFunction();
					break;
				case ELSE:
					execElse();
					break;
				case WAIT:
					debug("waiting");
					break;
				}
				debug("Done with command");
				break;
			// 宏指定
			case MACROS:
				switch (macroType) {
				case 0:
					callMacros();
					itemType = EOL;
					break;
				case 1:
					itemType = EOL;
					break;
				}
				break;
			}

			// 判定解析完毕
			if (itemType != EOL && itemType != EOP) {
				handleError(UNEXPITEM);
			}
		}
		return null;
	}

	public boolean isWaitMacros() {
		return waitMacros;
	}

	public void setWaitMacros(boolean waitMacros) {
		this.waitMacros = waitMacros;
	}

	public void setCallMacros(boolean f) {
		this.initNextMacros = f;
	}

	public boolean isCallMacros() {
		return this.initNextMacros;
	}

	private void callMacros() throws ScriptException {
		nextItem();
		debug("Macros:");
		debug(item);
		macrosCommand(this.item, this.textLine);
	}

	public void setMacrosListener(ArrayList<IMacros> list) {
		if (list == null) {
			return;
		}
		this.macros_listeners = list;
	}

	public void setMacrosListener(IMacros... args) {
		if (args == null) {
			return;
		}
		for (int i = 0; i < args.length; i++) {
			this.macros_listeners.add(args[i]);
		}
	}

	public void setMacrosListener(IMacros macros) {
		addMacrosListener(macros);
	}

	public void addMacrosListener(IMacros macros) {
		if (this.macros_listeners != null && macros != null) {
			this.macros_listeners.add(macros);
		}
	}

	private void macrosCommand(String context, int id) {
		if (!initNextMacros) {
			return;
		}
		DMacros.resetCache();
		String[] res = StringUtils.split(context, "\n");
		if (macros_executer == null) {
			macros_executer = new DMacros("script" + id, res);
		} else {
			macros_executer.formatCommand("script" + id, res);
		}
		for (int i = 0; i < vars.size(); i++) {
			ArrayMap maps = vars.get(i);
			macros_executer.setVariables(maps);
		}
		for (; macros_executer.next();) {
			String result = macros_executer.doExecute();
			if (result == null) {
				continue;
			}
			if (macros_listeners != null) {
				for (IMacros macros_listener : macros_listeners) {
					macros_listener.call(scriptLog, textLine, macros_executer,
							result);
					if (waitMacros) {
						for (; macros_listener.isSyncing();) {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
							}
						}
					}
				}
			}
		}
		vars.add(macros_executer.getVariables());
	}

	private void print() throws ScriptException {
		debug("Print");
		String lastDelim = "";
		while (nextItem() && itemType != EOL && itemType != EOP) {
			scriptLog.info(analysis());
			lastDelim = item;
			if (lastDelim.equals(",")) {
				scriptLog.line(" ");
			} else if (lastDelim.equals(";")) {
				scriptLog.line("\t");
			} else if (itemType != EOL && itemType != EOP) {
				handleError(SYNTAX);
			} else {
				break;
			}
		}
		scriptLog.newline();
	}

	private void input() throws ScriptException {
		debug("Get Input");
		String str = "";

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		nextItem();
		if (itemType == STRING) {
			scriptLog.line(item);
			nextItem();
			if (!item.equals(",")) {
				handleError(SYNTAX);
				return;
			}
			nextItem();
		} else {
			scriptLog.line("? ");
		}
		if (!Character.isLetter(item.charAt(0))) {
			handleError(UNKOWN);
			return;
		}
		try {
			str = br.readLine();
			vars.last().put(item, str);
		} catch (IOException e) {
			handleError(INPUTIOERROR);
			return;
		}
		nextItem();
	}

	private void execIf() throws ScriptException {
		debug("If select");
		boolean result = false;

		nextItem();

		try {
			Object o = analysis();
			if (o instanceof Boolean) {
				result = (boolean) o;
			} else if (o instanceof Number) {
				result = Double.parseDouble(o.toString()) > 0;
			}
		} catch (ClassCastException exc) {
			handleError(NOTABOOL);
			return;
		}

		vars.add(new ArrayMap());
		commands.add(new IfStat(result));

		if (result) {
			if (commType != THEN) {
				handleError(THENEXPECTED);
				return;
			}
			nextItem();

		} else {
			nextEnd();
		}
	}

	private void execElse() throws ScriptException {
		debug("Else select");
		Command c = commands.last();

		if (c.comm != IF) {
			handleError(SYNTAX);
			return;
		}

		if (!((IfStat) c).done) {
			nextItem();

			if (commType == IF) {

				nextItem();

				boolean result;

				try {
					result = (boolean) analysis();
				} catch (ClassCastException exc) {
					handleError(NOTABOOL);
					return;
				}

				if (result) {
					((IfStat) c).done = true;

					if (commType != THEN) {
						handleError(THENEXPECTED);
						return;
					}
					nextItem();

				} else {
					nextEnd();
				}
				return;
			}
		} else {
			nextEnd();
		}
	}

	// 循环执行
	private void execFor() throws ScriptException {
		debug("For Loop");
		double i;
		int expLoc, ittLoc, loc;
		String vname;

		nextItem();
		vname = item;
		// =
		nextItem();
		if (item.equals("=")) {
			nextItem();
			try {
				i = (double) analysis();
			} catch (ClassCastException exc) {
				handleError(EXPERR);
				return;
			}
			vars.last().put(vname, i);
		}

		// ,
		if (!item.equals(",")) {
			handleError(SYNTAX);
			return;
		}

		expLoc = textIdx;
		nextItem();

		try {
			if (!(boolean) analysis()) {
				vars.add(new ArrayMap());
				commands.add(new ForLoop());
				nextEnd();
				return;
			}
		} catch (ClassCastException exc) {
			handleError(NOTABOOL);
		}

		if (!item.equals(",")) {
			handleError(SYNTAX);
			return;
		}

		ittLoc = textIdx;
		nextItem();

		analysis();

		if (commType != BEGIN) {
			handleError(DOEXPECTED);
			return;
		}

		loc = textIdx;
		nextItem();

		ForLoop newfor;

		try {
			newfor = new ForLoop(vname, expLoc, ittLoc, loc, textLine);
		} catch (ClassCastException exc) {
			handleError(EXPERR);
			return;
		}

		vars.add(new ArrayMap());
		commands.add(newfor);
	}

	private void execWhile() throws ScriptException {
		debug("While Loop");
		int expLoc;

		expLoc = textIdx;

		nextItem();

		try {
			if (!(boolean) analysis()) {
				vars.add(new ArrayMap());
				commands.add(new WhileLoop());
				nextEnd();
				return;
			}
		} catch (ClassCastException exc) {
			handleError(NOTABOOL);
		}

		if (commType != BEGIN) {
			handleError(DOEXPECTED);
			return;
		}

		int loc = textIdx;
		nextItem();

		WhileLoop loop = new WhileLoop(expLoc, loc, textLine);

		vars.add(new ArrayMap());
		commands.add(loop);
	}

	private void newFunction() throws ScriptException {
		debug("New Function");

		String fName;

		nextItem();

		if (!(Character.isLetter(item.charAt(0)))) {
			handleError(UNKOWN);
		}

		fName = item;

		nextItem();

		if (!item.equals("(")) {
			handleError(SYNTAX);
		}

		nextItem();

		ArrayList<String> lists = new ArrayList<String>();

		if (!item.equals(")")) {
			if (Character.isLetter(item.charAt(0))) {
				lists.add(item);
				while (nextItem() && item.equals(",")) {
					nextItem();
					lists.add(item);
				}
				if (!item.equals(")")) {
					handleError(SYNTAX);
				}

			} else {
				handleError(UNKOWN);
				return;
			}
		}

		nextItem();

		if (!(commType == BEGIN)) {
			handleError(SYNTAX);
		}

		Function f = new Function(textIdx, -1, lists);

		functs.put(fName.toLowerCase(), f);
		vars.add(new ArrayMap());
		commands.add(f);

		nextEnd();
		nextItem();

	}

	private Object execFunct() throws ScriptException {
		debug("Execute Function");

		Function f = (Function) functs.get(item.toLowerCase());

		nextItem();
		if (!item.equals("(")) {
			handleError(UNBALPARENS);
			return null;
		}

		nextItem();

		ArrayMap newVars = new ArrayMap();

		int i = 0;

		if (!item.equals(")")) {
			newVars.put(f.lists.get(i++), analysis());
			while (item.equals(",")) {
				nextItem();
				newVars.put(f.lists.get(i++), analysis());
			}

			if (f.lists.size() < i) {
				handleError(TOOMANYPARAMS);
			}

			if (!item.equals(")")) {
				handleError(UNBALPARENS);
				return null;
			}
		}

		f.backLoc = textIdx;
		vars.add(newVars);
		commands.add(f);

		textIdx = f.loc;

		return running();
	}

	private boolean endCommand(boolean force) throws ScriptException {
		debug("End Command");

		if (force) {
			while (commands.last().comm != FUNCTION) {
				commands.pop();
			}
		}

		Command p = commands.last();
		if (p == null) {
			handleError(SYNTAX);
		}
		switch (p.comm) {
		case IF:
			nextItem();
			passBack();
			commands.pop();
			return false;
		case FOR:
			int loc = textIdx;
			ForLoop f = (ForLoop) p;
			if (f.loc > 0) {
				textIdx = f.itLoc;
				nextItem();
				vars.last().put(f.vName, (double) analysis());
				textIdx = f.expLoc;
				nextItem();

				if ((boolean) analysis()) {
					textIdx = f.loc;
					textLine = f.line;
				} else {
					passBack();
					commands.pop();
					textIdx = loc;
				}
			} else {
				passBack();
				commands.pop();
			}
			nextItem();
			return false;

		case WHILE:
			loc = textIdx;
			WhileLoop w = (WhileLoop) p;
			if (w.loc > 0) {
				textIdx = w.expLoc;
				nextItem();

				if ((boolean) analysis()) {
					textIdx = w.loc;
					textLine = w.line;
				} else {
					passBack();
					commands.pop();
					textIdx = loc;
				}
			} else {
				passBack();
				commands.pop();
			}
			nextItem();
			return false;
		case FUNCTION:
			loc = textIdx;
			Function funct = (Function) p;
			if (funct.backLoc > 0) {
				textIdx = funct.backLoc;
			}
			passBack();
			commands.pop();

			return true;
		}
		return false;
	}

	private void nextEnd() throws ScriptException {
		debug("Next end");
		int count = 1;
		while (count > 0 && nextItem()) {
			if (commType > 7) {
				count++;
				debug("Find End: " + count);
			}
			if (commType == END) {
				count--;
				debug("Find End: " + count);
			}
			if (commType == ELSE) {
				if (count == 1) {
					execElse();
					return;
				} else if (nextItem() && commType == IF) {
					debug("Find End: " + count);
				}
			}
		}
		if (commType != END) {
			handleError(ENDEXPECTED);
			return;
		}

		endCommand(false);
	}

	/**
	 * 单纯判定脚本命令是否存在，以及向下移动一次命令行
	 * 
	 * @return
	 * @throws ScriptException
	 */
	private boolean nextItem() throws ScriptException {
		boolean result = nextCommand();
		debug(new String[] { "Item: " + item, "CommandStack: " + commands,
				"Type: " + itemType });

		return result;
	}

	/**
	 * 分步检索脚本命令
	 * 
	 * @return
	 * @throws ScriptException
	 */
	private boolean nextCommand() throws ScriptException {
		if (itemType == MACROS && macroType != -1) {
			char flag = macros[1].toCharArray()[0];
			StringBuffer sbr = new StringBuffer(1024);
			while (textIdx < contexts.length) {
				char ch = contexts[textIdx++];
				if (flag == ch) {
					break;
				}
				sbr.append(ch);
			}
			item = sbr.toString();
			itemType = EOL;
			macroType = -1;
			return true;
		}

		char ch = ' ';

		item = "";
		itemType = NONE;
		commType = UNKNCOM;
		macroType = -1;

		while (textIdx < contexts.length && isSpaceOrTab(contexts[textIdx])) {
			textIdx++;
		}

		if (textIdx >= contexts.length) {
			itemType = EOP;
			item = " ";
			return false;
		}

		if (contexts[textIdx] == '\r') {
			textIdx += 2;
			itemType = EOL;
			item = " ";
			textLine++;
			return true;
		}

		ch = contexts[textIdx];

		if (ch == '#'
				|| (ch == '/' && textIdx + 1 < contexts.length && contexts[textIdx + 1] == '/')) {
			while (textIdx < contexts.length && contexts[textIdx] != '\r') {
				textIdx++;
			}
			textIdx += 2;
			itemType = EOL;
			item = " ";
			textLine++;
			return true;
		}

		if (ch == '<' || ch == '>' || ch == '=') {
			switch (ch) {
			case '<':
				if (contexts[textIdx + 1] == '=') {
					item = String.valueOf(LE);
					textIdx += 2;
				} else {
					item = "<";
					textIdx++;
				}
				break;
			case '>':
				if (contexts[textIdx + 1] == '=') {
					item = String.valueOf(GE);
					textIdx += 2;
				} else {
					item = ">";
					textIdx++;
				}
				break;
			case '=':
				if (contexts[textIdx + 1] == '=') {
					item = String.valueOf(EQ);
					textIdx += 2;
				} else {
					item = "=";
					textIdx++;
				}
				break;

			}
			itemType = DELIMITER;
			return true;
		}

		if (isDelim(ch)) {
			item += contexts[textIdx];
			textIdx++;
			itemType = DELIMITER;
			return true;
		} else if (ch == '"') {
			textIdx++;
			ch = contexts[textIdx];
			while (ch != '"' && ch != '\r') {
				item += ch;
				textIdx++;
				ch = contexts[textIdx];
			}
			if (ch == '\r') {
				handleError(MISSQUOTE);
				return false;
			}
			textIdx++;
			itemType = STRING;
			return true;
		} else {
			while (textIdx < contexts.length && !isDelim(contexts[textIdx])) {
				item += contexts[textIdx];
				textIdx++;
			}
			if (isNumber(item)) {
				itemType = NUMBER;
				return true;
			} else if (isBoolean(item)) {
				itemType = BOOLEAN;
				return true;
			} else {
				// 匹配命令
				itemType = lookup(item);
				if (itemType == UNKNCOM) {
					itemType = VARIABLE;
				}
				if (commType == WAIT) {
					item = "";
					int count = 0;
					while (textIdx < contexts.length) {
						ch = contexts[textIdx];
						if ((ch == ' ') || (ch == '\n') || (ch == '\t')
								|| (ch == '\r')) {
							count++;
						}
						if (count > 1) {
							break;
						}
						if (ch != ' ') {
							item += contexts[textIdx];
						}
						textIdx++;
					}
					long sleep = 0;
					if (isNumber(item)) {
						sleep = (long) Double.parseDouble(item);
					} else {
						sleep = waitTimes.get(item.toLowerCase());
					}
					if (sleep <= 0) {
						sleep = 1;
					}
					try {
						Thread.sleep(sleep);
					} catch (Exception e) {
					}
					nextItem();
				}
				return true;
			}
		}
	}

	private Object analysis() throws ScriptException {
		debug("Analysis");
		Object result;
		if (item.equals(EOL) || item.equals(EOP)) {
			handleError(EXPERR);
		}
		result = evalExp1();

		debug("Analysis end: " + result);

		return result;
	}

	private Object evalExp1() throws ScriptException {
		Object result, pResult;
		double l_temp, r_temp;
		boolean lb, rb;
		String ls, rs;
		char op;
		String str;

		result = evalExp2();

		op = item.charAt(0);
		str = item.toLowerCase();

		while (isRelOp(op) || isBoolOp(str)) {
			nextItem();

			if (isNumber(result)) {
				pResult = evalExp2();
				if (isRelOp(op)) {
					if (isNumber(result)) {
						l_temp = (double) result;
						r_temp = (double) pResult;

						switch (op) {
						case '<':
							result = (l_temp < r_temp);
							break;
						case LE:
							result = (l_temp <= r_temp);
							break;
						case '>':
							result = (l_temp > r_temp);
							break;
						case GE:
							result = (l_temp >= r_temp);
							break;
						case EQ:
							result = (l_temp == r_temp);
							break;
						}
					} else {
						handleError(NOTANUMB);
						result = null;
					}
				}
			} else if (isBoolean(result)) {
				pResult = evalExp1();
				if (isBoolOp(str)) {
					if (isBoolean(result)) {

						lb = (boolean) result;
						rb = (boolean) pResult;
						switch (str) {
						case "and":
							result = (lb && rb);
							break;
						case "or":
							result = (lb || rb);
							break;
						case "xor":
							result = (lb ^ rb);
							break;
						case "xand":
							result = (lb == rb);
							break;
						}
					} else {
						handleError(NOTABOOL);
						result = null;
					}
				}
			} else {
				if (isRelOp(op)) {
					pResult = evalExp2();
					if (!isNumber(result)) {
						rs = (String) pResult;
						ls = (String) result;
						double test = (ls.compareTo(rs));

						switch (op) {
						case '<':
							result = test < 0;
							break;
						case LE:
							result = test <= 0;
							break;
						case '>':
							result = test > 0;
							break;
						case GE:
							result = test >= 0;
							break;
						case EQ:
							result = test == 0;
							break;
						}
					} else {
						handleError(NOTASTR);
						result = null;
					}
				}
			}
			op = item.charAt(0);
			str = item.toLowerCase();
		}

		if (DEBUG_E) {
			debug("1: " + result);
		}
		return result;
	}

	private Object evalExp2() throws ScriptException {
		char op;
		Object result;
		Object pResult;

		result = evalExp3();

		while ((op = item.charAt(0)) == '+' || op == '-') {
			nextItem();
			pResult = evalExp3();

			if (isNumber(result)) {
				if (isNumber(pResult)) {
					switch (op) {
					case '-':
						result = (double) result - (double) pResult;
						break;
					case '+':
						result = (double) result + (double) pResult;
						break;
					}
				} else {
					handleError(NOTANUMB);
					return null;
				}
			} else if (!isBoolean(result)) {
				if (!isNumber(pResult) && !isBoolean(pResult)) {
					switch (op) {
					case '-':
						handleError(INVALIDEXP);
					case '+':
						result = (String) result + (String) pResult;
						break;
					}
				} else {
					handleError(NOTASTR);
					return null;
				}
			}
		}
		if (DEBUG_E) {
			debug("2: " + result);
		}
		return result;
	}

	private Object evalExp3() throws ScriptException {
		char op;
		Object result;
		Object partialResult;

		result = evalExp4();

		while ((op = item.charAt(0)) == '*' || op == '/' || op == '%') {
			if (!isNumber(result)) {
				handleError(NOTANUMB);
				return null;
			}

			nextItem();
			partialResult = evalExp4();

			if (!isNumber(partialResult)) {
				handleError(NOTANUMB);
				return null;
			}

			switch (op) {
			case '*':
				result = (double) result * (double) partialResult;
				break;
			case '/':
				if ((double) partialResult == 0.0)
					handleError(DIVBYZERO);
				result = (double) result / (double) partialResult;
				break;
			case '%':
				if ((double) partialResult == 0.0)
					handleError(DIVBYZERO);
				result = (double) result % (double) partialResult;
				break;
			}
		}
		if (DEBUG_E) {
			debug("3: " + result);
		}
		return result;
	}

	private Object evalExp4() throws ScriptException {
		Object result;
		Object partialResult;
		double ex;
		double t;

		result = evalExp5();

		if (item.equals("^")) {
			if (!isNumber(result)) {
				handleError(NOTANUMB);
				return null;
			}

			nextItem();
			partialResult = evalExp4();

			if (!isNumber(partialResult)) {
				handleError(NOTANUMB);
				return null;
			}

			ex = (double) result;
			if ((double) partialResult == 0.0) {
				result = 1.0;
			} else {
				for (t = (double) partialResult - 1; t > 0; t--) {
					result = (double) result * ex;
				}
			}
		}
		if (DEBUG_E) {
			debug("4: " + result);
		}
		return result;
	}

	private Object evalExp5() throws ScriptException {
		Object result;
		String op = item;
		if (item.equals("-") || item.toLowerCase().equals(selectOps[NOT])) {
			nextItem();
			result = evalExp6();
			if (isNumber(result)) {
				if (op.equals("-"))
					result = -(double) result;
				else {
					handleError(NOTABOOL);
					return null;
				}
			} else if (isBoolean(result)) {
				if (op.toLowerCase().equals(selectOps[NOT]))
					result = !(boolean) result;
				else {
					handleError(NOTANUMB);
					return null;
				}
			} else {
				handleError(INVALIDEXP);
				return null;
			}
		} else {
			result = evalExp6();
		}
		if (DEBUG_E) {
			debug("5: " + result);
		}
		return result;
	}

	private Object evalExp6() throws ScriptException {
		Object result;
		if (item.equals("(")) {
			nextItem();
			result = evalExp1();
			if (!item.equals(")")) {
				handleError(UNBALPARENS);
			}
			nextItem();
			return result;
		} else {
			result = atom();
			nextItem();
		}
		if (DEBUG_E) {
			debug("6: " + result);
		}
		return result;
	}

	private Object atom() throws ScriptException {
		switch (itemType) {
		case FUNCT:
			return execFunct();
		case NUMBER:
			try {
				return Double.parseDouble(item);
			} catch (NumberFormatException exc) {
				handleError(NOTANUMB);
			}
		case VARIABLE:
			Object o = getVarVal(item);
			if (o instanceof Number) {
				return (Number) o;
			} else if (isNumber(o)) {
				if (DEBUG_E) {
					debug("atom: " + o.toString());
				}
				return Double.parseDouble(o.toString());
			}
			if (o instanceof Boolean) {
				return (boolean) o;
			} else if (isBoolean(o)) {
				return toBoolean((String) o);
			}
			return o;
		case BOOLEAN:
			return toBoolean(item);
		case STRING:
			return item;
		default:
			return null;
		}
	}

	private boolean isDelim(char c) {
		if ((" \r,<>+-/*%^=();#".indexOf(c) != -1)) {
			return true;
		}
		return false;
	}

	boolean isSpaceOrTab(char c) {
		if (c == ' ' || c == '\t') {
			return true;
		}
		return false;
	}

	protected boolean isRelOp(char c) {
		if (relops.indexOf(c) != -1) {
			return true;
		}
		return false;
	}

	protected boolean isBoolOp(String str) {
		for (int i = 0; i < selectOps.length; i++) {
			if (selectOps[i].equals(str)) {
				return true;
			}
		}
		return false;
	}

	protected boolean isBoolOpId(int id) {
		for (int i = 0; i < selectOpsId.length; i++) {
			if (selectOpsId[i] == id) {
				return true;
			}
		}
		return false;
	}

	protected boolean isBoolean(Object o) {
		String str = o.toString().toLowerCase();
		return str.equals("true") || str.equals("false") || str.equals("yes")
				|| str.equals("no") || str.equals("ok")
				|| StringUtils.isNumber(str);
	}

	protected boolean toBoolean(Object o) {
		String str = o.toString().toLowerCase();
		if (str.equals("true") || str.equals("yes") || str.equals("ok")) {
			return true;
		} else if (StringUtils.isNumber(str)) {
			return Double.parseDouble(str) > 0;
		}
		return false;
	}

	protected boolean isNumber(Object o) {
		String str = o.toString();
		return StringUtils.isNumber(str);
	}

	private int lookup(String str) {
		int i;
		str = StringUtils.rtrim(str.toLowerCase());
		// 判定是否已定义的变量
		for (int j = 0; j < vars.size(); j++) {
			ArrayMap tm = vars.get(j);
			if (tm.containsKey(str)) {
				return VARIABLE;
			}
		}
		// 判定是否已定义的函数
		if (functs.containsKey(str)) {
			return FUNCT;
		}
		// 判定是否分支指令
		for (i = 0; i < selectOps.length; i++) {
			if (selectOps[i].equals(str)) {
				return DELIMITER;
			}
		}
		// 判定是否存在于命令表中
		for (i = 0; i < commTable.length; i++) {
			if (commTable[i].equals(str)) {
				commType = i;
				return COMMAND;
			}
		}
		// 判定是否为宏指令
		for (i = 0; i < macros.length; i++) {
			if (macros[i].equals(str)) {
				macroType = i;
				return MACROS;
			}
		}
		return UNKNCOM;
	}

	private void assignVar() throws ScriptException {
		debug("Assign variable");
		String var;
		var = item;
		if (!Character.isLetter(var.charAt(0))) {
			handleError(UNKOWN);
			return;
		}
		nextItem();
		if (!item.equals("=")) {
			handleError(EQUALEXPECTED);
			return;
		}

		nextItem();

		vars.last().put(var, analysis());
	}

	private void passBack() {
		ArrayMap tvars = vars.pop();
		for (int i = 0; i < tvars.size(); i++) {
			String str = (String) tvars.getKey(i);
			if (vars.last().containsKey(str)) {
				vars.last().put(str, tvars.get(str));
			}
		}
	}

	private Object[] findVar(String vname) {
		Object o = null;
		int idx = vname.lastIndexOf('.');
		if (idx == -1) {
			return null;
		}
		String name = vname.substring(0, idx);
		for (int i = 0; i < vars.size(); i++) {
			ArrayMap tm = vars.get(i);
			if (tm.containsKey(name)) {
				o = tm.get(name);
				break;
			}
		}
		if (o != null) {
			String method = vname.substring(idx + 1, vname.length());
			return new Object[] { name, method, o };
		}
		return findVar(name);
	}

	private Object getReflector(String method, Object value) {
		Object o = null;
		try {
			o = ReflectorUtils.getField(value, method);
		} catch (Exception e) {
			o = null;
		}
		try {
			o = ReflectorUtils.getNotPrefixInvoke(value, method);
		} catch (Exception e) {
			o = null;
		}
		try {
			o = ReflectorUtils.getInvoke(value, method);
		} catch (Exception e) {
			o = null;
		}
		return o;
	}

	/**
	 * 查询指定的json对象元素（暂时未使用递归，预防有不愿展露的数据被穷举出来）
	 * 
	 * @param value
	 * @param vname
	 * @param method
	 * @return
	 */
	private Object queryJson(Object value, String vname, String method) {
		int start = 0;
		int end = 0;
		Object o = null;
		JSONObject json = (JSONObject) value;
		if (json.has(method)) {
			o = json.get(method);
		}
		if (!vname.equals(method)) {
			start = vname.indexOf(method);
			String packName = vname.substring(start + method.length() + 1,
					vname.length());
			json = (JSONObject) o;
			if (packName.indexOf("[") != -1 && packName.indexOf("]") != -1) {
				if (packName.indexOf(".") == -1) {
					start = packName.indexOf('[');
					end = packName.indexOf(']');
					String idxName = packName.substring(start + 1, end);
					packName = packName.substring(0, start);
					if (json.has(packName)) {
						o = json.get(packName);
						if (o != null && o instanceof JSONArray) {
							JSONArray arrays = (JSONArray) o;
							int idx = 0;
							try {
								idx = (int) Double.parseDouble(idxName);
							} catch (Exception ex) {
								idx = 0;
							}
							if (idx < arrays.length()) {
								o = arrays.get(idx);
							} else {
								o = null;
							}
						}
					}
				} else {
					String[] split = StringUtils.split(packName, ".");
					Object obj = null;
					for (String n : split) {
						if (obj == null) {
							if (n.indexOf("[") != -1 && n.indexOf("]") != -1) {
								if (n.indexOf(".") == -1) {
									start = n.indexOf('[');
									end = n.indexOf(']');
									String idxName = n
											.substring(start + 1, end);
									n = n.substring(0, start);
									if (json.has(n)) {
										obj = json.get(n);
										if (obj != null
												&& obj instanceof JSONArray) {
											JSONArray arrays = (JSONArray) obj;
											int idx = 0;
											try {
												idx = (int) Double
														.parseDouble(idxName);
											} catch (Exception ex) {
												idx = 0;
											}
											if (idx < arrays.length()) {
												obj = arrays.get(idx);
											} else {
												obj = null;
											}
										}
									}
								}
							} else {
								if (packName.indexOf(".") == -1) {
									if (o != null && o instanceof JSONObject) {
										if (json.has(packName)) {
											o = json.get(packName);
										}
									}
								} else {
									if (o != null && o instanceof JSONObject) {
										String[] splits = StringUtils.split(
												packName, ",");
										Object v = null;
										for (String s : splits) {
											if (v == null) {
												if (json.has(s)) {
													v = json.get(s);
												}
											} else {
												if (v instanceof JSONObject) {
													if (((JSONObject) v).has(s)) {
														v = ((JSONObject) v)
																.get(s);
													}
												}
											}
										}
									}
								}
							}
						} else {
							if (n.indexOf("[") != -1 && n.indexOf("]") != -1) {
								if (n.indexOf(".") == -1) {
									start = n.indexOf('[');
									end = n.indexOf(']');
									String idxName = n
											.substring(start + 1, end);
									n = n.substring(0, start);
									if (((JSONObject) obj).has(n)) {
										obj = ((JSONObject) obj).get(n);
										if (obj != null
												&& obj instanceof JSONArray) {
											JSONArray arrays = (JSONArray) obj;
											int idx = 0;
											try {
												idx = (int) Double
														.parseDouble(idxName);
											} catch (Exception ex) {
												idx = 0;
											}
											if (idx < arrays.length()) {
												obj = arrays.get(idx);
											} else {
												obj = null;
											}
										}
									}
								}
							} else if (n.indexOf(".") == -1) {
								if (obj != null && obj instanceof JSONObject) {
									if (((JSONObject) obj).has(n)) {
										obj = ((JSONObject) obj).get(n);
									}
								}
							} else {
								if (obj != null && obj instanceof JSONObject) {
									String[] splits = StringUtils.split(n, ",");
									Object v = null;
									for (String s : splits) {
										if (v == null) {
											if (((JSONObject) obj).has(s)) {
												v = ((JSONObject) obj).get(s);
											}
										} else {
											if (v instanceof JSONObject) {
												if (((JSONObject) v).has(s)) {
													v = ((JSONObject) v).get(s);
												}
											}
										}
									}
								}
							}

						}
					}
					o = obj;
				}

			} else {
				if (packName.indexOf(".") == -1) {
					if (o != null && o instanceof JSONObject) {
						if (json.has(packName)) {
							o = json.get(packName);
						}
					}
				} else {
					if (o != null && o instanceof JSONObject) {
						String[] split = StringUtils.split(packName, ",");
						Object v = null;
						for (String n : split) {
							if (v == null) {
								if (json.has(n)) {
									v = json.get(n);
								}
							} else {
								if (v instanceof JSONObject) {
									if (((JSONObject) v).has(n)) {
										v = ((JSONObject) v).get(n);
									}
								}
							}
						}
					}
				}
			}
		}
		return o;
	}

	private Object getVarVal(String vname) throws ScriptException {
		if (!Character.isLetter(vname.charAt(0))) {
			handleError(UNKOWN);
			return 0;
		}
		Object o = null;
		for (int i = 0; i < vars.size(); i++) {
			ArrayMap tm = vars.get(i);
			if (tm.containsKey(vname)) {
				o = tm.get(vname);
				break;
			}
		}
		if (o == null) {
			o = findVar(vname);
			int start = 0;
			int end = 0;
			if (o != null) {
				Object[] result = (Object[]) o;
				// String key = (String) result[0];
				String method = (String) result[1];
				Object value = result[2];
				if (value instanceof JSONObject) {
					o = queryJson(value, vname, method);
				} else if (method.indexOf("|") == -1) {
					o = getReflector(method, value);
				} else {
					start = method.indexOf("|");
					end = method.lastIndexOf("|");
					if (end - start <= 1) {
						method = StringUtils.replace(method, "|", "");
						o = getReflector(method, value);
					} else {
						String[] split = StringUtils.split(
								method.substring(start + 1, end), ",");
						method = method.substring(0, start);
						ArrayList<Object> objs = new ArrayList<Object>(5);
						for (String s : split) {
							s = StringUtils.rtrim(s);
							if (s.indexOf("\"") != -1 || s.indexOf("'") != -1) {
								s = s.replace("\"", "").replace("'", "");
								objs.add(s);
							} else {
								if ("true".equalsIgnoreCase(s)
										|| "yes".equalsIgnoreCase(s)
										|| "ok".equalsIgnoreCase(s)) {
									objs.add(true);
								} else if ("false".equalsIgnoreCase(s)
										|| "no".equalsIgnoreCase(s)) {
									objs.add(false);
								} else if (StringUtils.isNumber(s)) {
									objs.add(Double.parseDouble(s));
								} else {
									Object var = getVarVal(s);
									if (var != null) {
										objs.add(var);
									}
								}
							}
						}
						o = ReflectorUtils.getNotPrefixInvoke(value, method,
								objs.toArray());
					}
				}
			}
		}
		if (vname.indexOf('.') != -1 && o == null) {
			o = "unkown";
		} else if (o == null) {
			handleError(UNKOWN);
		}
		debug("Get var: " + o);
		return o;
	}
}
