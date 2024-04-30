package com.programs.main;

public class IndexValuePair implements Comparable<IndexValuePair> {
	private String index;
	private String value;
	
	public IndexValuePair(String index, String value) {
		this.index = index;
		this.value = value;
	}

	public String getIndex() {
		return index;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int compareTo(IndexValuePair obj) {
		return this.index.compareTo(obj.getIndex());
	}

	@Override
	public String toString() {
		return "IndexValuePair [index=" + index + ", value=" + value + "]";
	}
}
