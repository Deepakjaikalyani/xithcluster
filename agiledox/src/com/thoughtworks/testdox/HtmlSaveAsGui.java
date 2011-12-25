package com.thoughtworks.testdox;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

public class HtmlSaveAsGui extends SaveAsGui {
	
	private static final long serialVersionUID = 1L;
	
	public HtmlSaveAsGui() {
		super("Generate HTML", "Save HTML");
	}
	
	public DocumentGenerator createDocumentGenerator() {
		try {
			return new HtmlDocumentGenerator(new PrintWriter(new FileWriter(fileName.getText()), true));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error creating file", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}
}
