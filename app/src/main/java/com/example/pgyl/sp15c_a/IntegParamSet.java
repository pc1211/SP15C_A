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
    public ArrayList<ArrayList<Double>> rombergLines;
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
        rombergLines = new ArrayList<ArrayList<Double>>();
        oldNextProgLineNumber = 0;
        userFxLineNumber = 0;
        count = 0;
        retLevel = 0;
        iterCount = 0;
        tol = 0;
    }

    public void close() {
        rombergLines.clear();
        rombergLines = null;
    }

    public void setNextLevel() {
        n = n + 1;
        h = h / 2.0;
        countFxMax = (n == 1 ? 1 : countFxMax * 2);
        sumFx = 0;
        countFx = 0;
    }

    public double getRombergCurrentValue() {
        return rombergLines.get(n).get(n);
    }

    public double getRombergPreviousValue() {
        return rombergLines.get(n - 1).get(n - 1);
    }

    public void calcRombergFirstValue() {
        rombergLines.add(new ArrayList<Double>());   //  Ligne n (0)
        rombergLines.get(0).add(sumFx / 2.0 * (b - a));
    }

    public void calcRombergLineValues() {
        rombergLines.add(new ArrayList<Double>());   //  Ligne n
        rombergLines.get(n).add(rombergLines.get(n - 1).get(0) / 2.0 + h * sumFx);
        for (int i = 0; i <= (n - 1); i = i + 1) {
            rombergLines.get(n).add((Math.pow(4, n) * rombergLines.get(n).get(i) - rombergLines.get(n - 1).get(i)) / (Math.pow(4, n) - 1));
        }
    }
}
