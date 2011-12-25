package com.thoughtworks.testdox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

public class SaveAsGuiTest {
	
	private SaveAsGui gui;
	
	private static final String HELPFUL_TEXT = "Some helpful text";
	
	private static final String SAVE_TEXT = "Save HTML";
	
	@Before
	public void setUp() throws Exception {
		gui = new SaveAsGui(HELPFUL_TEXT, SAVE_TEXT) {
			
			private static final long serialVersionUID = 1L;
			
			public DocumentGenerator createDocumentGenerator() {
				return null;
			}
		};
	}
	
	@Test
	public void testClickSaveAsSetsFileText() throws IOException {
		gui.fileChooser = GuiTestUtil.selectSrcChooser;
		gui.saveAsButton.doClick();
		assertEquals(GuiTestUtil.selectedFile.getCanonicalPath(), gui.fileName.getText());
	}
	
	@Test
	public void testLabelHasHelpfulText() throws IOException {
		assertEquals(HELPFUL_TEXT, gui.titledBorder.getTitle());
	}
	
	@Test
	public void testSaveAsHasMeaningfulText() throws IOException {
		assertEquals(SAVE_TEXT, gui.saveAsButton.getText());
	}
	
	@Test
	public void testHtmlSaveAsGuiReturnsHtmlDocGenerator() {
		HtmlSaveAsGui gui = new HtmlSaveAsGui();
		gui.fileName.setText("test.html");
		DocumentGenerator documentGenerator = gui.createDocumentGenerator();
		assertNotNull(documentGenerator);
		assertTrue(documentGenerator instanceof HtmlDocumentGenerator);
	}
	
	@Test
	public void testTextSaveAsGuiReturnsTextGenerator() {
		TextSaveAsGui gui = new TextSaveAsGui();
		gui.fileName.setText("test.txt");
		DocumentGenerator documentGenerator = gui.createDocumentGenerator();
		assertNotNull(documentGenerator);
		assertTrue(documentGenerator instanceof ConsoleGenerator);
	}
}
