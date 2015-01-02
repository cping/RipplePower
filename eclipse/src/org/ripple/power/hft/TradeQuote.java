package org.ripple.power.hft;


public class TradeQuote implements Comparable<TradeQuote>{


	
    final Long time;
    final double volume;
    final double price;

    public TradeQuote(Long time, Long volume, double price) {
        this.time = time;
        this.volume = volume;
        this.price = price;
    }


    @Override
    public int compareTo(TradeQuote o) {
        return time.compareTo(o.time);
    }


}
