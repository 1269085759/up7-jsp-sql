package Xproer;
import java.sql.*;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;

import net.sf.json.JSONObject;

/*
 * 原型
*/
public class DBFolder {

	public DBFolder()
	{
	}
	/// <summary>
	/// 向数据库添加一条记录
	/// </summary>
	/// <param name="inf"></param>
	/// <returns></returns>
	static public int Add(FolderInf inf)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("insert into xdb_folders(");
		sb.append("fd_name");
		sb.append(",fd_pid");
		sb.append(",fd_uid");
		sb.append(",fd_length");
		sb.append(",fd_size");
		sb.append(",fd_pathLoc");
		sb.append(",fd_pathSvr");
		sb.append(",fd_folders");
		sb.append(",fd_files");
		sb.append(",fd_pidRoot");
		sb.append(",fd_pathRel");

		sb.append(") values(");
		sb.append("?");//sb.append("@fd_name");
		sb.append(",?");//sb.append(",@pid");
		sb.append(",?");//sb.append(",@uid");
		sb.append(",?");//sb.append(",@length");
		sb.append(",?");//sb.append(",@size");
		sb.append(",?");//sb.append(",@pathLoc");
		sb.append(",?");//sb.append(",@pathSvr");
		sb.append(",?");//sb.append(",@folders");
		sb.append(",?");//sb.append(",@files");
		sb.append(",?");//sb.append(",@fd_pidRoot");
		sb.append(",?");//sb.append(",@fd_pathRel");
		sb.append(")");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, inf.name);
			cmd.setInt(2, inf.pidSvr);
			cmd.setInt(3, inf.uid);
			cmd.setString(4, inf.length);
			cmd.setString(5, inf.size);
			cmd.setString(6, inf.pathLoc);
			cmd.setString(7, inf.pathSvr);
			cmd.setInt(8, inf.folders);
			cmd.setInt(9, inf.filesCount);//fix(2015-03-16):读取文件列表错误的问题。
			cmd.setInt(10, inf.pidRoot);
			cmd.setString(11, inf.pathRel);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*db.AddInParameter(cmd, "@fd_name", DbType.AnsiString,50, inf.name);
		db.AddInParameter(cmd, "@pid", DbType.Int32, inf.pidSvr);
		db.AddInParameter(cmd, "@uid", DbType.Int32, inf.uid);
		db.AddInParameter(cmd, "@length", DbType.AnsiString, 19, inf.length);
		db.AddInParameter(cmd, "@size", DbType.AnsiString, 50, inf.size);
		db.AddInParameter(cmd, "@pathLoc", DbType.AnsiString, 255, inf.m_pathLoc);
		db.AddInParameter(cmd, "@pathSvr", DbType.AnsiString, 255, inf.m_pathSvr);
		db.AddInParameter(cmd, "@folders", DbType.Int32, inf.foldersCount);
		db.AddInParameter(cmd, "@files", DbType.Int32, inf.filesCount);*/

		db.ExecuteNonQuery(cmd);
		
		//获取新插入的ID
		String sql = "select top 1 fd_id from xdb_folders order by fd_id desc";
		int fid = db.ExecuteScalar(sql);
		return fid;
	}

	/**
	 * 将文件夹上传状态设为已完成
	 * @param fid
	 * @param uid
	 */
	static public void Complete(int fid,int uid)
	{
		String sql = "update xdb_folders set fd_complete=1 where fd_id=? and fd_uid=?;";
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);
		try 
		{
			cmd.setInt(1, fid);
			cmd.setInt(2, uid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.ExecuteNonQuery(cmd);
	}

	static public void Remove(int fid,int uid)
	{
		String sql = "update xdb_folders set fd_delete=1 where fd_id=? and fd_uid=?;";
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);
		try 
		{
			cmd.setInt(1, fid);
			cmd.setInt(2, uid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.ExecuteNonQuery(cmd);
	}

	/**
	 * 根据文件夹ID获取文件夹信息和未上传完的文件列表，转为JSON格式。
	 * @param fid
	 * @param root [out]
	 * @return
	 */
	static public String GetFilesUnComplete(int fid,FolderInf root)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append("fd_name");
		sb.append(",fd_length");
		sb.append(",fd_size");
		sb.append(",fd_pid");
		sb.append(",fd_pathLoc");
		sb.append(",fd_pathSvr");
		sb.append(",fd_folders");
		sb.append(",fd_files");
		sb.append(",fd_filesComplete");
		sb.append(" from xdb_folders where fd_id=?;");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setInt(1, fid);
			ResultSet r = db.ExecuteDataSet(cmd);
			if (r.next())
			{
				root.name = r.getString(1);
				root.length = r.getString(2);
				root.size = r.getString(3);
				root.pidSvr = r.getInt(4);
				root.idSvr = fid;
				root.pathLoc = r.getString(5);
				root.pathSvr = r.getString(6);
				root.folders = r.getInt(7);
				root.files = r.getInt(8);
				root.filesComplete = r.getInt(9);
			}
			r.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//单独取已上传长度
		root.lenSvr = Long.toString( DBFolder.GetLenPosted(fid) );

		//取文件信息
		ArrayList<FileInf> files = new ArrayList<FileInf>();
		DBFile.GetUnCompletes(fid,files);
		
		JSONObject obj = JSONObject.fromObject(root);//报错
		Gson g = new Gson();
		String js = g.toJson(files);
		//XDebug.Output(js);
		
		obj.element("files", js);//fix(2015-03-18):files.toString()没有自动解析成JSON格式		
		//return obj.toString();
		
		//fix:需要转换为JSON
		
		js = g.toJson( obj );//fix(2015-03-18):obj.toString()没有自动解析成JSON格式
		//XDebug.Output(js);
		return js;
	    //return g.toJson( obj );//bug:arrFiles为空时，此行代码有异常
	}

	/// <summary>
	/// 根据文件夹ID获取文件夹信息和未上传完的文件列表，转为JSON格式。
	/// </summary>
	/// <param name="fid"></param>
	/// <returns></returns>
	static public String GetFilesUnComplete(String fid)
	{
		return GetFilesUnComplete(fid);
	}

	static public FolderInf GetInf(String fid)
	{
		FolderInf inf = new FolderInf();
		GetInf(inf, fid);
		return inf;
	}

	/// <summary>
	/// 根据文件夹ID填充文件夹信息
	/// </summary>
	/// <param name="inf"></param>
	/// <param name="fid"></param>
	static public boolean GetInf(FolderInf inf, String fid)
	{
		boolean ret = false;
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append("fd_name");
		sb.append(",fd_length");
		sb.append(",fd_size");
		sb.append(",fd_pid");
		sb.append(",fd_pathLoc");
		sb.append(",fd_pathSvr");
		sb.append(",fd_folders");
		sb.append(",fd_files");
		sb.append(",fd_filesComplete");
		sb.append(" from xdb_folders where fd_id=?;");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		
		try 
		{
			cmd.setInt(1, Integer.parseInt(fid) );
			ResultSet r = db.ExecuteDataSet(cmd);
			if (r.next())
			{
				inf.name = r.getString(1);
				inf.length = r.getString(2);
				inf.size = r.getString(3);
				inf.pidSvr = r.getInt(4);
				inf.idSvr = Integer.parseInt(fid);
				inf.pathLoc = r.getString(5);
				inf.pathSvr = r.getString(6);
				inf.folders = r.getInt(7);
				inf.files = r.getInt(8);
				inf.filesComplete = r.getInt(9);
				ret = true;
			}
			r.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	/// <summary>
	/// 获取文件夹已上传大小
	/// 计算所有文件已上传大小。
	/// </summary>
	/// <param name="fidRoot"></param>
	/// <returns></returns>
	static public long GetLenPosted(int fidRoot)
	{
		String sql = "select sum(cast(PostedLength as int)) as lenPosted from (select distinct FileMD5,PostedLength from xdb_files where f_pidRoot=? and LEN(FileMD5) > 0) a";
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);
		try {
			cmd.setInt(1, fidRoot);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long len = db.ExecuteLong(cmd);

		return len;
	}		
	
	/**
	 * 获取指定文件夹的JSON数据。提供给下载控件使用。
	 * @param fid
	 * @param root
	 * @return
	 */
	public static String GetFolderData(int fid,FolderInf root)
	{
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(" fd_name");
        sb.append(",fd_length");
        sb.append(",fd_size");
        sb.append(",fd_pid");
        sb.append(",fd_pathLoc");
        sb.append(",fd_pathSvr");
        sb.append(",fd_folders");
        sb.append(",fd_files");
        sb.append(",fd_filesComplete");
        sb.append(" from xdb_folders");
        sb.append(" where fd_id=? and fd_complete=1");

        DbHelper db = new DbHelper();
        PreparedStatement cmd = db.GetCommand(sb.toString());
        try {
			cmd.setInt(1, fid);
			ResultSet r = db.ExecuteDataSet(cmd);
			if (r.next())
			{
	            root.name = r.getString(1);
	            root.length = r.getString(2);
	            root.size = r.getString(3);
	            root.pidSvr = r.getInt(4);
	            root.idSvr = fid;
	            root.pathLoc = r.getString(5);
	            root.pathSvr = r.getString(6);
	            root.folders = r.getInt(7);
	            root.filesCount = r.getInt(8);
	            root.filesComplete = r.getInt(9);
			}
			r.close();
		} catch (SQLException e) {e.printStackTrace();}       

        //单独取已上传长度
        //root.lenPosted = DBFolder.GetLenPosted(fid).ToString();

        //取文件信息
        ArrayList<FileInf> files = new ArrayList<FileInf>();
        ArrayList<String> ids = new ArrayList<String>();
        DBFile.GetCompletes(fid, files,ids);

        Gson g = new Gson();
        String filesJson = g.toJson(files);

        JSONObject obj = JSONObject.fromObject(root);//报错
        obj.element("files",filesJson);
        obj.element("length",root.length);        
        obj.element("ids",StringUtils.join(ids.toArray(),",") );
        return obj.toString();
	}
}