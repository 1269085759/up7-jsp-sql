<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ 
	page contentType="text/html;charset=UTF-8"%><%@ 
	page import="Xproer.*" %><%@ 
	page import="org.apache.commons.lang.*" %><%@ 
	page import="java.net.URLDecoder" %><%@ 
	page import="java.net.URLEncoder" %><%@ 
	page import="org.apache.commons.fileupload.*" %><%@ 
	page import="org.apache.commons.fileupload.disk.*" %><%@ 
	page import="org.apache.commons.fileupload.servlet.*" %><%@
	page import="java.io.*" %><%
/*
	此页面负责将文件块数据写入文件中。
	此页面一般由控件负责调用
	参数：
		uid
		fid
		md5
		fileSize
		rangePos
	更新记录：
		2012-04-12 更新文件大小变量类型，增加对2G以上文件的支持。
		2012-04-18 取消更新文件上传进度信息逻辑。
		2012-10-25 整合更新文件进度信息功能。减少客户端的AJAX调用。
		2014-07-23 优化代码。
		2015-03-19 客户端提供pathSvr，此页面减少一次访问数据库的操作。
*/
//String path = request.getContextPath();
//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String struid 		= "";// 		= request.getParameter("uid");
String strfid 		= "";// 		= request.getParameter("fid");
String md5 			= "";// 			= request.getParameter("md5");
String strFileSize 	= "";// 	= request.getParameter("FileSize");
String strRangePos 	= "";// 	= request.getParameter("RangePos");
String complete		= "false";//文件块是否已发送完毕（最后一个文件块数据）
String pathSvr		= "";//add(2015-03-19):服务器文件路径由客户端提供，此页面减少一次访问数据库的操作。
 
// Check that we have a file upload request
boolean isMultipart = ServletFileUpload.isMultipartContent(request);
FileItemFactory factory = new DiskFileItemFactory();   
ServletFileUpload upload = new ServletFileUpload(factory);
//upload.setSizeMax(262144);//256KB
List files = null;
try 
{
	files = upload.parseRequest(request);
} 
catch (FileUploadException e) 
{// 解析文件数据错误  
    out.println("read file data error:" + e.toString());
    return;
   
}

FileItem rangeFile = null;
// 得到所有上传的文件
Iterator fileItr = files.iterator();
// 循环处理所有文件
while (fileItr.hasNext()) 
{
	// 得到当前文件
	rangeFile = (FileItem) fileItr.next();
	// 忽略简单form字段而不是上传域的文件域(<input type="text" />等)
	if(rangeFile.isFormField())
	{
		String fn = rangeFile.getFieldName();
		String fv = rangeFile.getString(); 
		if(fn.equals("uid")) struid = fv;
		if(fn.equals("fid")) strfid = fv;
		if(fn.equals("md5")) md5 = fv;
		if(fn.equals("FileSize")) strFileSize = fv;
		if(fn.equals("RangePos")) strRangePos = fv;
		if(fn.equals("complete")) complete = fv;
		if(fn.equals("pathSvr")) pathSvr = fv;//add(2015-03-19):
	}
	else 
	{
		break;
	}
}

//参数为空
if ( StringUtils.isBlank( strFileSize )
	|| StringUtils.isBlank( struid )
	|| StringUtils.isBlank( strfid )
	|| StringUtils.isBlank( md5 )
	|| StringUtils.isBlank( strRangePos ) 
	|| StringUtils.isBlank(pathSvr))
{
	XDebug.Output("fileSize", strFileSize);
	XDebug.Output("uid", struid);
	XDebug.Output("fid", strfid);
	XDebug.Output("md5", md5);
	XDebug.Output("param is null");
	return;
}

if (	!strFileSize.isEmpty()
	&&	!struid.isEmpty()
	&& 	!strfid.isEmpty()
	&&	!md5.isEmpty()
	&& 	!strRangePos.isEmpty()
	&&	!pathSvr.isEmpty())
{
	pathSvr				= pathSvr.replaceAll("\\+","%20");	
	pathSvr 			= URLDecoder.decode(pathSvr,"UTF-8");//utf-8解码//客户端使用的是encodeURIComponent编码，
	long fileSize 		= Long.parseLong(strFileSize);
	long rangePos 		= Long.parseLong(strRangePos);
	int uid 			= Integer.parseInt(struid);
	int fid 			= Integer.parseInt(strfid);
	long rangeSize 		= rangeFile.getSize();//控件上传的文件块大小	
	long postedLength 	= rangePos + rangeSize;//已上传大小 = 文件块索引 + 临时文件块大小
	//上传百分比 = 已上传大小 / 文件总大小
	double per 			= ((double)postedLength / (double)fileSize) * 100;
	double perd 		= Math.round(per);
	String postedPercent = Double.toString(perd) + "%";

	XDebug.Output("per", per);
	XDebug.Output("fileSize", fileSize);
	XDebug.Output("postedPercent", postedPercent);
	XDebug.Output("uid", struid);
	XDebug.Output("fid", strfid);
	XDebug.Output("rangePos", rangePos);
	XDebug.Output("postedLength", postedLength);
	XDebug.Output("postedPercent", postedPercent);
	XDebug.Output("complete", complete);
	XDebug.Output("pathSvr",pathSvr);
	
	//保存文件块数据
	FileResumerPart res = new FileResumerPart();
	res.m_RangePos = rangePos;
	res.m_FileSize = fileSize;
	res.SaveFileRange(rangeFile, pathSvr);
	
	//更新文件进度信息
	DBFile db = new DBFile();
	db.UpdateProgress(uid,fid,rangePos,postedLength,postedPercent);
			
	out.write("ok");
}
%>