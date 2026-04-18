package com.chappe.model;

import java.util.ArrayList;

public class Tower {
    private int id;
    private Arm regulator;
    private Arm indicator1;
    private Arm indicator2;
    private int port;
    private Tower nachfolger;
    private Tower vorgaenger;
    private ArrayList<Integer> signals = new ArrayList<>();

    public Tower(int id) {
        this.id = id;
    }
    public Tower(int id, Arm regulator, Arm indicator1, Arm indicator2) {
        this.id = id;
        this.regulator = regulator;
        this.indicator1 = indicator1;
        this.indicator2 = indicator2;
    }
     public Tower() {
    }

    public void setRegulator(int p) {
        if (regulator == null) {
            regulator = new Arm();
        }
        regulator.setPosition(p);
    }

    public void setIndicator1(int p) {
        if (indicator1 == null) {
            indicator1 = new Arm();
        }
        indicator1.setPosition(p);
    }

    public void setIndicator2(int p) {
        if (indicator2 == null) {
            indicator2 = new Arm();
        }
        indicator2.setPosition(p);
    }
    public void setPort(int port) {
        this.port = port;
    }
    public int getPort() {
        return port;
    }

    public int getId() {
        return id;
    }

    public Arm getRegulator() {
        return regulator;
    }

    public Arm getIndicator1() {
        return indicator1;
    }

    public Arm getIndicator2() {
        return indicator2;
    }

    public Tower getNachfolger() {
        return nachfolger;
    }
    public ArrayList<Integer> getSignals() {
        return signals;
    }
    public void setSignals(ArrayList<Integer> signals) {
        this.signals = signals;
    }

    public void setNachfolger(Tower nachfolger) {
        this.nachfolger = nachfolger;
    }

    public Tower getVorgaenger() {
        return vorgaenger;
    }
    public void setVorgaenger(Tower vorgaenger) {
        this.vorgaenger = vorgaenger;
    }
}
