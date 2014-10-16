package org.ripple.power.print;
import javax.print.PrintException;

public abstract class PrintFunction {
	
	protected String path;

	PrintFunction() {

	}

	protected abstract boolean print() throws PrintException;

	protected void setPath(String path) {
		this.path = path;
	}
}
