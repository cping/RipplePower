package org.ripple.power.hft.bot.ripple;

import org.ripple.power.config.Loop;
import org.ripple.power.hft.bot.TraderBase;
import org.ripple.power.timer.LTimerContext;
import org.ripple.power.txns.Updateable;

public class RippleBOTLoader extends Loop {

	private TraderBase _base;

	public RippleBOTLoader(TraderBase base) {
		this._base = base;
	}

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

	public TraderBase getBase() {
		return _base;
	}

	public void setBase(TraderBase base) {
		this._base = base;
	}

}
