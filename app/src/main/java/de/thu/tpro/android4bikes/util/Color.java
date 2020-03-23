package de.thu.tpro.android4bikes.util;

import org.json.JSONObject;

public class Color {
    private int r;
    private int g;
    private int b;
    private int a;

    public Color() {
        //todo
    }

    public Color(int argb) {
        //todo
    }

    public Color(JSONObject color) {
        //todo
    }

    public Color(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        //todo a?
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public int getA() {
        return a;
    }

    public void setR(int r) {
        this.r = r;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setB(int b) {
        this.b = b;
    }

    public void setA(int a) {
        this.a = a;
    }
}
