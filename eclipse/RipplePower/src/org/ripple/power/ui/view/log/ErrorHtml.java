package org.ripple.power.ui.view.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.ripple.power.config.LSystem;
import org.ripple.power.utils.FileUtils;

public abstract class ErrorHtml {

	protected static final String FIELD_START = "<td>";
	protected static final String FIELD_END = "</td>";
	protected static final String HEADER_START = "<th>";
	protected static final String HEADER_END = "</th>";
	protected static final String ROW_START = "<tr>";
	protected static final String ROW_END = "</tr>";
	protected static final String FIELD_BREAK = "<br>";
	private PrintWriter writer;

	protected ErrorHtml(String reportName) {
		try {
			String fullFileName = LSystem.getCurrentDirectory() + reportName + ".html";
			File reportFile = new File(fullFileName);
			boolean reportExisted = reportFile.exists();
			if (!reportExisted) {
				FileUtils.makedirs(reportFile);
			}
			writer = new PrintWriter(new BufferedWriter(new FileWriter(fullFileName, true)));
			StringBuilder sb = new StringBuilder();
			if (reportExisted) {
				sb.append("</table><br>");
			} else {
				sb.append("<html>");
			}
			sb.append("<table border=\"1\" cellpadding=\"2\" cellspacing=\"0\" width=100%>");
			write(sb);
		} catch (Throwable ex) {
		}
	}

	protected synchronized void write(StringBuilder sb) {
		writer.println(sb);
		writer.flush();
	}

}