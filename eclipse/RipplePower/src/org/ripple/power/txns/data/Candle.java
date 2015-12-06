package org.ripple.power.txns.data;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;
import org.ripple.power.utils.DateUtils;

import com.ripple.core.coretypes.RippleDate;

public class Candle {
	public String startTime;
	public String openTime;
	public String closeTime;
	public double baseVolume;
	public double counterVolume;
	public int count;
	public double open;
	public double high;
	public double low;
	public double close;
	public double vwap;

	public Date getStartTime() {
		if (startTime != null) {
			try {
				return DateUtils.stdString2date(startTime);
			} catch (ParseException e) {
			}
		}
		return null;
	}

	public boolean isPartial() {
		if (startTime == null) {
			return false;
		}
		Calendar time = DateUtils.getUTCCalendar();
		time.setTime(RippleDate.now());
		time.set(Calendar.MINUTE, -4);
		return getStartTime().getTime() > time.getTimeInMillis();
	}

	public void from(JSONObject obj) {
		if (obj != null) {
			this.startTime = obj.optString("startTime");
			this.openTime = obj.optString("openTime");
			this.closeTime = obj.optString("closeTime");
			this.baseVolume = obj.optDouble("baseVolume");
			this.counterVolume = obj.optDouble("counterVolume");
			this.count = obj.optInt("count");
			this.open = obj.optDouble("open");
			this.high = obj.optDouble("high");
			this.low = obj.optDouble("low");
			this.close = obj.optDouble("close");
			this.vwap = obj.optDouble("vwap");
		}
	}
}
