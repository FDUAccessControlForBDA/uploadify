package com.lufi.services.model;

/**
 * Created by Sunny on 2018/4/16.
 */

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name = "detect_history")
public class DetectHistoryPO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @Column
    private String user_id;

    @Column
    private String detect_detail;

    @Column
    private Timestamp detect_time;

    public DetectHistoryPO(){}

    public DetectHistoryPO(String user_id, String detect_detail, Timestamp time){
        this.user_id = user_id;
        this.detect_detail = detect_detail;
        this.detect_time = time;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDetect_detail() {
        return detect_detail;
    }

    public void setDetect_detail(String detect_detail) {
        this.detect_detail = detect_detail;
    }

    public Timestamp getDetect_time() {
        return detect_time;
    }

    public void setDetect_time(Timestamp detect_time) {
        this.detect_time = detect_time;
    }
}
