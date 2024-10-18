package com.example.pgyl.sp15c_a;

public class SolveParamSet {
    public final int ITER_COUNT_MAX = 50;

    public double a;
    public double b;
    public double c;
    public double q;
    public double r;
    public double s;
    public double t;
    public double y;
    public int oldNextProgLineNumber;
    public int userFxLineNumber;
    public int count;
    public int retLevel;
    public int iterCount;
    public double tol;

    public SolveParamSet() {
        init();
    }

    private void init() {
        clear();
    }

    public void clear() {
        a = 0;
        b = 0;
        c = 0;
        q = 0;
        r = 0;
        s = 0;
        t = 0;
        y = 0;
        oldNextProgLineNumber = 0;
        userFxLineNumber = 0;
        count = 0;
        retLevel = -1;
        iterCount = 0;
        tol = 0;
    }

    public void close() {
        //  NOP
    }
}
