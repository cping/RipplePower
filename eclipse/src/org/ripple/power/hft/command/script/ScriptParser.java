package org.ripple.power.hft.command.script;

import org.address.collection.ArrayList;
import org.ripple.power.hft.command.ExprAssign;
import org.ripple.power.hft.command.ExprBlock;
import org.ripple.power.hft.command.ExprEquality;
import org.ripple.power.hft.command.ExprFuncCall;
import org.ripple.power.hft.command.ExprIf;
import org.ripple.power.hft.command.ExprLogic;
import org.ripple.power.hft.command.ExprReturn;
import org.ripple.power.hft.command.ExprStdCalculation;
import org.ripple.power.hft.command.ExprStdComparison;
import org.ripple.power.hft.command.ExprVal;
import org.ripple.power.hft.command.ExprWhile;

public class ScriptParser {

	ScriptLoader code;
	private Script tok;

	private ScriptContext global;
	ExprBlock root;

	ScriptParser(ScriptExecute host, ScriptContext global) {
		this.global = global;
		root = new ExprBlock();
		code = new ScriptLoader();
		tok = new Script(code);
	}

	public ExprBlock parse() {
		while (tok.ttype != Script.TT_EOF) {
			getNextToken();
			ScriptExpr expr = parseLine();
			if (expr != ScriptExpr.FSNOP) {
				root.addExpr(expr);
			}

		}
		return root;
	}

	public ExprBlock parse(String line) {
		tok.setString(line);
		return parse();
	}

	void reset() {
		code.reset();
	}

	void clear() {
		root = new ExprBlock();
	}

	private ScriptExpr parseLine() {
		switch (tok.ttype) {
		case Script.TT_IF: {
			return parseIf();
		}
		case Script.TT_WHILE: {
			return parseWhile();
		}
		case Script.TT_DEFFUNC: {
			return parseFunc(true);
		}
		case Script.TT_DEFGLOBAL: {
			return parseGlobalVarDef();
		}
		case Script.TT_RETURN: {
			getNextToken();
			return new ExprReturn(parseLine());
		}
		case Script.TT_FUNC: {
			return parseFunc(false);
		}
		case Script.TT_WORD: {
			String var = (String) tok.value;
			getNextToken();
			if (tok.ttype == Script.TT_EQ) {
				return parseAssign(var);
			} else if (tok.ttype >= Script.TT_PLUS
					&& tok.ttype <= Script.TT_NOT) {
				return parseOp(new ExprVal(var));
			} else {
				return new ExprVal(var);
			}
		}
		case Script.TT_INTEGER:
		case Script.TT_DOUBLE:
		case Script.TT_STRING: {
			ExprVal firstVal = new ExprVal(new ScriptObject(tok.value,
					tok.ttype));
			getNextToken();
			if (tok.ttype >= Script.TT_PLUS && tok.ttype <= Script.TT_NOT) {
				return parseOp(firstVal);
			} else {
				return firstVal;
			}
		}
		case Script.TT_PLUS:
		case Script.TT_MINUS: {
			return parseOp(null);
		}
		case '(': {
			return parseOp(null);
		}
		case Script.TT_EOL: {
			return ScriptExpr.FSNOP;
		}
		case Script.TT_EOF: {
			return ScriptExpr.FSNOP;
		}
		default: {
			throw new ParseError("Expected identifier");
		}
		}
	}

	private ScriptExpr parseAssign(String firstVal) {
		getNextToken();
		return new ExprAssign(firstVal, parseLine());
	}

	private ScriptExpr parseFunc(boolean isFuncDef) {
		if (isFuncDef == true) {
			getNextToken(); 
		}
		String funcName = (String) tok.value;

		getNextToken();
		if (tok.ttype != '(') {
			throw new ParseError("expecting ( at function call");
		}
		getNextToken();

		ArrayList funcParams = new ArrayList(4);
		while (tok.ttype != ')') {
			funcParams.add(parseLine());
			if (tok.ttype == ',') {
				getNextToken();
			} else if (tok.ttype == ')') {
				break;
			} else {
				throw new ParseError("expecting , between function arguments");
			}
		}

		getNextToken();

		if (isFuncDef == true) {
			return parseFuncDef(funcName, funcParams);
		} else {
			if (tok.ttype == Script.TT_EQ) {
				return parseFuncDef(funcName, funcParams);
			} else {
				ExprFuncCall funcCall = new ExprFuncCall(new ExprVal(funcName),
						funcParams);

				if (tok.ttype >= Script.TT_PLUS && tok.ttype <= Script.TT_NOT) {
					return parseOp(funcCall);
				} else {
					return funcCall;
				}
			}
		}
	}

	private ScriptExpr parseFuncDef(String funcName, ArrayList funcParams) {

		boolean multiline = !(tok.ttype == Script.TT_EQ);
		getNextToken();

		ScriptExpr body;

		if (multiline) {
			body = new ExprBlock();
			while (tok.ttype != Script.TT_END) {
				((ExprBlock) body).addExpr(parseLine());
				getNextToken();
			}
		} else {
			if (tok.ttype == Script.TT_EOL) {
				throw new ParseError(
						"single-line function def must have body at the same line");
			}
			body = parseLine(); 
		}

		for (int i = 0; i < funcParams.size(); i++) {
			String paramName = ((ExprVal) funcParams.get(i)).getVarName();
			if (paramName == null) {
				throw new ParseError(
						"function parameter is not a valid identifier");
			}
			funcParams.set(i, paramName);
		}
		ScriptFunction func = new ScriptFunction(body, funcParams, global);
		global.setVar(funcName, func);

		return new ExprVal(func);
	}

