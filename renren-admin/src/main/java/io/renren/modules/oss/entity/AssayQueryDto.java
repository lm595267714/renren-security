package io.renren.modules.oss.entity;

import java.io.Serializable;


public class AssayQueryDto implements Serializable{

    private Integer limit;
    private Integer offset;
    private String username;
    private String sendExamineBeginTime;
    private String sendExamineEndTime;
    private String outPaientNo;
    private String doctor;
    private String reportBeginTime;
    private String reportEndTime;
    private String barCode;
    private String patientName;
    private String reportType;
    private String normal;
    private String printStatus;
    private String instrumentNo;
    private String materialStatus;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSendExamineBeginTime() {
        return sendExamineBeginTime;
    }

    public void setSendExamineBeginTime(String sendExamineBeginTime) {
        this.sendExamineBeginTime = sendExamineBeginTime;
    }

    public String getSendExamineEndTime() {
        return sendExamineEndTime;
    }

    public void setSendExamineEndTime(String sendExamineEndTime) {
        this.sendExamineEndTime = sendExamineEndTime;
    }

    public String getOutPaientNo() {
        return outPaientNo;
    }

    public void setOutPaientNo(String outPaientNo) {
        this.outPaientNo = outPaientNo;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getReportBeginTime() {
        return reportBeginTime;
    }

    public void setReportBeginTime(String reportBeginTime) {
        this.reportBeginTime = reportBeginTime;
    }

    public String getReportEndTime() {
        return reportEndTime;
    }

    public void setReportEndTime(String reportEndTime) {
        this.reportEndTime = reportEndTime;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getNormal() {
        return normal;
    }

    public void setNormal(String normal) {
        this.normal = normal;
    }

    public String getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(String printStatus) {
        this.printStatus = printStatus;
    }

    public String getInstrumentNo() {
        return instrumentNo;
    }

    public void setInstrumentNo(String instrumentNo) {
        this.instrumentNo = instrumentNo;
    }

    public String getMaterialStatus() {
        return materialStatus;
    }

    public void setMaterialStatus(String materialStatus) {
        this.materialStatus = materialStatus;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
