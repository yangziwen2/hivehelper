package net.yangziwen.hivehelper.format;

import java.util.List;

import com.google.common.collect.Lists;

public class JoinTable implements Table {
	
	private Keyword joinType;
	
	private Table baseTable;
	
	private List<String> joinOnList = Lists.newArrayList();
	
	private int startPos;
	
	private int endPos;

	@Override
	public String table() {
		return "JoinTable[" + baseTable.table() + "]";
	}

	@Override
	public String alias() {
		return baseTable.alias();
	}

	public int start() {
		return startPos;
	}
	
	public JoinTable start(int startPos) {
		this.startPos = startPos;
		return this;
	}
	
	@Override
	public int end() {
		return endPos;
	}
	
	public JoinTable end(int endPos) {
		this.endPos = endPos;
		return this;
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
