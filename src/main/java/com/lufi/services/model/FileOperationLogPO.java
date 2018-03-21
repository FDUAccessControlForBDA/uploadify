package com.lufi.services.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "file_operation_log")
public class FileOperationLogPO {

    //记录文件操作的日志

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @Column
    private String file_name;

    @Column
    private String md5;

    @Column
    private int flag;

    @Column
    private Timestamp upload_time;

    public FileOperationLogPO(){}

    public FileOperationLogPO(String file_name, String md5, int flag, Timestamp upload_time) {
        this.file_name = file_name;
        this.md5 = md5;
        this.flag = flag;
        this.upload_time = upload_time;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public Timestamp getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(Timestamp upload_time) {
        this.upload_time = upload_time;
    }

    @Override
    public String toString() {
        return "FileOperationLogPO{" +
                "Id=" + Id +
                ", file_name='" + file_name + '\'' +
                ", md5='" + md5 + '\'' +
                ", flag=" + flag +
                ", upload_time=" + upload_time +
                '}';
    }
}
