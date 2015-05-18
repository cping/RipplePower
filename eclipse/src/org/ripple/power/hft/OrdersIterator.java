package org.ripple.power.hft;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class OrdersIterator {

	static class NewOrderImpl implements NewOrder {

		private String symbol;
		private String orderId;
		public int size;
		public double limitPrice;

		NewOrderImpl(String symbol, String orderId, int size, double limitPrice) {
			this.symbol = symbol;
			this.orderId = orderId;
			this.size = size;
			this.limitPrice = limitPrice;
		}

		public String getSymbol() {
			return symbol;
		}

		public String getOrderId() {
			return orderId;
		}

		public int getSize() {
			return size;
		}

		public double getLimitPrice() {
			return limitPrice;
		}
	}

	static class OrderCxRImpl implements OrderCxR {

		private int size;
		private double limitPx;
		private String orderId;

		public OrderCxRImpl(int size, double limitPx, String orderId) {
			this.size = size;
			this.limitPx = limitPx;
			this.orderId = orderId;
		}

		public int getSize() {
			return size;
		}

		public double getLimitPrice() {
			return limitPx;
		}

		public String getOrderId() {
			return orderId;
		}
	}

	private List<Order> orders;

	public OrdersIterator(LinkedList<Order> list) {
		this.orders = new LinkedList<Order>();
		this.orders.addAll(list);
	}

	public Iterator<Order> getIterator() {
		return orders.iterator();

	}
}
