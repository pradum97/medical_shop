package com.techwhizer.medicalshop.util;

import com.techwhizer.medicalshop.method.Method;
import com.techwhizer.medicalshop.PropertiesLoader;

import java.sql.*;
import java.util.Properties;

public class DBConnection {

    Method method;

    public Connection getConnection() {
        method = new Method();

        Properties properties = new PropertiesLoader().getDbDetails();
        String DB_URL = properties.getProperty("DB_URL");
        String DB_USERNAME = properties.getProperty("DB_USERNAME");
        String DB_PASSWORD = properties.getProperty("DB_PASSWORD");

        try {

            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

        } catch (SQLException e) {
            return null;
        }

    }

    public static void rollBack(Connection connection){
        try {
            if (null != connection){
                connection.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection(Connection connection, PreparedStatement ps, ResultSet rs) {
        try {
            if (null != connection) {
                connection.close();
            }
            if (null != ps) {
                ps.close();
            }
            if (null != rs) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
