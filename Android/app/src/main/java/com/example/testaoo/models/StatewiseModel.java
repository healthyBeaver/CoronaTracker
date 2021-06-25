package com.example.testaoo.models;

public class StatewiseModel {
    private String state;
    private String infected;
    private String active;
    private String deceased;
    private String lastupdate;
    private String recovered;

    public StatewiseModel(String state, String confirmed, String active, String deceased, String newConfirmed, String newRecovered, String newDeceased, String lastupdate, String recovered) {
        this.state = state;
        this.infected = confirmed;
        this.active = active;
        this.deceased = deceased;
        this.lastupdate = lastupdate;
        this.recovered = recovered;
    }

    public String getState() {
        return state;
    }

    public String getConfirmed() {
        return infected;
    }

    public String getActive() {
        return active;
    }

    public String getDeceased() {
        return deceased;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public String getRecovered() {
        return recovered;
    }
}
