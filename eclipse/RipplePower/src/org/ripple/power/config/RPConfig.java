package org.ripple.power.config;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RPConfig {

	private String FLAG_L_TAG = "//";

	private String FLAG_C_TAG = "#";

	private String FLAG_I_TAG = "'";

	private final static HashMap<String, RPConfig> pConfigReaders = new HashMap<String, RPConfig>();

	public static RPConfig getInstance(String resName) {
		synchronized (pConfigReaders) {
			RPConfig reader = pConfigReaders.get(resName);
			if (reader == null || reader.isClose) {
				try {
					reader = new RPConfig(resName);
				} catch (IOException ex) {
					throw new RuntimeException(ex.getMessage());
				}
				pConfigReaders.put(resName, reader);
			}
			return reader;
		}
	}

	public static RPConfig getInstance(final InputStream in) {
		try {
			return new RPConfig(in);
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	private final HashMap<String, String> pConfigItems = new HashMap<String, String>();

	private StringBuffer values = new StringBuffer();

	private boolean isClose;

	public HashMap<String, String> getContent() {
		return new HashMap<String, String>(pConfigItems);
	}

	public RPConfig(final String resName) throws IOException {
		this(new FileInputStream(resName));
	}

	public RPConfig(final File file) throws IOException {
		this(file != null ? (file.exists() ? new FileInputStream(file)
				: RPConfig.class.getResourceAsStream(file.getPath()))
				: new FileInputStream(new File("default.cfg")));
	}

	public RPConfig(final InputStream in) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in,
					LSystem.encoding));
			String record = null;
			StringBuffer mapBuffer = new StringBuffer();
			boolean mapFlag = false;
			String mapName = null;
			for (; (record = reader.readLine()) != null;) {
				record = record.trim();
				if (record.length() > 0 && !record.startsWith(FLAG_L_TAG)
						&& !record.startsWith(FLAG_C_TAG)
						&& !record.startsWith(FLAG_I_TAG)) {
					if (record.startsWith("begin")) {
						mapBuffer.delete(0, mapBuffer.length());
						String mes = record.substring(5, record.length())
								.trim();
						if (mes.startsWith("name")) {
							mapName = loadItem(mes, false);
						}
						mapFlag = true;
					} else if (record.startsWith("end")) {
						mapFlag = false;
						if (mapName != null) {
							pConfigItems.put(mapName, mapBuffer.toString());
						}
					} else if (mapFlag) {
						mapBuffer.append(record);
					} else {
						loadItem(record, true);
					}
				}
			}
		} catch (Exception ex) {
			throw new IOException(ex.getMessage());
		} finally {
			LSystem.close(in);
			if (reader != null) {
				try {
					reader.close();
					reader = null;
				} catch (IOException e) {
				}
			}
		}
	}

	private final String loadItem(final String mes, final boolean save) {
		char[] chars = mes.toCharArray();
		int size = chars.length;
		StringBuffer sbr = values.delete(0, values.length());
		String key = null;
		String value = null;
		int idx = 0;
		int equals = 0;
		for (int i = 0; i < size; i++) {
			char flag = chars[i];
			switch (flag) {
			case '=':
				if (equals < 3) {
					equals++;
					if (idx == 0) {
						key = sbr.toString();
						sbr.delete(0, sbr.length());
					}
					idx++;
				}
				break;
			case '\'':
				if (equals > 1) {
					sbr.append(flag);
				}
				break;
			case '\"':
				equals++;
				break;
			default:
				sbr.append(flag);
				break;
			}
		}
		if (key != null) {
			value = sbr.toString();
			if (save) {
				pConfigItems.put(key.trim(), value.trim());
			}
		}
		return value;
	}

	public void putItem(String key, String value) {
		synchronized (pConfigItems) {
			pConfigItems.put(key, value);
		}
	}

	public void removeItem(String key) {
		synchronized (pConfigItems) {
			pConfigItems.remove(key);
		}
	}

	public boolean getBoolValue(String name) {
		return getBoolValue(name, false);
	}

	public boolean getBoolValue(String name, boolean fallback) {
		String v = null;
		synchronized (pConfigItems) {
			v = pConfigItems.get(name);
		}
		if (v == null) {
			return fallback;
		}
		return "true".equalsIgnoreCase(v) || "yes".equalsIgnoreCase(v)
				|| "ok".equalsIgnoreCase(v);
	}

	public int getIntValue(String name) {
		return getIntValue(name, 0);
	}

	public int getIntValue(String name, int fallback) {
		String v = null;
		synchronized (pConfigItems) {
			v = pConfigItems.get(name);
		}
		if (v == null) {
			return fallback;
		}
		return Integer.parseInt(v);
	}

	public float getFloatValue(String name) {
		return getFloatValue(name, 0f);
	}

	public double getDoubleValue(String name, float fallback) {
		String v = null;
		synchronized (pConfigItems) {
			v = pConfigItems.get(name);
		}
		if (v == null) {
			return fallback;
		}
		return Double.parseDouble(v);
	}

	public double getDoubleValue(String name) {
		return getFloatValue(name, 0f);
	}

	public float getFloatValue(String name, float fallback) {
		String v = null;
		synchronized (pConfigItems) {
			v = pConfigItems.get(name);
		}
		if (v == null) {
			return fallback;
		}
		return Float.parseFloat(v);
	}

	public String getValue(String name) {
		return getValue(name, null);
	}

	public String getValue(String name, String fallback) {
		String v = null;
		synchronized (pConfigItems) {
			v = pConfigItems.get(name);
		}
		if (v == null) {
			return fallback;
		}
		return v;
	}

	public String get(String name) {
		return getValue(name, null);
	}

	public boolean isClose() {
		return isClose;
	}

	public void dispose() {
		isClose = true;
		if (pConfigItems != null) {
			pConfigItems.clear();
		}
	}

}
