package com.example.witssocial;

public class CreateUserClass {
    String Username,email, cellPhoneNumber,password,name;

    public CreateUserClass() {
    }

    public CreateUserClass(String username) {
        Username = username;
    }

    public CreateUserClass(String username, String email, String cellPhoneNumber, String password, String name) {
        Username = username;
        this.email = email;
        this.cellPhoneNumber = cellPhoneNumber;
        this.password = password;
        this.name = name;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
