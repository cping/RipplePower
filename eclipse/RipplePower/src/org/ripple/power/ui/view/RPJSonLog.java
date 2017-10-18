package org.ripple.power.ui.view;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.ui.graphics.geom.Point;
import org.ripple.power.ui.view.RPToast.Style;
import org.ripple.power.utils.StringUtils;

public class RPJSonLog {

	private int _limit = 2000;

	private int _count;

	private static RPJSonLog instance = null;

	private final static String _def_name = "RPC";

	static int WIDTH = 180, HEIGHT = 220;

	public static void hideDialog() {
		if (instance != null) {
			instance.setVisible(false);
		}
	}

	public static void showDialog() {
		if (instance != null) {
			instance.setVisible(true);
		}
	}

	public synchronized static RPJSonLog get() {
		if (instance == null) {
			instance = new RPJSonLog();
		} else if (instance.isClose()) {
			instance._tool.close();
			instance = new RPJSonLog();
		}
		return instance;
	}

	private RPPushTool _tool;

	public boolean isClose() {
		return _tool.isClose();
	}

	public void setVisible(boolean v) {
		_tool.setVisible(v);
	}

	public boolean isVisible() {
		return _tool.isVisible();
	}

	public RPJSonLog() {
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		Insets screenInsets = Toolkit.getDefaultToolkit()
				.getScreenInsets(LSystem.applicationMain.getGraphicsConfiguration());
		Dimension panSize = new Dimension(WIDTH, HEIGHT);
		lConsole = new JConsole();
		lConsole.setPreferredSize(panSize);
		lConsole.setSize(panSize);
		_tool = RPPushTool.pop(new Point(20, size.getHeight()),
				(int) (screenInsets.bottom + lConsole.getHeight() + (RPPushTool.TITLE_SIZE) + 260), _def_name,
				lConsole);
	}

	private JConsole lConsole;

	public void print(String line) {
		if (lConsole != null) {
			if (_count > _limit) {
				lConsole.clear();
				_count = 0;
			}
			if (_tool != null) {
				_tool.setTitle(_def_name);
			}
			lConsole.uiprint(line);
			_count++;
		}
	}

	public void println(JSONObject o) {
		println(null, o, true);
	}

	public void println(String title, JSONObject o) {
		println(title, o, true);
	}

	public void println(JSONObject o, boolean show) {
		println(null, o, show);
	}

	public void println(String title, JSONObject o, boolean show) {
		if (o != null && o.has("result")) {
			JSONObject result = o.getJSONObject("result");
			if (lConsole != null) {
				if (_count > _limit) {
					lConsole.clear();
					_count = 0;
				}
				if (!StringUtils.isEmpty(title) && _tool != null) {
					_tool.setTitle(title);
				} else if (_tool != null) {
					_tool.setTitle(_def_name);
				}
				lConsole.uiprint(result + LSystem.LS);
				_count++;
			}
			if (show) {
				int engine_result_code = -1;
				if (result.has("engine_result_code")) {
					engine_result_code = result.getInt("engine_result_code");
				}
				if (result.has("engine_result_message")) {
					String engine_result_message = result.getString("engine_result_message");
					RPToast toast = RPToast.makeText(LSystem.applicationMain, engine_result_message,
							(engine_result_code == 0 ? Style.SUCCESS : Style.ERROR));
					toast.setDuration(6000);
					toast.display();
				}
			}
		}
	}

	public void println() {
		if (lConsole != null) {
			if (_count > _limit) {
				lConsole.clear();
				_count = 0;
			}
			if (_tool != null) {
				_tool.setTitle(_def_name);
			}
			lConsole.uiprint(LSystem.LS);
			_count++;
		}
	}

	public void println(String line) {
		println(line);
	}

	public void println(String title, String line) {
		if (lConsole != null) {
			if (_count > _limit) {
				lConsole.clear();
				_count = 0;
			}
			if (!StringUtils.isEmpty(title) && _tool != null) {
				_tool.setTitle(title);
			} else if (_tool != null) {
				_tool.setTitle(_def_name);
			}
			lConsole.uiprint(line + LSystem.LS);
			_count++;
		}
	}

	public void setImageIcon(ImageIcon icon) {
		setImageIcon(null, icon);
	}

	public void setImageIcon(String title, ImageIcon icon) {
		if (lConsole != null) {
			if (!StringUtils.isEmpty(title) && _tool != null) {
				_tool.setTitle(title);
			} else if (_tool != null) {
				_tool.setTitle(_def_name);
			}
			lConsole.setImageIcon(icon);
		}
	}

	public void imageShow() {
		if (lConsole != null) {
			lConsole.imageShow();
		}
	}

	public void imageHide() {
		if (lConsole != null) {
			lConsole.imageHide();
		}
	}

	public int getLimit() {
		return _limit;
	}

	public void setLimit(int l) {
		this._limit = l;
	}

	public int getCount() {
		return _count;
	}

}
