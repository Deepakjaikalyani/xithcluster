package com.thoughtworks.testdox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

public class NamePrettifierTest {
	
	private NamePrettifier namePrettifier;
	
	@Before
	public void setUp() throws Exception {
		namePrettifier = new NamePrettifier();
	}
	
	@Test
	public void testTitleHasSensibleDefaults() {
		assertEquals("Foo", namePrettifier.prettifyTestClass("FooTest"));
		assertEquals("Foo", namePrettifier.prettifyTestClass("TestFoo"));
		assertEquals("Foo", namePrettifier.prettifyTestClass("TestFooTest"));
	}
	
	@Test
	public void testCaterForUserDefinedSuffix() {
		namePrettifier.setSuffix("TestCase");
		namePrettifier.setPrefix(null);
		assertEquals("Foo", namePrettifier.prettifyTestClass("FooTestCase"));
		assertEquals("TestFoo", namePrettifier.prettifyTestClass("TestFoo"));
		assertEquals("FooTest", namePrettifier.prettifyTestClass("FooTest"));
	}
	
	@Test
	public void testCaterForUserDefinedPrefix() {
		namePrettifier.setSuffix(null);
		namePrettifier.setPrefix("XXX");
		assertEquals("Foo", namePrettifier.prettifyTestClass("XXXFoo"));
		assertEquals("TestXXX", namePrettifier.prettifyTestClass("TestXXX"));
		assertEquals("XXX", namePrettifier.prettifyTestClass("XXXXXX"));
	}
	
	@Test
	public void testTestNameIsConvertedToASentence() {
		assertEquals("This is a test", namePrettifier.prettifyTestMethod("testThisIsATest"));
		assertEquals("database_column_spec is set correctly", namePrettifier.prettifyTestMethod("testdatabase_column_specIsSetCorrectly"));
	}
	
	@Test
	public void testIsATestIsFalseForNonTestMethods() {
		assertFalse(namePrettifier.isATestMethod("setUp"));
		assertFalse(namePrettifier.isATestMethod("tearDown"));
		assertFalse(namePrettifier.isATestMethod("foo"));
	}
	
}
