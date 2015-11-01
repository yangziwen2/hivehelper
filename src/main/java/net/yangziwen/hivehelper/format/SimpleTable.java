package net.yangziwen.hivehelper.format;

import java.io.StringWriter;

import org.apache.commons.lang3.StringUtils;

public class SimpleTable extends AbstractTable<SimpleTable> implements Table<SimpleTable> {
	
	private String table;
	
	public SimpleTable(String table, String alias, int startPos, int endPos) {
		this.table = table;
		this.alias = alias;
		this.startPos = startPos;
		this.endPos = endPos;
	}

	@Override
	public String table() {
		return table;
	}
	
	@Override
	public String toString() {
		return table + (StringUtils.isNotBlank(alias)? " " + alias: "");
	}

	@Override
	public StringWriter format(String indent, int nestedDepth, StringWriter writer) {
		writer.append(table());
		if(StringUtils.isNotBlank(alias())) {
			writer.append(" ").append(alias());
		}
		if(headComment != null) {
			writer.append("  ").append(headComment().content());
		}
		return writer;
	}

}
