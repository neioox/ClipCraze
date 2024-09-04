package de.neiox.models;

public class User {

    private String username;
    private String password;
    private String email;
    private String role;
    private String userid;
    private Setting setting;



    public User(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public String getUserid() {
        return userid;
    }




    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public void setUsername(String username) {
        
        
        this.username = username;

        

    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setRole(String role) {
        this.role = role;
    }



    public Setting getSetting() {
        return setting;
    }




    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", userid='" + userid + '\'' +
                '}';
    }
}
