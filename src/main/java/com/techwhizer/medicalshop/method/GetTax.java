package com.techwhizer.medicalshop.method;

import com.techwhizer.medicalshop.PropertiesLoader;
import com.techwhizer.medicalshop.model.GstModel;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetTax {


    public   ObservableList<GstModel> getGst() {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = new DBConnection().getConnection();

            if (null == connection) {
                return null;
            }

            ps = connection.prepareStatement(new PropertiesLoader().getReadProp().getProperty("GET_TAX"));
            rs = ps.executeQuery();

            ObservableList<GstModel> gstModels = FXCollections.observableArrayList();

            while (rs.next()) {

                // tax

                int taxID = rs.getInt("tax_id");
                int hsn_sac = rs.getInt("hsn_sac");
                int sgst = rs.getInt("sgst");
                int cgst = rs.getInt("cgst");
                int igst = rs.getInt("igst");
                String tax_description = rs.getString("description");
                String gstName = rs.getString("gstName");

              gstModels.add(new GstModel(taxID,hsn_sac , sgst, cgst, igst, gstName, tax_description));
            }


            return gstModels;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {

            DBConnection.closeConnection(connection, ps, rs);
        }

    }
}
