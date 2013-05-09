package com.dinfo.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.dinfo.bean.Table;
import com.dinfo.util.ToolUtil;

public class CreateBean {
	public  static Map<String,String> tableMap = new HashMap<String, String>();
	private BeanDb db = new BeanDb(); 
	
	public  void  createBean(){
		List<String> tables = db.getAllTables();
		for(String table: tables){
			List<Table> tableFields = db.getTableField(table);
			Map<String,String> map = analyTables(tableFields);
			String javaName = table;
			Map<String,String> code =BeanCommon.createJavaCode(javaName, map);
			ToolUtil.writeJava(code);
		}
	}
	
	

	public Map<String,String> analyTables(List<Table> tables){
			Map<String,String> map = new HashMap<String, String>();
			for(Table table : tables){
				String name = table.getField();
				String type =ToolUtil.findType(table.getType());
				map.put(name, type);
			}
			
			return map;
	}
	
	
	
	
	public static void main(String[] argv){
		 	new CreateBean().createBean();
	}
}
