package org.ripple.power.hft.bot;

import org.ripple.power.config.Loop;
import org.ripple.power.timer.LTimerContext;
import org.ripple.power.txns.Updateable;

public class BOTLoader extends Loop {

	@Override
	public void runTaskTimer(LTimerContext context) {

		
	}

	@Override
	public Updateable main() {
		return new Updateable() {
			
			@Override
			public void action(Object o) {
				mainLoop();
			}
		};
	}
	
	

}
