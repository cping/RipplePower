package org.ripple.power.command;

import java.util.ArrayList;
import java.util.Date;

import org.ripple.power.txns.CurrencyUtils;
import org.ripple.power.txns.IssuedCurrency;
import org.ripple.power.txns.OfferPrice;
import org.ripple.power.utils.DateUtils;
import org.ripple.power.utils.StringUtils;

public class ROCFunction {
	// 系统函数
	final static ArrayList<String> system_functs = new ArrayList<String>();
	static {
		system_functs.add("roc.xrp_to_val");
		system_functs.add("roc.val_to_xrp");
		system_functs.add("roc.date_cn");
		system_functs.add("roc.date");
		system_functs.add("roc.price");
		system_functs.add("roc.trim");
		system_functs.add("roc.rtrim");
		system_functs.add("roc.isnumber");
		system_functs.add("roc.ischinese");
		system_functs.add("roc.tocurrency");
	}

	public static Object getValue(String name, String value) {
		if ("roc.xrp_to_val".equalsIgnoreCase(name)) {
			if (value.indexOf(",") == -1) {
				return CurrencyUtils.getRippleToValue(value);
			}
		} else if ("roc.val_to_xrp".equalsIgnoreCase(name)) {
			if (value.indexOf(",") == -1) {
				return CurrencyUtils.getValueToRipple(value);
			}
		} else if ("roc.date_cn".equalsIgnoreCase(name)) {
			if (value.indexOf(",") == -1) {
				return DateUtils.toChineseFullDate(new Date());
			}
		} else if ("roc.date".equalsIgnoreCase(name)) {
			if (value.indexOf(",") == -1) {
				return DateUtils.toDate();
			}
		} else if ("roc.price".equalsIgnoreCase(name)) {
			if (value.indexOf(",") != -1) {
				String[] split = StringUtils.split(value, ",");
				if (split.length == 3) {
					return OfferPrice.getMoneyConvert(split[0], split[1], split[2]);
				}
			}
		} else if ("roc.trim".equalsIgnoreCase(name)) {
			if (value.indexOf(",") == -1) {
				return  StringUtils.trim(value);
			}
		}else if ("roc.rtrim".equalsIgnoreCase(name)) {
			if (value.indexOf(",") == -1) {
				return  StringUtils.rtrim(value);
			}
		}else if ("roc.isnumber".equalsIgnoreCase(name)) {
			if (value.indexOf(",") == -1) {
				return  StringUtils.isNumber(value);
			}
		}else if ("roc.ischinese".equalsIgnoreCase(name)) {
			if (value.indexOf(",") == -1) {
				return  StringUtils.isChinaLanguage(value.toCharArray());
			}
		}else if ("roc.tocurrency".equalsIgnoreCase(name)) {
			if (value.indexOf(",") == -1 && value.indexOf("/")!=-1) {
				return new IssuedCurrency(value);
			}
		}
		return "unkown";

	}
}
