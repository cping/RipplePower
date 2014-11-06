package org.ripple.power.hft.command.script;

import org.address.collection.ArrayList;

final class ScriptLoader {

	public ArrayList lines;

	public int curLine;

	public ScriptLoader() {

		lines = new ArrayList(20);
		curLine = -1;
	}

	public final void reset() {
		lines = null;
		lines = new ArrayList(20);
		curLine = -1;
	}

	public final void addLine(String s) {
		if (!s.trim().equals("")) {
			lines.add(s);
		} else {
			lines.add("");
		}
	}

	public final void addLines(String script) {
		int pos;
		String tmp = script.trim();
		if (!"".equals(tmp)) {
			pos = script.indexOf('\n');
			while (pos >= 0) {
				addLine(script.substring(0, pos));
				script = script.substring(pos + 1, script.length());
				pos = script.indexOf('\n');
			}
			if (!"".equals(script.trim())) {
				addLine(script);
			}
		}
	}

	public final void setCurLine(int n) {
		if (n >= lines.size()) {
			n = lines.size() - 1;
		} else if (n < 0) {
			n = 0;
		}
		curLine = n;
	}

	public final String nextLine() {
		if (++curLine >= lines.size()) {
			curLine = lines.size() - 1;
			return "";
		}
		return (String) lines.get(curLine);
	}

	public final int getCurLine() {
		return curLine;
	}

	public final int lineCount() {
		return lines.size() - 1;
	}

	public final String getLine() {
		if (curLine == -1){
			return "";
		}
		return (String) lines.get(curLine);
	}

	public final String getLine(int n) {
		if (n < 0 || n >= lines.size()){
			return "";
		}
		return (String) lines.get(n);
	}

}
