package org.ripple.power.hft;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ripple.power.txns.data.Candle;

public class InstrumentAo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String symbolName;
	private List<Candle> priceList;

	public void setPriceList(Collection<Candle> priceList) {
		this.priceList = new ArrayList<Candle>(priceList);
	}

	public void setSymbolName(String symbolName) {
		this.symbolName = symbolName;
	}

	public String getSymbolName() {
		return symbolName;
	}

	public List<Candle> getPriceList() {
		return priceList;
	}

	public InstrumentAo() {
	}

	public InstrumentAo(String symbolName) {
		this.symbolName = symbolName;
	}

	public Candle getCurrentPrice() {
		int index = priceList.size() - 1;
		return priceList.get(index);
	}
}