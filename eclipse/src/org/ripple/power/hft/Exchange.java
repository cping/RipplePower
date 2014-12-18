package org.ripple.power.hft;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Exchange implements IExchange{
	
    private final Object lock = new Object();

    private final Map<String, SimpleOrder> marketData;
    
    private static Map<String, Double> DEFAULT_PRICES;

    static double getDefaultPrice(String security) {
        return DEFAULT_PRICES.get(security);
    }

    static Set<String> getAllSecurities() {
        return DEFAULT_PRICES.keySet();
    }
    
    Exchange(Set<String> curs) {
        Map<String, SimpleOrder> marketData = new HashMap<>();
        for (String security : curs) {
            double price = getDefaultPrice(security);
            marketData.put(security, new SimpleOrder(price - 0.000001, price + 0.000001));
        }
        this.marketData = marketData;
    }

    @Override
    public Set<String> getAvailableCurs() {
        synchronized(lock) {
            return marketData.keySet();
        }
    }

    @Override
    public double getMaxBid(String security) {
        synchronized(lock) {
            return marketData.get(security).bid;
        }
    }

    @Override
    public double getMinAsk(String security) {
        synchronized(lock) {
            return marketData.get(security).ask;
        }
    }

    @Override
    public boolean buy(String security, double price) {
        synchronized(lock) {
            SimpleOrder order = marketData.get(security);
            if (order.ask >= price) {
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean sell(String security, double price) {
        synchronized(lock) {
            SimpleOrder order = marketData.get(security);
            if (order.bid <= price) {
                return true;
            }
            return false;
        }
    }

    void setNewPrices(String security, SimpleOrder order) {
        synchronized(lock) {
            marketData.put(security, order);
        }
    }
}
