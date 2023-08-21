package com.Utils;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import static com.data.Globals.*;

public class JDBCUtils {
    //连接数据库
    public static Connection getConnections(){
        String SQLUrl = "jdbc:mysql://"+DB_BASE_URI+"/"+DB_NAME+"?useUnicode=true&characterEncoding=utf-8&autoReconnect=true&useSSL=false";
        String user = DB_USER;
        String pwd = DB_PASSWORD;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(SQLUrl,user,pwd);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }
    //更新类语句
    public static void upDate(String updateSql){
        Connection connection =getConnections();
        QueryRunner queryRunner = new QueryRunner();
        try {
            queryRunner.update(updateSql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    //查询类语句
    //查询结果为一个数据
    public static Object singleData(String sql){
        Connection connection = getConnections();
        QueryRunner queryRunner = new QueryRunner();
        Object result = null;
        try {
            result = queryRunner.query(connection,sql,new ScalarHandler<>());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }
    //查询结果为一条列表数据
    public static Object oneData(String sql){
        Connection connection = getConnections();
        QueryRunner queryRunner = new QueryRunner();
        Object result = null;
        try {
            result = queryRunner.query(connection,sql,new MapHandler());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }
    //查询结果为多条列表数据
    public static Object listData(String sql){
        Connection connection = getConnections();
        QueryRunner queryRunner = new QueryRunner();
        Object result = null;
        try {
            result = queryRunner.query(connection,sql,new MapListHandler());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }
    //断开数据库连接
    public static void closeSql(Connection connection){
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
