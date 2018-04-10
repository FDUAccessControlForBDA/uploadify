package com.lufi.services.model;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class UserPO {

    //记录用户信息

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    public UserPO(){}

    public UserPO(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserPO{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
