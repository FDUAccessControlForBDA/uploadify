package com.lufi.services.dao;

import com.lufi.services.model.FileOperationLogPO;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

public interface FileOperationLogDao {

    public long addLog(String fileName, String md5, int flag, Timestamp uploadTime);
    public List<FileOperationLogPO> getLogList(int flag);
    public long deleteLog(int flag);
    public long modifyLog(String fileName, int flag);

}
