package org.ripple.power.ui.view.log;

import java.util.List;

import com.google.common.collect.Lists;

public class ErrorReport {

	  private String osName;

	  private String osVersion;

	  private String osArch;

	  private String appVersion;

	  private String userNotes;

	  private List<ErrorReportLog> logs = Lists.newArrayList();

	  public void setOsName(String osName) {
	    this.osName = osName;
	  }

	  public String getOsName() {
	    return osName;
	  }

	  public void setOsVersion(String osVersion) {
	    this.osVersion = osVersion;
	  }

	  public String getOsVersion() {
	    return osVersion;
	  }

	  public String getOsArch() {
	    return osArch;
	  }

	  public void setOsArch(String osArch) {
	    this.osArch = osArch;
	  }

	  public void setAppVersion(String appVersion) {
	    this.appVersion = appVersion;
	  }

	  public String getAppVersion() {
	    return appVersion;
	  }

	  public void setUserNotes(String userNotes) {
	    this.userNotes = userNotes;
	  }

	  public String getUserNotes() {
	    return userNotes;
	  }

	  public List<ErrorReportLog> getLogEntries() {
	    return logs;
	  }

	  public void setLogEntries(List<ErrorReportLog> log) {
	    this.logs = log;
	  }
}
