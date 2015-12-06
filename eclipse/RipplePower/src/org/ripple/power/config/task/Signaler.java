package org.ripple.power.config.task;

public final class Signaler {

	public static final int SIG0 = 0;

	public static final int SIG1 = 1;

	public static final int SIG2 = 2;

	private int bits = 0;

	private int count = 0;

	synchronized public void raise(int E) {
		if (E == Signaler.SIG1) {
			count++;
		}
		bits |= bit(E);

		notify();
	}

	synchronized public int waitSignal(long timeout) {
		if (bits == 0) {
			long start = System.currentTimeMillis();
			try {
				wait(timeout);
			} catch (InterruptedException e) {
				return -1;
			}

			long end = System.currentTimeMillis();
			if (timeout > 0 && timeout <= (end - start)) {
				bits = 0;
				return Signaler.SIG2;
			}
		}

		if ((bits & bit(Signaler.SIG0)) != 0) {
			bits &= ~bit(Signaler.SIG0);
			return Signaler.SIG0;
		}

		if ((bits & bit(Signaler.SIG1)) != 0) {
			count--;
			if (count == 0) {
				bits &= ~bit(Signaler.SIG1);
			}
			return Signaler.SIG1;
		}

		return -1;

	}

	private int bit(int bitIndex) {
		return 1 << (bitIndex & 31);
	}
}
