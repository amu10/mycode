package com.dinfo.tool;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;

import com.dinfo.bean.BeanConnection;
import com.dinfo.bean.Table;

public class BeanDb {
	
	
		private Connection conn=null;
		private QueryRunner runner;
		public BeanDb(){
			init();
			runner = new QueryRunner();
		}
		public void init()
		{
			loadProperties();
			try {
				Class.forName(BeanConnection.driver);
				conn = DriverManager.getConnection(BeanConnection.url,BeanConnection.username,BeanConnection.password);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
		}
		
		public  void loadProperties(){
			
			Properties properties = new Properties();
			FileReader reader;
			try {
				reader = new FileReader("config.properties");
				properties.load(reader);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BeanConnection.drivertype = properties.get("drivertype").toString(); 
			BeanConnection.username = properties.get("username").toString(); 
			BeanConnection.password = properties.get("password").toString(); 
			BeanConnection.url = properties.get("url").toString(); 
			BeanConnection.driver = properties.get("driver").toString(); 
			String tables = properties.get("tables").toString(); 
			String[] tableseg = tables.split(";");
			for(String s :tableseg){
				String[] sseg = s.split("=");
				CreateBean.tableMap.put(sseg[0], sseg[0]);
			}
		}
	
		public  List<Table> getTableField(String tablename){
			String sql = "show COLUMNS from  "+tablename;
			List<Table> tables = null;
			try {
				tables = runner.query(conn, sql, new BeanListHandler<Table>(Table.class));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return  tables;
		}
		
		public List<String> getAllTables(){
			
			String sql = "show tables";
			List<String> tables = null;
			try {
				tables = runner.query(conn, sql,new ColumnListHandler<String>());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return  tables;
		}
		
}
