package com.youli.oldageassess.entity;

import java.io.Serializable;

/**
 * Created by liutao on 2018/1/13.
 *
 * http://web.youli.pw:81/Json/Get_Qa_Detil_Special.aspx
 *
 * http://183.194.4.58:81/Json/Get_Qa_Detil_Special.aspx
 */

public class InvestInfo implements Serializable{


    /**
     * ID : 1
     * TITLE_L : 姓名:
     * TITLE_R : null
     * CODE : 1.
     * ORDER_V : 1
     * INPUT : true
     * INPUT_TYPE : 文本
     * JUMP_CODE : null
     * PARENT_ID : 0
     * MASTER_ID : 1
     * REMOVE_CODE : null
     * TYPE_ID : 2
     * BINDINFO :
     * WIDTH : 200
     * TITLE_TOP : 0
     * OUT_VALUE : null
     * BINDINFO_INPUT : A01
     * RecordCount : 0
     * TreeLevel : 0
     */

    private int ID;
    private String TITLE_L;
    private String TITLE_R;
    private String CODE;//题号
    private int ORDER_V;//排序
    private boolean INPUT;
    private String INPUT_TYPE;//无,数字,文本,时间,单选,多选
    private Object JUMP_CODE;
    private int PARENT_ID;//父节点id
    private int MASTER_ID;
    private Object REMOVE_CODE;
    private int TYPE_ID;//大类id:1诚信声明，2家庭状态，3基本项目，4总体状况，5疾病诊断
    private String BINDINFO;//绑定字段xml
    private int WIDTH;//填空宽度
    private String TITLE_TOP;//标题换行
    private Object OUT_VALUE;//导出时对应值
    private String BINDINFO_INPUT;//输入项绑定xml节点名
    private int RecordCount;
    private int TreeLevel;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTITLE_L() {
        return TITLE_L;
    }

    public void setTITLE_L(String TITLE_L) {
        this.TITLE_L = TITLE_L;
    }

    public String getTITLE_R() {
        return TITLE_R;
    }

    public void setTITLE_R(String TITLE_R) {
        this.TITLE_R = TITLE_R;
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public int getORDER_V() {
        return ORDER_V;
    }

    public void setORDER_V(int ORDER_V) {
        this.ORDER_V = ORDER_V;
    }

    public boolean isINPUT() {
        return INPUT;
    }

    public void setINPUT(boolean INPUT) {
        this.INPUT = INPUT;
    }

    public String getINPUT_TYPE() {
        return INPUT_TYPE;
    }

    public void setINPUT_TYPE(String INPUT_TYPE) {
        this.INPUT_TYPE = INPUT_TYPE;
    }

    public Object getJUMP_CODE() {
        return JUMP_CODE;
    }

    public void setJUMP_CODE(Object JUMP_CODE) {
        this.JUMP_CODE = JUMP_CODE;
    }

    public int getPARENT_ID() {
        return PARENT_ID;
    }

    public void setPARENT_ID(int PARENT_ID) {
        this.PARENT_ID = PARENT_ID;
    }

    public int getMASTER_ID() {
        return MASTER_ID;
    }

    public void setMASTER_ID(int MASTER_ID) {
        this.MASTER_ID = MASTER_ID;
    }

    public Object getREMOVE_CODE() {
        return REMOVE_CODE;
    }

    public void setREMOVE_CODE(Object REMOVE_CODE) {
        this.REMOVE_CODE = REMOVE_CODE;
    }

    public int getTYPE_ID() {
        return TYPE_ID;
    }

    public void setTYPE_ID(int TYPE_ID) {
        this.TYPE_ID = TYPE_ID;
    }

    public String getBINDINFO() {
        return BINDINFO;
    }

    public void setBINDINFO(String BINDINFO) {
        this.BINDINFO = BINDINFO;
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public void setWIDTH(int WIDTH) {
        this.WIDTH = WIDTH;
    }

    public String getTITLE_TOP() {
        return TITLE_TOP;
    }

    public void setTITLE_TOP(String TITLE_TOP) {
        this.TITLE_TOP = TITLE_TOP;
    }

    public Object getOUT_VALUE() {
        return OUT_VALUE;
    }

    public void setOUT_VALUE(Object OUT_VALUE) {
        this.OUT_VALUE = OUT_VALUE;
    }

    public String getBINDINFO_INPUT() {
        return BINDINFO_INPUT;
    }

    public void setBINDINFO_INPUT(String BINDINFO_INPUT) {
        this.BINDINFO_INPUT = BINDINFO_INPUT;
    }

    public int getRecordCount() {
        return RecordCount;
    }

    public void setRecordCount(int RecordCount) {
        this.RecordCount = RecordCount;
    }

    public int getTreeLevel() {
        return TreeLevel;
    }

    public void setTreeLevel(int TreeLevel) {
        this.TreeLevel = TreeLevel;
    }


    @Override
    public String toString() {
        return "InvestInfo{" +
                "TITLE_L='" + TITLE_L + '\'' +
                ", TITLE_R='" + TITLE_R + '\'' +
                '}';
    }
}
