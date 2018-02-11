package com.youli.oldageassess.entity;

import java.io.Serializable;

/**
 * Created by liutao on 2018/2/8.
 *
 *  服务器记录的答案
 * http://183.194.4.58:81/Json/Get_Qa_Receiv_Special.aspx?SQH=0013CH101201801020001
 *
 * {"ID":5137,"SQH":"0013CH101201801020001","DETIL_ID":1,"INPUT_VALUE":"杭逸萍","MASTER_ID":1,"RecordCount":0}
 */

public class ResultInfo implements Serializable{


    /**
     * ID : 5137
     * SQH : 0013CH101201801020001
     * DETIL_ID : 1
     * INPUT_VALUE : 杭逸萍
     * MASTER_ID : 1
     * RecordCount : 0
     */

    private int ID;
    private String SQH;
    private int DETIL_ID;//要这个
    private String INPUT_VALUE;//要这个
    private int MASTER_ID;
    private int RecordCount;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getSQH() {
        return SQH;
    }

    public void setSQH(String SQH) {
        this.SQH = SQH;
    }

    public int getDETIL_ID() {
        return DETIL_ID;
    }

    public void setDETIL_ID(int DETIL_ID) {
        this.DETIL_ID = DETIL_ID;
    }

    public String getINPUT_VALUE() {
        return INPUT_VALUE;
    }

    public void setINPUT_VALUE(String INPUT_VALUE) {
        this.INPUT_VALUE = INPUT_VALUE;
    }

    public int getMASTER_ID() {
        return MASTER_ID;
    }

    public void setMASTER_ID(int MASTER_ID) {
        this.MASTER_ID = MASTER_ID;
    }

    public int getRecordCount() {
        return RecordCount;
    }

    public void setRecordCount(int RecordCount) {
        this.RecordCount = RecordCount;
    }
}
