package org.ripple.power.hft.def.mock;

import org.ripple.power.hft.def.IHInstrumentsPicker;

public interface PickerFunction {

	IHInstrumentsPicker pick(IHInstrumentsPicker universe);

}