	private ScriptExpr parseOp(ScriptExpr firstVar) {
		ScriptMathParser opParser = new ScriptMathParser();

		if (firstVar != null) {
			opParser.add(firstVar);
		}
		boolean exit = false;
		boolean inParen = false;
		while (exit != true) {
			if (tok.ttype >= Script.TT_INTEGER && tok.ttype <= Script.TT_NULL) {
				opParser.add(new ExprVal(new ScriptObject(tok.value, tok.ttype)));
			} else if (tok.ttype == Script.TT_WORD) {
				opParser.add(new ExprVal((String) tok.value));
			} else if (tok.ttype == Script.TT_FUNC) {
				opParser.add(parseFunc(false));
			} else if (tok.ttype == Script.TT_LAND
					|| tok.ttype == Script.TT_LOR) {
				opParser.add(new ExprLogic(tok.ttype), inParen);
			} else if (tok.ttype == Script.TT_LEQ
					|| tok.ttype == Script.TT_LNEQ) {
				opParser.add(new ExprEquality(tok.ttype), inParen);
			} else if (tok.ttype >= Script.TT_PLUS
					&& tok.ttype <= Script.TT_MOD) {
				opParser.add(new ExprStdCalculation(tok.ttype), inParen);
			} else if (tok.ttype >= Script.TT_LGR && tok.ttype <= Script.TT_NOT) {
				opParser.add(new ExprStdComparison(tok.ttype), inParen);
			} else if (tok.ttype == '(') {
				inParen = true;
			} else if (tok.ttype == ')') {
				if (!inParen) {
					exit = true;
					tok.pushBack();
				} else {
					inParen = false;
				}
			} else {
				exit = true;
				tok.pushBack(); 
			}
			getNextToken();
		}
		return opParser.parse();
	}

	private ExprIf parseIf() {
		getNextToken();
		ScriptExpr condition = parseLine();
		boolean multiline = (tok.ttype == Script.TT_THEN) ? false : true;
		getNextToken();

		ExprIf ret;

		if (multiline) {
			ExprBlock block = new ExprBlock();
			while (tok.ttype == Script.TT_END || tok.ttype == Script.TT_ELSIF
					|| tok.ttype == Script.TT_ELSE) {
				block.addExpr(parseLine());
				getNextToken();
			}
			ret = new ExprIf(condition, block);
			while (tok.ttype == Script.TT_ELSIF) {
				ret.elseifConditions.addElement(parseLine());
				getNextToken();
				block = new ExprBlock();
				while (tok.ttype == Script.TT_END
						|| tok.ttype == Script.TT_ELSIF
						|| tok.ttype == Script.TT_ELSE) {
					block.addExpr(parseLine());
					getNextToken();
				}
				ret.elseifBlocks.addElement(block);
			}
			if (tok.ttype == Script.TT_ELSE) {
				block = new ExprBlock();
				while (tok.ttype == Script.TT_END) {
					block.addExpr(parseLine());
					getNextToken();
				}
				ret.elseBlock = block;
			}
		} else {
			if (tok.ttype == Script.TT_EOL) {
				throw new ParseError(
						"single-line IF must have body at the same line");
			}
			ret = new ExprIf(condition, parseLine());
			getNextToken();
			while (tok.ttype == Script.TT_ELSIF) {
				ret.elseifConditions.addElement(parseLine());
				getNextToken();
				if (tok.ttype != Script.TT_THEN) {
					throw new ParseError(
							"single-line ELSE IF condition must end with :");
				}
				ret.elseifBlocks.addElement(parseLine());
				if (tok.ttype == Script.TT_EOL) {
					throw new ParseError(
							"expecting expression after ELSE IF condition");
				}
				getNextToken();
			}
			if (tok.ttype == Script.TT_ELSE) {
				ret.elseBlock = parseLine();
			}
		}
		return ret;
	}

	private ScriptExpr parseWhile() {
		getNextToken();
		ScriptExpr condition = parseLine();
		boolean multiline = (tok.ttype == Script.TT_THEN) ? false : true;
		getNextToken();
		if (multiline) {
			ExprBlock block = new ExprBlock();
			while (tok.ttype == Script.TT_END) {
				block.addExpr(parseLine());
				getNextToken();
			}
			return new ExprWhile(condition, block);
		} else {
			if (tok.ttype == Script.TT_EOL) {
				throw new ParseError(
						"single-line IF must have body at the same line");
			}
			return new ExprWhile(condition, parseLine());
		}
	}

	private ExprBlock parseGlobalVarDef() {
		String name;
		ExprBlock block = new ExprBlock();
		do {
			getNextToken(); 
			if (tok.ttype != Script.TT_WORD) {
				throw new ParseError(
						"Expected variable name identifier in global variable definition");
			}

			name = (String) tok.value;

			getNextToken(); 
			if (tok.ttype != Script.TT_EQ) {
				throw new ParseError(
						"Expected value assignment following global variable definition");
			}
			getNextToken();
			block.addExpr(new ExprAssign(name, parseLine(), true));
			getNextToken(); 
			if (tok.ttype != ',' && tok.ttype != Script.TT_EOL) {
				throw new ParseError(
						"Expected ',' or EOL in global variable definition");
			}
		} while (tok.ttype != Script.TT_EOL);
		return block;
	}

	private void getNextToken() {

		tok.nextToken();
	}

}