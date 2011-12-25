package com.thoughtworks.testdox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockDocumentGenerator implements DocumentGenerator {
	
	Map<String, List<String>> descriptions = new HashMap<String, List<String>>();
	
	List<String> currentList;
	
	public void startClass(String className) {
		currentList = new ArrayList<String>();
		descriptions.put(className, currentList);
	}
	
	public void onTest(String name) {
		currentList.add(name);
	}
	
	public void endClass(String name) {
	}
	
	public List<String> getTestDescriptions(String className) {
		return descriptions.get(className);
	}
	
	public void startRun() {
	}
	
	public void endRun() {
	}
	
}
