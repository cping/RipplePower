package org.ripple.power.ui.projector.action.avg.command;

import java.util.Random;

import org.ripple.power.config.LSystem;

public interface Expression {

	// 全局随机数
	Random GLOBAL_RAND = LSystem.random;

	// 默认变量1,用于记录当前选择项
	String V_SELECT_KEY = "SELECT";

	// 左括号
	String BRACKET_LEFT_TAG = "(";

	// 右括号
	String BRACKET_RIGHT_TAG = ")";

	// 代码段开始标记
	String BEGIN_TAG = "begin";

	// 代码段结束标记
	String END_TAG = "end";

	// 代码段调用标记
	String CALL_TAG = "call";

	// 缓存刷新标记
	String RESET_CACHE_TAG = "reset";

	// 累计输入数据标记
	String IN_TAG = "in";

	// 累计输入数据停止（输出）标记
	String OUT_TAG = "out";

	// 多选标记
	String SELECTS_TAG = "selects";

	// 打印标记
	String PRINT_TAG = "print";

	// 随机数标记
	String RAND_TAG = "rand";

	// 设定环境变量标记
	String SET_TAG = "set";

	// 载入内部脚本标记
	String INCLUDE_TAG = "include";

	// 条件判定标记
	String IF_TAG = "if";

	// 条件判定结束标记
	String IF_END_TAG = "endif";

	// 转折标记
	String ELSE_TAG = "else";

	// 以下为注视符号
	String FLAG_L_TAG = "//";

	String FLAG_C_TAG = "#";

	String FLAG_I_TAG = "'";

	String FLAG_LS_B_TAG = "/*";

	String FLAG_LS_E_TAG = "*/";

	String FLAG = "@";

	char FLAG_CHAR = FLAG.toCharArray()[0];

}
