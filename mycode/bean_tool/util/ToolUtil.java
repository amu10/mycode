package com.dinfo.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolUtil {
	
	public static String findType(String type) {
		String dataType=null;
		if(isIntegerType(type)){
			dataType="Integer";
		}else if(isStringType(type)){
			dataType="String";
		}else if("float".equals(type)){
			dataType="Float";
		}else if("double".equals(type)){
			dataType="Double";
		}
		return dataType;
	}
	public static  boolean isIntegerType(String type){
		String pat = "int|bit";
		Pattern pattern = Pattern.compile(pat);
		Matcher matcher = pattern.matcher(type);
		if(matcher.find()){
			return true;
		}
		return false;
	}
	public static  boolean isStringType(String type){
		String pat = "varchar|text|blob|date|time";
		Pattern pattern = Pattern.compile(pat);
		Matcher matcher = pattern.matcher(type);
		if(matcher.find()){
			return true;
		}
		return false;
	}
	
	public static void writeJava(Map<String, String> code){
		for(Map.Entry<String, String> entry :code.entrySet()){
			String java = entry.getKey();
			String codestr = entry.getValue();
			try {
			
				URI uri = ToolUtil.class.getResource("/").toURI();
				File file =new File("./code") ;
				if(!file.exists()){
					file.mkdir();
				}
				String newPath = file.getPath()+"/"+java;
				FileOutputStream out = new FileOutputStream(new File(newPath));
				out.write(codestr.getBytes());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
