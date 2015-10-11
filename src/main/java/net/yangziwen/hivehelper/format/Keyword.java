package net.yangziwen.hivehelper.format;

import org.apache.commons.lang3.StringUtils;

public class Keyword {
	
	public Keyword() {}
	
	public Keyword(String name, int start, int end) {
		this.name(name).start(start).end(end);
	}

	private String name;
	
	private int start;
	
	private int end;
	
	public String name() {
		return name;
	}
	
	public Keyword name(String name) {
		this.name = name;
		return this;
	}
	
	public int start() {
		return start;
	}
	
	public Keyword start(int start) {
		this.start = start;
		return this;
	}
	
	public int end() {
		return end;
	}
	
	public Keyword end(int end) {
		this.end = end;
		return this;
	}
	
	public boolean contains(String keyword) {
		if(StringUtils.isBlank(keyword) || StringUtils.isBlank(name())) {
			return false;
		}
		return name().toLowerCase().contains(keyword.toLowerCase());
	}
	
	public boolean is(String keyword) {
		if(StringUtils.isBlank(keyword) || StringUtils.isBlank(name())) {
			return false;
		}
		return keyword.equalsIgnoreCase(name());
	}
	
	@Override
	public String toString() {
		return String.format("%s [%d, %d]", name(), start(), end());
	}
	
}
