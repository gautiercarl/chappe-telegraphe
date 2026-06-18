package com.chappe.model;

public class Zustand {
    private int regulator = -1;
    private int indicator1 = -1;
    private int indicator2 = -1;

    public Zustand() {}
    public Zustand(int regulator, int indicator1, int indicator2) {
        this.regulator = regulator;
        this.indicator1 = indicator1;
        this.indicator2 = indicator2;
    }
    public int getRegulator() {
        return regulator;
    }

    public int getIndicator1() {
        return indicator1;
    }

    public int getIndicator2() {
        return indicator2;
    }

    public static Zustand fromBytes(byte[] data) {
        Zustand zustand = new Zustand();
        if (data != null && data.length >= 3) {
            zustand.regulator = data[2];
            zustand.indicator1 = data[3];
            zustand.indicator2 = data[4];
        }
        return zustand;
    }
}
