package com.example.pgyl.sp15c_a;

import java.util.ArrayList;

public class IntegParamSet {
    public final int ITER_COUNT_MAX = 50;

    public double a;
    public double b;
    public int n;
    public double h;
    public int countFx;
    public int countFxMax;
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
        countFx = 0;
        countFxMax = 0;
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

    public String calcRombergLine() {
        String error = "";
        ArrayList<Double> romLine = new ArrayList<Double>();
        romberg.add(romLine);   //  Ligne n
        romberg.get(n).add(romberg.get(n - 1).get(0) / 2.0 + h * sumFx);
        for (int i = 0; i <= (n - 1); i = i + 1) {
            romberg.get(n).add((Math.pow(4, n) * romberg.get(n).get(i) - romberg.get(n - 1).get(i)) / (Math.pow(4, n) - 1));
        }
        return error;
    }
}
