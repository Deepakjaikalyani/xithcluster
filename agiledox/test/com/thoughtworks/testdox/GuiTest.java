package com.thoughtworks.testdox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA. User: stevcc Date: 11-Jun-2003 Time: 19:09:15 To
 * change this template use Options | File Templates.
 */
public class GuiTest {
	
	private Gui gui;
	
	private TestGenerator gen;
	
	@Before
	public void setUp() throws Exception {
		// Avoid overwriting any of the users real preferences by setting a test
		// set
		Preferences testPrefs = Preferences.userNodeForPackage(GuiTest.class);
		Gui.prefs = testPrefs;
		
		gen = new TestGenerator();
		gui = new Gui("foo", gen);
	}
	
	@After
	public void tearDown() throws Exception {
		Gui.prefs.clear();
		Gui.prefs.flush();
	}
	
	@Test
	public void testShowsFileChooserIfBrowseIsClicked() {
		final boolean[] wasShown = new boolean[] {
				false
		};
		
		JFileChooser chooser = new JFileChooser() {
			
			private static final long serialVersionUID = 1L;
			
			public int showOpenDialog(Component parent) throws HeadlessException {
				wasShown[0] = true;
				return JFileChooser.CANCEL_OPTION;
			}
		};
		
		gui.fileChooser = chooser;
		gui.browseButton.doClick();
		assertNotNull(gui.fileChooser);
		assertTrue(gui.fileChooser.isDirectorySelectionEnabled());
		assertTrue(wasShown[0]);
	}
	
	@Test
	public void testExitJvmOnClose() {
		gui = new Gui("foo", gen);
		assertEquals(JFrame.EXIT_ON_CLOSE, gui.getDefaultCloseOperation());
	}
	
	@Test
	public void testSelectedFileIsShownInTextField() throws IOException {
		
		gui.fileChooser = GuiTestUtil.selectSrcChooser;
		gui.browseButton.doClick();
		
		assertEquals(GuiTestUtil.selectedFile.getCanonicalPath(), gui.path.getText());
	}
	
	@Test
	public void testSelectedDirectoryIsWrittenToPreferences() throws IOException {
		gui.fileChooser = GuiTestUtil.selectSrcChooser;
		gui.browseButton.doClick();
		assertEquals(GuiTestUtil.selectedFile.getCanonicalPath(), Gui.prefs.get(Gui.SELECTED_DIRECTORY_KEY, null));
	}
	
	@Test
	public void testSelectedDirectoryIsReadFromPreferences() throws IOException {
		testSelectedDirectoryIsWrittenToPreferences();
		Gui gui = new Gui("second gui", gen);
		assertEquals(GuiTestUtil.selectedFile.getCanonicalPath(), gui.path.getText());
		assertEquals(null, gui.fileChooser);
		gui.initializeFileChooser();
		assertEquals(GuiTestUtil.selectedFile.getCanonicalPath(), gui.fileChooser.getSelectedFile().getCanonicalPath());
	}
	
	@Test
	public void testSelectedFileIsNotShownIfUserClickedCancel() {
		JFileChooser chooser = new JFileChooser() {
			
			private static final long serialVersionUID = 1L;
			
			public File getSelectedFile() {
				return null;
			}
			
			public int showOpenDialog(Component parent) throws HeadlessException {
				return JFileChooser.CANCEL_OPTION;
			}
		};
		
		gui = new Gui("foo", gen);
		gui.fileChooser = chooser;
		gui.browseButton.doClick();
		
		assertEquals("", gui.path.getText());
	}
	
	@Test
	public void testGoButtonEnableUponUserFileSelection() {
		assertFalse(gui.goButton.isEnabled());
		gui.fileChooser = GuiTestUtil.selectSrcChooser;
		gui.browseButton.doClick();
		assertTrue(gui.goButton.isEnabled());
	}
	
	public static class TestGenerator implements Generator {
		
		private File file;
		
		public void setInputFile(File file) {
			this.file = file;
		}
		
		public void generate() {
		}
		
		public void addGenerator(DocumentGenerator generator) {
			
		}
		
		public void reset() {
			
		}
		
		public File getFile() {
			return file;
		}
	};
	
	@Test
	public void testClickGoButtonRunsIt() {
		
		gui.path.setText("src");
		gui.goButton.setEnabled(true);
		gui.goButton.doClick();
		
		assertEquals(GuiTestUtil.selectedFile, gen.getFile());
	}
	
	@Test
	public void testGoButtonDisabledIfFileDoesNotExist() {
		gui.path.setText("non-existent-file");
		assertFalse(gui.goButton.isEnabled());
	}
	
	@Test
	public void testEnteringPathFreehandEnablesGoButtonAndMakesItDefault() throws IOException, InterruptedException {
		gui.path.setText(GuiTestUtil.selectedFile.getCanonicalPath());
		assertTrue(gui.goButton.isEnabled());
		assertTrue(gui.goButton.isDefaultButton());
	}
	
	@Test
	public void testConfiguredDocumentGeneratorIsAddedToGenerators() throws IOException, InterruptedException {
		MockDocumentGenerator testDocumentGenerator = new MockDocumentGenerator();
		JLabel guiComponent = new JLabel();
		MockDocumentGeneratorGui generatorGui = new MockDocumentGeneratorGui(testDocumentGenerator, guiComponent);
		gui.addDocumentGeneratorGui(generatorGui);
		assertTrue(guiComponent.isVisible());
		gui.goButton.setEnabled(true);
		gui.goButton.doClick();
		assertTrue(generatorGui.createDocumentGeneratorWasCalled());
	}
	
	@Test
	public void testUnConfiguredDocumentGeneratorIsNotAddedToGenerators() throws IOException, InterruptedException {
		MockDocumentGenerator testDocumentGenerator = new MockDocumentGenerator();
		JLabel guiComponent = new JLabel();
		MockDocumentGeneratorGui generatorGui = new MockDocumentGeneratorGui(testDocumentGenerator, guiComponent) {
			
			public boolean isConfigured() {
				return false;
			}
		};
		gui.addDocumentGeneratorGui(generatorGui);
		gui.goButton.setEnabled(true);
		gui.goButton.doClick();
		assertFalse(generatorGui.createDocumentGeneratorWasCalled());
	}
}
