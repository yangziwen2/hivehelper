package net.yangziwen.hivehelper;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import net.yangziwen.hivehelper.format.Parser;
import net.yangziwen.hivehelper.format.Query;

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
		Query query = Parser.parseQuery(sql, 0);
		System.out.println(query.toString());
	}
	
}
