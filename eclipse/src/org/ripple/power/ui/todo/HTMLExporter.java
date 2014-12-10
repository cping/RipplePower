package org.ripple.power.ui.todo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class HTMLExporter implements Exporter {

	private String content;
	private String file;

	public HTMLExporter(List<TodoItem> items, String file) {
		this.file = file;
		StringBuilder sbr = new StringBuilder();
		for (TodoItem item : items) {
			sbr.append("Id:"+item.getId());
			sbr.append("<br>");
			sbr.append("Note:"+item.getNote());
			sbr.append("<br>");
			sbr.append("Status:"+item.getStatus());
			sbr.append("<br>");
			sbr.append("Period:"+item.getPeriod());
			sbr.append("<br>");
			sbr.append("Timeout:"+item.getTimeout());
			sbr.append("<br>");
		}
		this.content = sbr.toString();
	}

	public String doExport() {
		return content;
	}

	public void store() {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
				}
			}
		}
	}

}
