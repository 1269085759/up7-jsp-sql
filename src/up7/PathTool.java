package up7;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;

public class PathTool {

	public static String getName(String n){
		File f = new File(n);
		return f.getName();
	}
	
	//返回值：exe
	public static String getExtention(String n)
	{
		String name = getName(n);

		int extIndex = name.lastIndexOf(".");
		//有扩展名
		if(-1 != extIndex)
		{
			String ext = name.substring(extIndex + 1);
			return ext;
		}
		return "";
	}
	
	public static Boolean exist(String v)
	{
		File f = new File(v);
		return f.exists();
	}
	
	public static void createDirectory(String v){

		File fd = new File(v);		
		//fix():不创建文件夹
		if(!fd.exists()) fd.mkdirs();
	}
	
	//规范化路径，与操作系统保持一致。
	public static String canonicalPath(String v) throws IOException{
		File f = new File(v);
		return f.getCanonicalPath();
	}
	
	public static String combine(String a,String b) throws IOException
	{
		boolean split = a.endsWith("\\");
		if(!split) split = a.endsWith("/");		
		//没有斜杠
		if(!split)
		{
			File ps = new File(a.concat("/").concat(b));
			return ps.getCanonicalPath().replace('\\', '/');
		}//有斜框
		else{
			File ps = new File(a.concat(b));
			return ps.getCanonicalPath().replace('\\', '/');
		}
	}
	/**
	 * 返回byte的数据大小对应的文本
	 * @param size
	 * @return
	 */
	public static String getDataSize(long size){
		DecimalFormat formater = new DecimalFormat("####.00");
		if(size<1024){
			return size+"bytes";
		}else if(size<1024*1024){
			float kbsize = size/1024f;  
			return formater.format(kbsize)+"KB";
		}else if(size<1024*1024*1024){
			float mbsize = size/1024f/1024f;  
			return formater.format(mbsize)+"MB";
		}else if(size<1024*1024*1024*1024){
			float gbsize = size/1024f/1024f/1024f;  
			return formater.format(gbsize)+"GB";
		}else{
			return "size: error";
		}
	}
	
	public static String url_decode(String v)
	{
		if(v== null) return "";
		if(StringUtils.isEmpty(v)) return "";
		
		v = v.replaceAll("\\+","%20");
		try {
			v = URLDecoder.decode(v,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			v = "";
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//utf-8解码
		return v;
	}
}
