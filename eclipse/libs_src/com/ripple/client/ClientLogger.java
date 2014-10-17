package com.ripple.client;

import java.util.logging.Level;

public class ClientLogger {
	public static boolean quiet = false;

	public static void entering(String name, String mes) {
		System.out.println(name + ":" + mes);
	}

	public static void fine(String mes) {
		System.out.println(mes);
	}

	public static void log(String fmt, Object... args) {
		if (quiet) {
			return;
		}
		if (args.length > 0) {

			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof String) {
					fmt = fmt.replace("{" + i + "}", (String) args[i]);
				} else if (args[i] instanceof Exception) {
					fmt = fmt.replace("{" + i + "}",
							((Exception) args[i]).getMessage());
					((Exception) args[i]).printStackTrace();
				} else if (args[i] instanceof Thread) {
					fmt = fmt.replace("{" + i + "}",
							((Thread) args[i]).getName());
				} else if (args[i] instanceof Enum) {
					fmt = fmt.replace("{" + i + "}", ((Enum) args[i]).name());
				}
			}
			System.err.println(fmt);
		} else {
			System.err.println(fmt);
		}
	}
}
