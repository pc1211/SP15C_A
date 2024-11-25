package com.example.pgyl.sp15c_a;

public class SolveParamSet {
    public final int ITER_COUNT_MAX = 50;
    public final double GUESS_DIFF_MAX = 1e-14;
    public final double GUESS_CORRECTION = 5e-7;
    public final String ERROR_MATH = "Math error";

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

    public void separateAB() {   //  Pour empêcher a = b au démarrage du Solve
        if (Math.abs(a - b) < GUESS_DIFF_MAX) {
            a = a - GUESS_CORRECTION;
            b = b + GUESS_CORRECTION;   //  La différence entre a et b sera de 1E-6 (si GUESS_CORRECTION = 5E-7)
        }
    }

    public void setNextLevel() {
        b = a;
        s = r;
        a = c;
        r = q;
    }

    public String transform() {
        String error = "";
        Double newX = b - s * (b - a) / (s - r);
        if ((Double.isNaN(newX)) || (Double.isInfinite(newX))) {
            error = ERROR_MATH;
        }
        t = newX;
        return error;
    }
}
