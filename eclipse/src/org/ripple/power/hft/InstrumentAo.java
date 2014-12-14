package org.ripple.power.hft;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InstrumentAo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String symbolName;
	private List<Price> priceList;

	public void setPriceList(Collection<Price> priceList) {
		this.priceList = new ArrayList<Price>(priceList);
	}

	public void setSymbolName(String symbolName) {
		this.symbolName = symbolName;
	}

	public String getSymbolName() {
		return symbolName;
	}

	public List<Price> getPriceList() {
		return priceList;
	}

	public InstrumentAo() {
	}

	public InstrumentAo(String symbolName) {
		this.symbolName = symbolName;
	}

	public Price getCurrentPrice() {
		int index = priceList.size() - 1;
		return priceList.get(index);
	}
}