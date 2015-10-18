package net.yangziwen.hivehelper.format;

public interface Table {
	
	public String table();
	
	public String alias();
	
	public int start();
	
	public int end();
	
	public StringBuilder format(String indent, String baseIndent, StringBuilder buff);
	
}
