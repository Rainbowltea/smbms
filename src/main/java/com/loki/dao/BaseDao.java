package com.loki.dao;
//操作数据库的公共类
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class BaseDao {
    private static String driver;
    private static String url;
    private static String username;
    private static String password;
    //静态代码块
    static {
        Properties pro = new Properties();
        //通过类加载器读取对应的资源
        InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("dp.properties");
        try {
            pro.load(is);

        } catch (IOException e) {
            e.printStackTrace();
        }
        driver=pro.getProperty("driver");
        url=pro.getProperty("url");
        username=pro.getProperty("username");
        password=pro.getProperty("password");
    }
    //获取数据库的链接
    public static Connection getConnection(){
        Connection connection=null;
        try {
            Class.forName(driver);
             connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
    //编写查询公共类
    public static ResultSet execute(Connection connection,String sql,Object[] params,PreparedStatement preparedStatement,ResultSet resultSet) throws SQLException {

         preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1,params[i]);
        }
         resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    //编写增删改公共方法
    public static int execute(Connection connection,String sql,Object[] params,PreparedStatement preparedStatement) throws SQLException {

        preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1,params[i]);
        }
        int  updateRows = preparedStatement.executeUpdate();
        return updateRows;
    }

    //释放资源
    public static boolean closeResource(Connection connection,PreparedStatement preparedStatement,ResultSet resultSet){
        boolean flag=true;
        if(resultSet!=null){
            try {
                resultSet.close();
                //GC回收
                resultSet=null;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag=false;
            }
        }
        if(connection!=null){
            try {
                connection.close();
                //GC回收
                connection=null;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag=false;
            }
        }
        if(preparedStatement!=null){
            try {
                preparedStatement.close();
                //GC回收
                preparedStatement=null;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag=false;
            }
        }

        return flag;
    }
}
