package net.yangziwen.hivehelper.format;

import java.io.StringWriter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;

public class Query {
	
	protected List<String> selectList = Lists.newArrayList();
	
	protected List<Table> tableList = Lists.newArrayList();
	
	protected List<String> whereList = Lists.newArrayList();

	protected List<String> groupByList = Lists.newArrayList();
	
	protected int startPos;
	
	protected int endPos;
	
	public int start() {
		return startPos;
	}
	
	public Query start(int startPos) {
		this.startPos = startPos;
		return this;
	}
	
	public int end() {
		return endPos;
	}

	public Query end(int endPos) {
		this.endPos = endPos;
		return this;
	}

	public List<String> selectList() {
		return selectList;
	}
	
	public List<Table> tableList() {
		return tableList;
	}
	
	public List<String> whereList() {
		return whereList;
	}
	
	public List<String> groupByList() {
		return groupByList;
	}
	
	public Query addSelects(List<String> selectList) {
		this.selectList.addAll(selectList);
		return this;
	}
	
	public Query addTables(List<Table> tableList) {
		this.tableList.addAll(tableList);
		return this;
	}
	
	public Query addWheres(List<String> whereList) {
		this.whereList.addAll(whereList);
		return this;
	}
	
	public Query addGroupBys(List<String> groupByList) {
		this.groupByList.addAll(groupByList);
		return this;
	}
	
	@Override
	public String toString() {
		return toString("    ");
	}
	
	public String toString(String indent) {
		return format(indent, new StringWriter()).toString();
	}
	
	public StringWriter format(String indent, StringWriter writer) {
		return format(indent, 0, writer);
	}
	
	public StringWriter format(String indent, int nestedDepth, StringWriter writer) {
		this.formatSelect(indent, nestedDepth, writer)
			.formatFrom(indent, nestedDepth, writer)
			.formatWhere(indent, nestedDepth, writer)
			.formatGroupBy(indent, nestedDepth, writer)
		;
		return writer;
	}
	
	private Query formatSelect(String indent, int nestedDepth, StringWriter writer) {
		String sep = chooseSeprator(selectList, indent, nestedDepth + 1);
		writer.append("SELECT ").append(selectList.get(0));
		for(int i = 1; i < selectList.size(); i++) {
			writer.append(",").append(sep)
				.append(selectList.get(i));
		}
		return this;
	}
	
	private Query formatFrom(String indent, int nestedDepth, StringWriter writer) {
		writer.append("\n").append(StringUtils.repeat(indent, nestedDepth)).append("FROM ");
		tableList.get(0).format(indent, nestedDepth + 1, writer);
		for(int i = 1; i < tableList.size(); i++) {
			//writer.append("\n").append(baseIndent).append(indent);
			tableList.get(i).format(indent, nestedDepth + 1, writer);
		}
		return this;
	}
	
	private Query formatWhere(String indent, int nestedDepth, StringWriter writer) {
		if(CollectionUtils.isEmpty(whereList)) {
			return this;
		}
		String sep = chooseSeprator(whereList, indent, nestedDepth + 1);
		writer.append("\n").append(StringUtils.repeat(indent, nestedDepth))
			.append("WHERE ").append(whereList.get(0));
		for(int i = 1; i < whereList.size(); i++) {
			writer.append(sep).append("AND ").append(whereList.get(i));
		}
		return this;
	}
	
	private Query formatGroupBy(String indent, int nestedDepth, StringWriter writer) {
		if(CollectionUtils.isEmpty(groupByList)) {
			return this;
		}
		writer.append("\n").append(StringUtils.repeat(indent, nestedDepth))
			.append("GROUP BY ")
			.append(StringUtils.join(groupByList.toArray(), ", "))
		;
		return this;
	}
	
	
	private static String chooseSeprator(List<String> list, String indent, int nestedDepth) {
		String sep = "\n" + StringUtils.repeat(indent, nestedDepth);
		if(list.size() > 5) {
			return sep;
		}
		int totalLen = 0;
		for(String select: list) {
			totalLen += select.length();
		}
		if(totalLen < 80) {
			sep = " ";
		}
		return sep;
	}
	
}
