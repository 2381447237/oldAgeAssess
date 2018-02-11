package com.youli.oldageassess.entity;

/**
 * Created by liutao on 2018/1/25.
 *
 * http://183.194.4.58:81/Json/Get_JD.aspx
 *
 * 街道
 */

public class JdInfo {


    /**
     * JDDM : 0601
     * JDMC : 江宁路街道
     * JDQX : 06
     * RecordCount : 0
     * Checked : false
     */

    private String JDDM;
    private String JDMC;
    private String JDQX;
    private int RecordCount;
    private boolean Checked;

    public JdInfo(String JDMC) {
        this.JDMC = JDMC;
    }

    public String getJDDM() {
        return JDDM;
    }

    public void setJDDM(String JDDM) {
        this.JDDM = JDDM;
    }

    public String getJDMC() {
        return JDMC;
    }

    public void setJDMC(String JDMC) {
        this.JDMC = JDMC;
    }

    public String getJDQX() {
        return JDQX;
    }

    public void setJDQX(String JDQX) {
        this.JDQX = JDQX;
    }

    public int getRecordCount() {
        return RecordCount;
    }

    public void setRecordCount(int RecordCount) {
        this.RecordCount = RecordCount;
    }

    public boolean isChecked() {
        return Checked;
    }

    public void setChecked(boolean Checked) {
        this.Checked = Checked;
    }

    @Override
    public String toString() {
        return "JdInfo{" +
                "JDMC='" + JDMC + '\'' +
                '}';
    }
}
