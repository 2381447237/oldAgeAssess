package com.youli.oldageassess.entity;

import java.io.Serializable;

/**
 * Created by liutao on 2018/1/12.
 *
 * http://web.youli.pw:81/Json/Get_SL.aspx?page=0&rows=20&type=1
 *
 * type:1待查,2已查

 */

public class PersonInfo implements Serializable{


    /**
     * XM : 沈统胜
     * XB : 1
     * CSNY : 19271112
     * SFZH : 310106192711124014
     * SBKH : 5003010649
     * HJQX : 06
     * HJJD : 0603
     * HJJW : 4031
     * HJL : 南京西
     * HJN : 1213
     * HJH : 80
     * HJS : 303
     * ZZQX : 06
     * ZZJD : 0603
     * ZZJW : 4031
     * ZZL : 南京西
     * ZZN : 1213
     * ZZH : 80
     * ZZS : 303
     * SXDZ :
     * SXYB : 200040
     * DHHM :
     * SJHM : 13916523317
     * DBRXM : 沈坚
     * DBRDHHM :
     * DBRSJHM : 13916523317
     * DBRSFZH : 310106195403144033
     * JTYSXM :
     * JTYSLXFS :
     * JTYSDW :
     * PGLX : 1
     * PGSJ : 0000010
     * SJD : 10
     * FWLX : 2
     * SQFWJGMC : Y0160600100
     * SFDB : 1
     * SFDSR : 0
     * FJS :
     * FJ : null
     * SQH : 0012CH101201801020001
     * CHXZG : 1
     * SQDATE : 2018-01-02T00:00:00
     * RECEIVED : false
     * RECEIVED_STAFF1 : null
     * RECEIVED_STAFF2 : null
     * RECEIVED_TIME : null
     * RecordCount : 72
     */

    private String XM;//姓名
    private String XB;//性别
    private String CSNY;//出生年月日
    private String SFZH;//身份证号
    private String SBKH;//社保卡号
    private String HJQX;//户籍区县代码
    private String HJJD;//户籍街道代码
    private String HJJW;//户籍居委代码
    private String HJL;//户籍路
    private String HJN;//户籍弄
    private String HJH;//户籍号
    private String HJS;//户籍室
    private String ZZQX;//住址区县代码
    private String ZZJD;//住址街道代码
    private String ZZJW;//住址居委代码
    private String ZZL;//住址路
    private String ZZN;//住址弄
    private String ZZH;//住址号
    private String ZZS;//住址室
    private String SXDZ;//收信地址
    private String SXYB;//收信邮编
    private String DHHM;//电话号码
    private String SJHM;//手机号码
    private String DBRXM;//代办人姓名
    private String DBRDHHM;//代办人电话号码
    private String DBRSJHM;//代办人手机号码
    private String DBRSFZH;//代办人身份证号
    private String JTYSXM;//家庭医生姓名
    private String JTYSLXFS;//家庭医生联系方式
    private String JTYSDW;//家庭医生单位
    private String PGLX;//评估类型('1'=初次评估;'2'=复核评估;'3'=状态评估;'4'=期末评估;'6'=绿色通道;'7'=终核评估)
    private String PGSJ;//评估时间('0'=星期一;'1'=星期二;'2'=星期三;'3'=星期四;'4'=星期五;'5'=星期六;'6'=星期日)
    private String SJD;//时间段('0'=上午;'1'=下午)
    private String FWLX;//服务类型('1'=养老机构照护;'2'=社区居家照护;'3'=住院医疗护理)
    private String SQFWJGMC;//申请服务机构代码
    private String SFDSR;//是否低收入('0'=是;'1'=否)
    private String FJS;//绿色通道附件结构
    private Object FJ;//绿色通道附件
    private String SQH;//申请号
    private String CHXZG;//长护险资格('0'=否;'1'=是)
    private String SQDATE;
    private boolean RECEIVED;
    private Object RECEIVED_STAFF1;
    private Object RECEIVED_STAFF2;
    private Object RECEIVED_TIME;
    private int RecordCount;

    private String HJD_Name;//户籍街道
    private String HJW_Name;//户籍居委
    private String ZJD_Name;//居住街道
    private String ZJW_Name;//居住居委
    private int Age;//年龄

