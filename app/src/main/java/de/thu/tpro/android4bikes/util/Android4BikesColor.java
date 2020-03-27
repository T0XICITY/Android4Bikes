package de.thu.tpro.android4bikes.util;

import org.json.JSONObject;

public class Android4BikesColor {
    private int r;
    private int g;
    private int b;
    private int a;

    public Android4BikesColor() {
        //todo
    }

    public Android4BikesColor(int argb) {
        //todo
    }

    public Android4BikesColor(JSONObject color) {
        //todo
    }

    public Android4BikesColor(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Android4BikesColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        //todo a?
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }
}
