<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ 
	page contentType="text/html;charset=UTF-8"%><%@ 
	page import="Xproer.*" %><%@ 
	page import="java.net.URLDecoder" %><%@ 
	page import="java.net.URLEncoder" %><%@ 
	page import="org.apache.commons.lang.*" %><%@ 
	page import="com.google.gson.Gson" %><%@ 
	page import="java.io.*" %><%
/*
	此页面主要用来向数据库添加一条记录。
	一般在 HttpUploader.js HttpUploader_MD5_Complete(obj) 中调用
	更新记录：
		2012-05-24 完善
		2012-06-29 增加创建文件逻辑，
*/
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String md5 			= request.getParameter("md5");
String uid 			= request.getParameter("uid");
String pidSvr       = request.getParameter("pidSvr");//文件夹ID，默认为空
String idSvr        = request.getParameter("idSvr");//数据库ID，在ajax_fd_create.aspx中创建
String fileLength 	= request.getParameter("fileLength");//数字化的文件大小。12021
String fileSize 	= request.getParameter("fileSize");//格式化的文件大小。10MB
String fdChild      = request.getParameter("fdChild");//表示此文件是不是文件夹的子项。
if(StringUtils.isBlank(fdChild)) fdChild = "";
String pathLocal	= request.getParameter("pathLocal");
pathLocal			= pathLocal.replaceAll("\\+","%20");
pathLocal			= URLDecoder.decode(pathLocal,"UTF-8");//utf-8解码
String pathSvr		= request.getParameter("pathSvr");
if(StringUtils.isBlank(pathSvr)) pathSvr = "";
pathSvr			= pathSvr.replaceAll("\\+","%20");
pathSvr			= URLDecoder.decode(pathSvr,"UTF-8");//utf-8解码

String callback 	= request.getParameter("callback");//jsonp

String nameLoc		= pathLocal.substring(pathLocal.lastIndexOf("\\")+1);
String extLoc = "";
int index = nameLoc.lastIndexOf(".");
//无后缀名
if(index != -1)
{
	extLoc = nameLoc.substring(index).toLowerCase();
}

//参数为空
if (	StringUtils.isBlank(md5)
	&& StringUtils.isBlank(uid)
	&& StringUtils.isBlank(fileSize))
{
	out.write(callback + "({\"value\":null})");
	return;
}

DBFile db = new DBFile();
xdb_files fileSvr = new xdb_files();
fileSvr.f_fdChild = fdChild.toLowerCase() == "true";
fileSvr.nameLoc = nameLoc;
fileSvr.pathLoc = pathLocal;
//以MD5方式命名
fileSvr.nameSvr = md5 + extLoc;
//以原始文件命名
//inf.nameSvr = nameLoc;

UploaderCfg cfg = new UploaderCfg();
//cfg.CreateUploadPath();

//有idSvr,更新数据库信息，创建文件，一般上传文件夹中的子文件。此信息在ajax_fd_create.aspx中创建。
//有idSvr，表示是上传的文件夹中的子文件。
if (	!StringUtils.isBlank(idSvr)
    && !StringUtils.isBlank(pathSvr) )
{
	fileSvr.md5 = md5;
	fileSvr.idSvr = Integer.parseInt(idSvr);
	fileSvr.pathSvr = fileSvr.nameSvr;
	db.UpdateMD5_path(fileSvr);

    //inf.pathSvr = pathSvr;//在ajax_fd_create.aspx.cs中创建
	//创建文件，不在ajax_f_post.aspx中创建，
	//FileResumerPart fr = new FileResumerPart();
	//fr.CreateFile(inf.pathSvr,fileLength);
}
//数据库存在相同文件
else if (db.GetFileInfByMd5(md5, fileSvr))
{
	fileSvr.nameLoc = nameLoc;
	fileSvr.pathLoc = pathLocal;
	fileSvr.uid = Integer.parseInt(uid);//将当前文件UID设置为当前用户UID
	fileSvr.IsDeleted = false;
	fileSvr.idSvr = db.Add(fileSvr);
}//数据库不存在相同文件
else
{
	fileSvr.uid = Integer.parseInt(uid);//将当前文件UID设置为当前用户UID
	fileSvr.sizeLoc = fileSize;
	fileSvr.md5 = md5;
	fileSvr.lenLoc = Long.parseLong(fileLength);//fix(2015-03-18):文件长度超过2G时报错，int长度不够
	//
	if ( StringUtils.isBlank(pidSvr) )
	{
		fileSvr.pathSvr = fileSvr.nameSvr;
	}//有文件夹，保存在父级文件夹中，并以原文件名称存储
	else
	{
		FolderInf fd = DBFolder.GetInf(pidSvr);
		fileSvr.pathSvr = fd.pathSvr + "/" +  fileSvr.nameLoc;
	}
	fileSvr.pathRel = cfg.GetRelatPath() + fileSvr.nameSvr;
	fileSvr.idSvr = db.Add(fileSvr);

	//创建文件，不在ajax_f_post.aspx中创建，
	//FileResumerPart fr = new FileResumerPart();
	//fr.CreateFile(inf.pathSvr,fileLength);
}
Gson gson = new Gson();
String json = gson.toJson(fileSvr);
json = URLEncoder.encode(json,"UTF-8");
json = callback + "({\"value\":\"" + json + "\"})";//返回jsonp格式数据。
out.write(json);
%>