package net.yangziwen.hivehelper.format;

import java.util.List;

import com.google.common.collect.Lists;

public class UnionTable implements Table {
	
	private List<Table> unionTables = Lists.newArrayList();	// 按道理，这些table都应该是QueryTable
	
	private String alias;
	
	private int end;

	@Override
	public String table() {
		return null;
	}

	@Override
	public String alias() {
		return alias;
	}
	
	public UnionTable alias(String alias) {
		this.alias = alias;
		return this;
	}
	
	public UnionTable end(int endPos) {
		this.end = endPos;
		return this;
	}

	@Override
	public int end() {
//		Table lastTable = unionTables.get(unionTables.size() - 1);
//		return lastTable.end();
		return end;
	}
	
	public List<Table> unionTableList() {
		return unionTables;
	}
	
	public Table lastTable() {
		int size = unionTables.size();
		return unionTables.get(size - 1);
	}
	
	public UnionTable addUnionTable(Table table) {
		if(table instanceof UnionTable) {
			UnionTable another = (UnionTable) table;
			unionTables.addAll(another.unionTables);
		} else {
			unionTables.add(table);
		}
		return this;
	}

}
