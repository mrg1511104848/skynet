package org.skynet.frame.util.excel;

import java.util.List;

public class ExcelBean {
	
	public ExcelBean(List<String> cells, List<List<String>> values, String name) {
		super();
		this.cells = cells;
		this.values = values;
		this.name = name;
	}
	private List<String> cells;
	private List<List<String>> values;
	private String name;
	public List<String> getCells() {
		return cells;
	}
	public void setCells(List<String> cells) {
		this.cells = cells;
	}
	public List<List<String>> getValues() {
		return values;
	}
	public void setValues(List<List<String>> values) {
		this.values = values;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
