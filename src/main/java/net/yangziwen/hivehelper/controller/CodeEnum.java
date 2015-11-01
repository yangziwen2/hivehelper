package net.yangziwen.hivehelper.controller;

public enum CodeEnum {
	
	OK(0, ""),
	ERROR_PARAM(1, "参数有误!"),
	PARSE_FAILED(101, "sql格式化失败!")
	;
	
	private int code;
	private String msg;
	
	private CodeEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int code() {
		return code;
	}

	public String msg() {
		return msg;
	}
	
}
