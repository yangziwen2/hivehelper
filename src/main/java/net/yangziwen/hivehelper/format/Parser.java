package net.yangziwen.hivehelper.format;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

public class Parser {
	
	public static final Pattern KEY_WORD_PATTERN = Pattern.compile("(?<=^|[^\\w\\d])(select|from|join|inner\\s+?join|left\\s+?outer\\s+?join|on|union\\s+all|where|group\\s+?by|having)[^\\w\\d]", Pattern.CASE_INSENSITIVE);
	
	public static Query parseQuery(String sql, int start) {
		Keyword selectKeyword = findKeyWord(sql, start);
		Keyword fromKeyword = findKeyWord(sql, selectKeyword.end() + 1);
		
		List<Table> tableList = parseTables(sql, fromKeyword.end() + 1);
		Table lastTable = tableList.get(tableList.size() - 1);
		
		Keyword nextKeyword = findKeyWord(sql, lastTable.end() + 1);
		Keyword whereKeyword = null;
		Keyword groupByKeyword = null;
		
		int endPos = findEndPos(sql, lastTable.end());
		
		if(nextKeyword.is("where") && nextKeyword.end() < endPos) {
			whereKeyword = nextKeyword;
			nextKeyword = findKeyWord(sql, whereKeyword.end() + 1);
		}
		
		if(nextKeyword.is("group by") && nextKeyword.end() < endPos) {
			groupByKeyword = nextKeyword;
			nextKeyword = findKeyWord(sql, groupByKeyword.end() + 1);
		}
		if(nextKeyword.is("union all") && nextKeyword.end() < endPos) {
			endPos = nextKeyword.start() - 1;
		}
		
		List<String> selectList = parseClauseList(sql, selectKeyword.end() + 1, fromKeyword.start());
		List<String> whereList = Collections.emptyList();
		List<String> groupByList = Collections.emptyList();
		
		if(whereKeyword != null) {
			whereList = splitByAnd(sql, whereKeyword.end() + 1, groupByKeyword != null? groupByKeyword.start() - 1: endPos);
		}
		if(groupByKeyword != null) {
			groupByList = parseClauseList(sql, groupByKeyword.end() + 1, endPos);
		}
		
		return new Query()
			.addSelects(selectList)
			.addTables(tableList)
			.addWheres(whereList)
			.addGroupBys(groupByList)
			.start(selectKeyword.start())
			.end(endPos - 1)
		;
	}
	
	public static List<String> parseClauseList(String sql, int start, int end) {
		String substring = sql.substring(start, end);
		List<String> list = Lists.newArrayList();
		int pos = 0;
		int bracketCnt = 0;
		boolean quoteFlag = false;
		boolean doubleQuoteFlag = false;
		for(int i = 0, len = substring.length(); i < len; i++) {
			char c = substring.charAt(i);
			if(c == '(') {
				bracketCnt ++;
			} 
			else if (c == ')') {
				bracketCnt --;
			} 
			else if (c == '\'') {
				quoteFlag = !quoteFlag;
			}
			else if (c == '"') {
				doubleQuoteFlag = !doubleQuoteFlag;
			}
			else if (c == ',' && bracketCnt == 0 && !quoteFlag && !doubleQuoteFlag) {
				list.add(substring.substring(pos, i).trim());
				pos = i + 1;
			}
		}
		list.add(substring.substring(pos, substring.length()).trim());
		return list;
	}
	
	public static List<String> splitBy(String str, String regex, int start, int end) {
		String substring = str.substring(start, end);
		String[] arr = substring.split(regex);
		List<String> list = Lists.newArrayList();
		for(String s: arr) {
			list.add(s.trim());
		}
		return list;
	}
	
	public static List<String> splitByAnd(String str, int start, int end) {
		return splitBy(str, "[aA][nN][dD]", start, end);
	}
	
	public static int findEndPos(String sql, int start) {
		int pos = findEndBracket(sql, start);
		if(pos < 0) {
			pos = sql.length();
		}
		return pos;
	}
	
	public static int findEndBracket(String sql, int start) {
		return findEndBracket(sql, start, sql.length());
	}
	
	public static int findEndBracket(String sql, int start, int end) {
		int cnt = 0;
		for(int i = start; i < end; i ++) {
			char c = sql.charAt(i);
			if(c == '(') {
				cnt ++;
			} else if(c == ')') {
				cnt --;
			}
			if(cnt < 0) {
				return i;
			}
		}
		return -1;
	}
	
