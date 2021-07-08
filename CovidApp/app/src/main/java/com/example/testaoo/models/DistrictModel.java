package com.example.testaoo.models;

public class DistrictModel {
    private String district;
    private String confirmed;
    private String recovered;
    private String deceased;
    private String newConfirmed;
    private String newRecovered;
    private String newDeceased;

    public DistrictModel(String district, String confirmed, String recovered, String deceased, String newConfirmed, String newRecovered, String newDeceased) {
        this.district = district;
        this.confirmed = confirmed;
        this.recovered = recovered;
        this.deceased = deceased;
        this.newConfirmed = newConfirmed;
        this.newRecovered = newRecovered;
        this.newDeceased = newDeceased;
    }

    public String getDistrict() {
        return district;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public String getRecovered() {
        return recovered;
    }

    public String getDeceased() {
        return deceased;
    }

    public String getNewConfirmed() {
        return newConfirmed;
    }

    public String getNewRecovered() {
        return newRecovered;
    }

    public String getNewDeceased() {
        return newDeceased;
    }
}
