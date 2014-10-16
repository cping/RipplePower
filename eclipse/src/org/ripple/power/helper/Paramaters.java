
package org.ripple.power.helper;

import java.awt.Frame;
import java.awt.Image;

import org.ripple.power.utils.GraphicsUtils;


public class Paramaters
{

    public static final int Width_MaidSystem = 886;
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
        Image image =  GraphicsUtils.loadImage("icons/win.png");
        image = GraphicsUtils.transparencyBlackColor(image);
        Image_BOX.SplitWindow(GraphicsUtils.getBufferImage(image));
     
    }



}
