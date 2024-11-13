package com.example.pgyl.sp15c_a;

public class IntegParamSet {
    public final int ITER_COUNT_MAX = 50;
    public final int SAMPLING_MAX = 2048;

    public double a;
    public double b;
    public double h;
    public int l;
    public int n;
    public double p;
    public double u;
    public double x;
    public double z;
    public double sumFx;
    public int countFx;
    public int countFxMax;
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
        h = 0;
        l = 0;
        n = 0;
        u = 0;
        x = 0;
        z = 0;
        p = 0;
        sumFx = 0;
        countFx = 0;
        countFxMax = 0;
        oldNextProgLineNumber = 0;
        userFxLineNumber = 0;
        count = 0;
        retLevel = 0;
        tol = 0;
        iterCount = 0;
    }

    public void close() {
        //  NOP
    }

    public String calc() {
        String ret = "";
        z = h * (4 * sumFx + 2 * u + p) / 3.0;   //  sumFx=somme points impairs de l'étape n); u=somme points pairs (cumul des points impairs de l'étape n-1)
        return ret;
    }

    public void setNextLevel() {
        u = u + sumFx;   //  Mettre à jour la somme cumulée des y de tous les points impairs calculés depuis le début; ces points impairs seront pairs au tour suivant
        countFxMax = n;
        n = n * 2;
        h = h / 2.0;
        countFx = 0;
        sumFx = 0;
    }
}
