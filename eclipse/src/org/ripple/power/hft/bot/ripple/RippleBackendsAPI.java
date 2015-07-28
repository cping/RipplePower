package org.ripple.power.hft.bot.ripple;

//select rippled api or other api
public class RippleBackendsAPI {

	public static enum Model {
		RippleRestAPI, Rippled
	}
	
	public Model model = Model.Rippled;
	
	

}
