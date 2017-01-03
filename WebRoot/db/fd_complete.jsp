<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ 
	page contentType="text/html;charset=UTF-8"%><%@ 
	page import="Xproer.*" %><%@ 
	page import="java.net.URLDecoder" %><%@ 
	page import="org.apache.commons.lang.*" %><%@
	page import="java.io.*" %><%
/*
	此页面主要更新文件夹数据表。已上传字段
	更新记录：
		2014-07-23 创建
*/
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String fid	= request.getParameter("fid");
String uid	= request.getParameter("uid");
String cbk 	= request.getParameter("callback");//jsonp
int ret = 0;

//参数为空
if (	!StringUtils.isBlank(uid)
	||	!StringUtils.isBlank(fid))
{
	DBFolder.Complete(Integer.parseInt(fid),Integer.parseInt(uid));
	DBFile.Complete(Integer.parseInt(uid),Integer.parseInt(fid));
	ret = 1;
}
out.write(cbk + "(" + ret + ")");
%>