package org.ripple.power.ioc;

import java.util.Set;

import org.ripple.power.ioc.injector.Container;

public interface Ioc extends IControl {

	/**
	 * 返回当前Ioc对象中的Feild集合
	 * 
	 * @return
	 */
	public Set getFeilds();

	/**
	 * 获得一个指定的Feild数值
	 * 
	 * @param name
	 * @return
	 */
	public Object getFeild(final String name);

	/**
	 * 检查当前Ioc对象是否为指定接口的实现
	 * 
	 * @param clazz
	 * @return
	 */
	public boolean isImplInterface(final Class clazz);

	/**
	 * 检查当前Ioc对象是否为指定接口的实现
	 * 
	 * @param className
	 * @return
	 */
	public boolean isImplInterface(final String className);

	/**
	 * 指定一个函数，传参并执行
	 * 
	 * @param methodName
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public Object doInvoke(final String methodName, final Object[] args)
			throws Exception;

	/**
	 * 指定一个函数并执行
	 * 
	 * @param methodName
	 * @return
	 * @throws Exception
	 */
	public Object doInvoke(String methodName) throws Exception;

	/**
	 * 获得Ioc中一个子类的Ioc对象
	 * 
	 * @param methodName
	 * @return
	 */
	public Ioc getChild(String methodName);

	/**
	 * 返回当前容器
	 * 
	 * @return
	 */
	public Container getContainer();

	/**
	 * 注入指定方法
	 * 
	 * @param attributeName
	 * @param value
	 */
	public void setMethod(String attributeName, int value);

	/**
	 * 注入指定方法
	 * 
	 * @param attributeName
	 * @param value
	 */
	public void setMethod(String attributeName, long value);

	/**
	 * 注入指定方法
	 * 
	 * @param attributeName
	 * @param value
	 */
	public void setMethod(String attributeName, double value);

	/**
	 * 注入指定方法
	 * 
	 * @param attributeName
	 * @param value
	 */
	public void setMethod(String attributeName, float value);

	/**
	 * 注入指定方法
	 * 
	 * @param attributeName
	 * @param value
	 */
	public void setMethod(String attributeName, boolean value);

	/**
	 * 注入指定方法
	 * 
	 * @param attributeName
	 * @param value
	 */
	public void setMethod(final String attributeName, final Object value);

}
