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

	/**
	 * 中国时区设置
	 */
	private static final TimeZone timeZone = TimeZone.getTimeZone("GMT+08:00");

	final static private String flag = ":";

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
			SimpleDateFormat sdFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			String str_Date = sdFormat.format(date);
			return str_Date;
		}
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
		return new Integer(sb.toString()).intValue();
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
	public static String causeDate(String date, int yearNum, int monthNum,
			int dateNum) {
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
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
	public static String toFormatDate(long msel) {
		Date date = new Date(msel);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(timeZone);
		return formatter.format(date);
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
			SimpleDateFormat simFormat = new SimpleDateFormat(
					"yyyy-MM-dd kk:mm ");
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
		Date dt = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy年MM月dd日 kk时mm分ss秒");
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
	public static String toDateProgression(int year, int month, int day,
			String seed, int pedometer) throws Exception {
		GregorianCalendar dateAntetype = new GregorianCalendar(year, month - 1,
				day);
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
	public static long toDayInterval(GregorianCalendar startDate,
			GregorianCalendar endDate) {
		return (endDate.getTimeInMillis() - startDate.getTimeInMillis()) / 86400000;

	}

	/**
	 * 计算给定的两个日期之间的工作日的天数(不计算六日)
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static long toCountWorkDays(GregorianCalendar start,
			GregorianCalendar end) {
		long result = 0;
		GregorianCalendar startDate = new GregorianCalendar();
		GregorianCalendar endDate = new GregorianCalendar();

		startDate.setTime(start.getTime());
		endDate.setTime(end.getTime());
		if ((startDate.get(GregorianCalendar.DAY_OF_WEEK) % 7) <= 1) {
			startDate.add(GregorianCalendar.DATE,
					2 - (startDate.get(GregorianCalendar.DAY_OF_WEEK) % 7));
		}
		if ((endDate.get(GregorianCalendar.DAY_OF_WEEK) % 7) <= 1) {
			endDate.add(GregorianCalendar.DATE,
					-1 - (endDate.get(GregorianCalendar.DAY_OF_WEEK) % 7));
		}
		long totaldays = toDayInterval(startDate, endDate);
		int s = endDate.get(GregorianCalendar.DAY_OF_WEEK)
				- startDate.get(GregorianCalendar.DAY_OF_WEEK);
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
		} else if (Integer.parseInt(strHours) > 12
				&& Integer.parseInt(strHours) < 24) {
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
				if (nowYear % 4 == 0 && nowYear % 100 != 0
						|| nowYear % 400 != 0) {
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
		return year + "-" + (month < 10 ? "0" + month : "" + month) + "-"
				+ (day < 10 ? "0" + day : "" + day) + " "
				+ (hour < 10 ? "0" + hour : "" + hour) + ":"
				+ (min < 10 ? "0" + min : "" + min);
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
		tmp = tmp.substring(tmp.indexOf("DAY_OF_WEEK=") + 12,
				tmp.indexOf("DAY_OF_WEEK=") + 13);
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
				tmpData.set(Integer.valueOf(year).intValue(),
						Integer.valueOf(month).intValue() - 1,
						Integer.valueOf(day).intValue());
				return tmpData;
			} else if (!year.equals("") && !month.equals("") && day.equals("")) {
				Calendar tmpData1 = Calendar.getInstance();
				tmpData1.set(Integer.valueOf(year).intValue(),
						Integer.valueOf(month).intValue() - 1, 1);
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
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		timeperiodMSEL[0] = calendar.getTime().getTime();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
		timeperiodMSEL[1] = calendar.getTime().getTime();
		if (timeperiodType == TimeState.YESTERDAY) { // 日期时段为“昨天”
			timeperiodMSEL[0] = timeperiodMSEL[0] - (24 * 60 * 60 * 1000);
			timeperiodMSEL[1] = timeperiodMSEL[1] - (24 * 60 * 60 * 1000);
		} else if (timeperiodType == TimeState.LAST7DAYS) { // 日期时段为“前 7 天”
			timeperiodMSEL[0] = timeperiodMSEL[0] - (7 * 24 * 60 * 60 * 1000);
			timeperiodMSEL[1] = timeperiodMSEL[1] - (24 * 60 * 60 * 1000);
		} else if (timeperiodType == TimeState.THISMONTH) { // 日期时段为“本月”
			calendar.set(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.getActualMinimum(Calendar.DAY_OF_MONTH), 0, 0, 0);
			timeperiodMSEL[0] = calendar.getTime().getTime();
			calendar.set(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59,
					59);
			timeperiodMSEL[1] = calendar.getTime().getTime();
		} else if (timeperiodType == TimeState.LASTMONTH) { // 日期时段为“上月”
			calendar.set(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH) - 1, 1, 0, 0, 0);
			timeperiodMSEL[0] = calendar.getTime().getTime();
			calendar.set(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59,
					59);
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
	public static String toTomorrow(String strDate) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = formatter.parse(strDate);
		long temp = date.getTime() + 24 * 3600 * 1000;
		return toFormatDate(temp);
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
			return String.valueOf(lmin) + " [min] " + String.valueOf(lsec)
					+ " [s]";
		} else if (diffInMills < day) {
			long lhour = diffInMills / hour;
			long lmin = (diffInMills - lhour * hour) / min;
			long lsec = (diffInMills - lhour * hour - lmin * min) / sec;
			return String.valueOf(lhour) + " [h] " + String.valueOf(lmin)
					+ " [min] " + String.valueOf(lsec) + " [s]";
		} else if (diffInMills < month) {
			long lday = diffInMills / day;
			long lhour = (diffInMills - lday * day) / hour;
			long lmin = (diffInMills - lday * day - lhour * hour) / min;
			long lsec = (diffInMills - lday * day - lhour * hour - lmin * min)
					/ sec;
			return String.valueOf(lday) + " [d] " + String.valueOf(lhour)
					+ " [h] " + String.valueOf(lmin) + " [min] "
					+ String.valueOf(lsec) + " [s]";
		} else if (diffInMills < year) {
			long mn = diffInMills / month;
			long lday = (diffInMills - mn * month) / day;
			long lhour = (diffInMills - mn * month - lday * day) / hour;
			long lmin = (diffInMills - mn * month - lday * day - lhour * hour)
					/ min;
			long lsec = (diffInMills - mn * month - lday * day - lhour * hour - lmin
					* min)
					/ sec;
			return String.valueOf(mn) + " [m] " + String.valueOf(lday)
					+ " [d] " + String.valueOf(lhour) + " [h] "
					+ String.valueOf(lmin) + " [min] " + String.valueOf(lsec)
					+ " [s]";
		} else { 
			long lyear = diffInMills / year;
			long mn = (diffInMills - lyear * year) / month;
			long lday = (diffInMills - lyear * year - mn * month) / day;
			long lhour = (diffInMills - lyear * year - mn * month - lday * day)
					/ hour;
			long lmin = (diffInMills - lyear * year - mn * month - lday * day - lhour
					* hour)
					/ min;
			long lsec = (diffInMills - lyear * year - mn * month - lday * day
					- lhour * hour - lmin * min)
					/ sec;
			return String.valueOf(lyear) + " [y] " + String.valueOf(mn)
					+ " [m] " + String.valueOf(lday) + " [d] "
					+ String.valueOf(lhour) + " [h] " + String.valueOf(lmin)
					+ " [min] " + String.valueOf(lsec) + " [s]";
		}
	}
}
