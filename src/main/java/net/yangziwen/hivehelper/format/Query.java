package net.yangziwen.hivehelper.format;

import java.util.List;

import com.google.common.collect.Lists;

public class Query {
	
	protected List<String> selectList = Lists.newArrayList();
	
	protected List<Table> tableList = Lists.newArrayList();
	
	protected List<String> whereList = Lists.newArrayList();

	protected List<String> groupByList = Lists.newArrayList();
	
	protected int endPos;
	
	public int endPos() {
		return endPos;
	}

	public Query endPos(int endPos) {
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
	
}