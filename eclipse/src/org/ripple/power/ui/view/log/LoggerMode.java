package org.ripple.power.ui.view.log;

public interface LoggerMode{
	
	public static enum Level{
		DEBUG(0),
		INFO(1),
		WARN(2),
		ERROR(3),
		FATAL(4),
		MAX(5),
		NO_LOG(-1), 
		ALL_LOG(0);	

		private int val;

		Level(int val){
			this.val = val;
		}

		public int getVal() {
			return val;
		}
	};

	public abstract void Log(String tag, String msg,Object... obj);
	public abstract void setLevel(Level level);

	public abstract void debug(String message,Object... obj);
	public abstract void info(String message,Object... obj);
	public abstract void warn(String message,Object... obj);
	public abstract void fatal(String message,Object... obj);
	public abstract void error(String message,Object... obj);
}
