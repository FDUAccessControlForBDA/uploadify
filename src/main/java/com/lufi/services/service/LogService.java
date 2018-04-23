package com.lufi.services.service;

import com.lufi.services.dao.FileOperationDaoImp;
import com.lufi.services.model.DetectHistoryPO;
import com.lufi.utils.Constants;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

@Service("LogService")
public class LogService {

    @Autowired
    private FileOperationDaoImp logDao;

    @Transactional
    public int addFeedback(String content, String contact) {
        long ret = logDao.addFeedback(content, contact);
        if (ret > 0)
            return 1;
        return 0;
    }

    @Transactional
    public List<DetectHistoryPO> getHistories(String userId) {
        return logDao.getHistories(userId);
    }

    @Transactional
    public DetectHistoryPO getHistory(String historyId) {
        return logDao.getHistory(historyId);
    }

    @Transactional
    public long deleteHistory(String historyId) {
        return logDao.deleteHistory(historyId);
    }

    @Transactional
    public long addHistory(String userId, String detectFiles, String reportPath, String detail){
        return logDao.addHistory(userId,detectFiles,reportPath,detail);
    }
}
