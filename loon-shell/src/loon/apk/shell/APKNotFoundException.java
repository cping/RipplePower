package loon.apk.shell;

import android.content.res.Resources;

public class APKNotFoundException extends Resources.NotFoundException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public APKNotFoundException(String apk){
        super("Not found APK on :" + apk);
    }
}
