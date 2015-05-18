package org.ripple.power.utils;

public class Query {

	private String proxyWeb="www.baidu.com"; // 目标站点
	private String proxyHost; // 代理服务器的ip地址
	private Integer proxyPort; // 代理服务器的端口
	private String proxyType;  // 类型
	private String proxyAddress; // 地域
	private String proxyUser="test"; // 代理服务器用户名
	private String proxyPassword="test"; // 代理服务器密码
	private Integer live;// 是否存活 0 ：存活 1 ：死亡
	private String  proxyStartTime ;//开始时间
	private String  proxyEendTime;//结束时间
	public String getProxyWeb() {
		return proxyWeb;
	}

	public void setProxyWeb(String proxyWeb) {
			this.proxyWeb = proxyWeb;
	}
	public String getProxyHost() {
		return proxyHost;
	}
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}
	public Integer getProxyPort() {
		return proxyPort;
	}
	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}
	public String getProxyType() {
		return proxyType;
	}
	public void setProxyType(String proxyType) {
		this.proxyType = proxyType;
	}
	public String getProxyAddress() {
		return proxyAddress;
	}
	public void setProxyAddress(String proxyAddress) {
		this.proxyAddress = proxyAddress;
	}
	public String getProxyUser() {
		return proxyUser;
	}
	public void setProxyUser(String proxyUser) {
			this.proxyUser = proxyUser;
	}
	public String getProxyPassword() {
		return proxyPassword;
	}
	public void setProxyPassword(String proxyPassword) {
			this.proxyPassword = proxyPassword;
	}
	public Integer getLive() {
		return live;
	}
	public void setLive(Integer live) {
		this.live = live;
	}


	public String getProxyStartTime() {
		return proxyStartTime;
	}

	public void setProxyStartTime(String proxyStartTime) {
		this.proxyStartTime = proxyStartTime;
	}

	public String getProxyEendTime() {
		return proxyEendTime;
	}

	public void setProxyEendTime(String proxyEendTime) {
		this.proxyEendTime = proxyEendTime;
	}


}
