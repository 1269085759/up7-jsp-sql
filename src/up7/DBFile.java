package up7;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


import net.sf.json.JSONArray;

import up7.biz.folder.fd_file_redis;
import up7.DbHelper;
import up7.model.FileInf;
import up7.model.FolderInf;
import up7.model.xdb_files;

import com.google.gson.Gson;

/*
 * 原型
*/
public class DBFile {

	public DBFile()
	{
	}
	
	/**
	 * 获取指定文件夹下面的所有文件，
	 * @param f_id
	 * @param files
	 * @param ids
	 */
	public static void GetCompletes(int f_id,ArrayList<FileInf> files,ArrayList<String> ids)
	{
        StringBuilder sql = new StringBuilder("select ");
        sql.append("f_id");
        sql.append(",f_nameLoc");
        sql.append(",f_pathLoc");
        sql.append(",f_lenLoc");
        sql.append(",f_sizeLoc");
        sql.append(",f_md5");
        sql.append(",f_pidRoot");
        sql.append(",f_pid");
        sql.append(",f_lenSvr");
        sql.append(",f_pathSvr");//fix:服务器会重复创建文件项的问题
        sql.append(",fd_pathRel");//
        sql.append(" from up7_files");
        sql.append(" left join up7_folders");
        sql.append(" on fd_id = f_pid");
        sql.append(" where f_pidRoot=? and f_complete=1");

        DbHelper db = new DbHelper();
        PreparedStatement cmd = db.GetCommand(sql.toString());
        try
        {
        	cmd.setInt(1, f_id);
        	ResultSet r = db.ExecuteDataSet(cmd);
            while (r.next())
            {
                FileInf fi 		= new FileInf();
                fi.idSvr 		= r.getInt(1);
                fi.nameLoc 		= r.getString(2);
                fi.pathLoc 		= r.getString(3);
                fi.lenLoc 		= r.getLong(4);
                fi.sizeLoc 		= r.getString(5);
                fi.md5 			= r.getString(6);
                fi.pidRoot 		= r.getInt(7);
                fi.pidSvr 		= r.getInt(8);
                fi.lenSvr 		= r.getLong(9);
                fi.pathSvr 		= r.getString(10);//fix:服务器会重复创建文件项的问题
                fi.pathRel 		= r.getString(11) + "\\";//相对路径：root\\child\\folder\\
                files.add(fi);
                //添加到列表
                ids.add( Integer.toString(fi.idSvr) );
            }
            r.close();            
            cmd.close();
        }
        catch (SQLException e) {e.printStackTrace();}
        
	}
	
	/**
	 * 获取所有已经上传完的文件和文件夹供下载列表使用。
	 * @param f_uid
	 * @return
	 */
	public static String GetAllComplete(int f_uid)
	{
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append("f_id");
        sb.append(",f_fdTask");
        sb.append(",f_fdID");
        sb.append(",f_nameLoc");
        sb.append(",f_pathLoc");
        sb.append(",f_lenLoc");
        sb.append(",f_sizeLoc");
        sb.append(",f_perSvr");
        //sb.append(",fd_size");
        sb.append(" from up7_files");
        //sb.append(" left join up7_folders");
        //sb.append(" on f_fdID = fd_id");
        sb.append(" where f_deleted=0 and f_fdChild=0 and f_complete=1");

        ArrayList<xdb_files> files = new ArrayList<xdb_files>();
        DbHelper db = new DbHelper();
        PreparedStatement cmd = db.GetCommand(sb.toString());
        try
        {
        	ResultSet r = db.ExecuteDataSet(cmd);
            while (r.next())
            {
                xdb_files f = new xdb_files();
                f.idSvr 	= r.getInt(1);
                f.f_fdTask 	= r.getBoolean(2);
                f.f_fdID 	= r.getInt(3);
                f.nameLoc 	= r.getString(4);
                f.pathLoc 	= r.getString(5);
                f.lenLoc 	= r.getLong(6);
                f.sizeLoc 	= r.getString(7);
                f.perSvr 	= r.getString(8);//已下载百分比

                files.add(f);

            }
            r.close();
        }
        catch (SQLException e) {e.printStackTrace();}
        
        Gson g = new Gson();
	    return g.toJson( files );//bug:arrFiles为空时，此行代码有异常
	}
	
