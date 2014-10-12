
package org.ripple.power.helper;

import java.awt.Container;
import java.awt.Frame;

import org.ripple.power.utils.GraphicsUtils;


public class Paramaters
{

    public static final int Width_MaidSystem = 890;
    public static final int Height_MaidSystem = 180;
    public static final int defaultFrameSize = 0;
    public static int frameTop = 0;
    public static int frameLeft = 0;
    public static ImageSet Image_BOX;
	private static Frame _container;
	
	public static Frame getContainer(){
		return _container;
	}
	
    public Paramaters()
    {
    }

    public static void format(Frame f)
    {
    	_container = f;
        Image_BOX = new ImageSet();
        Image_BOX.SplitWindow(GraphicsUtils.loadBufferedImage("icons/win.png"));
     
    }



}
