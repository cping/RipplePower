package org.ripple.power.print;

import java.io.FileInputStream;
import java.util.Locale;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PageRanges;

import org.ripple.power.config.LSystem;
import org.ripple.power.utils.FileUtils;

public class PrintDirectFunction extends PrintFunction {

	private DocFlavor df;

	private SimpleDoc doc;

	public PrintDirectFunction() {
		super();
	}

	private void setExt(String name) {
		if (name == null) {
			df = null;
		} else
			switch (name.toLowerCase()) {
			case "jpg":
				df = DocFlavor.INPUT_STREAM.JPEG;
				break;
			case "png":
				df = DocFlavor.INPUT_STREAM.PNG;
				break;
			case "gif":
				df = DocFlavor.INPUT_STREAM.GIF;
				break;
			case "txt":
				df = DocFlavor.INPUT_STREAM.AUTOSENSE;
				break;
			case "html":
				df = DocFlavor.INPUT_STREAM.TEXT_HTML_UTF_8;
				break;
			case "pdf":
				df = DocFlavor.INPUT_STREAM.AUTOSENSE;
				break;
			case "print":
				df = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
				break;
			default:
				df = null;
			}

	}

	@Override
	public boolean print() {
		if (df == null) {
			return false;
		} else {
			try {
				PrintRequestAttributeSet requestAttributeSet = new HashPrintRequestAttributeSet();
				requestAttributeSet.add(MediaSizeName.ISO_A4);
				requestAttributeSet.add(new JobName(LSystem.applicationName + "-" + LSystem.getTime(), Locale.ENGLISH));
				PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
				DocPrintJob job = defaultService.createPrintJob();
				if (df == DocFlavor.SERVICE_FORMATTED.PRINTABLE) {
					doc = new SimpleDoc(new BufferedImagePrintable(path), df, null);
					requestAttributeSet.add(new PageRanges("1"));
				} else {
					FileInputStream fin = new FileInputStream(path);
					doc = new SimpleDoc(fin, df, null);
				}
				job.print(doc, requestAttributeSet);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}

	@Override
	public void setPath(String path) {
		super.setPath(path);
		setExt(FileUtils.getExtension(path));
	}
}