	static public String GetAllUnComplete(int f_uid)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append("f_id");
		sb.append(",f_fdTask");
		sb.append(",f_fdID");
		sb.append(",f_nameLoc");
		sb.append(",f_pathLoc");
		sb.append(",f_md5");
		sb.append(",f_lenLoc");
		sb.append(",f_sizeLoc");
		sb.append(",f_pos");
		sb.append(",f_lenSvr");
		sb.append(",f_perSvr");
		sb.append(",f_complete");
		sb.append(",f_pathSvr");//fix(2015-03-16):修复无法续传文件的问题。
		//文件夹信息
		sb.append(",fd_files");
		sb.append(",fd_filesComplete");
		sb.append(" from up7_files left join up7_folders on up7_files.f_fdID = up7_folders.fd_id");//change(2015-03-18):联合查询文件夹数据
		sb.append(" where f_uid=? and f_deleted=0 and f_fdChild=0 and f_complete=0;");//fix(2015-03-18):只加载未完成列表

		ArrayList<xdb_files> files = new ArrayList<xdb_files>();
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setInt(1, f_uid);
			ResultSet r = db.ExecuteDataSet(cmd);
			while(r.next())
			{
				xdb_files f 	= new xdb_files();
				f.uid			= f_uid;
				f.idSvr 		= r.getInt(1);
				f.f_fdTask 		= r.getBoolean(2);
				f.f_fdID 		= r.getInt(3);
				f.nameLoc 		= r.getString(4);
				f.pathLoc 		= r.getString(5);
				f.md5 			= r.getString(6);
				f.lenLoc 		= r.getLong(7);
				f.sizeLoc 		= r.getString(8);
				f.FilePos 		= r.getLong(9);
				f.lenSvr 		= r.getLong(10);
				f.perSvr 		= r.getString(11);
				f.complete 		= r.getBoolean(12);
				f.pathSvr		= r.getString(13);//fix(2015-03-19):修复无法续传文件的问题。
				f.filesCount 	= r.getInt(14);//add(2015-03-18):
				f.filesComplete = r.getInt(15);//add(2015-03-18):

				files.add(f);
				
			}
			r.close();
			cmd.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(files.size() < 1) return null;


