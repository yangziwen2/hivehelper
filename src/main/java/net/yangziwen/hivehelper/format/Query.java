package net.yangziwen.hivehelper.format;

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
		return format(indent, new StringBuilder()).toString();
	}
	
	public StringBuilder format(String indent, StringBuilder buff) {
		return format(indent, "", buff);
	}
	
	public StringBuilder format(String indent, String baseIndent, StringBuilder buff) {
		this.formatSelect(indent, baseIndent, buff)
			.formatFrom(indent, baseIndent, buff)
			.formatWhere(indent, baseIndent, buff)
			.formatGroupBy(indent, baseIndent, buff)
		;
		return buff;
	}
	
	private Query formatSelect(String indent, String baseIndent, StringBuilder buff) {
		buff.append("SELECT ").append(selectList.get(0));
		for(int i = 1; i < selectList.size(); i++) {
			buff.append(",").append("\n").append(baseIndent).append(indent)
				.append(selectList.get(i));
		}
		return this;
	}
	
	private Query formatFrom(String indent, String baseIndent, StringBuilder buff) {
		buff.append("\n").append(baseIndent).append("FROM ");
		tableList.get(0).format(indent, baseIndent + indent, buff);
		for(int i = 1; i < tableList.size(); i++) {
			buff.append("\n").append(baseIndent).append(indent);
			tableList.get(i).format(indent, baseIndent + indent, buff);
		}
		return this;
	}
	
	private Query formatWhere(String indent, String baseIndent, StringBuilder buff) {
		if(CollectionUtils.isEmpty(whereList)) {
			return this;
		}
		buff.append("\n").append(baseIndent)
			.append("WHERE ").append(whereList.get(0));
		for(int i = 1; i < whereList.size(); i++) {
			buff.append("\n").append(baseIndent).append(indent)
				.append("AND ").append(whereList.get(i));
		}
		return this;
	}
	
	private Query formatGroupBy(String indent, String baseIndent, StringBuilder buff) {
		if(CollectionUtils.isEmpty(groupByList)) {
			return this;
		}
		buff.append("\n").append(baseIndent)
			.append("GROUP BY ")
			.append(StringUtils.join(groupByList.toArray(), ", "))
		;
		return this;
	}
	
}
