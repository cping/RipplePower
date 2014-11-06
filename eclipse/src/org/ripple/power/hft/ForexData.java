package org.ripple.power.hft;

public class ForexData extends MarketMatrix{

	public static final long ONE_MINUTE = 1000l*60l;
	private final String currency;
	
	private final int indexof_lastRecord;
	
	private static final int OPEN = 0;
	private static final int HIGH = 1;
	private static final int LOW  = 2;
	private static final int CLOSE = 3;
	private static final int VOL = 4;
	private static final int TS = 5;
	
	public ForexData(int rows, String currency) {
		super(rows, 6);
		this.currency = currency;
		indexof_lastRecord = rows - 1;
	}

	public synchronized boolean update(long lastValue, long timestamp){
		long lastTimestamp = getLastTimestamp(); 
		if(timestamp < lastTimestamp){
			return false;
		}
		long minutes = diffInMinutes(timestamp,lastTimestamp);
		for(int i = 0; i < minutes; i++){
			shift();
			copy(indexof_lastRecord, indexof_lastRecord-1);
			noUpdateForIndex(indexof_lastRecord);
		}
		if(get(indexof_lastRecord, OPEN) == UNSET) {
			set(indexof_lastRecord,0,lastValue); 
		}
		if(get(indexof_lastRecord, HIGH) < lastValue || get(indexof_lastRecord, 1) == UNSET){
			set(indexof_lastRecord, 1, lastValue);
		}
		if(get(indexof_lastRecord, LOW) > lastValue || get(indexof_lastRecord, 2) == UNSET){
			set(indexof_lastRecord, 2, lastValue);
		}
		set(indexof_lastRecord, CLOSE, lastValue);
		set(indexof_lastRecord, TS, timestamp);
		return true;
	}

	public synchronized void initRecord(int recordIndex, long open, long high, long low, long close, long volume, long timestamp) {
		set(recordIndex, OPEN, open);
		set(recordIndex, HIGH, high);
		set(recordIndex, LOW, low);
		set(recordIndex, CLOSE, close);
		set(recordIndex, VOL, volume);
		set(recordIndex, TS, timestamp);
	}

	public synchronized long getOpen(int i) {
		return get(i,OPEN);
	}

	public synchronized long getHigh(int i) {
		return get(i,HIGH);
	}

	public synchronized long getLow(int i) {
		return get(i,LOW);
	}

	public synchronized long getClose(int i) {
		return get(i,CLOSE);
	}

	public synchronized long getVolume(int i) {
		return get(i,VOL);
	}
	
	public synchronized long getTime(int i) {
		return get(i,TS);
	}

	public synchronized long getLastClose() {
		return get(indexof_lastRecord,CLOSE);
	}

	public synchronized long getLastTimestamp() {
		return get(indexof_lastRecord,TS);
	}

	private void noUpdateForIndex(int recordIndex){
		long lastClose = getLastClose();
		long lastTimestamp = getLastTimestamp();
		set(recordIndex, OPEN, lastClose);
		set(recordIndex, HIGH, lastClose);
		set(recordIndex, LOW, lastClose);
		set(recordIndex, CLOSE, lastClose);
		set(recordIndex, VOL, 0);
		set(recordIndex, TS, lastTimestamp + ONE_MINUTE);
	}
	
	private long diffInMinutes(long timestamp, long lastTimestamp) {
		return timestamp/1000l/60l - lastTimestamp /1000l/60l;
	}

	public String getCurrency() {
		return currency;
	}

}