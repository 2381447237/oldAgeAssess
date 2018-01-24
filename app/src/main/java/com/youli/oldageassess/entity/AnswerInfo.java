package com.youli.oldageassess.entity;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by liutao on 2018/1/16.
 */

public class AnswerInfo implements Serializable{

    private int answerId;
    private double answerNo;
    private String answerText;

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public double getAnswerNo() {
        return answerNo;
    }

    public void setAnswerNo(double answerNo) {
        this.answerNo = answerNo;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public AnswerInfo() {

    }

    public AnswerInfo(int answerId, double answerNo, String answerText) {
        this.answerId = answerId;
        this.answerNo = answerNo;
        this.answerText = answerText;
    }

    @Override
    public String toString() {
        return "AnswerInfo [answerId=" + answerId + ", answerNo=" + answerNo
                + ", answerText=" + answerText + "]";
    }


    @Override
    public boolean equals(Object o) {//这个方法不能删除
        if((this.answerText.equals(((AnswerInfo)o).answerText))&&(this.answerId==((AnswerInfo)o).answerId)&&(this.answerNo==((AnswerInfo)o).answerNo)){
            return true;
        }

        return false;
    }
}
