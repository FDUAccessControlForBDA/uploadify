package com.lufi.services.dao;

public interface UserDao {
    public long signup(String userName,String password);
    public long signin(String userName,String password);
}
