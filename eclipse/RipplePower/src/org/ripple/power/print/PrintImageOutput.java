package org.ripple.power.print;

import java.awt.image.BufferedImage;
import java.util.Locale;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaSizeName;

import org.ripple.power.config.LSystem;

public class PrintImageOutput {

	public static boolean out(BufferedImage image) {
		try {
			DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
			PrintRequestAttributeSet requestAttributeSet = new HashPrintRequestAttributeSet();
			requestAttributeSet.add(MediaSizeName.ISO_A4);
			requestAttributeSet.add(new JobName(LSystem.applicationName
					+ LSystem.getTime(), Locale.ENGLISH));
			PrintService[] services = PrintServiceLookup.lookupPrintServices(
					flavor, requestAttributeSet);
			PrintService defaultService = PrintServiceLookup
					.lookupDefaultPrintService();
			PrintService service = ServiceUI.printDialog(null, 100, 100,
					services, defaultService, flavor, requestAttributeSet);
			if (service != null) {
				DocPrintJob job = service.createPrintJob();
				SimpleDoc doc = new SimpleDoc(
						new BufferedImagePrintable(image), flavor, null);
				job.print(doc, requestAttributeSet);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
