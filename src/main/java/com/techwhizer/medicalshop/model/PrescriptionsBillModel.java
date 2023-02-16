package com.techwhizer.medicalshop.model;

public class PrescriptionsBillModel {
    private int patientId;
    private  String bp;
    private  String pulse;
    private  String spo2;
    private  String temp;
    private  String chest;
    private  String cvs;
    private  String cns;
    private  String sugar;

    public PrescriptionsBillModel(int patientId, String bp, String pulse, String spo2,
                                  String temp, String chest, String cvs, String cns, String sugar) {
        this.patientId = patientId;
        this.bp = bp;
        this.pulse = pulse;
        this.spo2 = spo2;
        this.temp = temp;
        this.chest = chest;
        this.cvs = cvs;
        this.cns = cns;
        this.sugar = sugar;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getBp() {
        return bp;
    }

    public void setBp(String bp) {
        this.bp = bp;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getSpo2() {
        return spo2;
    }

    public void setSpo2(String spo2) {
        this.spo2 = spo2;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getChest() {
        return chest;
    }

    public void setChest(String chest) {
        this.chest = chest;
    }

    public String getCvs() {
        return cvs;
    }

    public void setCvs(String cvs) {
        this.cvs = cvs;
    }

    public String getCns() {
        return cns;
    }

    public void setCns(String cns) {
        this.cns = cns;
    }

    public String getSugar() {
        return sugar;
    }

    public void setSugar(String sugar) {
        this.sugar = sugar;
    }
}
