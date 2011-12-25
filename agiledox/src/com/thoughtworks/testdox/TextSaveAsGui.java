package com.thoughtworks.testdox;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.JOptionPane;

public class TextSaveAsGui extends SaveAsGui {
	
	private static final long serialVersionUID = 1L;
	
	public TextSaveAsGui() {
		super("Generate Text", "Save Text");
	}
	
	public DocumentGenerator createDocumentGenerator() {
		try {
			return new ConsoleGenerator(new PrintStream(new FileOutputStream(fileName.getText()), true));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error creating file", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}
}