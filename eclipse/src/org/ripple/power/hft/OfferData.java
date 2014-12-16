package org.ripple.power.hft;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.ripple.power.config.LSystem;
import org.ripple.power.hft.OrdersIterator.NewOrderImpl;
import org.ripple.power.hft.OrdersIterator.OrderCxRImpl;

public class OfferData {

	static HashMap<String, Hashtable<Double, LinkedList<NewOrderImpl>>> bid = new HashMap<String, Hashtable<Double, LinkedList<NewOrderImpl>>>();
	static HashMap<String, Hashtable<Double, LinkedList<NewOrderImpl>>> ask = new HashMap<String, Hashtable<Double, LinkedList<NewOrderImpl>>>();

	OrdersIterator ordersIterator;

	public Double getHighestBid(
			Hashtable<Double, LinkedList<NewOrderImpl>> bidbook) {
		Set<Double> keys = bidbook.keySet();
		Double highestBid = Double.MIN_VALUE;
		for (Double key : keys) {
			if (bidbook.get(key).size() > 0 && key > highestBid) {
				highestBid = key;
			}
		}
		return highestBid;
	}

	public Double getLowestAsk(
			Hashtable<Double, LinkedList<NewOrderImpl>> askbook) {
		Set<Double> keys = askbook.keySet();
		Double lowestAsk = Double.MAX_VALUE;
		for (Double key : keys) {
			if (askbook.get(key).size() > 0 && key < lowestAsk) {
				lowestAsk = key;
			}
		}
		return lowestAsk;
	}

	public void addNewOrder(NewOrderImpl myNewOrder, String symbol,
			Hashtable<Double, LinkedList<NewOrderImpl>> bidbook,
			Hashtable<Double, LinkedList<NewOrderImpl>> askbook) {
		double myprice = myNewOrder.getLimitPrice();
		LinkedList<NewOrderImpl> temp = new LinkedList<NewOrderImpl>();
		if (myNewOrder.size >= 0) {
			if (myprice == Double.NaN) {
				Double marketprice = getLowestAsk(askbook);
				LinkedList<NewOrderImpl> asktrade = askbook.get(marketprice);
				while (myNewOrder.size != 0) {
					if (asktrade != null) {
						NewOrderImpl firstOrder = asktrade.getFirst();
						if (Math.abs(firstOrder.size) > Math
								.abs(myNewOrder.size)) {
							firstOrder.size += myNewOrder.size;
							myNewOrder.size = 0;
						} else {
							myNewOrder.size += firstOrder.size;
							asktrade.removeFirst();
						}
					} else {
						marketprice = getHighestBid(bidbook);
						asktrade = bidbook.get(marketprice);
					}
				}
			} else {
				Double marketprice = getLowestAsk(askbook);
				if (marketprice <= myprice) {
					LinkedList<NewOrderImpl> asktrade = new LinkedList<NewOrderImpl>();
					while (myNewOrder.size != 0 && marketprice <= myprice) {
						asktrade = askbook.get(marketprice);
						NewOrderImpl firstOrder = asktrade.getFirst();
						if (Math.abs(firstOrder.size) > Math
								.abs(myNewOrder.size)) {
							firstOrder.size += myNewOrder.size;
							myNewOrder.size = 0;
						} else {
							myNewOrder.size += firstOrder.size;
							asktrade.removeFirst();
						}
						marketprice = getLowestAsk(askbook);
					}
					if (myNewOrder.size != 0) {
						if (bidbook.containsKey(myprice)) {
							temp = bidbook.get(myprice);
						}
						temp.push(myNewOrder);
						bidbook.put(myprice, temp);
					}
				} else {
					if (bidbook.containsKey(myprice)) {
						temp = bidbook.get(myprice);
					}
					temp.push(myNewOrder);
					bidbook.put(myprice, temp);
				}
			}
		} else {
			if (myprice == Double.NaN) {
				Double marketprice = getHighestBid(bidbook);
				LinkedList<NewOrderImpl> bidtrade = bidbook.get(marketprice);
				while (myNewOrder.size != 0) {
					if (bidtrade != null) {
						NewOrderImpl firstOrder = bidtrade.getFirst();
						if (Math.abs(firstOrder.size) > Math
								.abs(myNewOrder.size)) {
							firstOrder.size += myNewOrder.size;
							myNewOrder.size = 0;
						} else {
							myNewOrder.size += firstOrder.size;
							bidtrade.removeFirst();
						}
					} else {
						marketprice = getHighestBid(bidbook);
						bidtrade = bidbook.get(marketprice);
					}
				}
			} else {
				Double marketprice = getHighestBid(bidbook);
				if (marketprice >= myprice) {
					LinkedList<NewOrderImpl> bidtrade = new LinkedList<NewOrderImpl>();

					while (myNewOrder.size != 0 && marketprice >= myprice) {
						bidtrade = bidbook.get(marketprice);
						NewOrderImpl firstOrder = bidtrade.getFirst();
						if (Math.abs(firstOrder.size) > Math
								.abs(myNewOrder.size)) {
							firstOrder.size += myNewOrder.size;
							myNewOrder.size = 0;
						} else {
							myNewOrder.size += firstOrder.size;
							bidtrade.removeFirst();
						}
						marketprice = getHighestBid(bidbook);
					}

					if (myNewOrder.size != 0) {
						if (askbook.containsKey(myprice)) {
							temp = askbook.get(myprice);
						}
						temp.push(myNewOrder);
						askbook.put(myprice, temp);
					}
				}

				else {
					if (askbook.containsKey(myprice)) {
						temp = askbook.get(myprice);
					}
					temp.push(myNewOrder);
					askbook.put(myprice, temp);
				}
			}
		}
		bid.put(symbol, bidbook);
		ask.put(symbol, askbook);
	}

