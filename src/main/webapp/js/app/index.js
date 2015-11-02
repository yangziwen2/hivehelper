define(function(require, exports, module) {
	
	"use strict";
	
	var common = require('app/common'),
		$ = require('jquery');
	
	var editor = null;
	
	function initEditor() {
		
		resizeCodeMirror();
		
		$(window).on('resize', resizeCodeMirror);
		
		editor = CodeMirror.fromTextArea($('#J_sql')[0], {
			mode: 'text/x-mysql',
			tabMode: 'indent',
			scrollbarStyle: 'simple',
			matchBrackets: true,
			lineNumbers: true,
			indentUnit: 4
		});
	}
	
	function resizeCodeMirror() {
		//-- 让CodeMirror对屏幕的高度自适应 --//
		var defaultWinHeight = 700,
			minCmHeight = 425;
		var $wrapper = $('#J_sql').parent();
		var cmHeight = $(window).height() - defaultWinHeight + minCmHeight;
		if(cmHeight > minCmHeight) {
			$('style').filter(function(i, v) {
				return /^\.CodeMirror\{height:\d+px;\}$/.test($(v).html())
			}).remove();
			$wrapper.height(cmHeight);
			$(document.head).append('<style>.CodeMirror{height:' + cmHeight + 'px;}</style>');
		}
	}
	
	function initClearBtn() {
		$('#J_clearBtn').on('click', function() {
			if(editor) {
				editor.setValue('');
			}
		});
	}
	
	function initFormatBtn() {
		$('#J_formatBtn').on('click', function() {
			if(!editor) {
				common.alertMsg('编辑区尚未初始化!');
				return;
			}
			var sql = editor.getValue();
			if(!sql || /^\s*$/.test(sql) === true) {
				common.alertMsg('请先输入sql!');
				return;
			}
			doFormatSql(sql);
		});
	}
	
	function doFormatSql(sql) {
		$.ajax({
			type: 'POST',
			url: '/sql/format',
			data: {sql: sql},
			success: function(result) {
				if(result.code !== 0) {
					common.alertMsg(result.msg || '格式化失败，请检查sql语法!');
					return;
				}
				var sql = result.data;
				editor.setValue(sql);
			},
			error: function() {
				common.alertMsg('请求失败，请检查是否已开启格式化工具!');
			}
			
		});
	}
	
	function init() {
		initEditor();
		initClearBtn();
		initFormatBtn();
	}
	
	module.exports = {init: init};
	
});