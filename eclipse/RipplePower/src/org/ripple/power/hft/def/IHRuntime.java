package org.ripple.power.hft.def;

/**
 * 该结构体包含了一些与市场无直接关系，而与策略运行相关的信息
 * 
 * @author Administrator
 *
 */
public interface IHRuntime {

	/**
	 * 策略已运行的天数
	 * 
	 * @return
	 */
	int getDayPassed();
}
