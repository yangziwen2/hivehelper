package net.yangziwen.hivehelper.format;

import java.util.List;

import com.google.common.collect.Lists;

public class UnionTable implements Table {
	
	private List<Table> unionTables = Lists.newArrayList();

	@Override
	public String table() {
		return null;
	}

	@Override
	public String alias() {
		return null;
	}

	@Override
	public int end() {
		Table lastTable = unionTables.get(unionTables.size() - 1);
		return lastTable.end();
	}
	
	public List<Table> unionTableList() {
		return unionTables;
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
