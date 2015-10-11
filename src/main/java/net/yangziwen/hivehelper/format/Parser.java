package net.yangziwen.hivehelper.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class Parser {
	
	public static final Pattern KEY_WORDS = Pattern.compile("(?<=^|[^\\w\\d])(select|from|join|inner\\s+?join|left\\s+?outer\\s+?join|on|union\\s+all|where|group\\s+?by|having)[^\\w\\d]", 
			Pattern.CASE_INSENSITIVE);
	
	public static Query parseQuery(String sql, int start) {
		System.out.println("start: " + start);
		Keyword selectKeyword = findKeyWord(sql, start);
		Keyword fromKeyword = findKeyWord(sql, selectKeyword.end() + 1);
		
		List<Table> tableList = parseTables(sql, fromKeyword.end() + 1);
		Table lastTable = tableList.get(tableList.size() - 1);
		
		Keyword nextKeyword = findKeyWord(sql, lastTable.end() + 1);
		Keyword whereKeyword = null;
		Keyword groupByKeyword = null;
		
		int endPos = findEndPos(sql, lastTable.end());
		
		System.out.println("endPos: " + endPos);
		
		if(nextKeyword != null && nextKeyword.is("where") && nextKeyword.end() < endPos) {
			whereKeyword = nextKeyword;
			nextKeyword = findKeyWord(sql, whereKeyword.end() + 1);
		}
		
		if(nextKeyword != null && nextKeyword.is("group by") && nextKeyword.end() < endPos) {
			groupByKeyword = nextKeyword;
			nextKeyword = findKeyWord(sql, groupByKeyword.end() + 1);
		}
		if(nextKeyword != null && nextKeyword.is("union all") && nextKeyword.end() < endPos) {
			endPos = nextKeyword.start() - 1;
		}
		
		List<String> selectList = parseClauseList(sql, selectKeyword.end() + 1, fromKeyword.start());
		List<String> whereList = Collections.emptyList();
		List<String> groupByList = Collections.emptyList();
		
		if(whereKeyword != null) {
			whereList = splitByAnd(sql, whereKeyword.end() + 1, groupByKeyword != null? groupByKeyword.start() - 1: endPos);
		}
		if(groupByKeyword != null) {
			groupByList = splitByAnd(sql, groupByKeyword.end() + 1, endPos);
		}
		
		return new Query()
			.addSelects(selectList)
			.addTables(tableList)
			.addWheres(whereList)
			.addGroupBys(groupByList)
			.end(endPos - 1)
		;
	}
	
	public static List<String> parseClauseList(String sql, int start, int end) {
		String substring = sql.substring(start, end);
		List<String> list = Lists.newArrayList();
		int pos = 0;
		int cnt = 0;
		for(int i = 0, len = substring.length(); i < len; i++) {
			char c = substring.charAt(i);
			if(c == '(') {
				cnt ++;
			} 
			else if (c == ')') {
				cnt --;
			} 
			else if (c == ',' && cnt == 0) {
				list.add(substring.substring(pos, i).trim());
				pos = i + 1;
			}
		}
		list.add(substring.substring(pos, substring.length()).trim());
		return list;
	}
	
	public static List<String> splitByAnd(String sql, int start, int end) {
		String substring = sql.substring(start, end);
		String[] arr = substring.split("and|AND");
		List<String> list = Lists.newArrayList();
		for(String str: arr) {
			list.add(str.trim());
		}
		return list;
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
		while(nextKeyword != null && nextKeyword.contains("join")) {
			table = parseTable(sql, table.end() + 1);
			if(table != null) {
				tables.add(table);
			}
		}
		return tables;
	}
	
	private static Table parseTable(String sql, int start) {
		Keyword nextKeyword = findKeyWord(sql, start);
		Keyword joinKeyword = null;
		if(nextKeyword != null && nextKeyword.contains("join")) {
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
			table = parseSimpleTable(sql, i + 1);
		}
		int curPos = table.end();
		// 处理union all的情形
		nextKeyword = findKeyWord(sql, table.end() + 1);
		if(nextKeyword != null && nextKeyword.is("union all")) {
			Keyword unionKeyword = nextKeyword;
			int endPos = findEndPos(sql, table.end() + 1);
			if(endPos > unionKeyword.end()) {
				Table nextTable = parseTable(sql, unionKeyword.end());
				table = new UnionTable()
					.addUnionTable(table)
					.addUnionTable(nextTable);
			}
			curPos = table.end();
		}
		// 处理join on的情形
		if(joinKeyword != null) {
			JoinTable joinTable = new JoinTable()
				.baseTable(table)
				.joinType(joinKeyword);	
			nextKeyword = findKeyWord(sql, curPos);
			if(nextKeyword != null && nextKeyword.is("on")) {
				Keyword onKeyword = nextKeyword;
				nextKeyword = findKeyWord(sql, onKeyword.end() + 1);
				int endPos = findEndBracket(sql, start, nextKeyword.start());
				if(endPos == -1) {
					endPos = nextKeyword.start() - 1;
				}
				joinTable.addJoinOns(splitByAnd(sql, onKeyword.end(), endPos));
			}
			table = joinTable;
		}
		return table;
	}
	
	public static Table parseQueryTable(String sql, int start) {
		Query query = parseQuery(sql, start);
		Keyword nextKeyword = findKeyWord(sql, query.end() + 1);
		int endPos = findEndBracket(sql, query.end() + 1, nextKeyword.start());
		String alias = sql.substring(endPos + 1, nextKeyword.start()).trim();
		return new QueryTable(query).alias(alias).end(nextKeyword.start() - 1);
	}
	
	public static SimpleTable parseSimpleTable(String sql, int start) {
		Keyword nextKeyword = findKeyWord(sql, start);
		if(nextKeyword == null) {
			return null;
		}
		int end = nextKeyword != null? nextKeyword.start() - 1: sql.length();
		String str = sql.substring(start, end).trim();
		String[] arr = str.split("\\s");
		String tableName = arr[0];
		String alias = arr.length >= 2? arr[1]: "";
		return new SimpleTable(tableName, alias, end);
	}
	
	public static void main(String[] args) throws Exception {
		File sqlFile = new File("d:/calculation_dm_city_performance_month_v2.sql");
//		File sqlFile = new File("d:/test.sql");
		String sql = FileUtils.readFileToString(sqlFile);
		Query query = parseQuery(sql, 0);
		
		for(String select: query.selectList()) {
			System.out.println(select);
		}
		System.out.println(query.tableList());
		for(String where: query.whereList()) {
			System.out.println(where);
		}
	}
	
	public static void main1(String[] args) throws Exception {
//		File sqlFile = new File("d:/calculation_dm_city_performance_month_v2.sql");
		File sqlFile = new File("d:/calculation_dm_deal_data_day.sql");
		String sql = FileUtils.readFileToString(sqlFile);
//		sql = sql.replaceAll("\n", "");
//		System.out.println(sql);
//		System.out.println(sql.substring(170, 181));
		Keyword keyword = null;
		int start = 0;
		int cnt = 0;
		int depth = -1;
		Keyword lastKeyword = new Keyword();
		Stack<KeywordWrapper> joinStack = new Stack<KeywordWrapper>();
		while((keyword = findKeyWord(sql, start)) != null) {
			if(keyword.is("select") && !lastKeyword.contains("union")) {
				depth ++;
			} else if(keyword.contains("join")) {
				if(!lastKeyword.is("from") && !lastKeyword.is("on")) {
					 depth --;
				}
				joinStack.push(new KeywordWrapper(keyword, depth));
			} else if(keyword.is("on")) {
				depth = joinStack.pop().depth();
			} else if(keyword.is("group by")) {
				if(lastKeyword.is("group by")) {
					depth --;
				}
			}
			System.out.print(StringUtils.repeat("  ", depth));
			System.out.println(keyword);
			start = keyword.end();
			lastKeyword = keyword;
			cnt ++;
		}
		System.out.println(cnt);
	}
	
	public static void main2(String[] args) throws Exception {
		File sqlFile = new File("d:/calculation_dm_city_performance_month_v2.sql");
		BufferedReader reader = new BufferedReader(new FileReader(sqlFile));
		
		String line = "";
		while((line = reader.readLine()) != null) {
			int commentIdx = -1;
			while((commentIdx = line.lastIndexOf("--")) >= 0) {
				line = line.substring(0, commentIdx);
			}
			System.out.println(line);
//			Matcher matcher = KEY_WORDS.matcher(line);
//			int start = 0;
//			while(matcher.find(start)) {
//				System.out.println(matcher.group() + " " + matcher.start());
//				start = matcher.end();
//			}
			Keyword keyword = null;
			int start = 0;
			while((keyword = findKeyWord(line, start)) != null) {
				System.out.println(keyword);
				start = keyword.end();
			}
		}
		
		IOUtils.closeQuietly(reader);
	}
	
	public static Keyword findKeyWord(String sql, int start) {
		Matcher matcher = KEY_WORDS.matcher(sql);
		if(matcher.find(start)) {
			return new Keyword(matcher.group(1), matcher.start(1), matcher.end(1));
		}
		return null;
	}
	
	public static class KeywordWrapper {
		
		private Keyword keyword;
		
		private int depth;
		
		public KeywordWrapper(Keyword keyword, int depth) {
			this.keyword = keyword; 
			this.depth = depth;
		}
		
		public Keyword keyword() {
			return keyword;
		}
		
		public int depth() {
			return depth;
		}
	}
}
