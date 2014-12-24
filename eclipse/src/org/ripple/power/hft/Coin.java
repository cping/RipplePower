package org.ripple.power.hft;

public class Coin {

	private final Code code;
	private final Symbol symbol;
	private final String name;
	private final double prevPrice;
	private final double openPrice;
	private final double lastPrice;
	private final double highPrice;
	private final double lowPrice;
	private final long volume;
	private final double changePrice;
	private final double changePricePercentage;
	private final int lastVolume;
	private final double buyPrice;
	private final int buyQuantity;
	private final double sellPrice;
	private final int sellQuantity;
	private final double secondBuyPrice;
	private final int secondBuyQuantity;
	private final double secondSellPrice;
	private final int secondSellQuantity;
	private final double thirdBuyPrice;
	private final int thirdBuyQuantity;
	private final double thirdSellPrice;
	private final int thirdSellQuantity;

	private final long timestamp;

	public static class Builder {
		private final Code code;
		private final Symbol symbol;

		private String name = "";

		private double prevPrice = 0.0;
		private double openPrice = 0.0;
		private double lastPrice = 0.0;
		private double highPrice = 0.0;
		private double lowPrice = 0.0;
		private long volume = 0;
		private double changePrice = 0.0;
		private double changePricePercentage = 0.0;
		private int lastVolume = 0;
		private double buyPrice = 0.0;
		private int buyQuantity = 0;
		private double sellPrice = 0.0;
		private int sellQuantity = 0;
		private double secondBuyPrice = 0.0;
		private int secondBuyQuantity = 0;
		private double secondSellPrice = 0.0;
		private int secondSellQuantity = 0;
		private double thirdBuyPrice = 0.0;
		private int thirdBuyQuantity = 0;
		private double thirdSellPrice = 0.0;
		private int thirdSellQuantity = 0;

		private long timestamp = 0;
		private volatile boolean hasTimestampInitialized = false;

		public Builder(Code code, Symbol symbol) {
			this.code = code;
			this.symbol = symbol;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder prevPrice(double prevPrice) {
			this.prevPrice = prevPrice;
			return this;
		}

		public Builder openPrice(double openPrice) {
			this.openPrice = openPrice;
			return this;
		}

		public Builder lastPrice(double lastPrice) {
			this.lastPrice = lastPrice;
			return this;
		}

		public Builder highPrice(double highPrice) {
			this.highPrice = highPrice;
			return this;
		}

		public Builder lowPrice(double lowPrice) {
			this.lowPrice = lowPrice;
			return this;
		}

		public Builder volume(long volume) {
			this.volume = volume;
			return this;
		}

		public Builder changePrice(double changePrice) {
			this.changePrice = changePrice;
			return this;
		}

		public Builder changePricePercentage(double changePricePercentage) {
			this.changePricePercentage = changePricePercentage;
			return this;
		}

		public Builder lastVolume(int lastVolume) {
			this.lastVolume = lastVolume;
			return this;
		}

		public Builder buyPrice(double buyPrice) {
			this.buyPrice = buyPrice;
			return this;
		}

		public Builder buyQuantity(int buyQuantity) {
			this.buyQuantity = buyQuantity;
			return this;
		}

		public Builder sellPrice(double sellPrice) {
			this.sellPrice = sellPrice;
			return this;
		}

		public Builder sellQuantity(int sellQuantity) {
			this.sellQuantity = sellQuantity;
			return this;
		}

		public Builder secondBuyPrice(double secondBuyPrice) {
			this.secondBuyPrice = secondBuyPrice;
			return this;
		}

		public Builder secondBuyQuantity(int secondBuyQuantity) {
			this.secondBuyQuantity = secondBuyQuantity;
			return this;
		}

		public Builder secondSellPrice(double secondSellPrice) {
			this.secondSellPrice = secondSellPrice;
			return this;
		}

		public Builder secondSellQuantity(int secondSellQuantity) {
			this.secondSellQuantity = secondSellQuantity;
			return this;
		}

		public Builder thirdBuyPrice(double thirdBuyPrice) {
			this.thirdBuyPrice = thirdBuyPrice;
			return this;
		}

		public Builder thirdBuyQuantity(int thirdBuyQuantity) {
			this.thirdBuyQuantity = thirdBuyQuantity;
			return this;
		}

		public Builder thirdSellPrice(double thirdSellPrice) {
			this.thirdSellPrice = thirdSellPrice;
			return this;
		}

		public Builder thirdSellQuantity(int thirdSellQuantity) {
			this.thirdSellQuantity = thirdSellQuantity;
			return this;
		}

		public Builder timestamp(long timestamp) {
			this.timestamp = timestamp;
			this.hasTimestampInitialized = true;
			return this;
		}

		public Coin build() {
			if (hasTimestampInitialized == false) {
				this.timestamp = System.currentTimeMillis();
			}
			return new Coin(this);
		}
	}

	private Coin(Builder builder) {
		this(builder.code, builder.symbol, builder.name, builder.prevPrice,
				builder.openPrice, builder.lastPrice, builder.highPrice,
				builder.lowPrice, builder.volume, builder.changePrice,
				builder.changePricePercentage, builder.lastVolume,
				builder.buyPrice, builder.buyQuantity, builder.sellPrice,
				builder.sellQuantity, builder.secondBuyPrice,
				builder.secondBuyQuantity, builder.secondSellPrice,
				builder.secondSellQuantity, builder.thirdBuyPrice,
				builder.thirdBuyQuantity, builder.thirdSellPrice,
				builder.thirdSellQuantity, builder.timestamp);
	}