		ArrayList<xdb_files> arrFiles = new ArrayList<xdb_files>();
		for(xdb_files f : files)
		{
			//是文件夹任务=>取文件夹JSON
			if (f.f_fdTask)
			{
				FolderInf fd = new FolderInf();
				f.fd_json = DBFolder.GetFilesUnComplete(f.f_fdID,fd);
                float pdPer = 0;
                long lenPosted = DBFolder.GetLenPosted(f.f_fdID);
                fd.lenSvr = lenPosted;
                f.lenSvr = lenPosted;//给客户端使用。
                fd.filesCount = f.filesCount;//add(2015-03-18):
                fd.filesComplete = f.filesComplete;//add(2015-03-18)
                long len = fd.lenLoc;
                if (lenPosted > 0 && len > 0)
                {
                    pdPer = (float)Math.round(((lenPosted*1.0f) / len*1.0f) * 100.0f);
                }
                f.idSvr = f.f_fdID;//将文件ID改为文件夹的ID，客户端续传文件夹时将会使用这个ID。
				f.perSvr = Float.toString( pdPer ) + "%";
                f.sizeLoc = fd.size;
			}
			arrFiles.add( f );
		}
		Gson g = new Gson();
	    return g.toJson( arrFiles );//bug:arrFiles为空时，此行代码有异常	
	}
	


	/// <summary>
	/// 根据文件MD5获取文件信息
	/// </summary>
	/// <param name="md5"></param>
	/// <param name="inf"></param>
	/// <returns></returns>
	public boolean exist_file(String md5, xdb_files fileSvr)
	{
		boolean ret = false;
		StringBuilder sb = new StringBuilder();
		sb.append("select top 1 ");
		sb.append("f_id");
		sb.append(",f_uid");
		sb.append(",f_nameLoc");
		sb.append(",f_nameSvr");
		sb.append(",f_pathLoc");
		sb.append(",f_pathSvr");
		sb.append(",f_pathRel");
		sb.append(",f_md5");
		sb.append(",f_lenLoc");
		sb.append(",f_sizeLoc");
		sb.append(",f_pos");
		sb.append(",f_lenSvr");
		sb.append(",f_perSvr");
		sb.append(",f_complete");
		sb.append(",f_time");
		sb.append(",f_deleted");
		sb.append(" from up7_files where f_md5=? order by f_perSvr DESC");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, md5);
			ResultSet r = db.ExecuteDataSet(cmd);
			if (r.next())
			{
				fileSvr.idSvr 			= r.getInt(1);
				fileSvr.uid 			= r.getInt(2);
				fileSvr.nameLoc 		= r.getString(3);
				fileSvr.nameSvr 		= r.getString(4);
				fileSvr.pathLoc 		= r.getString(5);
				fileSvr.pathSvr 		= r.getString(6);
				fileSvr.pathRel 		= r.getString(7);
				fileSvr.md5 			= r.getString(8);
				fileSvr.lenLoc 			= r.getLong(9);
				fileSvr.sizeLoc 		= r.getString(10);
				fileSvr.FilePos 		= r.getLong(11);
				fileSvr.lenSvr 			= r.getLong(12);
				fileSvr.perSvr 			= r.getString(13);
				fileSvr.complete 		= r.getBoolean(14);
				fileSvr.PostedTime 		= r.getDate(15);
				fileSvr.deleted 		= r.getBoolean(16);
				ret = true;
			}
			cmd.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	/// <summary>
	/// 增加一条数据，并返回新增数据的ID
	/// 在ajax_create_fid.aspx中调用
	/// 文件名称，本地路径，远程路径，相对路径都使用原始字符串。
	/// d:\soft\QQ2012.exe
	/// </summary>
	public void Add(xdb_files model)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("insert into up7_files(");
		sb.append(" f_idSign");
		sb.append(",f_sizeLoc");
		sb.append(",f_pos");
		sb.append(",f_lenSvr");
		sb.append(",f_perSvr");
		sb.append(",f_complete");
		sb.append(",f_deleted");
		sb.append(",f_fdChild");
		sb.append(",f_uid");
		sb.append(",f_nameLoc");
		sb.append(",f_nameSvr");
		sb.append(",f_pathLoc");
		sb.append(",f_pathSvr");
		sb.append(",f_pathRel");
		sb.append(",f_md5");
		sb.append(",f_lenLoc");
		sb.append(",f_sign");
		
		sb.append(") values (");
		
		sb.append(" ?");//sb.append("@f_idSign");
		sb.append(",?");//sb.append(",@f_sizeLoc");
		sb.append(",?");//sb.append(",@f_pos");
		sb.append(",?");//sb.append(",@f_lenSvr");
		sb.append(",?");//sb.append(",@f_perSvr");
		sb.append(",?");//sb.append(",@f_complete");
		sb.append(",?");//sb.append(",@f_deleted");
		sb.append(",?");//sb.append(",@f_fdChild");
		sb.append(",?");//sb.append(",@f_uid");
		sb.append(",?");//sb.append(",@f_nameLoc");
		sb.append(",?");//sb.append(",@f_nameSvr");
		sb.append(",?");//sb.append(",@f_pathLoc");
		sb.append(",?");//sb.append(",@f_pathSvr");
		sb.append(",?");//sb.append(",@f_pathRel");
		sb.append(",?");//sb.append(",@f_md5");
		sb.append(",?");//sb.append(",@f_lenLoc");
		sb.append(",?");//sb.append(",@f_sign");
		sb.append(") ");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		
		try {
			cmd.setString(1, model.idSign);
			cmd.setString(2, model.sizeLoc);
			cmd.setLong(3, model.FilePos);
			cmd.setLong(4, model.lenSvr);
			cmd.setString(5, model.perSvr);
			cmd.setBoolean(6, model.complete);
			//cmd.setDate(6, (java.sql.Date) model.PostedTime);
			cmd.setBoolean(7, false);
			cmd.setBoolean(8, model.f_fdChild);
			cmd.setInt(9, model.uid);
			cmd.setString(10, model.nameLoc);
			cmd.setString(11, model.nameSvr);
			cmd.setString(12, model.pathLoc);
			cmd.setString(13, model.pathSvr);
			cmd.setString(14, model.pathRel);
			cmd.setString(15, model.md5);
			cmd.setLong(16, model.lenLoc);
			cmd.setString(17, model.sign);
		} catch (SQLException e) {
			System.out.println("添加文件信息错误，数据库错误");
			e.printStackTrace();
		}

		db.ExecuteNonQuery(cmd);
	}

	/// <summary>
	/// 添加一个文件夹上传任务
	/// </summary>
	/// <param name="inf"></param>
	/// <returns></returns>
	static public int Add(FolderInf inf)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("insert into up7_files(");
		sb.append("f_nameLoc");
		sb.append(",f_fdTask");
		sb.append(",f_fdID");
		sb.append(",f_lenLoc");
		sb.append(",f_sizeLoc");
		sb.append(",f_pathLoc");
		sb.append(") values(");
		sb.append("?");
		sb.append(",1");
		sb.append(",?");//fdID
		sb.append(",?");//lenLoc
		sb.append(",?");//sizeLoc
		sb.append(",?");//pathLoc
		sb.append(");");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString(),"f_id");
		try 
		{
			cmd.setString(1, inf.nameLoc);
			cmd.setInt(2, inf.idSvr);
			cmd.setLong(3, inf.lenLoc);
			cmd.setString(4, inf.size);
			cmd.setString(5, inf.pathLoc);
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int id = (int)db.ExecuteGenKey(cmd);
		return id;
	}

	/// <summary>
	/// 添加一条文件信息，一船提供给fd_create使用。
	/// </summary>
	/// <param name="inf"></param>
	/// <returns></returns>
	static public int Add(FileInf inf)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("insert into up7_files(");
		sb.append(" f_pid");//1
		sb.append(",f_pidRoot");//2
		sb.append(",f_fdChild");//3
		sb.append(",f_uid");//4
		sb.append(",f_nameLoc");//5
		sb.append(",f_nameSvr");//6
		sb.append(",f_pathLoc");//7
		sb.append(",f_pathSvr");//8
        sb.append(",f_md5");//9
		sb.append(",f_lenLoc");//10
        sb.append(",f_lenSvr");//11
        sb.append(",f_perSvr");//12
		sb.append(",f_sizeLoc");//13
        sb.append(",f_complete");//14
		sb.append(") values(");
		sb.append(" ?");//sb.append(" @f_pid");
		sb.append(",?");//sb.append(",@f_pidRoot");
		sb.append(",?");//sb.append(",@f_fdChild");
		sb.append(",?");//sb.append(",@f_uid");
		sb.append(",?");//sb.append(",@f_nameLoc");
		sb.append(",?");//sb.append(",@f_nameSvr");
		sb.append(",?");//sb.append(",@f_pathLoc");
		sb.append(",?");//sb.append(",@f_pathSvr");
		sb.append(",?");//sb.append(",@f_md5");
		sb.append(",?");//sb.append(",@f_lenLoc");
		sb.append(",?");//sb.append(",@f_lenSvr");
		sb.append(",?");//sb.append(",@f_perSvr");
		sb.append(",?");//sb.append(",@f_sizeLoc");
		sb.append(",?");//sb.append(",@f_complete");
		sb.append(");");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setInt(1, inf.pidSvr);
			cmd.setInt(2, inf.pidRoot);
			cmd.setBoolean(3, true);
			cmd.setInt(4, inf.uid);
			cmd.setString(5, inf.nameLoc);
			cmd.setString(6, inf.nameSvr);
			cmd.setString(7, inf.pathLoc);
			cmd.setString(8, inf.pathSvr);
			cmd.setString(9, inf.md5);
			cmd.setLong(10, inf.lenLoc);
			cmd.setLong(11, inf.lenSvr);
			cmd.setString(12, inf.perSvr);
			cmd.setString(13, inf.sizeLoc);
			cmd.setBoolean(14, inf.complete);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		db.ExecuteNonQuery(cmd);
		int f_id = db.ExecuteScalar("select top 1 f_id from up7_files order by f_id desc");
		
		return f_id;
	}
	
	/**
	 * 将文件缓存信息添加到数据库，
	 * @param inf
	 */
	public void addComplete(fd_file_redis inf)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("insert into up7_files(");
		sb.append(" f_idSign");//1		
		sb.append(",f_uid");//2
		sb.append(",f_nameLoc");//3
		sb.append(",f_nameSvr");//4
		sb.append(",f_pathLoc");//5
		sb.append(",f_pathSvr");//6		
		sb.append(",f_lenLoc");//7
		sb.append(",f_lenSvr");//8
		sb.append(",f_perSvr");//9
		sb.append(",f_sizeLoc");//10
		sb.append(",f_complete");//11
		sb.append(",f_blockCount");//12
		sb.append(",f_blockSize");//13
		
		sb.append(") values(");
				
		sb.append(" ?");//sb.append("@f_idSign");
		sb.append(",?");//sb.append(",@f_uid");
		sb.append(",?");//sb.append(",@f_nameLoc");
		sb.append(",?");//sb.append(",@f_nameSvr");
		sb.append(",?");//sb.append(",@f_pathLoc");
		sb.append(",?");//sb.append(",@f_pathSvr");
		sb.append(",?");//sb.append(",@f_lenLoc");
		sb.append(",?");//sb.append(",@f_lenSvr");
		sb.append(",'100%'");//sb.append(",@f_perSvr");
		sb.append(",?");//sb.append(",@f_sizeLoc");
		sb.append(",1");//sb.append(",@f_complete");
		sb.append(",?");//sb.append(",@f_blockCount");
		sb.append(",?");//sb.append(",@f_blockSize");
		sb.append(")");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, inf.idSign);
			cmd.setInt(2, inf.uid);
			cmd.setString(3, inf.nameLoc);
			cmd.setString(4, inf.nameLoc);
			cmd.setString(5, inf.pathLoc);
			cmd.setString(6, inf.pathSvr);
			cmd.setLong(7, inf.lenLoc);
			cmd.setLong(8, inf.lenLoc);
			cmd.setString(9, inf.lenLoc>1024 ? inf.sizeLoc : PathTool.getDataSize(inf.lenLoc));
			cmd.setInt(12, inf.blockCount);
			cmd.setInt(13,inf.blockSize);
			cmd.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/// <summary>
	/// 更新文件夹中子文件信息，
	/// f_pathSvr
	/// md5
	/// f_id
	/// </summary>
	/// <param name="inf"></param>
	public void UpdateChild(FileInf inf)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("update up7_files set ");
		sb.append(" f_pathSvr = ?, ");
		sb.append(" f_md5 = ? ");
		sb.append(" where f_id=? ");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, inf.pathSvr);
			cmd.setString(1, inf.md5);
			cmd.setInt(3, inf.idSvr);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.ExecuteNonQuery(cmd);
	}
	
	/// <summary>
    /// 根据文件idSvr信息，更新文件数据表中对应项的MD5。
    /// </summary>
    /// <param name="inf"></param>
    public void UpdateMD5(xdb_files inf)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("update up7_files set ");
		sb.append(" f_md5 = ? ");
		sb.append(" where f_id=? ");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, inf.md5);
			cmd.setInt(2, inf.idSvr);
		} catch (SQLException e) {e.printStackTrace();}

		db.ExecuteNonQuery(cmd);
	}

    /// <summary>
    /// 根据文件idSvr信息，更新文件数据表中对应项的MD5。
    /// </summary>
    /// <param name="inf"></param>
    public void UpdateMD5_path(xdb_files inf)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("update up7_files set ");
		sb.append(" f_md5 = ? ");
		sb.append(",f_pathSvr = ? ");
		sb.append(" where f_id=? ");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, inf.md5);
			cmd.setString(2, inf.pathSvr);//fix(2015-07-30):重新更新路径
			cmd.setInt(3, inf.idSvr);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		db.ExecuteNonQuery(cmd);
	}
    
    /**
     * 更新文件MD5，服务器存储路径。
     */
    public void updateInf(xdb_files inf)
    {
		StringBuilder sb = new StringBuilder();
		sb.append("update up7_files set ");
		sb.append(" f_md5 = ? ");
		sb.append(",f_pathSvr = ? ");//
		sb.append(",f_lenSvr = ? ");//
		sb.append(",f_perSvr = ? ");//
		sb.append(",f_complete = ? ");//
		sb.append(" where f_id=? ");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, inf.md5);
			cmd.setString(2, inf.pathSvr);
			cmd.setLong(3, inf.lenSvr);
			cmd.setString(4, inf.perSvr);
			cmd.setBoolean(5, inf.complete);
			cmd.setInt(6, inf.idSvr);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		db.ExecuteNonQuery(cmd);
	}

	/**
	 * 清空文件表，文件夹表数据。
	 */
	static public void Clear()
	{
		DbHelper db = new DbHelper();
		db.ExecuteNonQuery("delete from up7_files;");
		db.ExecuteNonQuery("delete from up7_folders;");
	}

	/**
	 * @param f_uid
	 * @param f_id
	 */
	static public void Complete(int f_uid, int f_id)
	{
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand("update up7_files set f_perSvr='100%' ,f_complete=1 where f_uid=? and f_fdID=?;");
		try {
			cmd.setInt(1, f_uid);
			cmd.setInt(2, f_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		db.ExecuteNonQuery(cmd);
	}
	
	/**
	 * @param f_uid
	 * @param f_id
	 */
	static public void fd_complete(String f_id, String fd_id, String uid)
	{
		String sql = "update up7_files set f_perSvr='100%' ,f_complete=1 ,f_lenSvr=f_lenLoc where f_id=?;";//更新文件夹状态，设为已完成
		sql += "update up7_folders set fd_complete=1 where fd_id=? and fd_uid=?;";
		sql += "update up7_files set f_perSvr='100%',f_lenSvr=f_lenLoc,f_complete=1 where f_pidRoot=?;";//更新所有子文件状态，设为已完成
		
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);
		try {
			cmd.setInt(1, Integer.parseInt(f_id));
			cmd.setInt(2, Integer.parseInt(fd_id));
			cmd.setInt(3, Integer.parseInt(uid));
			cmd.setInt(4, Integer.parseInt(fd_id));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		db.ExecuteNonQuery(cmd);
	}
	
	public boolean fd_fileProcess(int uid, int f_id, long f_pos, long lenSvr, String perSvr, int fd_idSvr, long fd_lenSvr,String fd_perSvr,boolean complete)
    {
		/*
		 * @f_pos		1
		 * @uid			2
		 * @idSvr		3
		 * @lenSvr		4
		 * @perSvr		5
		 * @complete	6
		 * @fd_idSvr	7
		 * @fd_lenSvr	8
		 * @fd_perSvr	9
		 * */
        String sql = "{call fd_fileProcess(?,?,?,?,?,?,?,?,?)}";
        DbHelper db = new DbHelper();
        PreparedStatement cmd = db.GetCommandStored(sql);     

		try 
		{
			cmd.setLong(1, f_pos);
			cmd.setInt(2, uid);
			cmd.setInt(3, f_id);
			cmd.setLong(4, lenSvr);
			cmd.setString(5, perSvr);
			cmd.setBoolean(6, complete);
			cmd.setInt(7, fd_idSvr);
			cmd.setLong(8, fd_lenSvr);
			cmd.setString(9, fd_perSvr);			
		} catch (SQLException e) {e.printStackTrace();}

		db.ExecuteNonQuery(cmd);
		return true;
    }

	/// <summary>
	/// 更新上传进度
	/// </summary>
	///<param name="f_uid">用户ID</param>
	///<param name="f_id">文件ID</param>
	///<param name="f_pos">文件位置，大小可能超过2G，所以需要使用long保存</param>
	///<param name="f_lenSvr">已上传长度，文件大小可能超过2G，所以需要使用long保存</param>
	///<param name="f_perSvr">已上传百分比</param>
	public boolean f_process(int f_uid,int f_id,long f_pos,long f_lenSvr,String f_perSvr)
	{
		//String sql = "update up7_files set f_pos=?,f_lenSvr=?,f_perSvr=? where f_uid=? and f_id=?";
		String sql = "{call f_process(?,?,?,?,?,?)}";
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommandStored(sql);
		
		try 
		{
			cmd.setString(1, Long.toString(f_pos) );
			cmd.setInt(2, f_uid);
			cmd.setInt(3, f_id);
			cmd.setString(4, Long.toString(f_lenSvr) );
			cmd.setString(5, f_perSvr);
			cmd.setBoolean(6, false);
		} catch (SQLException e) {e.printStackTrace();}

		db.ExecuteNonQuery(cmd);
		return true;
	}

	/// <summary>
	/// 上传完成。将所有相同MD5文件进度都设为100%
	/// </summary>
	public void complete(int uid,int idSvr)
	{
		String sql = "update up7_files set f_lenSvr=f_lenLoc,f_perSvr='100%',f_complete=1 where f_id=? and f_uid=?";
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);
		
		try {
			cmd.setInt(1, idSvr);
			cmd.setInt(2, uid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.ExecuteNonQuery(cmd);
	}
	
	/// <summary>
	/// 上传完成。将所有相同MD5文件进度都设为100%
	/// </summary>
	public void complete(int uid,String idSign)
	{
		String sql = "update up7_files set f_lenSvr=f_lenLoc,f_perSvr='100%',f_complete=1 where f_idSign=? and f_uid=?";
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);
		
		try
		{
			cmd.setString(1,idSign);
			cmd.setInt(2, uid);
		} catch(SQLException e){
			e.printStackTrace();
		}
		db.ExecuteNonQuery(cmd);//在部分环境中测试发现执行后没有效果。f_complete仍然为0
	}

	/// <summary>
	/// 检查相同MD5文件是否有已经上传完的文件
	/// </summary>
	/// <param name="md5"></param>
	public boolean HasCompleteFile(String md5)
	{
		//为空
		if (md5 == null) return false;
		if(md5.isEmpty()) return false;

		String sql = "select f_id from up7_files where f_complete=1 and f_md5=?";
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);

		try {
			cmd.setString(1, md5);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean ret = db.Execute(cmd);

		return ret;
	}

	/// <summary>
	/// 删除一条数据，并不真正删除，只更新删除标识。
	/// </summary>
	/// <param name="f_uid"></param>
	/// <param name="f_id"></param>
	public void Delete(int f_uid,int f_id)
	{
		String sql = "update up7_files set f_deleted=1 where f_uid=? and f_id=?";
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);

		try {
			cmd.setInt(1, f_uid);
			cmd.setInt(2, f_id);
			db.ExecuteNonQuery(cmd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void remove(String idSign)
	{
		String sql = "update up7_files set f_deleted=1 where f_idSign=?";
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);

		try {			
			cmd.setString(1, idSign);
			db.ExecuteNonQuery(cmd);
		} catch (SQLException e) {
			System.out.println("更新数据库文件信息失败，");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/// <summary>
	/// 根据根文件夹ID获取未上传完成的文件列表，并转换成JSON格式。
	/// 说明：
	///		1.此函数会自动对文件路径进行转码
	/// </summary>
	/// <param name="fidRoot"></param>
	/// <returns></returns>
	static public String GetUnCompletes(int fidRoot)
	{
		StringBuilder sql = new StringBuilder("select ");
		sql.append("f_nameLoc");
		sql.append(",f_pathLoc");
		sql.append(",f_lenLoc");
		sql.append(",f_sizeLoc");
		sql.append(",f_md5");
		sql.append(",f_pidRoot");
		sql.append(",f_pid");
		sql.append(" from up7_files where f_pidRoot=?;");
		ArrayList<FileInf> arrFiles = new ArrayList<FileInf>();

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql.toString());
		try 
		{
			cmd.setInt(1, fidRoot);
			ResultSet r = db.ExecuteDataSet(cmd);
			while (r.next())
			{
				FileInf fi = new FileInf();
				fi.nameLoc = r.getString(0);
				fi.pathLoc = r.getString(1);
				fi.lenLoc = r.getLong(2);
				fi.sizeLoc = r.getString(3);
				fi.md5 = db.GetStringSafe(r.getString(4),"");			
				fi.pidRoot = r.getInt(5);
				fi.pidSvr = r.getInt(6);
				arrFiles.add( fi );
			}
			r.close();
		}
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    JSONArray json = JSONArray.fromObject( arrFiles );
		return json.toString();
	}

    /// <summary>
    /// 获取未上传完的文件列表
    /// </summary>
    /// <param name="fidRoot"></param>
    /// <param name="files"></param>
	static public void GetUnCompletes(int fidRoot,ArrayList<FileInf> files)
	{
		StringBuilder sql = new StringBuilder("select ");
        sql.append(" f_id");
        sql.append(",f_nameLoc");
		sql.append(",f_pathLoc");
		sql.append(",f_lenLoc");
		sql.append(",f_sizeLoc");
		sql.append(",f_md5");
		sql.append(",f_pidRoot");
        sql.append(",f_pid");
        sql.append(",f_lenSvr");
        sql.append(",f_pathSvr");//fix(2015-03-18):续传文件时服务器会创建重复文件项信息
		sql.append(" from up7_files where f_pidRoot=? and f_complete=0;");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql.toString());
		try 
		{
			cmd.setInt(1, fidRoot);
			ResultSet r = db.ExecuteDataSet(cmd);
			while (r.next())
			{
				FileInf fi = new FileInf();
	            fi.idSvr = r.getInt(1);
				fi.nameLoc = r.getString(2);
				fi.pathLoc = r.getString(3);
				fi.lenLoc = r.getLong(4);
				fi.sizeLoc = r.getString(5);
				fi.md5 = db.GetStringSafe(r.getString(6),"");
				fi.pidRoot = r.getInt(7);
				fi.pidSvr = r.getInt(8);
	            fi.lenSvr = r.getLong(9);
	            fi.pathSvr = r.getString(10);//fix(2015-03-18):修复续传文件时服务器会创建重复文件信息的问题。
				files.add(fi);
			}
			r.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * 根据文件ID获取文件信息
	 * @param fid
	 * @param inf
	 * @return
	 */
	public boolean GetFileInfByFid(int fid,xdb_files inf)
	{
		boolean ret = false;
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append(" f_uid");
		sb.append(",f_nameLoc");
		sb.append(",f_nameSvr");
		sb.append(",f_pathLoc");
		sb.append(",f_pathSvr");
		sb.append(",f_pathRel");
		sb.append(",f_md5");
		sb.append(",f_lenLoc");
		sb.append(",f_sizeLoc");
		sb.append(",f_pos");
		sb.append(",f_lenSvr");
		sb.append(",f_perSvr");
		sb.append(",f_complete");
		sb.append(",f_time");
		sb.append(",f_deleted");
		sb.append(" from up7_files where f_id=? ");
		
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setInt(1, fid);
			ResultSet r = db.ExecuteDataSet(cmd);

			if (r.next())
			{
				inf.idSvr 			= fid;
				inf.uid 			= r.getInt(1);
				inf.nameLoc 		= r.getString(2);
				inf.nameSvr 		= r.getString(3);
				inf.pathLoc 		= r.getString(4);
				inf.pathSvr 		= r.getString(5);
				inf.pathRel 		= r.getString(6);
				inf.md5 			= r.getString(7);
				inf.lenLoc 			= r.getLong(8);
				inf.sizeLoc 		= r.getString(9);
	            inf.FilePos 		= r.getLong(10);
	            inf.lenSvr 			= r.getLong(11);
				inf.perSvr 			= r.getString(12);
				inf.complete 		= r.getBoolean(13);
				inf.PostedTime 		= r.getDate(14);
				inf.deleted 		= r.getBoolean(15);
				ret = true;
			}
			cmd.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}
}