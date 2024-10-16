package com.example.pgyl.sp15c_a;

import java.util.ArrayList;

public class IntegParamSet {
    public double a;
    public double b;
    public double n;
    public double h;
    public double m;
    private double x;
    public int countFx;
    public double sumFx;
    public ArrayList<ArrayList<Double>> romberg;

    public IntegParamSet() {
        init();
    }

    private void init() {
        a = 0;
        b = 0;
        n = 0;
        h = 0;
        m = 0;
        countFx = 0;
        sumFx = 0;
        romberg = new ArrayList<ArrayList<Double>>();
    }
}
