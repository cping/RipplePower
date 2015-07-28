package org.ripple.power.hft.bot.ripple;

import java.util.HashSet;

public class Const {

	public final static double DROPS_IN_XRP = 1000000.0;

	public final static int MAX_FEE = 12000;

	public static HashSet<String> OkResultCodes = new HashSet<String>(10);
	static {
		OkResultCodes.add("tesSUCCESS");
		OkResultCodes.add("telINSUF_FEE_P");
		OkResultCodes.add("tefPAST_SEQ");
		OkResultCodes.add("terPRE_SEQ");
		OkResultCodes.add("temBAD_SEQUENCE");
	}
}
