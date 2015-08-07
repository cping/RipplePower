package org.ripple.power.ui.view.log;

import java.util.ArrayList;
import java.util.List;

import org.ripple.power.ui.view.log.LoggerMode.Level;

public class LoggerManager {
	
	private static boolean debug;	
	private static boolean debugFramework;	
	private static List<Logger> frameworkLoggerList;	

	static{
		frameworkLoggerList = new ArrayList<Logger>();
		setDebug(true);
		setFrameworkDebug(true);
	}

	public static void setDebug(boolean debug) {
		LoggerManager.debug = debug;
	}

	public static void setFrameworkDebug(boolean debug) {
		LoggerManager.debugFramework = debug;
	}

	public static Logger getLogger(boolean debug, String className) {
		Logger logger = new Logger(className);
		logger.setLogMode(debug);
		logger.setLevel(Level.ALL_LOG);
		return logger;
	}

	public static Logger getLogger(String className){
		return getLogger(debug, className);
	}

	public static Logger getLogger(){
		return getLogger(debug, "Logger");
	}

	public static Logger getFrameworkLogger(boolean debug, String className) {
		Logger logger = getLogger(debugFramework && debug, className);
		frameworkLoggerList.add(logger);
		return logger;
	}

	public static Logger getFrameworkLogger(String className){
		return getFrameworkLogger(debugFramework, className);
	}
}