	public static List<Table> parseTables(String sql, int start) {
		List<Table> tables = Lists.newArrayList();
		Table table = parseTable(sql, start);
		tables.add(table);
		Keyword nextKeyword = findKeyWord(sql, table.end() + 1);
		while(nextKeyword.contains("join")) {
			table = parseTable(sql, table.end() + 1);
			if(table != null) {
				tables.add(table);
			}
			nextKeyword = findKeyWord(sql, table.end());
		}
		return tables;
	}
	
	private static Table parseTable(String sql, int start) {
		Keyword nextKeyword = findKeyWord(sql, start);
		Keyword joinKeyword = null;
		if(nextKeyword.contains("join")) {
			joinKeyword = nextKeyword;
			start = joinKeyword.end() + 1;
		}
		int i = start;
		char c = sql.charAt(i);
		while (Character.isWhitespace(c)) {
			i++;
			c = sql.charAt(i);
		}
		Table table = null;
		if(c == '(') {
			table = parseQueryTable(sql, i + 1);
		} else {
			table = parseSimpleTable(sql, i);
		}
		int curPos = table.end();
		
		// 处理join on的情形
		if(joinKeyword != null) {
			JoinTable joinTable = new JoinTable()
				.baseTable(table)
				.joinType(joinKeyword);	
			nextKeyword = findKeyWord(sql, curPos);
			if(nextKeyword.is("on")) {
				Keyword onKeyword = nextKeyword;
				nextKeyword = findKeyWord(sql, onKeyword.end() + 1);
				int endPos = findEndBracket(sql, start, nextKeyword.start());
				if(endPos == -1) {
					endPos = nextKeyword.start() - 1;
				}
				joinTable.addJoinOns(splitByAnd(sql, onKeyword.end(), endPos))
					.start(joinKeyword.start()).end(endPos);
			}
			table = joinTable;
		}
		return table;
	}
	
	public static Table parseQueryTable(String sql, int start) {
		Query query = parseQuery(sql, start);
		Keyword nextKeyword = findKeyWord(sql, query.end() + 1);
		if(!nextKeyword.is("union all")) {
			int endPos = findEndBracket(sql, query.end() + 1, nextKeyword.start());
			String alias = sql.substring(endPos + 1, nextKeyword.start()).trim();
			return new QueryTable(query).alias(alias)
					.start(start).end(nextKeyword.start() - 1);
		}
		// 处理union all的情形
		Keyword unionKeyword = nextKeyword;
		UnionTable unionTable = new UnionTable();
		QueryTable table = new QueryTable(query)
				.start(start).end(unionKeyword.start() - 1);
		unionTable.addUnionTable(table);
		while(nextKeyword.is("union all")) {
			unionKeyword = nextKeyword;
			query = parseQuery(sql, unionKeyword.end() + 1);
			table = new QueryTable(query).start(unionKeyword.end() + 1).end(query.end());
			unionTable.addUnionTable(table);
			nextKeyword = findKeyWord(sql, table.end());
		}
		int endPos = findEndBracket(sql, unionTable.lastTable().end() + 1, nextKeyword.start());
		String alias = sql.substring(endPos + 1, nextKeyword.start()).trim();
		return unionTable.alias(alias).start(start).end(nextKeyword.start() - 1);
	}
	
	public static SimpleTable parseSimpleTable(String sql, int start) {
		Keyword nextKeyword = findKeyWord(sql, start);
		if(nextKeyword.is("null")) {
			return null;
		}
		int end = nextKeyword != null? nextKeyword.start() - 1: sql.length();
		String str = sql.substring(start, end).trim();
		String[] arr = str.split("\\s");
		String tableName = arr[0];
		String alias = arr.length >= 2? arr[1]: "";
		if (arr.length >= 3 && "as".equalsIgnoreCase(alias)) {
			alias = arr[2];
		}
		return new SimpleTable(tableName, alias, start, end);
	}
	
	public static Keyword findKeyWord(String sql, int start) {
		Matcher matcher = KEY_WORD_PATTERN.matcher(sql);
		if(matcher.find(start)) {
			return new Keyword(matcher.group(1), matcher.start(1), matcher.end(1));
		}
		return Keyword.returnNull(sql);
	}
	
}
