package org.ripple.power.txns.btc;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class LogManagerOverride extends LogManager {

	/** Logging reconfiguration in progress */
	private volatile boolean loggingReconfiguration = false;

	/**
	 * Create the JavaBitcoin log manager
	 *
	 * We will let the Java LogManager create its shutdown hook so that the
	 * shutdown context will be set up properly. However, we will intercept the
	 * reset() method so we can delay the actual shutdown until we are done
	 * terminating the JavaBitcoin processes.
	 */
	public LogManagerOverride() {
		super();
	}

	/**
	 * Reconfigure logging support using a configuration file
	 *
	 * @param inStream
	 *            Input stream
	 * @throws IOException
	 *             Error reading input stream
	 * @throws SecurityException
	 *             Caller does not have LoggingPermission("control")
	 */
	@Override
	public void readConfiguration(InputStream inStream) throws IOException, SecurityException {
		loggingReconfiguration = true;
		super.readConfiguration(inStream);
		loggingReconfiguration = false;
	}

	/**
	 * Reset the log handlers
	 *
	 * This method is called to reset the log handlers. We will forward the call
	 * during logging reconfiguration but will ignore it otherwise. This allows
	 * us to continue to use logging facilities during JavaBitcoin shutdown.
	 */
	@Override
	public void reset() {
		if (loggingReconfiguration)
			super.reset();
	}

	/**
	 * JavaBitcoin shutdown is complete, so we can now reset the log handlers.
	 */
	public void logShutdown() {
		super.reset();
	}
}
