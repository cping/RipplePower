package org.ripple.power.txns;

public interface Rollback {
	
	 void success(String res);

	 void failure(String res);

	 void error(String res);

	 void validated(String res);
}
