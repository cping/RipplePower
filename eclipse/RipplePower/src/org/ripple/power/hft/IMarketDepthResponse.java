package org.ripple.power.hft;

import java.util.List;

public interface IMarketDepthResponse<T extends IMarketOrder> {

	List<T> getBids();

	List<T> getAsks();

	void setBids(List<T> b);

	void setAsks(List<T> a);
}
