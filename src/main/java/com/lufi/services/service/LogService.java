package com.lufi.services.service;

import com.lufi.services.dao.FileOperationLogDaoImp;
import com.lufi.services.model.FileOperationLogPO;
import com.lufi.utils.Constants;
import com.lufi.utils.TimerUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scala.collection.immutable.Stream;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

@Service("LogService")
public class LogService {

    @Autowired
    private FileOperationLogDaoImp logDao;

    @Transactional
    public int  addLog(String file_name, String md5, int flag, Timestamp upload_time){
        long ret = logDao.addLog(file_name, md5, flag, upload_time);
        if(ret > 0){
            return 1;
        }
        return 0;
    }

    @Transactional
    public int deleteLog(int flag){
        List<FileOperationLogPO> deleteLogList = logDao.getLogList(Constants.FLAG_INVALID);

        if(deleteLogList.size()>0){
            for (FileOperationLogPO log : deleteLogList){
                String fileName = log.getFile_name();
                File file = new File(Constants.ADDRESS_STORE+fileName);
                if(file.exists()){
                    FileUtils.deleteQuietly(file);
                    System.out.println("删除文件:"+fileName);
                }else{
                    return 0;
                }
            }

            if(logDao.deleteLog(Constants.FLAG_INVALID)>0){
                return 1;
            }
        }
        return 0;
     }

     @Transactional
    public int modifyLog(String fileName, int flag){
         long ret = logDao.modifyLog(fileName, flag);
         if(ret > 0){
             return 1;
         }
         return 0;
     }

}
