package com.youli.oldageassess.entity;

/**
 * Created by liutao on 2018/1/16.
 */

public class AnswerInfo {

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


    @Override
    public String toString() {
        return "AnswerInfo [answerId=" + answerId + ", answerNo=" + answerNo
                + ", answerText=" + answerText + "]";
    }
}
