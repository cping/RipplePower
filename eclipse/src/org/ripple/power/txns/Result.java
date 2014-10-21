package org.ripple.power.txns;

import java.util.HashSet;

public class Result {

	private final static HashSet<String> okResultCodes = new HashSet<String>(10);

	static {
		okResultCodes.add("tesSUCCESS");
		okResultCodes.add("telINSUF_FEE_P");
		okResultCodes.add("tefPAST_SEQ");
		okResultCodes.add("terPRE_SEQ");
		okResultCodes.add("temBAD_SEQUENCE");
	}

}
