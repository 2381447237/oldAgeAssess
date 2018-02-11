package com.youli.oldageassess.entity;

import java.io.Serializable;

/**
 * Created by liutao on 2018/1/23.
 *
 * http://web.youli.pw:81/Json/Get_Staff.aspx
 *
 * 操作员信息
 */

public class AdminInfo implements Serializable{


    /**
     * ID : 2
     * NAME : 黄涤非
     * INPUT_CODE : 801170
     * PWD : WnllRjkwRXZyYW89
     * PHONE :
     * EMAIL :
     * PHOTO : null
     * CREATE_DATE : 2018-01-20T15:37:24.467
     * CREATE_STAFF : 1
     * UPDATE_DATE : 2018-01-24T09:43:17.023
     * UPDATE_STAFF : 1
     * STOP : false
     * PASS_TYPE : A类
     * SFZ :
     * DEPT : 静安区石门二路街道居家养老服务中心
     * JD : 静安区
     * IMEI :
     * RecordCount : 0
     * Enable : true
     * Line_name : null
     */

    private int ID;
    private String NAME;
    private String INPUT_CODE;
    private String PWD;
    private String PHONE;
    private String EMAIL;
    private Object PHOTO;
    private String CREATE_DATE;
    private int CREATE_STAFF;
    private String UPDATE_DATE;
    private int UPDATE_STAFF;
    private boolean STOP;
    private String PASS_TYPE;
    private String SFZ;
    private String DEPT;
    private String JD;
    private String IMEI;
    private int RecordCount;
    private boolean Enable;
    private Object Line_name;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getINPUT_CODE() {
        return INPUT_CODE;
    }

    public void setINPUT_CODE(String INPUT_CODE) {
        this.INPUT_CODE = INPUT_CODE;
    }

    public String getPWD() {
        return PWD;
    }

    public void setPWD(String PWD) {
        this.PWD = PWD;
    }

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public Object getPHOTO() {
        return PHOTO;
    }

    public void setPHOTO(Object PHOTO) {
        this.PHOTO = PHOTO;
    }

    public String getCREATE_DATE() {
        return CREATE_DATE;
    }

    public void setCREATE_DATE(String CREATE_DATE) {
        this.CREATE_DATE = CREATE_DATE;
    }

    public int getCREATE_STAFF() {
        return CREATE_STAFF;
    }

    public void setCREATE_STAFF(int CREATE_STAFF) {
        this.CREATE_STAFF = CREATE_STAFF;
    }

    public String getUPDATE_DATE() {
        return UPDATE_DATE;
    }

    public void setUPDATE_DATE(String UPDATE_DATE) {
        this.UPDATE_DATE = UPDATE_DATE;
    }

    public int getUPDATE_STAFF() {
        return UPDATE_STAFF;
    }

    public void setUPDATE_STAFF(int UPDATE_STAFF) {
        this.UPDATE_STAFF = UPDATE_STAFF;
    }

    public boolean isSTOP() {
        return STOP;
    }

    public void setSTOP(boolean STOP) {
        this.STOP = STOP;
    }

    public String getPASS_TYPE() {
        return PASS_TYPE;
    }

    public void setPASS_TYPE(String PASS_TYPE) {
        this.PASS_TYPE = PASS_TYPE;
    }

    public String getSFZ() {
        return SFZ;
    }

    public void setSFZ(String SFZ) {
        this.SFZ = SFZ;
    }

    public String getDEPT() {
        return DEPT;
    }

    public void setDEPT(String DEPT) {
        this.DEPT = DEPT;
    }

    public String getJD() {
        return JD;
    }

    public void setJD(String JD) {
        this.JD = JD;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public int getRecordCount() {
        return RecordCount;
    }

    public void setRecordCount(int RecordCount) {
        this.RecordCount = RecordCount;
    }

    public boolean isEnable() {
        return Enable;
    }

    public void setEnable(boolean Enable) {
        this.Enable = Enable;
    }

    public Object getLine_name() {
        return Line_name;
    }

    public void setLine_name(Object Line_name) {
        this.Line_name = Line_name;
    }
}
