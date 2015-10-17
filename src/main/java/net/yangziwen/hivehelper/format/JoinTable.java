package net.yangziwen.hivehelper.format;

import java.util.List;

import com.google.common.collect.Lists;

public class JoinTable implements Table {
	
	private Keyword joinType;
	
	private Table baseTable;
	
	private List<String> joinOnList = Lists.newArrayList();
	
	private int end;

	@Override
	public String table() {
		return null;	// TODO
	}

	@Override
	public String alias() {
		return baseTable.alias();
	}

	@Override
	public int end() {
		return end;
	}
	
	public JoinTable joinType(Keyword joinType) {
		this.joinType = joinType;
		return this;
	}
	
	public Keyword joinType() {
		return joinType;
	}
	
	public JoinTable addJoinOns(List<String> joinOnList, int endPos) {
		this.joinOnList = joinOnList;
		end = endPos;
		return this;
	}
	
	public List<String> joinOnList() {
		return joinOnList;
	}
	
	public Table baseTable() {
		return baseTable;
	}
	
	public JoinTable baseTable(Table baseTable) {
		this.baseTable = baseTable;
		return this;
	}

}
