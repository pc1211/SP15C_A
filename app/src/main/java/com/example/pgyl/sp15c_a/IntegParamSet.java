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
    public int oldNextProgLineNumber;
    public int userFxLineNumber;
    public int count;
    public int retLevel;
    public int iterCount;
    public double tol;

    public IntegParamSet() {
        init();
    }

    private void init() {
        clear();
    }

    public void clear() {
        a = 0;
        b = 0;
        n = 0;
        h = 0;
        m = 0;
        countFx = 0;
        sumFx = 0;
        romberg = new ArrayList<ArrayList<Double>>();
        oldNextProgLineNumber = 0;
        userFxLineNumber = 0;
        count = 0;
        retLevel = 0;
        iterCount = 0;
        tol = 0;
    }

    public void close() {
        romberg.clear();
        romberg = null;
    }
}
