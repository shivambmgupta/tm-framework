package com.thinking.machines.tool;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.thinking.machines.annotation.*;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

public class ServiceWriter extends Thread {

	private final String TITLE = "Services";
	private final String PDF_EXTENSION = ".pdf";

	private java.util.List<MetaService> services;
	private String toSave;

	public ServiceWriter(String toSave, java.util.List<MetaService> services) {
		this.services = services;
		this.toSave = toSave;
	}
	public void run() {

		Document document = null;
		try {
			document = new Document(PageSize.A4);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(
                    new File(this.toSave + TITLE + PDF_EXTENSION)));

			document.open();
			ToolPDFCreator.addMetaData(document, TITLE);
			ToolPDFCreator.addTitlePage(document, TITLE);
			this.addContent(document);

		} catch(Exception exception) {
			System.err.printf("\n\n\nError in ServiceWriter:\n\n");
			exception.printStackTrace();
		} finally {
			if(null != document) {
				document.close();
			}
		}

	}
	private void addContent(Document document) throws Exception {

		Paragraph servicePara, serviceHead, serviceBody, bodyLine;
		Chunk glue = new Chunk(new VerticalPositionMark());
		Font font;

		for(MetaService mService : services) {

			servicePara = new Paragraph();
			servicePara.setAlignment(Element.ALIGN_CENTER);

			ToolPDFCreator.addEmptyLine(servicePara, 5);

			if(mService.isBadService()) {
				serviceHead = new Paragraph("Service(" + mService.service.getServicePath() + ")", ToolPDFCreator.RED_LARGE_BOLD);
			}
			else {
				serviceHead = new Paragraph("Service(" + mService.service.getServicePath() + ")", ToolPDFCreator.BLACK_LARGE_BOLD);
			}

			serviceBody = new Paragraph();
			
			bodyLine = new Paragraph();
			bodyLine.setFont(ToolPDFCreator.BLACK_SMALL_BOLD);
			bodyLine.add("Class Name: ");

			if(mService.hasBadClass)
				font = ToolPDFCreator.RED_SMALL_NORMAL;
			else 
				font = ToolPDFCreator.BLACK_SMALL_NORMAL;

			bodyLine.setFont(font);
			bodyLine.add(new Chunk(glue));
			bodyLine.add(mService.service.getServiceClass().getSimpleName());
			
			serviceBody.add(bodyLine);

			// Pin 1
			bodyLine = new Paragraph();
			bodyLine.setFont(ToolPDFCreator.BLACK_SMALL_BOLD);
			bodyLine.add("Method Name: ");

			if(mService.hasBadMethod)
				font = ToolPDFCreator.RED_SMALL_NORMAL;
			else 
				font = ToolPDFCreator.BLACK_SMALL_NORMAL;

			bodyLine.setFont(font);
			bodyLine.add(new Chunk(glue));
			bodyLine.add(mService.service.getServiceMethod().getName());
			
			serviceBody.add(bodyLine);
			//Pin 2

		 	ToolPDFCreator.addEmptyLine(serviceBody, 2);
			this.createAnnotationTable(serviceBody, mService);
			ToolPDFCreator.addEmptyLine(serviceBody, 2);
			this.createParameterTable(serviceBody, mService);

			servicePara.add(serviceHead);
			ToolPDFCreator.addEmptyLine(servicePara, 2);
			servicePara.add(serviceBody);
			document.add(servicePara);
			document.newPage();

		}
	}

	private void createAnnotationTable(Paragraph paragraph, MetaService mService) throws Exception {
		
		 PdfPTable table = new PdfPTable(3);
         table.setWidthPercentage(100);

         Font font;
         String temp;
         String NOT_PRESENT = "Not present";
         
         paragraph.add(new Chunk("Annotation Table: ", ToolPDFCreator.BLACK_SMALL_BOLD));

         String[] header = {"Annotation", "Value", "Level"};
         ToolPDFCreator.addHeaderInTable(header, table);

         //Pin 1
		 ToolPDFCreator.addToTable(table, "Path", ToolPDFCreator.BLACK_SMALL_BOLD);
         temp = mService.service.getServicePath();
 		 ToolPDFCreator.addToTable(table, temp);
		 ToolPDFCreator.addToTable(table, "Class, Method");
		//Pin 2

         //Pin 1
         ToolPDFCreator.addToTable(table, "Forward", ToolPDFCreator.BLACK_SMALL_BOLD);

         temp = mService.service.forwardValue;
		 if (temp == null) {
			temp = NOT_PRESENT;
			font = ToolPDFCreator.BLACK_SMALL_ITALIC;
		 }
		 else if (mService.hasBadForward)
			font = ToolPDFCreator.RED_SMALL_NORMAL;
		 else 
			font = ToolPDFCreator.BLACK_SMALL_NORMAL;

		 ToolPDFCreator.addToTable(table, temp, font);

		 if(!temp.equals(NOT_PRESENT)) {
		 	Forward fwd = mService.service.getServiceMethod().getAnnotation(Forward.class);
		 	if(fwd != null)
		 		temp = "Method";
		 	else
		 		temp = "Class";
		 	font = ToolPDFCreator.BLACK_SMALL_NORMAL;
		 }
		 else font =ToolPDFCreator.BLACK_SMALL_ITALIC;

		 ToolPDFCreator.addToTable(table, temp, font);
		//Pin 2


		//Pin 1
         ToolPDFCreator.addToTable(table, "Secured", ToolPDFCreator.BLACK_SMALL_BOLD);

         temp = mService.service.securedValue;
		 if (temp == null) {
			temp = NOT_PRESENT;
			font = ToolPDFCreator.BLACK_SMALL_ITALIC;
		 }
		 else if (mService.hasBadSecured)
			font = ToolPDFCreator.RED_SMALL_NORMAL;
		 else 
			font = ToolPDFCreator.BLACK_SMALL_NORMAL;

		 ToolPDFCreator.addToTable(table, temp, font);

		 if(!temp.equals(NOT_PRESENT)) {
		 	Secured secured = mService.service.getServiceMethod().getAnnotation(Secured.class);
		 	if(secured != null)
		 		temp = "Method";
		 	else
		 		temp = "Class";
		  	font = ToolPDFCreator.BLACK_SMALL_NORMAL;
		 }
		 else font =ToolPDFCreator.BLACK_SMALL_ITALIC;

		 ToolPDFCreator.addToTable(table, temp, font);
		//Pin 2
         
         //Pin 1
         ToolPDFCreator.addToTable(table, "ResponseType", ToolPDFCreator.BLACK_SMALL_BOLD);

         temp = mService.service.responseTypeValue;
		 if (temp == null) {
			temp = NOT_PRESENT;
			font = ToolPDFCreator.BLACK_SMALL_ITALIC;
		 }
		 else if (mService.hasBadResponseType)
			font = ToolPDFCreator.RED_SMALL_NORMAL;
		 else 
			font = ToolPDFCreator.BLACK_SMALL_NORMAL;

		 ToolPDFCreator.addToTable(table, temp, font);

		 if(!temp.equals(NOT_PRESENT)) {
		 	ResponseType res = mService.service.getServiceMethod().getAnnotation(ResponseType.class);
		 	if(res != null)
		 		temp = "Method";
		 	else
		 		temp = "Class";
		  	font = ToolPDFCreator.BLACK_SMALL_NORMAL;
		 }
		 else font =ToolPDFCreator.BLACK_SMALL_ITALIC;
		 ToolPDFCreator.addToTable(table, temp, font);
		//Pin 2

		 //Pin 1
         ToolPDFCreator.addToTable(table, "File Upload", ToolPDFCreator.BLACK_SMALL_BOLD);

		 if (!mService.service.getFileAnnotation) {
			temp = NOT_PRESENT;
			font = ToolPDFCreator.BLACK_SMALL_ITALIC;
		 }
		 else {
		 	temp = "Present";
			font = ToolPDFCreator.BLACK_SMALL_NORMAL;
		 }

		 ToolPDFCreator.addToTable(table, temp, font);

		 if(!temp.equals(NOT_PRESENT)) {
		 	GetFiles getFiles = mService.service.getServiceMethod().getAnnotation(GetFiles.class);
		 	if(getFiles != null)
		 		temp = "Method";
		 	else
		 		temp = "Class";
		  	font = ToolPDFCreator.BLACK_SMALL_NORMAL;
		 }
		 else font =ToolPDFCreator.BLACK_SMALL_ITALIC;
		 ToolPDFCreator.addToTable(table, temp, font);	
		//Pin 2

         paragraph.add(table);
	}

	private void createParameterTable(Paragraph paragraph, MetaService mService) throws Exception {

		Method method = mService.service.getServiceMethod();

		PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        String NOT_PRESENT = "Not present";

        int N = method.getParameterCount();
        if(N == 0) {
        	paragraph.add(new Chunk("No parameters are present in service method: " + method.getName(), ToolPDFCreator.BLACK_SMALL_BOLD));
        	return;
        }
         
        paragraph.add(new Chunk("Parameter Table: ", ToolPDFCreator.BLACK_SMALL_BOLD));

        String[] header = {"Parameter Index", "Parameter Class", "RequestType Annotation"};
        ToolPDFCreator.addHeaderInTable(header, table);

        Class<?>[] paramTypes = method.getParameterTypes();
        java.lang.annotation.Annotation[][] annotations = method.getParameterAnnotations();
        java.lang.annotation.Annotation[] annotationRow;

        int i = 0;

        while(i < N) {
        	annotationRow = annotations[i];
        	ToolPDFCreator.addToTable(table, i + "");
        	ToolPDFCreator.addToTable(table, paramTypes[i].getSimpleName());
        	String anonValue = getRequestDataAnnonValue(annotationRow);

        	if(anonValue == null)
        		ToolPDFCreator.addToTable(table, NOT_PRESENT ,ToolPDFCreator.BLACK_SMALL_ITALIC);
        	else
        		ToolPDFCreator.addToTable(table, anonValue);

        	++i;
        }

        paragraph.add(table);
	}

    private String getRequestDataAnnonValue(java.lang.annotation.Annotation[] annos) {
   		if(annos == null || annos.length == 0) return null;
   		for(java.lang.annotation.Annotation annotation : annos)
    		if(RequestData.class.isInstance(annotation)) {
      		return ((RequestData)annotation).value();
    	}
    	return null;
	}

}