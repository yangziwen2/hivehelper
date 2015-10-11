package net.yangziwen.hivehelper.format;

import java.util.List;

import com.google.common.collect.Lists;

public class JoinTable implements Table {
	
	private Keyword joinType;
	
	private Table baseTable;
	
	private List<String> joinOnList = Lists.newArrayList();

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
		return 0;
	}
	
	public JoinTable joinType(Keyword joinType) {
		this.joinType = joinType;
		return this;
	}
	
	public Keyword joinType() {
		return joinType;
	}
	
	public JoinTable addJoinOns(List<String> joinOnList) {
		this.joinOnList = joinOnList;
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
