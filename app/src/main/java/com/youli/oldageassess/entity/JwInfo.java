package com.youli.oldageassess.entity;

/**
 * Created by liutao on 2018/1/26.
 *
 * http://183.194.4.58:81/Json/Get_JW.aspx
 *
 * 居委
 */

public class JwInfo {


    /**
     * JWDM : 3046
     * JWMC : 达安城居委会
     * JWJD : 605
     * RecordCount : 0
     */

    private String JWDM;
    private String JWMC;
    private String JWJD;
    private int RecordCount;

    public JwInfo(String JWMC) {
        this.JWMC = JWMC;
    }

    public String getJWDM() {
        return JWDM;
    }

    public void setJWDM(String JWDM) {
        this.JWDM = JWDM;
    }

    public String getJWMC() {
        return JWMC;
    }

    public void setJWMC(String JWMC) {
        this.JWMC = JWMC;
    }

    public String getJWJD() {
        return JWJD;
    }

    public void setJWJD(String JWJD) {
        this.JWJD = JWJD;
    }

    public int getRecordCount() {
        return RecordCount;
    }

    public void setRecordCount(int RecordCount) {
        this.RecordCount = RecordCount;
    }
}