	public Coin(Code code, Symbol symbol, String name, double prevPrice,
			double openPrice, double lastPrice, double highPrice,
			double lowPrice, long volume, double changePrice,
			double changePricePercentage, int lastVolume, double buyPrice,
			int buyQuantity, double sellPrice, int sellQuantity,
			double secondBuyPrice, int secondBuyQuantity,
			double secondSellPrice, int secondSellQuantity,
			double thirdBuyPrice, int thirdBuyQuantity, double thirdSellPrice,
			int thirdSellQuantity, long timestamp) {
		this.code = code;
		this.symbol = symbol;
		this.name = name;
		this.prevPrice = prevPrice;
		this.openPrice = openPrice;
		this.lastPrice = lastPrice;
		this.highPrice = highPrice;
		this.lowPrice = lowPrice;
		this.volume = volume;
		this.changePrice = changePrice;
		this.changePricePercentage = changePricePercentage;
		this.lastVolume = lastVolume;
		this.buyPrice = buyPrice;
		this.buyQuantity = buyQuantity;
		this.sellPrice = sellPrice;
		this.sellQuantity = sellQuantity;
		this.secondBuyPrice = secondBuyPrice;
		this.secondBuyQuantity = secondBuyQuantity;
		this.secondSellPrice = secondSellPrice;
		this.secondSellQuantity = secondSellQuantity;
		this.thirdBuyPrice = thirdBuyPrice;
		this.thirdBuyQuantity = thirdBuyQuantity;
		this.thirdSellPrice = thirdSellPrice;
		this.thirdSellQuantity = thirdSellQuantity;
		this.timestamp = timestamp;
	}

	public Coin(Coin coin) {
		this.code = coin.code;
		this.symbol = coin.symbol;
		this.name = coin.name;
		this.prevPrice = coin.prevPrice;
		this.openPrice = coin.openPrice;
		this.lastPrice = coin.lastPrice;
		this.highPrice = coin.highPrice;
		this.lowPrice = coin.lowPrice;
		this.volume = coin.volume;
		this.changePrice = coin.changePrice;
		this.changePricePercentage = coin.changePricePercentage;
		this.lastVolume = coin.lastVolume;
		this.buyPrice = coin.buyPrice;
		this.buyQuantity = coin.buyQuantity;
		this.sellPrice = coin.sellPrice;
		this.sellQuantity = coin.sellQuantity;
		this.secondBuyPrice = coin.secondBuyPrice;
		this.secondBuyQuantity = coin.secondBuyQuantity;
		this.secondSellPrice = coin.secondSellPrice;
		this.secondSellQuantity = coin.secondSellQuantity;
		this.thirdBuyPrice = coin.thirdBuyPrice;
		this.thirdBuyQuantity = coin.thirdBuyQuantity;
		this.thirdSellPrice = coin.thirdSellPrice;
		this.thirdSellQuantity = coin.thirdSellQuantity;
		this.timestamp = coin.timestamp;
	}



	public Code getCode() {
		return code;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public String getName() {
		return name;
	}

	public double getPrevPrice() {
		return prevPrice;
	}

	public double getOpenPrice() {
		return openPrice;
	}

	public double getLastPrice() {
		return lastPrice;
	}

	public double getHighPrice() {
		return highPrice;
	}

	public double getLowPrice() {
		return lowPrice;
	}

	public long getVolume() {
		return volume;
	}

	public double getChangePrice() {
		return changePrice;
	}

	public double getChangePricePercentage() {
		return changePricePercentage;
	}

	public int getLastVolume() {
		return lastVolume;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public int getBuyQuantity() {
		return buyQuantity;
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public int getSellQuantity() {
		return sellQuantity;
	}

	public double getSecondBuyPrice() {
		return secondBuyPrice;
	}

	public int getSecondBuyQuantity() {
		return secondBuyQuantity;
	}

	public double getSecondSellPrice() {
		return secondSellPrice;
	}

	public int getSecondSellQuantity() {
		return secondSellQuantity;
	}

	public double getThirdBuyPrice() {
		return thirdBuyPrice;
	}

	public int getThirdBuyQuantity() {
		return thirdBuyQuantity;
	}

	public double getThirdSellPrice() {
		return thirdSellPrice;
	}

	public int getThirdSellQuantity() {
		return thirdSellQuantity;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public Coin deriveStock(String name) {
		return new Coin(this.code, this.symbol, name, this.prevPrice,
				this.openPrice, this.lastPrice, this.highPrice, this.lowPrice,
				this.volume, this.changePrice, this.changePricePercentage,
				this.lastVolume, this.buyPrice, this.buyQuantity,
				this.sellPrice, this.sellQuantity, this.secondBuyPrice,
				this.secondBuyQuantity, this.secondSellPrice,
				this.secondSellQuantity, this.thirdBuyPrice,
				this.thirdBuyQuantity, this.thirdSellPrice,
				this.thirdSellQuantity, this.timestamp);
	}

	public Coin deriveStock(Symbol symbol) {
		return new Coin(this.code, symbol, this.name, this.prevPrice,
				this.openPrice, this.lastPrice, this.highPrice, this.lowPrice,
				this.volume, this.changePrice, this.changePricePercentage,
				this.lastVolume, this.buyPrice, this.buyQuantity,
				this.sellPrice, this.sellQuantity, this.secondBuyPrice,
				this.secondBuyQuantity, this.secondSellPrice,
				this.secondSellQuantity, this.thirdBuyPrice,
				this.thirdBuyQuantity, this.thirdSellPrice,
				this.thirdSellQuantity, this.timestamp);
	}

	@Override
	public String toString() {
		return symbol.toString();
	}
}
