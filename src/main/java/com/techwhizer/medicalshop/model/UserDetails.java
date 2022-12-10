package com.techwhizer.medicalshop.model;

public class UserDetails {

    private int userID;
    private String accountStatus;
    private long phone;
    private String firstName, lastName, gender, role, email,
            username, password, fullAddress, userImage, createdTime;
    private int role_id;

    //  public UserProfile (){}

    public UserDetails(int userID, String accountStatus, long phone, String firstName, String lastName,
                       String gender, String role, String email, String username, String password,
                       String fullAddress, String userImage, String createdTime , int role_id) {
        this.userID = userID;
        this.accountStatus = accountStatus;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.role = role;
        this.email = email;
        this.username = username;
        this.password = password;
        this.fullAddress = fullAddress;
        this.userImage = userImage;
        this.createdTime = createdTime;
        this.role_id = role_id;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}
