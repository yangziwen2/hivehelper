<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="./include/include.jsp" %>
<!DOCTYPE html>
<html lang="zh_CN">
<head>
<title>HiveQL格式化工具</title>
<link rel="stylesheet" href="/css/bootstrap.min.css"/>
<link rel="stylesheet" href="/css/style.css"/>
<link rel="stylesheet" href="/css/codemirror.css" >
<link rel="stylesheet" href="/css/simplescrollbars.css" >
<link rel="shortcut icon" href="/img/favicon.ico"/>
<style>
.CodeMirror {
	height: 425px;
	border-radius: 5px;
}
.cm-s-default .cm-keyword {
	color: rgb(17,50,133);
}
.cm-s-default .cm-variable {
	color: #333;
}
.cm-s-default .cm-comment {
	color: rgb(27,129,62);
}
div.CodeMirror span.CodeMirror-matchingbracket{
	font-weight: bold;
}
#J_sql {
	display: none;
}
</style>
</head>
<body>

<nav class="navbar navbar-default navbar-fixed-top top-bar" role="navigation">
	<div class="container-fluit">
		<div class="collapse navbar-collapse">
			<div class="col-xs-offset-2 col-xs-8">
				<ul class="nav navbar-left text-center" style="margin-top:6px; width: 100%;">
					<li class="title"><strong>HiveQL格式化工具</strong></li>
				</ul>
			</div>
		</div>
	</div>
</nav>

<div class="container" style="margin-bottom: 50px;">
	<div class="row"><!-- row1 -->
		<div class="col-xs-offset-1 col-xs-10">
			<div class="text-center">
				<h3 style="font-family: 'Microsoft Yahei'"><strong>请输入查询SQL</strong></h3>
			</div>
			<div style="border: 2px solid #ccc; border-radius: 5px; height: 430px;" >
				<textarea id="J_sql"></textarea>
			</div>
			<br/>
			<div class="text-center">
				<button class="btn btn-primary" id="J_formatBtn">格式化</button>
				&nbsp;
				<button class="btn btn-primary" id="J_clearBtn">&nbsp;清&nbsp;&nbsp;空&nbsp;</button>
			</div>
		</div>
	</div><!-- /row1 -->
</div>

<%@ include file="./include/includeJs.jsp" %>
<script type="text/javascript" src="/js/codemirror/codemirror.js"></script>
<script type="text/javascript" src="/js/codemirror/simplescrollbars.js"></script>
<script type="text/javascript" src="/js/codemirror/matchbrackets.js"></script>
<script type="text/javascript" src="/js/codemirror/mysql.js"></script>

<script>
seajs.use('app/index', function(index) {
	index.init();
});
</script>
</body>
</html>