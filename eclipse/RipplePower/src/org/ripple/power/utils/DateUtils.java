package org.ripple.power.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

final public class DateUtils {

	private static final TimeZone CN = TimeZone.getTimeZone("GMT+08:00");

	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

	final static private String flag = ":";

	public static final String DEFAULT_DATE_FORMATE = "yyyy-MM-dd HH:mm:ss";
	public static final String STANDARD_DATE_REGEX = "20\\d{2}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{0,3})?";
	public static final String STANDARD_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String FILENAME_DATE_PATTERN = "yyyyMMdd'T'HHmmss";

	public static final SimpleDateFormat standardDateFormat = new SimpleDateFormat(STANDARD_DATE_PATTERN);

	public static final SimpleDateFormat fileNameDateFormat = new SimpleDateFormat(FILENAME_DATE_PATTERN);
	static {
		standardDateFormat.setTimeZone(UTC);
		fileNameDateFormat.setTimeZone(UTC);
	}

	private Calendar calendar;
	private static TimeZone timezone;
	private static Locale locale;

	private SimpleDateFormat dateFormat;

	private static final String DASH = "-";
	private static final String COLON = ":";
	private static final String SPACE = " ";
	private static final String ZERO = "0";

	public DateUtils() {
		if (timezone == null) {
			timezone = TimeZone.getTimeZone(System.getProperty("user.country"));
		}
		if (locale == null) {
			locale = new Locale(System.getProperty("user.language"), System.getProperty("user.timezone"));
		}
		calendar = Calendar.getInstance();
	}

	public DateUtils(String format) {
		this();
		dateFormat = new SimpleDateFormat(format);
	}

	public void reset() {
		calendar = Calendar.getInstance(timezone, locale);
	}

	public long getTimeInMillis() {
		return calendar.getTimeInMillis();
	}

	public String getFormattedDate() {
		return dateFormat.format(calendar.getTime());
	}

	public void resetTimeZone(String timezoneString, String language, String country) {
		timezone = TimeZone.getTimeZone(timezoneString);
		locale = new Locale(language, country);
	}

	public int getYear() {
		return calendar.get(Calendar.YEAR);
	}

	public String getMonth() {
		int m = getMonthInt();
		String[] months = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
				"Dec" };
		if (m > 12) {
			return "Unknown to Man";
		}

