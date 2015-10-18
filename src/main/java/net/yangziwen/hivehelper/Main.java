package net.yangziwen.hivehelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import net.yangziwen.hivehelper.format.Keyword;
import net.yangziwen.hivehelper.format.Parser;
import net.yangziwen.hivehelper.format.Query;
import net.yangziwen.hivehelper.format.QueryTable;
import net.yangziwen.hivehelper.format.Table;

public class Main {

	public static void main(String[] args) throws Exception {
		File sqlFile = new File("d:/test.sql");
		List<String> lists = FileUtils.readLines(sqlFile);
		StringBuilder buff = new StringBuilder();
		for(String line: lists) {
			int commentIdx = line.indexOf("--");
			if(commentIdx >= 0) {
				line = line.substring(0, commentIdx);
			}
			buff.append(line).append("\n");
		}
		String sql = buff.toString();
		System.out.println(sql);
		Query query = Parser.parseQuery(sql, 0);
		Table table = query.tableList().get(0);
		System.out.println(table.getClass());
		System.out.println(query.tableList().size());
		System.out.println(table.start() + " " + table.end());
		
		QueryTable qt = (QueryTable) table;
		
		System.out.println(qt.query().start() + " " + qt.query().end());
		
		Query q2 = qt.query();
		
		System.out.println(q2.tableList().size());
		System.out.println("--------------------------");
		for(Table tbl: q2.tableList()) {
			System.out.println(tbl.alias() + " " + tbl.start() + " " + tbl.end());
			System.out.println(tbl.table());
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
		while((keyword = Parser.findKeyWord(sql, start)) != null) {
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
			while((keyword = Parser.findKeyWord(line, start)) != null) {
				System.out.println(keyword);
				start = keyword.end();
			}
		}
		
		IOUtils.closeQuietly(reader);
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
