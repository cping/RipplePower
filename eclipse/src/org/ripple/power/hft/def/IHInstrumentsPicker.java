package org.ripple.power.hft.def;

import java.util.function.Predicate;

public interface IHInstrumentsPicker {

	IHInstrumentsPicker add(String... idOrSymbol);

	IHInstrumentsPicker all();

	IHInstrumentsPicker remove(String... idOrSymbol);

	IHInstrumentsPicker filter(Predicate<IHInstrument> filter);
}
