package com.lufi.services.dao;

import com.lufi.services.model.DetectHistoryPO;
import com.lufi.services.model.FileOperationLogPO;

import java.sql.Timestamp;
import java.util.List;

public interface FileOperationDao {

    long addLog(String fileName, String md5, int flag, Timestamp uploadTime);
    List<FileOperationLogPO> getLogList(int flag);
    long deleteLog(int flag);
    long modifyLog(String fileName, int flag);
    long addFeedback(String content, String contact);
    List<DetectHistoryPO> getHistories(String userId);
    DetectHistoryPO getHistory(String historyId);
    long deleteHistory(String historyId);
}