	public NewOrderImpl lookUpOrder(String id) {
		Set<String> keys = bid.keySet();
		Hashtable<Double, LinkedList<NewOrderImpl>> temptable = new Hashtable<Double, LinkedList<NewOrderImpl>>();
		for (String key : keys) {
			temptable = bid.get(key);
			LinkedList<NewOrderImpl> temp;
			NewOrderImpl replaceOrder, tmp;
			Set<Double> tablekey = temptable.keySet();
			for (Double key2 : tablekey) {
				temp = temptable.get(key2);
				for (Iterator<NewOrderImpl> it = temp.iterator(); it.hasNext();) {
					tmp = it.next();
					if (tmp.getOrderId().equals(id)) {
						replaceOrder = tmp;
						temp.remove(tmp);
						temptable.put(key2, temp);
						bid.put(key, temptable);
						return replaceOrder;
					}
				}
			}
		}
		keys = ask.keySet();
		for (String key : keys) {
			temptable = ask.get(key);
			LinkedList<NewOrderImpl> temp;
			NewOrderImpl replaceOrder, tmp;
			Set<Double> tablekey = temptable.keySet();
			for (Double key2 : tablekey) {
				temp = temptable.get(key2);
				for (Iterator<NewOrderImpl> it = temp.iterator(); it.hasNext();) {
					tmp = it.next();
					if (tmp.getOrderId().equals(id)) {
						replaceOrder = tmp;
						temp.remove(tmp);
						temptable.put(key2, temp);
						ask.put(key, temptable);
						return replaceOrder;
					}
				}
			}
		}
		return null;
	}

	public String cxrOrder(OrderCxRImpl changeOrder) {
		String orderID = changeOrder.getOrderId();
		NewOrderImpl renewOrder = lookUpOrder(orderID);
		String orderSymbol = renewOrder.getSymbol();
		if (renewOrder != null && changeOrder.getSize() != 0) {
			renewOrder.size = changeOrder.getSize();
			renewOrder.limitPrice = changeOrder.getLimitPrice();
			Hashtable<Double, LinkedList<NewOrderImpl>> tempbid = new Hashtable<Double, LinkedList<NewOrderImpl>>();
			Hashtable<Double, LinkedList<NewOrderImpl>> tempask = new Hashtable<Double, LinkedList<NewOrderImpl>>();
			if (bid.containsKey(orderSymbol)) {
				tempbid = bid.get(orderSymbol);
			}
			if (ask.containsKey(orderSymbol)) {
				tempask = ask.get(orderSymbol);
			}
			addNewOrder(renewOrder, orderSymbol, tempbid, tempask);
		}
		return orderSymbol;
	}

	public OfferData(LinkedList<Order> list) {
		ordersIterator = new OrdersIterator(list);
	}

	public static String result(LinkedList<Order> list, Iterator<Order> myiter,
			boolean flag) {
		StringBuilder sbr = new StringBuilder();
		OfferData book = new OfferData(list);
		while (myiter.hasNext()) {
			Object mymessage = myiter.next();
			Hashtable<Double, LinkedList<NewOrderImpl>> tempbid = new Hashtable<Double, LinkedList<NewOrderImpl>>();
			Hashtable<Double, LinkedList<NewOrderImpl>> tempask = new Hashtable<Double, LinkedList<NewOrderImpl>>();
			if (mymessage instanceof NewOrderImpl) {
				NewOrderImpl myneworder = (NewOrderImpl) mymessage;
				String mysymbol = myneworder.getSymbol();
				if (bid.containsKey(mysymbol)) {
					tempbid = bid.get(mysymbol);
				}
				if (ask.containsKey(mysymbol)) {
					tempask = ask.get(mysymbol);
				}
				book.addNewOrder(myneworder, mysymbol, tempbid, tempask);
				if (flag) {
					if (book.getHighestBid(tempbid) == Double.MIN_VALUE) {
						sbr.append(mysymbol + " Best Bid Price:  no bid orders")
								.append(LSystem.LS);
					} else {
						sbr.append(
								mysymbol + " Best Bid Price:  "
										+ book.getHighestBid(tempbid)).append(
								LSystem.LS);
					}
					if (book.getLowestAsk(tempask) == Double.MAX_VALUE) {
						sbr.append(mysymbol + " Best Ask Price:  no ask orders")
								.append(LSystem.LS);
					} else {
						sbr.append(
								mysymbol + " Best Ask Price:  "
										+ book.getLowestAsk(tempask)).append(
								LSystem.LS);
					}
				}
			} else if (mymessage instanceof OrderCxRImpl) {
				String mysymbol = book.cxrOrder((OrderCxRImpl) mymessage);
				tempbid = bid.get(mysymbol);
				tempask = ask.get(mysymbol);
				if (flag) {
					if (book.getHighestBid(tempbid) == Double.MIN_VALUE) {
						sbr.append(mysymbol + " Best Bid Price:  no bid orders")
								.append(LSystem.LS);
					} else {
						sbr.append(
								mysymbol + " Best Bid Price:  "
										+ book.getHighestBid(tempbid)).append(
								LSystem.LS);
					}
					if (book.getLowestAsk(tempask) == Double.MAX_VALUE) {
						sbr.append(mysymbol + " Best Ask Price:  no ask orders")
								.append(LSystem.LS);
					} else {
						sbr.append(
								mysymbol + " Best Ask Price:  "
										+ book.getLowestAsk(tempask)).append(
								LSystem.LS);
					}
				}
			}
		}
		return sbr.toString();
	}

}
