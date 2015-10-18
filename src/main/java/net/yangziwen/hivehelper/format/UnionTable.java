package net.yangziwen.hivehelper.format;

import java.io.StringWriter;
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
	public StringWriter format(String indent, String baseIndent, StringWriter buff) {
		StringWriter sw = new StringWriter();
		unionTables.get(0).format(indent, baseIndent, sw);
		int idx = sw.getBuffer().lastIndexOf(")") - 1;
		if(idx < 0) {
			idx = sw.getBuffer().length();
		}
		buff.append(sw.getBuffer().substring(0, idx));
		
		for(int i = 1; i < unionTables.size(); i++) {
			buff.append("\n").append(baseIndent).append("UNION ALL")
				.append("\n").append(baseIndent);
			sw = new StringWriter();
			unionTables.get(i).format(indent, baseIndent, sw);
			idx = sw.getBuffer().indexOf("(") + 1;
			buff.append(sw.getBuffer().substring(idx));
		}
		return buff.append(" ").append(alias()).append("\n");
	}

}
