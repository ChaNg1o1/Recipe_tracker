package com.chang1o.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    private static String DB_URL;
    private static String DB_USERNAME;
    private static String DB_PASSWORD;
    private static String DB_DRIVER;

    private static DBUtil instance;
    private static boolean isInitialized = false;

    private DBUtil(){
        loadConfiguration();
    }

   private static void loadConfiguration(){
        if (isInitialized) return;

        try{
            Properties props = new Properties();
            var inputStream = DBUtil.class.getClassLoader().getResourceAsStream("database.properties");
            
            // 检查资源文件是否存在，避免 NullPointerException
            if (inputStream == null) {
                System.out.println("无法找到 database.properties 配置文件，将使用默认配置");
                setDefaultConfiguration();
                return;
            }
            
            props.load(inputStream);
            inputStream.close();
            //DBUtil.class 指向ClassLoader，用getClassLoader方法获得JAR结构找到database
            // 文件并用getResourceAsStream做InputStream交给pros对象
            DB_URL = props.getProperty("db.url");
            DB_USERNAME = props.getProperty("db.username");
            DB_PASSWORD = props.getProperty("db.password");
            DB_DRIVER = props.getProperty("db.driver");

            Class.forName(DB_DRIVER);
            isInitialized = true;
        }catch (IOException e){
            System.out.println("无法加载数据库配置将会使用默认配置");
            setDefaultConfiguration();
        }catch (ClassNotFoundException e){
            System.out.println("数据库驱动加载失败，检查驱动是否正确安装");
            throw new RuntimeException("数据库驱动未找到",e);
            //throw将新的异常e抛出导致程序停止
        }
   }

   private static void setDefaultConfiguration(){
        DB_URL = "jdbc:mysql://localhost:3306/recipe_db";
        DB_USERNAME = "root";
        DB_PASSWORD = "";
        DB_DRIVER = "com.mysql.cj.jdbc.Driver";

        try{
            Class.forName(DB_DRIVER);
            isInitialized = true;

        }catch (ClassNotFoundException e){
            System.out.println("默认数据库驱动加载失败");
        }
   }

   public static DBUtil getInstance(){
        if (instance == null){
            instance = new DBUtil();
        }
        return instance;
   }

   public Connection getConnection() throws SQLException {
       if (!isInitialized) {
           loadConfiguration();
       }

       try {
           return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
       } catch (SQLException e) {
           throw new SQLException("数据库连接失败");
       }
   }

   public static void closeConnection(Connection conn){
        if (conn != null){
            try{
                conn.close();
            }catch (SQLException e){
                System.out.println("关闭数据库发生异常");
            }
        }
   }

   public boolean testConnection(){
        try(Connection conn = getConnection()){
            return conn != null & !conn.isClosed();
        }catch (SQLException e){
            return false;
        }
   }
}