package net.yangziwen.hivehelper.format;

import org.apache.commons.lang3.StringUtils;

public class SimpleTable implements Table {
	
	private String table;
	
	private String alias;
	
	private int endPos = 0;
	
	public SimpleTable(String table, String alias, int endPos) {
		this.table = table;
		this.alias = alias;
		this.endPos = endPos;
	}

	@Override
	public String table() {
		return table;
	}
	
	public SimpleTable table(String table) {
		this.table = table;
		return this;
	}

	@Override
	public String alias() {
		return alias;
	}
	
	public SimpleTable alias(String alias) {
		this.alias = alias;
		return this;
	}
	
	public int end() {
		return endPos;
	}
	
	public SimpleTable end(int endPos) {
		this.endPos = endPos;
		return this;
	}
	
	@Override
	public String toString() {
		return table + (StringUtils.isNotBlank(alias)? " " + alias: "");
	}

}
