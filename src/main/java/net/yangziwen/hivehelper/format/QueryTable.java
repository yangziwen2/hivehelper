package net.yangziwen.hivehelper.format;

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

}
