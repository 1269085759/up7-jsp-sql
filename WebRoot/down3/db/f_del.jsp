<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ 
	page contentType="text/html;charset=UTF-8"%><%@ 
	page import="down3.biz.*" %><%@
	page import="down3.biz.redis.*" %><%@
	page import="up7.*" %><%@
	page import="redis.clients.jedis.Jedis" %><%@
	page import="down3.model.*" %><%@ 
	page import="java.net.URLDecoder" %><%@ 
	page import="java.net.URLEncoder" %><%@ 
	page import="org.apache.commons.lang.*" %><%@ 
	page import="com.google.gson.FieldNamingPolicy" %><%@ 
	page import="com.google.gson.Gson" %><%@ 
	page import="com.google.gson.GsonBuilder" %><%@ 
	page import="com.google.gson.annotations.SerializedName" %><%@ 
	page import="java.io.*" %><%/*
	从down_files中删除文件下载任务
	更新记录：
		2015-05-13 创建
		2016-07-29 更新。
*/
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String id = request.getParameter("signSvr");
String uid = request.getParameter("uid");
String cbk = request.getParameter("callback");//jsonp

if (	StringUtils.isBlank(uid)
	||	StringUtils.isBlank(id)
	)
{
	out.write(cbk + "({\"value\":null})");
	return;
}

Jedis j = JedisTool.con();
tasks svr = new tasks(uid,j);
svr.del(id);
j.close();
out.write(cbk+"({\"value\":1})");%>