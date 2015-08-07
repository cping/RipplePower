package org.ripple.power.hft.ripple;

import org.ripple.power.config.LSystem;
import org.ripple.power.hft.BotLog;
import org.ripple.power.ui.view.log.LogView;
import org.ripple.power.utils.DateUtils;

public class RippleBOTLog implements BotLog {

	private LogView _view;

	public RippleBOTLog(String title) {
		this._view = new LogView(title);
		this._view.show();
	}

	@Override
	public void mes(String message, Object... args) {
		if (_view != null) {
			StringBuffer buf = new StringBuffer();
			buf.append(DateUtils.toDate());
			buf.append(" - ");
			buf.append(String.format(message, args));
			buf.append(LSystem.LS);
			_view.append(buf.toString());
		}
	}

	@Override
	public void close() {
		if (_view != null) {
			_view.closed();
		}
	}

}
