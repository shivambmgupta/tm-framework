package com.thinking.machines.tool;

import java.io.*;
import java.util.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class ErrorWriter extends Thread {

	private final String TITLE = "Errors";
	private final String PDF_EXTENSION = ".pdf";

	private java.util.List<MetaService> services;
	private String toSave; 

	public ErrorWriter(String toSave, java.util.List<MetaService> services) {
		this.services = services;
		this.toSave = toSave;
	}

	public void run() {
		Document document = null;
		try {
			document = new Document(PageSize.A4);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(
                    new File(toSave + TITLE + PDF_EXTENSION)));

			document.open();
			ToolPDFCreator.addMetaData(document, TITLE);
			ToolPDFCreator.addTitlePage(document, TITLE);
			this.addContent(document);

		} catch(Exception exception) {
			System.err.printf("\n\n\nError in ErrorWriter:\n\n");
			exception.printStackTrace();
		} finally {
			if(null != document) {
				document.close();
			}
		}
	}

	private void addContent(Document document) throws Exception {

		Paragraph servicePara, serviceHead, serviceBody, bodyLine, paragraph;

		paragraph = new Paragraph();
		paragraph.add(new Phrase("Total number of bad services : ", ToolPDFCreator.BLACK_MIDD_BOLD));
		paragraph.add(new Phrase(services.size() + "", ToolPDFCreator.RED_MIDD_BOLD));

		ToolPDFCreator.addEmptyLine(paragraph, 2);
		paragraph.add(new Phrase("Following are the bad services : ", ToolPDFCreator.BLACK_MIDD_BOLD));
		ToolPDFCreator.addEmptyLine(paragraph, 2);

		boolean flag = true;

		int k = 1;

		for(MetaService mService : services) {

			servicePara = new Paragraph();
			servicePara.setAlignment(Element.ALIGN_LEFT);
			ToolPDFCreator.addEmptyLine(servicePara, 2);

			paragraph.add(new Paragraph(k + ". " + mService.service.getServicePath(), ToolPDFCreator.BLACK_SMALL_NORMAL));
			++k;

			serviceHead = new Paragraph();
			serviceHead.add(new Phrase("Service: ", ToolPDFCreator.BLACK_MIDD_BOLD));
			serviceHead.add(new Phrase(mService.service.getServicePath(), ToolPDFCreator.RED_MIDD_BOLD));

			serviceBody = new Paragraph();
			int i = 1;
			for(String error : mService.errors) {
				bodyLine = new Paragraph("  " + i + ". " + error, ToolPDFCreator.BLACK_SMALL_NORMAL);
				serviceBody.add(bodyLine);
				++i;
			}

			if(flag) {
				ToolPDFCreator.addEmptyLine(servicePara, 3);
				servicePara.add(paragraph);
				flag = !flag;
			}
			ToolPDFCreator.addEmptyLine(servicePara, 1);
			servicePara.add(serviceHead);
			ToolPDFCreator.addEmptyLine(servicePara, 1);
			servicePara.add(serviceBody);
			ToolPDFCreator.addEmptyLine(servicePara, 1);
			document.add(servicePara);
		}
	}

}