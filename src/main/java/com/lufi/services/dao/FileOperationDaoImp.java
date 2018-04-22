package com.lufi.services.dao;

import com.lufi.services.model.DetectHistoryPO;
import com.lufi.services.model.FeedbackPO;
import com.lufi.utils.TimerUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository("FileOperationDao")
public class FileOperationDaoImp implements FileOperationDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public long addFeedback(String content, String contact){
        long rst;
        Session session = sessionFactory.getCurrentSession();
        Timestamp time = TimerUtil.getCurrentTime();
        FeedbackPO feedbackPO = new FeedbackPO(content, contact, time);
        rst = (Long)session.save(feedbackPO);
        return rst;
    }

    @Override
    public List<DetectHistoryPO> getHistories(String userId){
        Session session = sessionFactory.getCurrentSession();
        String hql = "from DetectHistoryPO history where history.user_id = ?";
        Query query = session.createQuery(hql).setParameter(0,userId);
        int count = query.list().size();
        if(count>0){
            List<DetectHistoryPO> ret = query.list();
            return ret;
        }
        return null;
    }

    @Override
    public DetectHistoryPO getHistory(String historyId){
        Session session = sessionFactory.getCurrentSession();
        String hql = "from DetectHistoryPO history where history.id = ?";
        Query query = session.createQuery(hql).setParameter(0,historyId);
        int count = query.list().size();
        if(count>0){
            DetectHistoryPO ret = (DetectHistoryPO) query.list();
            return ret;
        }
        return null;
    }

    @Override
    public long deleteHistory(String historyId){
        long rst = 0;
        Session session = sessionFactory.getCurrentSession();
        String hql = "from DetectHistoryPO history where history.id = ?";
        Query query = session.createQuery(hql).setParameter(0,historyId);
        int count = query.list().size();
        if(count>0){
            DetectHistoryPO dh = (DetectHistoryPO) query.list();
            session.delete(dh);
            rst = 1;
        }
        return rst;
    }
}
