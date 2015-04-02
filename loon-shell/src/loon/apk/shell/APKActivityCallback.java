package loon.apk.shell;

import android.os.Bundle;
import android.view.KeyEvent;

public interface APKActivityCallback {

    public void runOnCreate(Bundle saveInstance);

    public void runOnStart() ;

    public void runOnResume();

    public void runOnDestroy();

    public void runOnStop() ;
 
    public void runOnRestart();

    public void runOnSaveInstanceState(Bundle state);
    
    public void runOnRestoreInstanceState(Bundle savedInstanceState);

    public void runOnPause();

    public void runOnBackPressed();

    public boolean runOnKeyDown(int keyCode, KeyEvent e) ;

}
