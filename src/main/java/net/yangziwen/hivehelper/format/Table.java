package net.yangziwen.hivehelper.format;

import java.io.StringWriter;

public interface Table {
	
	public String table();
	
	public String alias();
	
	public int start();
	
	public int end();
	
	public StringWriter format(String indent, String baseIndent, StringWriter writer);
	
}
