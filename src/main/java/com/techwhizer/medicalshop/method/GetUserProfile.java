package com.techwhizer.medicalshop.method;

import com.techwhizer.medicalshop.PropertiesLoader;
import com.techwhizer.medicalshop.model.UserDetails;
import com.techwhizer.medicalshop.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetUserProfile {
    private Method method;
    private DBConnection dbConnection;

    public UserDetails getUser(int userId) {

        method = new Method();
        dbConnection = new DBConnection();

        Connection connection = null;
        PreparedStatement userPs = null;
        ResultSet userRs = null;
        UserDetails userDetails = null;

        try {
            connection = dbConnection.getConnection();

            if (null == connection) {
                System.out.println("GetUserProfile : connection Failed");
                return null;
            }
            userPs = connection.prepareStatement(new PropertiesLoader().getReadProp().getProperty("USERS"));
            userPs.setInt(1, userId);
            userRs = userPs.executeQuery();

            if (userRs.next()) {

                int id = userRs.getInt("user_id");
                long phone = userRs.getLong("phone");
                String user_image = userRs.getString("USER_IMG_PATH");
                String password = userRs.getString("password");
                String first_name = userRs.getString("first_name");
                String last_name = userRs.getString("last_name");
                String gender = userRs.getString("gender");
                String email = userRs.getString("email");
                String role = userRs.getString("role");
                int role_id = userRs.getInt("role_id");
                String username = userRs.getString("username");
                String address = userRs.getString("full_address");
                String create_time = userRs.getString("created_time");
                int accountStatus = userRs.getInt("account_status");
                String acStatus = switch (accountStatus) {
                    case 0 -> "Inactive";
                    default -> "Active";
                };

                userDetails = new UserDetails(id, acStatus, phone, first_name, last_name, gender, role,
                        email, username, password, address, user_image, create_time, role_id);

            }
            return userDetails;


        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {

            DBConnection.closeConnection(connection, userPs, userRs);
        }
    }

    public ObservableList<UserDetails> getAllUser() {

        method = new Method();
        dbConnection = new DBConnection();
        ObservableList<UserDetails> usersList = FXCollections.observableArrayList();

        Connection connection = null;
        PreparedStatement userPs = null;
        ResultSet userRs = null;

        try {

            connection = dbConnection.getConnection();

            if (null == connection) {
                System.out.println("GetUserProfile : connection Failed");
                return null;
            }

            userPs = connection.prepareStatement("SELECT  (TO_CHAR(created_time, 'YYYY-MM-DD HH12:MI:SS AM')) as created_time ,* FROM TBL_USERS tu \n" +
                    "    left join tbl_role tr on tu.role_id = tr.role_id ORDER BY USER_ID ASC");
            userRs = userPs.executeQuery();

            while (userRs.next()) {

                int id = userRs.getInt("user_id");
                long phone = userRs.getLong("phone");
                String user_image = userRs.getString("USER_IMG_PATH");
                String password = userRs.getString("password");
                String first_name = userRs.getString("first_name");
                String last_name = userRs.getString("last_name");
                String gender = userRs.getString("gender");
                String email = userRs.getString("email");
                String role = userRs.getString("role");
                int role_id = userRs.getInt("role_id");
                String username = userRs.getString("username");
                String address = userRs.getString("full_address");
                String create_time = userRs.getString("created_time");
                int accountStatus = userRs.getInt("account_status");

                String acStatus = switch (accountStatus) {
                    case 0 -> "Inactive";
                    default -> "Active";
                };


                usersList.add(new UserDetails(id, acStatus, phone, first_name, last_name, gender, role,
                        email, username, password, address, user_image, create_time, role_id));

            }
            return usersList;


        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {

            DBConnection.closeConnection(connection, userPs, userRs);
        }
    }
}
