package cn.javass.spring.chapter9;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;

public class TranditionalTransactionTest {
   
    private static ApplicationContext ctx;
    
    //id自增主键从0开始
    private static final String CREATE_TABLE_SQL = "create table test" +
    "(id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
    "name varchar(100))";
    private static final String DROP_TABLE_SQL = "drop table test";
    
    private static final String INSERT_SQL = "insert into test(name) values(?)";
    private static final String COUNT_SQL = "select count(*) from test";
    
    @BeforeClass
    public static void setUpClass() {
        String[] configLocations = new String[] {
                "classpath:chapter9/applicationContext-jta-derby.xml"};
        ctx = new ClassPathXmlApplicationContext(configLocations);
    }
    
    

    @Test
    public void testTranditionalJTAAndNotXATransaction() throws Exception {
        initTransactionManager();
        Connection conn = null;
        UserTransaction tx = null;
        try {
            tx = getUserTransaction();
            tx.begin();
            conn = getDataSource().getConnection();
            //2.声明SQL
            String sql = "select * from INFORMATION_SCHEMA.SYSTEM_TABLES";
            PreparedStatement pstmt = conn.prepareStatement(sql);//2.预编译SQL
            ResultSet rs = pstmt.executeQuery();//3.执行SQL
            process(rs);//4.处理结果集
            closeResultSet(rs);//5.释放结果集
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }
    
    private void process(ResultSet rs) throws SQLException {
        while(rs.next()) {
            String value = rs.getString("TABLE_NAME");
            System.out.println("Column TABLENAME:" + value);
        }        
    }


    private void closeResultSet(ResultSet rs) {
        if(rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                //处理异常
            }
        }
    }

    private void closeStatement(Statement stmt) {
        if(stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
                //处理异常
            }
        }
    }
    
    private UserTransaction getUserTransaction() {
        return new UserTransactionImp();
    }
    
    public void initTransactionManager() throws SystemException {
        UserTransactionManager tm = new UserTransactionManager();
        tm.setForceShutdown(true);
        tm.init();
    }
    
    private DataSource getDataSource() {
        AtomikosNonXADataSourceBean dataSourceBean = new AtomikosNonXADataSourceBean();
        dataSourceBean.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSourceBean.setUrl("jdbc:hsqldb:mem:test");
        dataSourceBean.setUser("sa");
        dataSourceBean.setPassword("");
        dataSourceBean.setUniqueResourceName("jdbc/test");
        return dataSourceBean;
    }
    
    
    
    @Test
    public void testJtaAndXATransaction() throws Exception {
        DataSource dataSource1 = ctx.getBean("dataSource1", DataSource.class);
        DataSource dataSource2 = ctx.getBean("dataSource2", DataSource.class);
        UserTransaction tx = ctx.getBean("atomikosUserTransaction", UserTransaction.class);
        Connection conn1 = null;
        Connection conn2 = null;
        try {
            tx.begin();
            conn1 = dataSource1.getConnection();
            conn1.prepareStatement(CREATE_TABLE_SQL).execute();
            conn2 = dataSource2.getConnection();
            conn2.prepareStatement(CREATE_TABLE_SQL).execute();
            PreparedStatement pstmt12 = conn1.prepareStatement(INSERT_SQL);
            pstmt12.setString(1, "test");
            pstmt12.executeUpdate();
            PreparedStatement pstmt22 = conn2.prepareStatement(INSERT_SQL);
            pstmt22.setString(1, "test");
            pstmt22.executeUpdate();
            //throw new Exception("测试回滚是否起作用");
            tx.commit();
            assertCountTestTableExpected(1, conn1, conn2);
        }
        catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
            assertCountTestTableExpected(0, conn1, conn2);
        } finally {
            conn1.close();
            conn2.close();
        }
        
        dataSource1.getConnection().prepareStatement(DROP_TABLE_SQL).execute();
        dataSource2.getConnection().prepareStatement(DROP_TABLE_SQL).execute();
        
    }
    
    private void assertCountTestTableExpected(int expected, Connection conn1, Connection conn2) throws SQLException {
        
        ResultSet rs1 = conn1.prepareStatement(COUNT_SQL).executeQuery();
        ResultSet rs2 = conn2.prepareStatement(COUNT_SQL).executeQuery();
        rs1.next();
        rs2.next();
        long id1 = rs1.getLong(1);
        long id2 = rs2.getLong(1);
        Assert.assertEquals(id1, id2);
        Assert.assertEquals(1, id1);
    }
  
    
}