		return months[m - 1];
	}

	public String getDay() {
		int x = getDayOfWeek();
		String[] days = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

		if (x > 7) {
			return "Unknown to Man";
		}

		return days[x - 1];
	}

	public int getMonthInt() {
		return 1 + calendar.get(Calendar.MONTH);
	}

	public String getDate() {
		String year = Integer.toString(getYear());
		return getDayOfMonth() + DASH + getMonth() + DASH + year.substring(2);
	}

	public String getDate(char delimeter) {
		String year = Integer.toString(getYear());
		return getDayOfMonth() + delimeter + getMonth() + delimeter + year.substring(2);
	}

	public String getDateInt(String delimeter) {
		String year = Integer.toString(getYear());
		return getDayOfMonth() + delimeter + getMonthInt() + delimeter + year.substring(2);
	}

	public String getTime() {
		return getHour() + COLON + getMinute();
	}

	public String getLongTime() {
		return getHour() + COLON + getMinute() + COLON + getSecond();
	}

	public String getDateTime() {
		return getDate() + SPACE + getTime();
	}

	public String getLongDateTime() {
		return getDayOfMonth() + DASH + getMonth() + DASH + getYear() + SPACE + getLongTime();
	}

	public int getDayOfMonth() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getDayOfWeek() {
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	public int getWeekOfMonth() {
		return calendar.get(Calendar.WEEK_OF_MONTH);
	}

	public int getHour() {
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public String getSecond() {
		int tempSecond = calendar.get(Calendar.SECOND);
		return tempSecond < 10 ? ZERO + tempSecond : Integer.toString(tempSecond);
	}

	public String getMinute() {
		int tempMinute = calendar.get(Calendar.MINUTE);
		return tempMinute < 10 ? ZERO + tempMinute : Integer.toString(tempMinute);
	}

	public int getMinuteForCalc() {
		return calendar.get(Calendar.MINUTE);
	}

	/**
	 * 获得指定ms的时、分、秒字符串信息
	 * 
	 * @return
	 */
	public static String toMillisInfoString(long ms) {
		StringBuffer buffer = new StringBuffer();
		long[] ret = toMillisInfo(ms);
		buffer.append(MathUtils.addZeros(ret[0], 2));
		buffer.append(flag);
		buffer.append(MathUtils.addZeros(ret[1], 2));
		buffer.append(flag);
		buffer.append(MathUtils.addZeros(ret[2], 2));
		return buffer.toString();
	}

	/**
	 * 获得指定ms换算的时、分、秒
	 * 
	 * @return
	 */
	public static long[] toMillisInfo(long ms) {
		long hour, minute, second;
		hour = ms / 1000 / 60 / 60;
		minute = (ms - hour * 60 * 60 * 1000) / 1000 / 60;
		second = ms / 1000 - hour * 60 * 60 - minute * 60;
		return new long[] { hour, minute, second };
	}

	/**
	 * 格式化日期,格式化后可直接insert into [DB]
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToString(Date date) {

		if (date == null)
			return "";
		else {
			SimpleDateFormat sdFormat = new SimpleDateFormat(DEFAULT_DATE_FORMATE, Locale.getDefault());
			String str_Date = sdFormat.format(date);
			return str_Date;
		}
	}

	public static long convert(String timeout) {
		long later = 0L;
		SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMATE);
		try {
			later = format.parse(timeout).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return later;
	}

	/**
	 * 返回时间的毫秒格式
	 * 
	 * @param date
	 * @return
	 */
	public static final String dateToMillis(Date date) {
		return MathUtils.addZeros(Long.toString(date.getTime()), 15);
	}

	/**
	 * 将指定的毫秒转换为YYYYMMDD的格式
	 * 
	 * @param msel
	 * @return
	 */
	public static int dateFormatMSEL(long msel) {
		Date date = new Date(msel);
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		StringBuffer sb = new StringBuffer();
		sb.append(year);
		if (String.valueOf(month).length() == 1)
			sb.append("0" + month);
		else
			sb.append(month);
		if (String.valueOf(day).length() == 1)
			sb.append("0" + day);
		else
			sb.append(day);
		return Integer.valueOf(sb.toString()).intValue();
	}

	/**
	 * 日期计算
	 * 
	 * @param date
	 * @param yearNum
	 * @param monthNum
	 * @param dateNum
	 * @return
	 */
	public static String causeDate(String date, int yearNum, int monthNum, int dateNum) {
		String result = "";
		try {
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.setTime(sd.parse(date));
			cal.add(Calendar.MONTH, monthNum);
			cal.add(Calendar.YEAR, yearNum);
			cal.add(Calendar.DATE, dateNum);
			result = sd.format(cal.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 返回当前时间，格式'yyyy-mm-dd HH:mm:ss'
	 * 
	 * @return
	 */
	public static String toLocalDate() {
		java.util.Date dt = new java.util.Date();
		SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATE_FORMATE);
		df.setTimeZone(TimeZone.getDefault());
		return df.format(dt);
	}

	/**
	 * 返回当前毫秒时间
	 * 
	 * @return
	 */
	public static long toLongTime() {
		return System.currentTimeMillis();
	}

	/**
	 * 返回当前毫秒时间的string形式
	 * 
	 * @return
	 */
	public static String toLongDate() {
		return String.valueOf(toLongTime());
	}

	/**
	 * 一个简单的时间返回
	 * 
	 * @return
	 */
	public static String toSimpleDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		Date dt = new Date();
		return df.format(dt);
	}

	/**
	 * 格式化时间为指定格式并返回
	 * 
	 * @param style
	 * @return
	 */
	public static String toFormatDate(String style) {
		SimpleDateFormat df = new SimpleDateFormat(style);
		Date dt = new Date();
		return df.format(dt);
	}

	/**
	 * 格式化ms为中国时间
	 * 
	 * @return
	 */
	public static String toCNFormatDate(long msel) {
		Date date = new Date(msel);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(CN);
		return formatter.format(date);
	}

	public static Calendar getUTCCalendar() {
		TimeZone.setDefault(UTC);
		return new GregorianCalendar(UTC);
	}

	public static Calendar getCNCalendar() {
		TimeZone.setDefault(CN);
		return new GregorianCalendar(CN);
	}

	/**
	 * 清除0返回
	 * 
	 * @param mord
	 * @return
	 */
	public static String deleteFrontZero(String mord) {
		int im = -1;
		try {
			im = Integer.parseInt(mord);
			return String.valueOf(im);
		} catch (Exception e) {
			return mord;
		}
	}

	/**
	 * 以.分隔时间并返回
	 * 
	 * @param orlTime
	 * @return
	 */
	public static String toPointDate(String orlTime) {
		if (orlTime == null || orlTime.length() <= 0) {
			return "";
		}
		String sMonth = deleteFrontZero(orlTime.substring(5, 7));
		String sDay = deleteFrontZero(orlTime.substring(8, 10));
		return (sMonth + "." + sDay).intern();
	}

	/**
	 * 24小时制转为12小时制 时间转换 23:00 -> 11:00 PM , 11:00 -> 11:00 AM
	 * 
	 * @param strTime
	 * @return
	 */
	public static String toConvertTimeto12(String strTime) {
		String strRet = "";
		try {
			SimpleDateFormat simFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm ");
			Date dDate = DateFormat.getDateInstance().parse(strTime);
			strRet = simFormat.format(dDate);
		} catch (ParseException ex) {
			ex.printStackTrace();
			strRet = "";
		}

		return strRet;
	}

	/**
	 * 时间格式化并返回
	 * 
	 * @param tmpDate
	 * @return
	 */
	public static String toDate() {
		return toDate(new Date());
	}

	public static String toDate(Date dt) {
		SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATE_FORMATE);
		df.setTimeZone(TimeZone.getDefault());
		return df.format(dt);
	}

	/**
	 * 返回中文时间
	 * 
	 * @param orlTime
	 * @return
	 */
	public static String toChineseDate(String orlTime) {
		if (orlTime == null || orlTime.length() <= 0) {
			return "";
		}

		if (orlTime.length() < 10) {
			return "";
		}
		String sYear = orlTime.substring(0, 4);
		String sMonth = deleteFrontZero(orlTime.substring(5, 7));
		String sDay = deleteFrontZero(orlTime.substring(8, 10));
		return (sYear + "年" + sMonth + "月" + sDay + "日").intern();
	}

	/**
	 * 返回中文标记的时间 转换时间格式,把date转换成年-月-日
	 * 
	 * @param
	 * @return
	 * 
	 */

	public static String toChineseDate(Date date) {
		String strDate = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
		strDate = dateFormat.format(date);
		return strDate;
	}

	/**
	 * 转换时间格式,把date转换成年-月-日-时-分-秒
	 * 
	 * @param
	 * @return
	 * 
	 */
	public static String toChineseFullDate(Date date) {
		String strDate = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 kk时mm分ss秒");
		strDate = dateFormat.format(date);
		return strDate;
	}

	/**
	 * 转换时间格式,把date转换成年
	 * 
	 * @param date
	 * @return
	 */
	public static String toNowYear(Date date) {
		String strDate = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
		strDate = dateFormat.format(date);
		return strDate;
	}

	/**
	 * 日期累加
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param seed
	 * @param pedometer
	 * @return
	 * @throws Exception
	 */
	public static String toDateProgression(int year, int month, int day, String seed, int pedometer) throws Exception {
		GregorianCalendar dateAntetype = new GregorianCalendar(year, month - 1, day);
		if ("year".equals(seed))
			dateAntetype.add(GregorianCalendar.YEAR, pedometer);
		else if ("month".equals(seed))
			dateAntetype.add(GregorianCalendar.MONTH, pedometer);
		else if ("day".equals(seed))
			dateAntetype.add(GregorianCalendar.DATE, pedometer);
		Date d = dateAntetype.getTime();
		DateFormat df = DateFormat.getDateInstance();
		return df.format(d);
	}

	/**
	 * 取得两个日期的天数之差(两个时间类型 第二个减第一个)
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static long toDayInterval(Date startDate, Date endDate) {
		return (endDate.getTime() - startDate.getTime()) / 86400000;
	}

	/**
	 * 取得两个日期的天数之差(两个时间类型 第二个减第一个)
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static long toDayInterval(GregorianCalendar startDate, GregorianCalendar endDate) {
		return (endDate.getTimeInMillis() - startDate.getTimeInMillis()) / 86400000;

	}

	/**
	 * 计算给定的两个日期之间的工作日的天数(不计算六日)
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static long toCountWorkDays(GregorianCalendar start, GregorianCalendar end) {
		long result = 0;
		GregorianCalendar startDate = new GregorianCalendar();
		GregorianCalendar endDate = new GregorianCalendar();

		startDate.setTime(start.getTime());
		endDate.setTime(end.getTime());
		if ((startDate.get(GregorianCalendar.DAY_OF_WEEK) % 7) <= 1) {
			startDate.add(GregorianCalendar.DATE, 2 - (startDate.get(GregorianCalendar.DAY_OF_WEEK) % 7));
		}
		if ((endDate.get(GregorianCalendar.DAY_OF_WEEK) % 7) <= 1) {
			endDate.add(GregorianCalendar.DATE, -1 - (endDate.get(GregorianCalendar.DAY_OF_WEEK) % 7));
		}
		long totaldays = toDayInterval(startDate, endDate);
		int s = endDate.get(GregorianCalendar.DAY_OF_WEEK) - startDate.get(GregorianCalendar.DAY_OF_WEEK);
		if (s < 0) {
			s += 5;
		}
		if (totaldays % 7 != 0)
			result = s + (totaldays / 7) * 5 + 1;
		else
			result = (totaldays / 7) * 5;

		if (result < 0) {
			result = 0;
		}
		return result;
	}

	/**
	 * 转换时间格式：把24小时制转成12小时制
	 * 
	 * @param strConvertTime
	 * @return
	 */
	public static String toConvertTime(String strConvertTime) {

		int n = strConvertTime.indexOf(":");
		int m = strConvertTime.lastIndexOf(":");
		int sf = 0;
		String strMin = "";
		if (m == n) {
			n = m;
			sf = strConvertTime.length() - m - 1;
			strMin = strConvertTime.substring(m + 1, strConvertTime.length());
		} else if (m != n) {
			sf = m - n - 1;
			strMin = strConvertTime.substring(n + 1, m);
		}
		int sh = n;
		String strHours = strConvertTime.substring(0, n);
		if (Integer.parseInt(strHours) <= 12) {
			int xiaoshi = Integer.parseInt(strHours);
			int fenzhong = Integer.parseInt(strMin);
			if (xiaoshi >= 10 && fenzhong >= 10) {
				strConvertTime = strHours + ":" + strMin + " AM";
			} else if (xiaoshi >= 10 && fenzhong < 10 && fenzhong != 0) {
				strConvertTime = strHours + ":0" + strMin + " AM";
			} else if (fenzhong == 0) {
				if (sf == 2) {
					strConvertTime = strHours + ":" + strMin + " AM";
				} else if (sf == 1) {
					strConvertTime = strHours + ":0" + strMin + " AM";
				}
			} else if (xiaoshi < 10 && fenzhong >= 10) {
				if (sh == 2 && sf == 2) {
					strConvertTime = strHours + ":" + strMin + " AM";
				} else if (sh == 2 && sf == 1) {
					strConvertTime = strHours + ":0" + strMin + " AM";
				} else if (sh == 1 && sf == 2) {
					strConvertTime = "0" + strHours + ":" + strMin + " AM";
				} else if (sh == 1 && sf == 1) {
					strConvertTime = "0" + strHours + ":0" + strMin + " AM";
				}
			} else if (xiaoshi < 10 && fenzhong < 10) {
				if (sh == 2 && sf == 2) {
					strConvertTime = strHours + ":" + strMin + " AM";
				} else if (sh == 2 && sf == 1) {
					strConvertTime = strHours + ":0" + strMin + " AM";
				} else if (sh == 1 && sf == 2) {
					strConvertTime = "0" + strHours + ":" + strMin + " AM";
				} else if (sh == 1 && sf == 1) {
					strConvertTime = "0" + strHours + ":0" + strMin + " AM";
				}
			}
		} else if (Integer.parseInt(strHours) > 12 && Integer.parseInt(strHours) < 24) {
			int xiaoshi = Integer.parseInt(strHours) - 12;
			int fenzhong = Integer.parseInt(strMin);
			if (fenzhong >= 10 && xiaoshi >= 10) {
				strConvertTime = xiaoshi + ":" + fenzhong + " PM";
			} else if (fenzhong < 10 && xiaoshi >= 10) {
				strConvertTime = xiaoshi + ":0" + fenzhong + " PM";
			} else if (fenzhong >= 10 && xiaoshi < 10) {
				strConvertTime = "0" + xiaoshi + ":" + fenzhong + " PM";
			} else if (fenzhong < 10 && xiaoshi < 10) {
				strConvertTime = "0" + xiaoshi + ":0" + fenzhong + " PM";
			}
		} else if (Integer.parseInt(strHours) == 24) {
			strHours = "00";
			if (sf == 2) {
				strConvertTime = strHours + ":" + strMin + " AM";
			} else if (sf == 1) {
				strConvertTime = strHours + ":0" + strMin + " AM";
			}
		}
		return strConvertTime;
	}

	/**
	 * 获得两个日期间天数差
	 * 
	 * @param day
	 * @param nday
	 * @return
	 */
	public static String toDayMinus(String day, String nday) {
		int n = Integer.parseInt(nday);
		int nowYear = Integer.parseInt(day.substring(0, 4));
		int nowMonth = Integer.parseInt(day.substring(4, 6));
		int nowDay = Integer.parseInt(day.substring(6, 8));
		int dayNum = 0;
		switch (nowMonth) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			dayNum = 31;
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			dayNum = 30;
			break;
		case 2:
			if ((nowYear % 4 == 0 || nowYear % 100 != 0) && nowYear % 400 != 0) {
				dayNum = 29;
			} else {
				dayNum = 28;
			}
			break;
		}
		for (; n - (dayNum - nowDay) > 0; nowDay = 1) {
			switch (nowMonth) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				dayNum = 31;
				break;

			case 4:
			case 6:
			case 9:
			case 11:
				dayNum = 30;
				break;
			case 2:
				if (nowYear % 4 == 0 && nowYear % 100 != 0 || nowYear % 400 != 0) {
					dayNum = 29;
				} else {
					dayNum = 28;
				}
				break;
			}
			if (nowMonth < 12) {
				nowMonth++;
			} else {
				nowYear++;
				nowMonth = 1;
			}
			n = n - (dayNum - nowDay) - 1;
		}

		nowDay += n;
		String temp = Integer.toString(nowYear);
		if (nowMonth < 10) {
			temp = temp + "0";
		}
		temp = temp + Integer.toString(nowMonth);
		if (nowDay < 10) {
			temp = temp + "0";
		}
		temp = temp + Integer.toString(nowDay);
		return temp;
	}

	/**
	 * 格式化指定数字为指定长度的String
	 * 
	 * @param number
	 * @param num
	 * @return
	 */
	public static String toDataFormat(int number, int num) {
		int len = 0;
		String temp = Integer.toString(number);
		len = temp.length();
		if (len < num) {
			for (int i = 0; i < num - len; i++) {
				temp = "0" + temp;
			}
		}
		return temp;
	}

	/**
	 * 获得指定年，指定月的日数
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int toDayNum(int year, int month) {
		int nowYear = year;
		int nowMonth = month;
		int dayNum = 0;
		switch (nowMonth) {
		default:
			break;

		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			dayNum = 31;
			break;

		case 4:
		case 6:
		case 9:
		case 11:
			dayNum = 30;
			break;

		case 2:
			if (nowYear % 4 == 0 && nowYear % 100 != 0 || nowYear % 400 != 0) {
				dayNum = 29;
			} else {
				dayNum = 28;
			}
			break;
		}
		return dayNum;
	}

	/**
	 * 将long形式的日期转为String形式输出
	 * 
	 * @param mill
	 * @return
	 */
	public static String toDateString(long mill) {
		if (mill < 0)
			return "";
		Date date = new Date(mill);
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(date);
		int year = rightNow.get(Calendar.YEAR);
		int month = rightNow.get(Calendar.MONTH);
		int day = rightNow.get(Calendar.DAY_OF_MONTH);
		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		int min = rightNow.get(Calendar.MINUTE);
		return year + "-" + (month < 10 ? "0" + month : "" + month) + "-" + (day < 10 ? "0" + day : "" + day) + " "
				+ (hour < 10 ? "0" + hour : "" + hour) + ":" + (min < 10 ? "0" + min : "" + min);
	}

	/**
	 * 返回今天的中文日期
	 * 
	 * @param Cal
	 * @return
	 */
	public static String toDayOfWeekName(Calendar cal) {
		int dayofweek = 0;
		String tmp = cal.toString();
		tmp = tmp.substring(tmp.indexOf("DAY_OF_WEEK=") + 12, tmp.indexOf("DAY_OF_WEEK=") + 13);
		dayofweek = Integer.parseInt(tmp);
		String dayName = "";
		switch (dayofweek) {
		case 2:
			dayName = "星期一";
			break;
		case 3:
			dayName = "星期二";
			break;
		case 4:
			dayName = "星期三";
			break;
		case 5:
			dayName = "星期四";
			break;
		case 6:
			dayName = "星期五";
			break;
		case 7:
			dayName = "星期六";
			break;
		case 1:
			dayName = "星期天";
			break;
		}
		return dayName;
	}

	/**
	 * 
	 * 把字符型变量[YYYY-MM-DD]转化为日历型
	 * 
	 * @param
	 * @return
	 */
	public static Calendar toCalendar(String tmpDate) {
		if (tmpDate.length() == 0 && tmpDate.equalsIgnoreCase(""))
			return null;
		Calendar result = null;
		String tmpYear = "";
		String tmpMonth = "";
		String tmpDay = "";
		try {
			if (tmpDate != null) {
				if (!tmpDate.equals("")) {
					tmpYear = tmpDate.substring(0, 4);
					tmpDate = tmpDate.substring(5);
					tmpMonth = tmpDate.substring(0, tmpDate.indexOf("-"));
					tmpDay = tmpDate.substring(tmpDate.indexOf("-") + 1);
				} else {
					return null;
				}
			}
		} catch (Exception e) {
			return null;
		}
		result = toCalendar(tmpYear, tmpMonth, tmpDay);
		return result;
	}

	/**
	 * 把字符型变量[YYYY-MM-DD]转化为日历型
	 * 
	 * @param
	 * @return
	 */
	public static Calendar toCalendar(String tmpDate, String strSplit) {

		Calendar result = null;
		String tmpYear = "";
		String tmpMonth = "";
		String tmpDay = "";
		String[] tmpResult = tmpDate.split(strSplit);
		switch (tmpResult.length) {
		case 3:
			tmpDay = tmpResult[2];
		case 2:
			tmpMonth = tmpResult[1];
		case 1:
			tmpYear = tmpResult[0];
			break;
		}
		result = toCalendar(tmpYear, tmpMonth, tmpDay);
		return result;
	}

	/**
	 * 将年、月、日生成Calendar对象
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return Calendar
	 */
	public static Calendar toCalendar(String year, String month, String day) {
		try {
			if (!year.equals("") && !month.equals("") && !day.equals("")) {
				Calendar tmpData = Calendar.getInstance();
				tmpData.set(Integer.valueOf(year).intValue(), Integer.valueOf(month).intValue() - 1,
						Integer.valueOf(day).intValue());
				return tmpData;
			} else if (!year.equals("") && !month.equals("") && day.equals("")) {
				Calendar tmpData1 = Calendar.getInstance();
				tmpData1.set(Integer.valueOf(year).intValue(), Integer.valueOf(month).intValue() - 1, 1);
				return tmpData1;
			} else if (!year.equals("") && month.equals("") && day.equals("")) {
				Calendar tmpData2 = Calendar.getInstance();
				tmpData2.set(Integer.valueOf(year).intValue(), 0, 1);
				return tmpData2;
			} else
				return null;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 根据选择的时间区段，返回起始和终止的毫秒数
	 * 
	 * @param timeperiodType
	 *            时钟值
	 * @param originalMSEL
	 *            开始毫秒
	 * @return
	 */
	public static long[] toTimeperiodMSEL(int timeperiodType, long originalMSEL) {
		long[] timeperiodMSEL = new long[2];
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		timeperiodMSEL[0] = calendar.getTime().getTime();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		timeperiodMSEL[1] = calendar.getTime().getTime();
		if (timeperiodType == TimeState.YESTERDAY) { // 日期时段为“昨天”
			timeperiodMSEL[0] = timeperiodMSEL[0] - (24 * 60 * 60 * 1000);
			timeperiodMSEL[1] = timeperiodMSEL[1] - (24 * 60 * 60 * 1000);
		} else if (timeperiodType == TimeState.LAST7DAYS) { // 日期时段为“前 7 天”
			timeperiodMSEL[0] = timeperiodMSEL[0] - (7 * 24 * 60 * 60 * 1000);
			timeperiodMSEL[1] = timeperiodMSEL[1] - (24 * 60 * 60 * 1000);
		} else if (timeperiodType == TimeState.THISMONTH) { // 日期时段为“本月”
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.getActualMinimum(Calendar.DAY_OF_MONTH), 0, 0, 0);
			timeperiodMSEL[0] = calendar.getTime().getTime();
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
			timeperiodMSEL[1] = calendar.getTime().getTime();
		} else if (timeperiodType == TimeState.LASTMONTH) { // 日期时段为“上月”
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) - 1, 1, 0, 0, 0);
			timeperiodMSEL[0] = calendar.getTime().getTime();
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
			timeperiodMSEL[1] = calendar.getTime().getTime();
		} else if (timeperiodType == TimeState.ALLTIME) { // 所有时间
			timeperiodMSEL[0] = originalMSEL;
			timeperiodMSEL[1] = System.currentTimeMillis();

		}
		return timeperiodMSEL;
	}

	/**
	 * 得到本周的第一天
	 * 
	 * @return
	 */
	public static int toCurrentFirstWeekDay() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		int day = cal.getMinimum(GregorianCalendar.DAY_OF_WEEK);
		return day;
	}

	/**
	 * 得到本周的最后一天
	 * 
	 * @return
	 */
	public static int toCurrentLastWeekDay() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		int day = cal.getMaximum(GregorianCalendar.DAY_OF_WEEK);
		return day;
	}

	/**
	 * 得到当月的第一天
	 * 
	 * @return
	 */
	public static int toCurrentFirstMonthDay() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		int day = cal.getMinimum(GregorianCalendar.DAY_OF_MONTH);
		return day;
	}

	/**
	 * 得到当月的最后一天
	 * 
	 * @return
	 */
	public static int toCurrentLastMonthDay() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		int day = cal.getMaximum(GregorianCalendar.DAY_OF_MONTH);
		return day;
	}

	/**
	 * 返回明天
	 * 
	 * @param strDate
	 * @return
	 * @throws Exception
	 */
	public static String toCNTomorrow(String strDate) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = formatter.parse(strDate);
		long temp = date.getTime() + 24 * 3600 * 1000;
		return toCNFormatDate(temp);
	}

	/**
	 * 返回推移目标天后时间
	 * 
	 * @param day
	 *            推移的天数
	 * @return 以当前时间加天数的日期
	 */
	public static String toCurrentDatedefer(int day) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(GregorianCalendar.DAY_OF_MONTH, day);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(calendar.getTime());
	}

	// 时间状态(今天,昨天,上周，本月,上月，全部时间)
	interface TimeState {

		public static final int TODAY = 0;

		public static final int YESTERDAY = 1;

		public static final int LAST7DAYS = 2;

		public static final int THISMONTH = 3;

		public static final int LASTMONTH = 4;

		public static final int ALLTIME = 5;

	}

	public static String getTimeDiff(long tStart, long tEnd) {
		long sec = 1000;
		long min = sec * 60;
		long hour = min * 60;
		long day = hour * 24;
		long month = day * 30;
		long year = 365 * day;
		long diffInMills = tEnd - tStart;
		if (diffInMills < sec) {
			return String.valueOf(diffInMills) + " [ms]";
		} else if (diffInMills < min) {
			return String.valueOf(diffInMills / sec) + " [s]";
		} else if (diffInMills < hour) {
			long lmin = diffInMills / min;
			long lsec = (diffInMills - lmin * min) / sec;
			return String.valueOf(lmin) + " [min] " + String.valueOf(lsec) + " [s]";
		} else if (diffInMills < day) {
			long lhour = diffInMills / hour;
			long lmin = (diffInMills - lhour * hour) / min;
			long lsec = (diffInMills - lhour * hour - lmin * min) / sec;
			return String.valueOf(lhour) + " [h] " + String.valueOf(lmin) + " [min] " + String.valueOf(lsec) + " [s]";
		} else if (diffInMills < month) {
			long lday = diffInMills / day;
			long lhour = (diffInMills - lday * day) / hour;
			long lmin = (diffInMills - lday * day - lhour * hour) / min;
			long lsec = (diffInMills - lday * day - lhour * hour - lmin * min) / sec;
			return String.valueOf(lday) + " [d] " + String.valueOf(lhour) + " [h] " + String.valueOf(lmin) + " [min] "
					+ String.valueOf(lsec) + " [s]";
		} else if (diffInMills < year) {
			long mn = diffInMills / month;
			long lday = (diffInMills - mn * month) / day;
			long lhour = (diffInMills - mn * month - lday * day) / hour;
			long lmin = (diffInMills - mn * month - lday * day - lhour * hour) / min;
			long lsec = (diffInMills - mn * month - lday * day - lhour * hour - lmin * min) / sec;
			return String.valueOf(mn) + " [m] " + String.valueOf(lday) + " [d] " + String.valueOf(lhour) + " [h] "
					+ String.valueOf(lmin) + " [min] " + String.valueOf(lsec) + " [s]";
		} else {
			long lyear = diffInMills / year;
			long mn = (diffInMills - lyear * year) / month;
			long lday = (diffInMills - lyear * year - mn * month) / day;
			long lhour = (diffInMills - lyear * year - mn * month - lday * day) / hour;
			long lmin = (diffInMills - lyear * year - mn * month - lday * day - lhour * hour) / min;
			long lsec = (diffInMills - lyear * year - mn * month - lday * day - lhour * hour - lmin * min) / sec;
			return String.valueOf(lyear) + " [y] " + String.valueOf(mn) + " [m] " + String.valueOf(lday) + " [d] "
					+ String.valueOf(lhour) + " [h] " + String.valueOf(lmin) + " [min] " + String.valueOf(lsec)
					+ " [s]";
		}
	}

	public static Date stdString2date(String dateString) throws ParseException {
		return standardDateFormat.parse(dateString);
	}

	public static String date2StdString(Date aDate) {
		return standardDateFormat.format(aDate);
	}

	public static String date2FmtString(Date aDate, String dateFormatString) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatString);
		return simpleDateFormat.format(aDate);
	}

	public static Date fileNameString2date(String dateString) throws ParseException {
		return fileNameDateFormat.parse(dateString);
	}

	public static String date2FileNameString(Date aDate) {
		return fileNameDateFormat.format(aDate);
	}

	public static Long timeDifferenceInSeconds(Date startDate, Date endDate) {
		if (startDate == null || endDate == null) {
			return null;
		}
		return (endDate.getTime() - startDate.getTime()) / (1000L);
	}

	public static Date addTime(Date aDate, int timeToAdd, int timeUnits) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(aDate);
		cal.add(timeUnits, timeToAdd);
		return cal.getTime();
	}

	public static Date addDays(Date aDate, int days) {
		return addTime(aDate, (24 * days), Calendar.HOUR);
	}

	public static Date addHours(Date aDate, int hours) {
		return addTime(aDate, hours, Calendar.HOUR);
	}

	public static Date addSeconds(Date aDate, int seconds) {
		return addTime(aDate, seconds, Calendar.SECOND);
	}

	public static long hoursBetween(Calendar startDate, Calendar endDate) {
		Calendar date = (Calendar) startDate.clone();
		long hoursBetween = -1;
		while (date.before(endDate) || date.equals(endDate)) {
			date.add(Calendar.HOUR, 1);
			hoursBetween++;
		}
		return hoursBetween;
	}

	public static boolean dateCompare(Date d1, Date d2) {
		Calendar c1 = GregorianCalendar.getInstance();
		Calendar c2 = GregorianCalendar.getInstance();
		c1.setTime(d1);
		c2.setTime(d2);
		boolean sameDate = c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
		sameDate = sameDate && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
		return sameDate;
	}

	public static Date endOfDay(Date aDate) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(aDate);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 24);
		Date midnight = cal.getTime();
		return midnight;
	}

	public static Long secondsToEndOfDay(Date aDate) {
		Date midnight = endOfDay(aDate);
		return timeDifferenceInSeconds(aDate, midnight);
	}

	public static Date startOfDay(Date aDate) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(aDate);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		Date dayBreak = cal.getTime();
		return dayBreak;
	}

	public static Long secondsFromStartOfDay(Date aDate) {
		Date dayBreak = startOfDay(aDate);
		return timeDifferenceInSeconds(dayBreak, aDate);
	}

	public static int hourOfDay(Date aDate) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(aDate);
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 转换格式为kk:mm:ss
	 * 
	 * @param mill
	 * @return
	 */
	public static String toMillisToKKMMSSTime(long mill) {
		String timeString = "";
		mill = mill / 1000;
		int hours = (int) (mill / 60 / 60);
		if (hours < 10) {
			timeString += "0" + hours + ":";
		} else {
			timeString += hours + ":";
		}
		int minutes = (int) ((mill - hours * 60 * 60) / 60);
		if (minutes < 10) {
			timeString += "0" + minutes + ":";
		} else {
			timeString += minutes + ":";
		}
		int seconds = (int) (mill % 60);
		if (seconds < 10) {
			timeString += "0" + seconds;
		} else {
			timeString += seconds;
		}
		return timeString;
	}

	/**
	 * convert to hh:mm:ss
	 * 
	 * @param timeMillis
	 * @return
	 */
	public static String toMillisToHHMMSSTime(long timeMillis) {
		String timeString = "";
		timeMillis = timeMillis / 1000;
		int days = (int) (timeMillis / 60 / 60 / 24);
		if (days > 0) {
			if (days < 10) {
				timeString += "0" + days + "-";
			} else {
				timeString += days + "-";
			}
		}
		int dayTime = days * 24 * 60 * 60;
		int hours = (int) ((timeMillis - dayTime) / 60 / 60);
		if (hours < 10) {
			timeString += "0" + hours + ":";
		} else {
			timeString += hours + ":";
		}

		int minutes = (int) ((timeMillis - dayTime - hours * 60 * 60) / 60);
		if (minutes < 10) {
			timeString += "0" + minutes + ":";
		} else {
			timeString += minutes + ":";
		}

		int seconds = (int) (timeMillis % 60);
		if (seconds < 10) {
			timeString += "0" + seconds;
		} else {
			timeString += seconds;
		}

		return timeString;
	}

	/**
	 * 转换格式为Day hh:mm:ss
	 * 
	 * @param mill
	 * @return
	 */
	public static String toMillisToDayTime(long mill) {
		String timeString = "";
		mill = mill / 1000;
		int days = (int) (mill / 60 / 60 / 24);
		if (days > 0) {
			timeString += days + " Day ";
		} else {
			timeString += 0 + " Day ";
		}
		int dayTime = days * 24 * 60 * 60;
		int hours = (int) ((mill - dayTime) / 60 / 60);
		if (hours < 10) {
			timeString += "0" + hours + ":";
		} else {
			timeString += hours + ":";
		}
		int minutes = (int) ((mill - dayTime - hours * 60 * 60) / 60);
		if (minutes < 10) {
			timeString += "0" + minutes + ":";
		} else {
			timeString += minutes + ":";
		}
		int seconds = (int) (mill % 60);
		if (seconds < 10) {
			timeString += "0" + seconds;
		} else {
			timeString += seconds;
		}
		return timeString;
	}

	public static String getSqlDateString(Calendar c) {
		try {
			String year = Integer.valueOf(c.get(Calendar.YEAR)).toString();
			String month = Integer.valueOf(c.get(Calendar.MONTH) + 1).toString();
			String day = Integer.valueOf(c.get(Calendar.DATE)).toString();
			if (month.length() == 1)
				month = "0" + month;
			if (day.length() == 1)
				day = "0" + day;
			String date = year + "-" + month + "-" + day;
			return date;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getSqlDateTimeString(Calendar c) {
		try {
			String year = Integer.valueOf(c.get(Calendar.YEAR)).toString();
			String month = Integer.valueOf(c.get(Calendar.MONTH) + 1).toString();
			String day = Integer.valueOf(c.get(Calendar.DATE)).toString();
			String hour = Integer.valueOf(c.get(Calendar.HOUR_OF_DAY)).toString();
			String minute = Integer.valueOf(c.get(Calendar.MINUTE)).toString();
			String second = Integer.valueOf(c.get(Calendar.SECOND)).toString();
			if (month.length() == 1)
				month = "0" + month;
			if (day.length() == 1)
				day = "0" + day;
			if (hour.length() == 1)
				hour = "0" + hour;
			if (minute.length() == 1)
				minute = "0" + minute;
			if (second.length() == 1)
				second = "0" + second;
			String date = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
			return date;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getGUIDateString(Calendar c) {
		try {
			String year = Integer.valueOf(c.get(Calendar.YEAR)).toString();
			String month = Integer.valueOf(c.get(Calendar.MONTH) + 1).toString();
			String day = Integer.valueOf(c.get(Calendar.DATE)).toString();
			if (month.length() == 1)
				month = "0" + month;
			if (day.length() == 1)
				day = "0" + day;
			String date = month + "-" + day + "-" + year;
			return date;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
