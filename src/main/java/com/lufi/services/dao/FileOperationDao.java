package com.lufi.services.dao;

import com.lufi.services.model.DetectHistoryPO;

import java.sql.Timestamp;
import java.util.List;

public interface FileOperationDao {

    long addFeedback(String content, String contact);
    List<DetectHistoryPO> getHistories(String userId);
    DetectHistoryPO getHistory(String historyId);
    long deleteHistory(String historyId);
}
