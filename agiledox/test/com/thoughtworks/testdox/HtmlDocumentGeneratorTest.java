package com.thoughtworks.testdox;

import static org.junit.Assert.fail;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.Before;
import org.junit.Test;

public class HtmlDocumentGeneratorTest {
	
	private StringWriter out;
	
	private HtmlDocumentGenerator gen;
	
	@Before
	public void setUp() throws Exception {
		out = new StringWriter();
		gen = new HtmlDocumentGenerator(new PrintWriter(out));
	}
	
	@Test
	public void testShowsHeadingForClass() {
		gen.startClass("Foo");
		assertMatches("<h2>Foo</h2>");
	}
	
	@Test
	public void testUnorderedListForMethods() {
		gen.startClass("Foo");
		gen.onTest("ATest");
		gen.endClass("Foo");
		assertMatches("<ul>");
		assertMatches("<li>ATest</li>");
		assertMatches("</ul>");
	}
	
	private void assertMatches(String pattern) {
		String result = out.toString();
		if (result.indexOf(pattern) == -1) {
			fail("Expected " + pattern + " but got " + result);
		}
	}
	
}
