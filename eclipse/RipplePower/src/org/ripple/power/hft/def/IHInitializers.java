package org.ripple.power.hft.def;

import org.ripple.power.hft.def.mock.EventHandlers;
import org.ripple.power.hft.def.mock.ParamBoolean;
import org.ripple.power.hft.def.mock.ParamDouble;
import org.ripple.power.hft.def.mock.PickerFunction;

public interface IHInitializers {

	ParamDouble slippage();

	ParamDouble commission();

	ParamBoolean shortsell();

	EventHandlers events();

	void instruments(PickerFunction pickerFactory);
}
