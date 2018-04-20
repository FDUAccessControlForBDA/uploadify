package com.lufi.services.model;

/**
 * Created by Sunny on 2018/4/16.
 */

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "user_feedback")
public class FeedbackPO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @Column
    private String feedback_content;

    @Column
    private String feedback_contact;

    @Column
    private Timestamp feedback_time;

    public FeedbackPO(){}

    public FeedbackPO(String content, String contact, Timestamp time) {
        this.feedback_contact = contact;
        this.feedback_content = content;
        this.feedback_time = time;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getFeedback_content() {
        return feedback_content;
    }

    public void setFeedback_content(String feedback_content) {
        this.feedback_content = feedback_content;
    }

    public String getFeedback_contact() {
        return feedback_contact;
    }

    public void setFeedback_contact(String feedback_contact) {
        this.feedback_contact = feedback_contact;
    }

    public Timestamp getFeedback_time() {
        return feedback_time;
    }

    public void setFeedback_time(Timestamp feedback_time) {
        this.feedback_time = feedback_time;
    }
}
