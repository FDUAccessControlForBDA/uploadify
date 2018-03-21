package com.lufi.services.dao;

import com.lufi.services.model.FileOperationLogPO;
import com.lufi.utils.Constants;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository("FileOperationDao")
public class FileOperationLogDaoImp implements FileOperationLogDao{

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public long addLog(String fileName, String md5, int flag,Timestamp uploadTime){
        long rst = 0;

        //添加Log到数据库
        Session session = sessionFactory.getCurrentSession();
        String hql = "from FileOperationLogPO log where log.file_name = ? and log.md5 = ?";
        Query query = session.createQuery(hql).setParameter(0,fileName).setParameter(1,md5);
        if(query.list().size()<=0){
            FileOperationLogPO logPO = new FileOperationLogPO(fileName,md5,flag,uploadTime);
            rst = (Long) session.save(logPO);
        }
        return  rst;
    }

    @Override
    public List<FileOperationLogPO> getLogList(int flag){
        Session session = sessionFactory.getCurrentSession();
        String hql = "from FileOperationLogPO log where log.flag = ?";
        Query query = session.createQuery(hql).setParameter(0,flag);
        int count = query.list().size();
        if(count>0){
            List<FileOperationLogPO> ret = query.list();
            return ret;
        }
        return null;
    }

    @Override
    public long deleteLog(int flag){
        long rst = 0;
        Session session = sessionFactory.getCurrentSession();
        String hql = "from FileOperationLogPO log where log.flag = ?";
        Query query = session.createQuery(hql).setParameter(0,flag);
        int count = query.list().size();
        if(count>0){
            List<FileOperationLogPO> logList = query.list();
            for(FileOperationLogPO log : logList){
                 session.delete(log);
            }
            rst = 1;
        }
        return rst;
    }


    @Override
    public long modifyLog(String fileName, int flag){
        long rst = 0;
        Session session = sessionFactory.getCurrentSession();
        String hql = "from FileOperationLogPO log where log.file_name = ?";
        Query query = session.createQuery(hql).setParameter(0,fileName);
        if(query.uniqueResult()!=null){
            hql = "update FileOperationLogPO log set log.flag = ? where log.file_name = ?";
            query = session.createQuery(hql).setParameter(0,flag).setParameter(1,fileName);
            rst = query.executeUpdate();
        }
        return  rst;
    }



}
