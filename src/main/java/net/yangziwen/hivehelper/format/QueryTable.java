package net.yangziwen.hivehelper.format;

import org.apache.commons.lang3.StringUtils;

public class QueryTable implements Table {
	
	private Query query;
	
	private String alias;
	
	private int startPos;
	
	private int endPos;
	
	public QueryTable(Query query) {
		this.query = query;
		endPos = query.end();
	}
	
	public Query query() {
		return query;
	}

	@Override
	public String table() {
		return "QueryTable[" + query + "]";
	}

	@Override
	public String alias() {
		return alias;
	}
	
	public QueryTable alias(String alias) {
		this.alias = alias;
		return this;
	}
	
	public QueryTable start(int startPos) {
		this.startPos = startPos;
		return this;
	}
	
	public int start() {
		return this.startPos;
	}
	
	public QueryTable end(int endPos) {
		this.endPos = endPos;
		return this;
	}

	@Override
	public int end() {
		return endPos;
	}

	@Override
	public StringBuilder format(String indent, String baseIndent, StringBuilder buff) {
		buff.append("(").append("\n").append(baseIndent);
		query.format(indent, baseIndent, buff);
		buff.append("\n").append(StringUtils.replaceOnce(baseIndent, indent, "")).append(")");
		if(StringUtils.isNotBlank(alias())) {
			buff.append(" ").append(alias());
		}
		return buff;
	}

}
