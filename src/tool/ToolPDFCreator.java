package com.thinking.machines.tool;

import java.io.*;
import java.util.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class ToolPDFCreator {

	public final static Font BLACK_SMALL_NORMAL = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
	public final static Font BLACK_SMALL_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	public final static Font RED_SMALL_NORMAL = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
	public final static Font RED_SMALL_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.RED);
	public final static Font BLACK_SMALL_ITALIC = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);

	public final static Font BLACK_MIDD_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
	public final static Font RED_MIDD_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.RED);

	public final static Font BLACK_LARGE_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    public final static Font RED_LARGE_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, BaseColor.RED);

    public final static Font RED_EXTRA_LARGE_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD, BaseColor.RED);

    public static void addMetaData(Document document, String title) throws Exception {
    	document.addTitle(title);
    	document.addSubject("iText");
    	document.addAuthor("Shivam Gupta");
    	document.addKeywords("Java, PDF");
    }

    public static void addTitlePage(Document document, String title) throws Exception {

    	Paragraph preface = new Paragraph();
    	addEmptyLine(preface, 3);

    	preface.add(new Phrase(title, RED_EXTRA_LARGE_BOLD));

        addEmptyLine(preface, 20);
        preface.add(new Paragraph("The aim of this document is to provide details about the services.", BLACK_SMALL_BOLD));

        addEmptyLine(preface, 20);
        preface.add(new Phrase("Date: ", BLACK_SMALL_BOLD));
        preface.add(new Phrase(new Date().toString(), BLACK_SMALL_NORMAL));

        preface.setAlignment(Element.ALIGN_CENTER);
        document.add(preface);
        document.newPage();
    }

    public static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
    public static void addHeaderInTable(String[] header, PdfPTable table){

    	for(int i = 0; i < header.length; ++i) {
        	PdfPCell c1 = new PdfPCell(new Phrase(header[i], ToolPDFCreator.BLACK_SMALL_BOLD));
          	c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        	table.addCell(c1);
        }
        table.setHeaderRows(1);
    }
    public static void addToTable(PdfPTable table, String data, Font font) { 
     	PdfPCell cell = new PdfPCell(new Phrase(data, font));
     	cell.setHorizontalAlignment(Element.ALIGN_CENTER);       
        table.addCell(cell);
    }
    public static void addToTable(PdfPTable table, String data) { 
     	PdfPCell cell = new PdfPCell(new Phrase(data, BLACK_SMALL_NORMAL));
     	cell.setHorizontalAlignment(Element.ALIGN_CENTER);       
        table.addCell(cell);
    }

}