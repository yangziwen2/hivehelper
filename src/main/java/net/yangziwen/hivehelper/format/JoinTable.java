package net.yangziwen.hivehelper.format;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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

	@Override
	public StringBuilder format(String indent, String baseIndent, StringBuilder buff) {
		baseIndent = StringUtils.replaceOnce(baseIndent, indent, "");
		buff.append("\n").append(baseIndent)
			.append(joinType.name()).append(" ");
		baseTable.format(indent, baseIndent + indent, buff);
		if(CollectionUtils.isNotEmpty(joinOnList)) {
			buff.append("\n").append(baseIndent)
				.append("ON").append(" ").append(joinOnList.get(0));
			for(int i = 1; i < joinOnList.size(); i++) {
				buff.append(" AND ").append(joinOnList.get(i));
			}
		}
		return buff;
	}

}
