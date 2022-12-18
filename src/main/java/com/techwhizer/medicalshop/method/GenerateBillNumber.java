package com.techwhizer.medicalshop.method;

import com.techwhizer.medicalshop.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GenerateBillNumber {

    public String generatePurchaseBillNum() {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();
            ps = connection.prepareStatement("select max(purchase_main_id) from tbl_purchase_main");
            rs = ps.executeQuery();
            String invoiceNum = null;

            if (rs.next()) {
                long id = rs.getInt(1) + 1;

                invoiceNum = String.format("%07d", id);
            }
            return "P"+invoiceNum;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBConnection.closeConnection(connection , ps , rs);

        }

    }

    public String getSaleBillNum() {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = new DBConnection().getConnection();
            ps = connection.prepareStatement("select max(sale_main_id) from tbl_sale_main");
            rs = ps.executeQuery();
            String invoiceNum = null;

            if (rs.next()) {
                long id = rs.getInt(1) + 1;

                invoiceNum = String.format("%07d", id);
            }
            return "INV"+invoiceNum;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBConnection.closeConnection(connection , ps , rs);

        }

    }
}
