package org.ripple.power.ui.view.log;

import java.util.HashMap;

public class ErrorReportLog {

	private String timestamp;

	private Integer version;

	private String message;

	private String stackTrace;

	private String loggerName;

	private String threadName;

	private String level;

	private Integer levelValue;

	private HashMap<String, Object> additionalProperties = new HashMap<String, Object>();

	public HashMap<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(HashMap<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Integer getLevelValue() {
		return levelValue;
	}

	public void setLevelValue(Integer levelValue) {
		this.levelValue = levelValue;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
