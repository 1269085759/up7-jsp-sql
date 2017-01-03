<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ 
	page contentType="text/html;charset=UTF-8"%><%@ 
	page import="Xproer.*" %><%@ 
	page import="org.apache.commons.lang.*" %><%@ 
	page import="java.net.URLDecoder" %><%@
	page import="java.net.URLEncoder" %><%@
	 page import="java.io.*" %><%
/*
	此页面主要用来向数据库添加一条记录。
	一般在 HttpUploader.js HttpUploader_MD5_Complete(obj) 中调用
	更新记录：
		2012-05-24 完善
		2012-06-29 增加创建文件逻辑，

	JSON格式化工具：http://tool.oschina.net/codeformat/json
*/
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String uid = request.getParameter("uid");
String cbk = request.getParameter("callback");//jsonp


if(! StringUtils.isBlank(uid) )
{
	String json = DBFile.GetAllUnComplete(Integer.parseInt(uid) );
	if(! StringUtils.isBlank(json))
	{
		XDebug.Output(json);
		json = URLEncoder.encode(json,"utf-8");
		XDebug.Output(json);
		json = json.replace("+","%20");
		out.write(cbk + "({\"value\":\"" + json + "\"})");
		return;
	}
}
out.write(cbk + "({\"value\":null})");
%>