package com.thoughtworks.testdox;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
		DocumentGeneratorTest.class,
		GuiTest.class,
		HtmlDocumentGeneratorTest.class,
		MainTest.class,
		NamePrettifierTest.class,
		SaveAsGuiTest.class
})
public class AllTests {
	
}
