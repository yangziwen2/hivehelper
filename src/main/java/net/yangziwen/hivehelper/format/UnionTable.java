package net.yangziwen.hivehelper.format;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class UnionTable implements Table {
	
	private List<Table> unionTables = Lists.newArrayList();	// 按道理，这些table都应该是QueryTable
	
	private String alias;
	
	private int startPos;
	
	private int endPos;

	@Override
	public String table() {
		Collection<String> tableNames = Collections2.transform(unionTables, new Function<Table, String>() {
			@Override public String apply(Table table) {
				return table.table();
			}
		});
		return "UnionTable[" + StringUtils.join(tableNames.toArray()) + "]";
	}

	@Override
	public String alias() {
		return alias;
	}
	
	public UnionTable alias(String alias) {
		this.alias = alias;
		return this;
	}
	
	public UnionTable start(int startPos) {
		this.startPos = startPos;
		return this;
	}
	
	public int start() {
		return startPos;
	}
	
	public UnionTable end(int endPos) {
		this.endPos = endPos;
		return this;
	}

	@Override
	public int end() {
		return endPos;
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

	@Override
	public StringBuilder format(String indent, String baseIndent, StringBuilder buff) {
		StringBuilder b = new StringBuilder();
		unionTables.get(0).format(indent, baseIndent, b);
		int idx = b.lastIndexOf(")") - 1;
		if(idx < 0) {
			idx = b.length();
		}
		buff.append(b.substring(0, idx));
		
		for(int i = 1; i < unionTables.size(); i++) {
			buff.append("\n").append(baseIndent).append("UNION ALL")
				.append("\n").append(baseIndent);
			b = new StringBuilder();
			unionTables.get(i).format(indent, baseIndent, b);
			idx = b.indexOf("(") + 1;
			buff.append(b.substring(idx));
		}
		buff.append("\n");
		return buff;
	}

}
