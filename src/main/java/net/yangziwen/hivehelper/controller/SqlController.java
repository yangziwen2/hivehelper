package net.yangziwen.hivehelper.controller;

import static net.yangziwen.hivehelper.controller.CodeEnum.ERROR_PARAM;
import static net.yangziwen.hivehelper.controller.CodeEnum.OK;
import static net.yangziwen.hivehelper.controller.CodeEnum.PARSE_FAILED;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.yangziwen.hivehelper.format.Parser;
import net.yangziwen.hivehelper.format.Query;

@Controller
@RequestMapping("/sql")
public class SqlController {
	
	private static final Logger logger = LoggerFactory.getLogger(SqlController.class);
	
	@ResponseBody
	@RequestMapping("/format")
	public ModelMap format(String sql) {
		ModelMap model = new ModelMap();
		if(StringUtils.isBlank(sql)) {
			return model
				.addAttribute("code", ERROR_PARAM.code())
				.addAttribute("msg", ERROR_PARAM.msg());
		}
		sql = purifySql(sql);
		try {
			Query query = Parser.parseSelectSql(sql);
			model.addAttribute("code",  OK.code());
			String fomattedSql = query.toString().replaceAll("(?m)^\\s*$\\n", ""); // 暂时先去除空行
			model.addAttribute("data", fomattedSql);	
		} catch (Exception e) {
			logger.info("failed to parse sql");
			model.addAttribute("code", PARSE_FAILED.code());
			model.addAttribute("msg", PARSE_FAILED.msg());
		}
		return model;
	}
	
	private static String purifySql(String sql) {
		sql = sql.replaceAll("/\\*[\\w\\W]*?\\*/", "");
		sql = sql.replace("\t", "    ");
		return sql + "   ";
	}
	
	
}
