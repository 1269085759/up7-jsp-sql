<%@ page language="java" import="up7.*" pageEncoding="UTF-8"%><%@
	page contentType="text/html;charset=UTF-8"%><%@
	page import="redis.clients.jedis.Jedis" %><%@
	page import="up7.biz.redis.*" %><%@ 
	page import="org.apache.commons.lang.StringUtils" %><%
/*
	此页面主要用来执行删除文件夹逻辑。
	只修改数据库中文件夹状态。
	更新记录：
		2014-07-24 创建
*/
String path = request.getContextPath();

String idSign	= request.getParameter("idSign");
String uid	= request.getParameter("uid");
String cbk 	= request.getParameter("callback");//jsonp
int ret = 0;

//参数为空
if (	!StringUtils.isBlank(idSign)
	&&	uid.length()>0 )
{
	Jedis j = JedisTool.con();
	tasks svr = new tasks(j);
	svr.uid = uid;
	svr.delFd(idSign);
	j.close();
	
	//清除数据库
	DBFolder fd = new DBFolder();
	fd.remove(idSign,Integer.parseInt(uid));
	ret = 1;
}
out.write(cbk + "({\"value\":" + ret + "})");
%>