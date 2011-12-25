package com.thoughtworks.testdox;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public abstract class SaveAsGui extends JPanel implements DocumentGeneratorGui {
	
	private static final long serialVersionUID = 1L;
	
	JFileChooser fileChooser;
	
	JButton saveAsButton;
	
	JTextField fileName;
	
	TitledBorder titledBorder;
	
	private ActionListener saveAsButtonActionListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			fileChooser.setDialogTitle("Save As");
			if (fileChooser.showSaveDialog(SaveAsGui.this) == JFileChooser.APPROVE_OPTION) {
				try {
					fileName.setText(fileChooser.getSelectedFile().getCanonicalPath());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	};
	
	public SaveAsGui(String helpfulText, String saveAsText) {
		
		fileChooser = new JFileChooser();
		saveAsButton = new JButton(saveAsText);
		fileName = new JTextField(30);
		
		titledBorder = BorderFactory.createTitledBorder(helpfulText);
		setBorder(titledBorder);
		
		setLayout(new FlowLayout());
		add(fileName);
		add(saveAsButton);
		
		saveAsButton.addActionListener(saveAsButtonActionListener);
	}
	
	public boolean isConfigured() {
		return fileName.getText().length() > 0;
	}
	
	public Component getComponent() {
		return this;
	}
	
}
