package org.ripple.power.hft.def;

import org.ripple.power.hft.def.mock.StatisticsFunction;

public interface IHStatisticsGroup extends Iterable<IHStatistics> {

	IHStatistics get(String idOrSymbol);

	IHStatistics get(IHInstrument instrument);

	void each(StatisticsFunction stat);
}
