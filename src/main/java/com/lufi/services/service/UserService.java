package com.lufi.services.service;

import com.lufi.services.dao.UserDaoImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("UserService")
public class UserService {

    @Autowired
    private UserDaoImp userDao;

    @Transactional
    public long signup(String userName,String password){
        return userDao.signup(userName,password);
    }

    @Transactional
    public long signin(String userName,String password){
        return userDao.signin(userName,password);
    }
}