    private String HJQX_Name;//户籍区县
    private String ZZQX_Name;//居住区县
    private String SFDB;//是否低保 0是1否

    public String getHJQX_Name() {
        return HJQX_Name;
    }

    public void setHJQX_Name(String HJQX_Name) {
        this.HJQX_Name = HJQX_Name;
    }

    public String getZZQX_Name() {
        return ZZQX_Name;
    }

    public void setZZQX_Name(String ZZQX_Name) {
        this.ZZQX_Name = ZZQX_Name;
    }

    public String getHJD_Name() {
        return HJD_Name;
    }

    public void setHJD_Name(String HJD_Name) {
        this.HJD_Name = HJD_Name;
    }

    public String getHJW_Name() {
        return HJW_Name;
    }

    public void setHJW_Name(String HJW_Name) {
        this.HJW_Name = HJW_Name;
    }

    public String getZJD_Name() {
        return ZJD_Name;
    }

    public void setZJD_Name(String ZJD_Name) {
        this.ZJD_Name = ZJD_Name;
    }

    public String getZJW_Name() {
        return ZJW_Name;
    }

    public void setZJW_Name(String ZJW_Name) {
        this.ZJW_Name = ZJW_Name;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public String getXM() {
        return XM;
    }

    public void setXM(String XM) {
        this.XM = XM;
    }

    public String getXB() {
        return XB;
    }

    public void setXB(String XB) {
        this.XB = XB;
    }

    public String getCSNY() {
        return CSNY;
    }

    public void setCSNY(String CSNY) {
        this.CSNY = CSNY;
    }

    public String getSFZH() {
        return SFZH;
    }

    public void setSFZH(String SFZH) {
        this.SFZH = SFZH;
    }

    public String getSBKH() {
        return SBKH;
    }

    public void setSBKH(String SBKH) {
        this.SBKH = SBKH;
    }

    public String getHJQX() {
        return HJQX;
    }

    public void setHJQX(String HJQX) {
        this.HJQX = HJQX;
    }

    public String getHJJD() {
        return HJJD;
    }

    public void setHJJD(String HJJD) {
        this.HJJD = HJJD;
    }

    public String getHJJW() {
        return HJJW;
    }

    public void setHJJW(String HJJW) {
        this.HJJW = HJJW;
    }

    public String getHJL() {
        return HJL;
    }

    public void setHJL(String HJL) {
        this.HJL = HJL;
    }

    public String getHJN() {
        return HJN;
    }

    public void setHJN(String HJN) {
        this.HJN = HJN;
    }

    public String getHJH() {
        return HJH;
    }

    public void setHJH(String HJH) {
        this.HJH = HJH;
    }

    public String getHJS() {
        return HJS;
    }

    public void setHJS(String HJS) {
        this.HJS = HJS;
    }

    public String getZZQX() {
        return ZZQX;
    }

    public void setZZQX(String ZZQX) {
        this.ZZQX = ZZQX;
    }

    public String getZZJD() {
        return ZZJD;
    }

    public void setZZJD(String ZZJD) {
        this.ZZJD = ZZJD;
    }

    public String getZZJW() {
        return ZZJW;
    }

    public void setZZJW(String ZZJW) {
        this.ZZJW = ZZJW;
    }

    public String getZZL() {
        return ZZL;
    }

    public void setZZL(String ZZL) {
        this.ZZL = ZZL;
    }

    public String getZZN() {
        return ZZN;
    }

    public void setZZN(String ZZN) {
        this.ZZN = ZZN;
    }

    public String getZZH() {
        return ZZH;
    }

    public void setZZH(String ZZH) {
        this.ZZH = ZZH;
    }

    public String getZZS() {
        return ZZS;
    }

    public void setZZS(String ZZS) {
        this.ZZS = ZZS;
    }

    public String getSXDZ() {
        return SXDZ;
    }

    public void setSXDZ(String SXDZ) {
        this.SXDZ = SXDZ;
    }

    public String getSXYB() {
        return SXYB;
    }

    public void setSXYB(String SXYB) {
        this.SXYB = SXYB;
    }

    public String getDHHM() {
        return DHHM;
    }

    public void setDHHM(String DHHM) {
        this.DHHM = DHHM;
    }

    public String getSJHM() {
        return SJHM;
    }

    public void setSJHM(String SJHM) {
        this.SJHM = SJHM;
    }

    public String getDBRXM() {
        return DBRXM;
    }

    public void setDBRXM(String DBRXM) {
        this.DBRXM = DBRXM;
    }

    public String getDBRDHHM() {
        return DBRDHHM;
    }

    public void setDBRDHHM(String DBRDHHM) {
        this.DBRDHHM = DBRDHHM;
    }

    public String getDBRSJHM() {
        return DBRSJHM;
    }

    public void setDBRSJHM(String DBRSJHM) {
        this.DBRSJHM = DBRSJHM;
    }

    public String getDBRSFZH() {
        return DBRSFZH;
    }

    public void setDBRSFZH(String DBRSFZH) {
        this.DBRSFZH = DBRSFZH;
    }

    public String getJTYSXM() {
        return JTYSXM;
    }

    public void setJTYSXM(String JTYSXM) {
        this.JTYSXM = JTYSXM;
    }

    public String getJTYSLXFS() {
        return JTYSLXFS;
    }

    public void setJTYSLXFS(String JTYSLXFS) {
        this.JTYSLXFS = JTYSLXFS;
    }

    public String getJTYSDW() {
        return JTYSDW;
    }

    public void setJTYSDW(String JTYSDW) {
        this.JTYSDW = JTYSDW;
    }

    public String getPGLX() {
        return PGLX;
    }

    public void setPGLX(String PGLX) {
        this.PGLX = PGLX;
    }

    public String getPGSJ() {
        return PGSJ;
    }

    public void setPGSJ(String PGSJ) {
        this.PGSJ = PGSJ;
    }

    public String getSJD() {
        return SJD;
    }

    public void setSJD(String SJD) {
        this.SJD = SJD;
    }

    public String getFWLX() {
        return FWLX;
    }

    public void setFWLX(String FWLX) {
        this.FWLX = FWLX;
    }

    public String getSQFWJGMC() {
        return SQFWJGMC;
    }

    public void setSQFWJGMC(String SQFWJGMC) {
        this.SQFWJGMC = SQFWJGMC;
    }

    public String getSFDB() {
        return SFDB;
    }

    public void setSFDB(String SFDB) {
        this.SFDB = SFDB;
    }

    public String getSFDSR() {
        return SFDSR;
    }

    public void setSFDSR(String SFDSR) {
        this.SFDSR = SFDSR;
    }

    public String getFJS() {
        return FJS;
    }

    public void setFJS(String FJS) {
        this.FJS = FJS;
    }

    public Object getFJ() {
        return FJ;
    }

    public void setFJ(Object FJ) {
        this.FJ = FJ;
    }

    public String getSQH() {
        return SQH;
    }

    public void setSQH(String SQH) {
        this.SQH = SQH;
    }

    public String getCHXZG() {
        return CHXZG;
    }

    public void setCHXZG(String CHXZG) {
        this.CHXZG = CHXZG;
    }

    public String getSQDATE() {
        return SQDATE;
    }

    public void setSQDATE(String SQDATE) {
        this.SQDATE = SQDATE;
    }

    public boolean isRECEIVED() {
        return RECEIVED;
    }

    public void setRECEIVED(boolean RECEIVED) {
        this.RECEIVED = RECEIVED;
    }

    public Object getRECEIVED_STAFF1() {
        return RECEIVED_STAFF1;
    }

    public void setRECEIVED_STAFF1(Object RECEIVED_STAFF1) {
        this.RECEIVED_STAFF1 = RECEIVED_STAFF1;
    }

    public Object getRECEIVED_STAFF2() {
        return RECEIVED_STAFF2;
    }

    public void setRECEIVED_STAFF2(Object RECEIVED_STAFF2) {
        this.RECEIVED_STAFF2 = RECEIVED_STAFF2;
    }

    public Object getRECEIVED_TIME() {
        return RECEIVED_TIME;
    }

    public void setRECEIVED_TIME(Object RECEIVED_TIME) {
        this.RECEIVED_TIME = RECEIVED_TIME;
    }

    public int getRecordCount() {
        return RecordCount;
    }

    public void setRecordCount(int RecordCount) {
        this.RecordCount = RecordCount;
    }
}
