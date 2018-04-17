package com.lufi.services.dao;

import com.lufi.services.model.UserPO;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("UserDao")
public class UserDaoImp implements UserDao{

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public long signup(String userName,String password){
        long rst = 0;
        //添加用户到数据库
        Session session = sessionFactory.getCurrentSession();
        String hql = "from UserPO user where user.userName = ?";
        Query query = session.createQuery(hql).setParameter(0,userName);
        if(query.list().size()<=0){
            UserPO user = new UserPO(userName,password);
            session.save(user);
            query = session.createQuery(hql).setParameter(0,userName);
            user = (UserPO) query.uniqueResult();
            if(user != null){
                rst = user.getId();
            }
        }
        return rst;
    }

    @Override
    public long signin(String userName,String password){
        long rst = 0;
        Session session = sessionFactory.getCurrentSession();
        String hql = "from UserPO user where user.userName = ? and user.password = ?";
        Query query = session.createQuery(hql).setParameter(0,userName).setParameter(1,password);
        UserPO user = (UserPO) query.uniqueResult();
        if(user != null){
            rst = user.getId();
        }
        return rst;
    }
}
