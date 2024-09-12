package com.example.pgyl.sp15c_a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Alu {
    public enum KEYS {
        KEY_11(11, OPERATIONS.SQRT, OPERATIONS.UNKNOWN, OPERATIONS.SQR),
        KEY_12(12, OPERATIONS.EXP, OPERATIONS.UNKNOWN, OPERATIONS.LN),
        KEY_13(13, OPERATIONS.EXP10, OPERATIONS.UNKNOWN, OPERATIONS.LOG),
        KEY_14(14, OPERATIONS.POWER, OPERATIONS.UNKNOWN, OPERATIONS.PC),
        KEY_15(15, OPERATIONS.INV, OPERATIONS.UNKNOWN, OPERATIONS.DPC),
        KEY_16(16, OPERATIONS.CHS, OPERATIONS.UNKNOWN, OPERATIONS.ABS),
        KEY_17(17, OPERATIONS.DIGIT_7, OPERATIONS.FIX, OPERATIONS.DEG),
        KEY_18(18, OPERATIONS.DIGIT_8, OPERATIONS.SCI, OPERATIONS.RAD),
        KEY_19(19, OPERATIONS.DIGIT_9, OPERATIONS.ENG, OPERATIONS.GRAD),
        KEY_10(10, OPERATIONS.DIV, OPERATIONS.UNKNOWN, OPERATIONS.UNKNOWN),
        KEY_21(21, OPERATIONS.SST, OPERATIONS.UNKNOWN, OPERATIONS.BST),
        KEY_22(22, OPERATIONS.UNKNOWN, OPERATIONS.HYP, OPERATIONS.AHYP),
        KEY_23(23, OPERATIONS.SIN, OPERATIONS.DIM, OPERATIONS.ASIN),
        KEY_24(24, OPERATIONS.COS, OPERATIONS.UNKNOWN, OPERATIONS.ACOS),
        KEY_25(25, OPERATIONS.TAN, OPERATIONS.UNKNOWN, OPERATIONS.ATAN),
        KEY_26(26, OPERATIONS.EEX, OPERATIONS.UNKNOWN, OPERATIONS.PI),
        KEY_27(27, OPERATIONS.DIGIT_4, OPERATIONS.XCHG, OPERATIONS.UNKNOWN),
        KEY_28(28, OPERATIONS.DIGIT_5, OPERATIONS.UNKNOWN, OPERATIONS.UNKNOWN),
        KEY_29(29, OPERATIONS.DIGIT_6, OPERATIONS.UNKNOWN, OPERATIONS.UNKNOWN),
        KEY_20(20, OPERATIONS.MULT, OPERATIONS.UNKNOWN, OPERATIONS.UNKNOWN),
        KEY_31(31, OPERATIONS.RS, OPERATIONS.UNKNOWN, OPERATIONS.PR),
        KEY_32(32, OPERATIONS.UNKNOWN, OPERATIONS.CLEAR_SIGMA, OPERATIONS.UNKNOWN),
        KEY_33(33, OPERATIONS.RDN, OPERATIONS.UNKNOWN, OPERATIONS.RUP),
        KEY_34(34, OPERATIONS.XCHGXY, OPERATIONS.CLEAR_REGS, OPERATIONS.RND),
        KEY_35(35, OPERATIONS.BACK, OPERATIONS.CLEAR_PREFIX, OPERATIONS.CLX),
        KEY_36(36, OPERATIONS.ENTER, OPERATIONS.RAND, OPERATIONS.LASTX),
        KEY_37(37, OPERATIONS.DIGIT_1, OPERATIONS.RECT, OPERATIONS.POL),
        KEY_38(38, OPERATIONS.DIGIT_2, OPERATIONS.HMS, OPERATIONS.H),
        KEY_39(39, OPERATIONS.DIGIT_3, OPERATIONS.TO_RAD, OPERATIONS.TO_DEG),
        KEY_30(30, OPERATIONS.MINUS, OPERATIONS.UNKNOWN, OPERATIONS.UNKNOWN),
        KEY_41(41, OPERATIONS.UNKNOWN, OPERATIONS.UNKNOWN, OPERATIONS.UNKNOWN),
        KEY_42(42, OPERATIONS.F, OPERATIONS.UNKNOWN, OPERATIONS.UNKNOWN),
        KEY_43(43, OPERATIONS.G, OPERATIONS.UNKNOWN, OPERATIONS.UNKNOWN),
        KEY_44(44, OPERATIONS.STO, OPERATIONS.FRAC, OPERATIONS.INT),
        KEY_45(45, OPERATIONS.RCL, OPERATIONS.UNKNOWN, OPERATIONS.UNKNOWN),
        KEY_47(47, OPERATIONS.DIGIT_0, OPERATIONS.FACT, OPERATIONS.MEAN),
        KEY_48(48, OPERATIONS.DOT, OPERATIONS.YER, OPERATIONS.STDEV),
        KEY_49(49, OPERATIONS.SIGMA_PLUS, OPERATIONS.LR, OPERATIONS.SIGMA_MINUS),
        KEY_40(40, OPERATIONS.PLUS, OPERATIONS.PERM, OPERATIONS.COMB);

        private int code;
        private OPERATIONS uOp;   //  Unshifted operation
        private OPERATIONS fOp;   //  Shift F operation
        private OPERATIONS gOp;   //  Shift G operation

        KEYS(int code, OPERATIONS uOp, OPERATIONS fOp, OPERATIONS gOp) {
            this.code = code;
            this.uOp = uOp;
            this.fOp = fOp;
            this.gOp = gOp;
        }

        public int CODE() {
            return code;
        }

        public OPERATIONS UNSHIFTED_OP() {
            return uOp;
        }

        public OPERATIONS SHIFT_F_OP() {
            return fOp;
        }

        public OPERATIONS SHIFT_G_OP() {
            return gOp;
        }

        public int INDEX() {
            return ordinal();
        }
    }

    public enum OPERATIONS {
        BEGIN("BEGIN"),
        PR("P/R"),
        RS("R/S"),
        SST("SST"),
        BST("BST"),
        F("F"),
        G("G"),
        I("I"),
        INDI("(i)"),
        DIGIT_0("0"),
        DIGIT_1("1"),
        DIGIT_2("2"),
        DIGIT_3("3"),
        DIGIT_4("4"),
        DIGIT_5("5"),
        DIGIT_6("6"),
        DIGIT_7("7"),
        DIGIT_8("8"),
        DIGIT_9("9"),
        DOT("."),
        EEX("E"),
        CHS("-"),
        ENTER("ENTER"),
        PLUS("+"),
        MINUS("-"),
        MULT("*"),
        DIV("/"),
        FIX("FIX"),
        SCI("SCI"),
        ENG("ENG"),
        PI("PI"),
        XCHGXY("x<>y"),
        RDN("RDN"),
        RUP("RUP"),
        LASTX("LASTX"),
        BACK("<-"),
        CLX("CLX"),
        DEG("DEG"),
        RAD("RAD"),
        GRAD("GRAD"),
        TO_DEG("->DEG"),
        TO_RAD("->RAD"),
        SIN("SIN"),
        COS("COS"),
        TAN("TAN"),
        ASIN("ASIN"),
        ACOS("ACOS"),
        ATAN("ATAN"),
        HYP("HYP"),
        AHYP("AHYP"),
        SINH("SINH"),
        COSH("COSH"),
        TANH("TANH"),
        ASINH("ASINH"),
        ACOSH("ACOSH"),
        ATANH("ATANH"),
        SQRT("SQRT"),
        SQR("SQR"),
        EXP("EXP"),
        LN("LN"),
        EXP10("10^X"),
        LOG("LOG"),
        POWER("Y^X"),
        PC("%"),
        INV("1/X"),
        DPC("DELTA%"),
        ABS("ABS"),
        RND("RND"),
        CLEAR_PREFIX("PREFIX"),
        RAND("RAND"),
        RECT("->R"),
        POL("->P"),
        HMS("->H.MS"),
        H("->H"),
        COMB("CY,X"),
        PERM("PY,X"),
        FRAC("FRAC"),
        INT("INT"),
        FACT("FACT"),
        STO("STO"),
        RCL("RCL"),
        DIM("DIM"),
        SIGMA_PLUS("SIGMA+"),
        SIGMA_MINUS("SIGMA-"),
        MEAN("MEAN"),
        STDEV("STDEV"),
        YER("YR"),
        LR("LR"),
        CLEAR_SIGMA("CLSIGMA"),
        CLEAR_REGS("CLREG"),
        XCHG("X<>"),
        UNKNOWN("?");

        private String symbol;

        OPERATIONS(String symbol) {
            this.symbol = symbol;
        }

        public String SYMBOL() {
            return symbol;
        }

        public int INDEX() {
            return ordinal();
        }
    }

    public enum STK_REGS {
        X, Y, Z, T, LX, LY, LZ, LT;

        public int INDEX() {
            return ordinal();
        }
    }

    public enum BASE_REGS {   //  Registres de base (I, R0 à R9, R.0 à R.9) (avec les registres classiques de données (data) à partir de R0)
        RI("I"), R0("0"), R1("1"), R2("2"), R3("3"), R4("4"), R5("5"), R6("6"), R7("7"), R8("8"), R9("9"),
        RDOTD0(".0"), RDOT1(".1"), RDOT2(".2"), RDOT3(".3"), RDOT4(".4"), RDOT5(".5"), RDOT6(".6"), RDOT7(".7"), RDOT8(".8"), RDOT9(".9");

        private String symbol;

        BASE_REGS(String symbol) {
            this.symbol = symbol;
        }

        public String SYMBOL() {
            return symbol;
        }

        public int INDEX() {
            return ordinal();
        }   //  Servira aussi d'index dans regs
    }

    public enum STATS {
        N(2), SUM_X(3), SUM_X2(4), SUM_Y(5), SUM_Y2(6), SUM_XY(7);

        private int dataRegIndex;

        STATS(int dataRegIndex) {
            this.dataRegIndex = dataRegIndex;
        }

        public int DATA_REG_INDEX() {
            return dataRegIndex;
        }

        public int INDEX() {
            return ordinal();
        }   //  Servira aussi d'index dans regs
    }

    final int MAX_DIGITS = 10;
    final int REGS_ABSOLUTE_SIZE_MAX = 1000;   //  Max, inclus les 21 registres de base de BASE_REGS (I, R0 à R9, R.0 à R.9)
    final int REGS_DEF_SIZE = 100;    //  Par défaut, inclus les 21 registres de base de BASE_REGS (I, R0 à R9, R.0 à R.9)

    final String ERROR_OVERFLOW = "Overflow";
    final String ERROR_LOG = "Log(Neg or 0)";
    final String ERROR_DIV_BY_0 = "Div By 0";
    final String ERROR_NOT_NUMBER = "Not Number";
    final String ERROR_SQRT_NEG = "Sqrt(Neg)";
    final String ERROR_STAT_0 = "Stat n <= 0";
    final String ERROR_STAT_1 = "Stat n <= 1";
    final String ERROR_PERM_COMB = "Perm/Comb ?";
    final int MAX_LINES = 9999;
    final int END_RETURN_STACK = -1;
    final int UNSHIFTED_KEY_CODE = 0;
    final int SHIFT_F_KEY_CODE = 42;
    final int SHIFT_G_KEY_CODE = 43;

    private double[] stkRegs;
    private ArrayList<Double> regs;   //  Les registres de BASE_REGS puis les suivants (accessibles par (i) )
    private OPERATIONS roundMode;
    private int roundParam;
    private OPERATIONS angleMode;
    private HashMap<String, BASE_REGS> symbolToBaseRegMap;
    private HashMap<OPERATIONS, KEYS> opToKeyMap;
    private HashMap<OPERATIONS, Integer> opToShiftKeyCodeMap;
    private HashMap<Integer, KEYS> keyCodeToKeyMap;
    private ArrayList<ProgLine> proglines;
    private ArrayList<Integer> stkRet;

    public Alu() {
        init();
    }

    private void init() {
        regs = new ArrayList<Double>();
        for (int i = 0; i <= (REGS_DEF_SIZE - 1); i = i + 1) {   //  Les 21 registres de base (I, R0 à R9, R.0 à R.9) sont au début
            regs.add(0d);
        }

        setupMaps();
        stkRegs = new double[STK_REGS.values().length];
        stackClear();

        setupProgLines();
        setupReturnStack();
        angleMode = OPERATIONS.RAD;
        roundMode = OPERATIONS.FIX;
        roundParam = 4;
    }

    public String setDataRegsSize(int dataRegsSize) {   //  les registres de données classiques (data) commencent à partir de R0
        String res = "";
        int oldSize = regs.size();
        int newSize = dataRegsSize + BASE_REGS.R0.INDEX();
        int n = newSize - oldSize;
        if (n > 0) {   //  Ajouter n registres
            if (newSize <= REGS_ABSOLUTE_SIZE_MAX) {   //  Respecte Max
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    regs.add(0d);
                }
            } else {  //  > > Max
                res = "Max 0-" + (REGS_ABSOLUTE_SIZE_MAX - 1 - BASE_REGS.R0.INDEX());
            }
        } else {   //  Retirer n registres, en commençant par les derniers de la liste
            if (newSize >= BASE_REGS.values().length) {   //   Respecte Min
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    regs.subList(newSize, oldSize).clear();
                }
            } else {   //  < Min
                res = "Max 0-" + (REGS_ABSOLUTE_SIZE_MAX - 1 - BASE_REGS.R0.INDEX());
            }
        }
        return res;
    }

    public String clRegs() {
        String res = "";
        for (int i = 0; i <= (regs.size() - 1); i = i + 1) {   //  Tout effacer: I, R0 à R9, R.0 à R.9 et suivants
            regs.set(i, 0d);
        }
        return res;
    }

    public int getRegIndexByDataIndex(int index) {
        return BASE_REGS.R0.INDEX() + index;
    }   //  les registres de données classiques (data) commencent à partir de R0

    public int getDataRegIndexByIndex(int index) {
        return index - BASE_REGS.R0.INDEX();
    }   //  les registres de données classiques (data) commencent à partir de R0

    public int getRegIndexBySymbol(String symbol) {   //  Pour les premiers registres de regs, ceux de BASE_REGS (I, R0 à R9, R.0 à R.9)
        return symbolToBaseRegMap.get(symbol).INDEX();
    }

    public KEYS getKeyByOp(OPERATIONS op) {
        return opToKeyMap.get(op);
    }

    public int getShiftKeyCodeByOp(OPERATIONS op) {
        return opToShiftKeyCodeMap.get(op);
    }

    public KEYS getKeyByKeyCode(int keyCode) {
        return keyCodeToKeyMap.get(keyCode);
    }

    public ArrayList<Double> getRegs() {
        return regs;
    }

    public void setAngleMode(OPERATIONS angleMode) {
        this.angleMode = angleMode;
    }

    public void setRoundMode(OPERATIONS roundMode) {
        this.roundMode = roundMode;
    }

    public void setRoundParam(int roundParam) {
        this.roundParam = Math.min(MAX_DIGITS - 1, roundParam);
    }

    public OPERATIONS getAngleMode() {
        return angleMode;
    }

    public OPERATIONS getRoundMode() {
        return roundMode;
    }

    public int getRoundParam() {
        return roundParam;
    }

    public int getRegsMaxIndex() {
        return regs.size() - 1;
    }

    public int getRegsAbsoluteSizeMax() {
        return REGS_ABSOLUTE_SIZE_MAX;
    }

    public double[] getStkRegs() {
        return stkRegs;
    }

    public double getStkRegContents(STK_REGS stkReg) {
        return stkRegs[stkReg.INDEX()];
    }

    public void setStkRegContent(STK_REGS stkReg, double value) {
        stkRegs[stkReg.INDEX()] = value;
    }

    public String getRoundXForDisplay() {
        return roundForDisplay(stkRegs[STK_REGS.X.INDEX()]);
    }

    public double getRegContentsByIndex(int index) {
        return regs.get(index);
    }

    public void setRegContentsByIndex(int index, double value) {
        regs.set(index, value);
    }

    public String xXchgReg(int index) {
        String error = "";
        double reg = regs.get(index);
        regs.set(index, stkRegs[STK_REGS.X.INDEX()]);
        stkRegs[STK_REGS.X.INDEX()] = reg;
        return error;
    }

    public String xToReg(int index) {
        String error = "";
        regs.set(index, stkRegs[STK_REGS.X.INDEX()]);
        return error;
    }

    public String regToX(int index) {
        String error = "";
        stkRegs[STK_REGS.X.INDEX()] = regs.get(index);
        return error;
    }

    public String xToRegOp(int index, OPERATIONS op) {   //  STO+-*/ R
        String error1 = "";
        String error2 = "";
        try {
            switch (op) {
                case PLUS:
                    regs.set(index, regs.get(index) + stkRegs[STK_REGS.X.INDEX()]);
                    error1 = ERROR_OVERFLOW;
                    break;
                case MINUS:
                    regs.set(index, regs.get(index) - stkRegs[STK_REGS.X.INDEX()]);
                    error1 = ERROR_OVERFLOW;
                    break;
                case MULT:
                    regs.set(index, regs.get(index) * stkRegs[STK_REGS.X.INDEX()]);
                    error1 = ERROR_OVERFLOW;
                    break;
                case DIV:
                    regs.set(index, regs.get(index) / stkRegs[STK_REGS.X.INDEX()]);
                    error1 = ERROR_DIV_BY_0;
                    break;
            }
            if ((Double.isNaN(regs.get(index))) || (Double.isInfinite(regs.get(index)))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error2 = error1;
        }
        return error2;
    }

    public String regToXOp(int index, OPERATIONS op) {   //   RCL+-*/ R
        String error1 = "";
        String error2 = "";
        try {
            switch (op) {
                case PLUS:
                    stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.X.INDEX()] + regs.get(index);
                    error1 = ERROR_OVERFLOW;
                    break;
                case MINUS:
                    stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.X.INDEX()] - regs.get(index);
                    error1 = ERROR_OVERFLOW;
                    break;
                case MULT:
                    stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.X.INDEX()] * regs.get(index);
                    error1 = ERROR_OVERFLOW;
                    break;
                case DIV:
                    stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.X.INDEX()] / regs.get(index);
                    error1 = ERROR_DIV_BY_0;
                    break;
            }
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error2 = error1;
        }
        return error2;
    }

    public String yDivX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.Y.INDEX()] / stkRegs[STK_REGS.X.INDEX()];
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_DIV_BY_0;
        }
        return error;
    }

    public String yMultX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.Y.INDEX()] * stkRegs[STK_REGS.X.INDEX()];
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String yMinusX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.Y.INDEX()] - stkRegs[STK_REGS.X.INDEX()];
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String yPlusX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.Y.INDEX()] + stkRegs[STK_REGS.X.INDEX()];
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String negX() {   //  LASTX non modifié
        String error = "";
        stkRegs[STK_REGS.X.INDEX()] = -stkRegs[STK_REGS.X.INDEX()];
        return error;
    }

    public String sqrX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.X.INDEX()] * stkRegs[STK_REGS.X.INDEX()];
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String sqrtX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = Math.sqrt(stkRegs[STK_REGS.X.INDEX()]);
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_SQRT_NEG;
        }
        return error;
    }

    public String xToRad() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        stkRegs[STK_REGS.X.INDEX()] = Math.toRadians(stkRegs[STK_REGS.X.INDEX()]);
        return error;
    }

    public String xToDeg() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.X.INDEX()] * (180d / Math.PI);
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String xyToRect() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            double r = stkRegs[STK_REGS.X.INDEX()];
            stkRegs[STK_REGS.X.INDEX()] = r * Math.cos(angleToRad(stkRegs[STK_REGS.Y.INDEX()]));
            stkRegs[STK_REGS.Y.INDEX()] = r * Math.sin(angleToRad(stkRegs[STK_REGS.Y.INDEX()]));
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
            if ((Double.isNaN(stkRegs[STK_REGS.Y.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.Y.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String xyToPol() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            double x = stkRegs[STK_REGS.X.INDEX()];
            stkRegs[STK_REGS.X.INDEX()] = Math.hypot(x, stkRegs[STK_REGS.Y.INDEX()]);
            stkRegs[STK_REGS.Y.INDEX()] = radToAngle(Math.atan2(stkRegs[STK_REGS.Y.INDEX()], x));
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
            if ((Double.isNaN(stkRegs[STK_REGS.Y.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.Y.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String expX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = Math.exp(stkRegs[STK_REGS.X.INDEX()]);
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String lnX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = Math.log(stkRegs[STK_REGS.X.INDEX()]);   //  Math.log est en fait ln
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_LOG;
        }
        return error;
    }

    public String exp10X() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = Math.pow(10, stkRegs[STK_REGS.X.INDEX()]);
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String logX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = Math.log10(stkRegs[STK_REGS.X.INDEX()]);   //  Math.log est en fait ln
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_LOG;
        }
        return error;
    }

    public String pow() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = Math.pow(stkRegs[STK_REGS.Y.INDEX()], stkRegs[STK_REGS.X.INDEX()]);
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String invX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = 1d / stkRegs[STK_REGS.X.INDEX()];
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_DIV_BY_0;
        }
        return error;
    }

    public String xPcY() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.Y.INDEX()] * (stkRegs[STK_REGS.X.INDEX()] / 100d);
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String xDpcY() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = (stkRegs[STK_REGS.X.INDEX()] / stkRegs[STK_REGS.Y.INDEX()] - 1d) * 100d;
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_DIV_BY_0;
        }
        return error;
    }

    public String absX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        stkRegs[STK_REGS.X.INDEX()] = Math.abs(stkRegs[STK_REGS.X.INDEX()]);
        return error;
    }

    public String rndX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        stkRegs[STK_REGS.X.INDEX()] = Double.parseDouble(roundForDisplay(stkRegs[STK_REGS.X.INDEX()]));
        return error;
    }

    public String prefX() {   //  LASTX non modifié
        String error = "";
        double val = Math.abs(stkRegs[STK_REGS.X.INDEX()]);
        int exp = 0;
        double mant = 0;
        if (val != 0) {
            exp = (int) Math.floor(1d + Math.log10(val));
            mant = val / Math.pow(10, exp);   //  Entre 0 et 1
        }
        double valr = mant * Math.pow(10, MAX_DIGITS);
        error = String.format(Locale.US, "%.0f", valr);
        return error;
    }

    public String hmsX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        stkRegs[STK_REGS.X.INDEX()] = (90d * stkRegs[STK_REGS.X.INDEX()] + (int) (60d * stkRegs[STK_REGS.X.INDEX()]) + 100d * (int) stkRegs[STK_REGS.X.INDEX()]) / 250d;
        return error;
    }

    public String hX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        stkRegs[STK_REGS.X.INDEX()] = (250d * stkRegs[STK_REGS.X.INDEX()] - (int) (100d * stkRegs[STK_REGS.X.INDEX()]) - 60d * (int) stkRegs[STK_REGS.X.INDEX()]) / 90d;
        return error;
    }

    public String xyToPerm() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        int m = (int) stkRegs[STK_REGS.Y.INDEX()];
        int n = (int) stkRegs[STK_REGS.X.INDEX()];
        if ((m >= 0) && (n >= 0) && (n <= m)) {
            try {
                stkRegs[STK_REGS.X.INDEX()] = factOver(m, m - n);
                if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                    throw new ArithmeticException();
                }
            } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
                stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
                error = ERROR_OVERFLOW;
            }
        } else {   //  Erreur
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_PERM_COMB;
        }
        return error;
    }

    public String xyToComb() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        int m = (int) stkRegs[STK_REGS.Y.INDEX()];
        int n = (int) stkRegs[STK_REGS.X.INDEX()];
        if ((m >= 0) && (n >= 0) && (n <= m)) {
            try {
                stkRegs[STK_REGS.X.INDEX()] = factOver(m, m - n) / (double) fact(n);
                if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                    throw new ArithmeticException();
                }
            } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
                stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
                error = ERROR_OVERFLOW;
            }
        } else {   //  Erreur
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_PERM_COMB;
        }
        return error;
    }

    public String fracX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        double val = Math.abs(stkRegs[STK_REGS.X.INDEX()]);
        val = val - (int) val;
        val = (stkRegs[STK_REGS.X.INDEX()] >= 0 ? val : -val);
        stkRegs[STK_REGS.X.INDEX()] = val;
        return error;
    }

    public String intX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        double val = (int) Math.abs(stkRegs[STK_REGS.X.INDEX()]);
        val = (stkRegs[STK_REGS.X.INDEX()] >= 0 ? val : -val);
        stkRegs[STK_REGS.X.INDEX()] = val;
        return error;
    }

    public String sinX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        stkRegs[STK_REGS.X.INDEX()] = Math.sin(angleToRad(stkRegs[STK_REGS.X.INDEX()]));
        return error;
    }

    public String cosX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        stkRegs[STK_REGS.X.INDEX()] = Math.cos(angleToRad(stkRegs[STK_REGS.X.INDEX()]));
        return error;
    }

    public String tanX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = Math.tan(angleToRad(stkRegs[STK_REGS.X.INDEX()]));
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String asinX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = radToAngle(Math.asin(stkRegs[STK_REGS.X.INDEX()]));
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String acosX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = radToAngle(Math.acos(stkRegs[STK_REGS.X.INDEX()]));
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String atanX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        stkRegs[STK_REGS.X.INDEX()] = radToAngle(Math.atan(stkRegs[STK_REGS.X.INDEX()]));
        return error;
    }

    public String sinhX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = Math.sinh(stkRegs[STK_REGS.X.INDEX()]);
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String coshX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = Math.cosh(stkRegs[STK_REGS.X.INDEX()]);
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String tanhX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        stkRegs[STK_REGS.X.INDEX()] = Math.tanh(stkRegs[STK_REGS.X.INDEX()]);
        return error;
    }

    public String asinhX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        double t = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = Math.log(t + Math.sqrt(t * t + 1));
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String acoshX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        double t = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = Math.log(t + Math.sqrt(t * t - 1));
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String atanhX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        double t = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = Math.log((1d + t) / (1d - t)) / 2d;
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String factX() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            stkRegs[STK_REGS.X.INDEX()] = gamma(1 + stkRegs[STK_REGS.X.INDEX()]);
            if ((Double.isNaN(stkRegs[STK_REGS.X.INDEX()])) || (Double.isInfinite(stkRegs[STK_REGS.X.INDEX()]))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String clStats() {   //  LASTX non modifié
        String error = "";
        for (STATS stat : STATS.values()) {
            int index = stat.DATA_REG_INDEX() + BASE_REGS.R0.INDEX();
            setRegContentsByIndex(index, 0);
        }
        stkRegs[STK_REGS.X.INDEX()] = 0;
        stkRegs[STK_REGS.Y.INDEX()] = 0;
        stkRegs[STK_REGS.Z.INDEX()] = 0;
        stkRegs[STK_REGS.T.INDEX()] = 0;
        return error;
    }

    public String sigmaPlus() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            int index = getRegIndexByDataIndex(STATS.N.DATA_REG_INDEX());
            int nMod = (int) getRegContentsByIndex(index) + 1;
            setRegContentsByIndex(index, nMod);

            index = getRegIndexByDataIndex(STATS.SUM_X.DATA_REG_INDEX());
            double sumXMod = getRegContentsByIndex(index) + stkRegs[STK_REGS.X.INDEX()];
            setRegContentsByIndex(index, sumXMod);

            index = getRegIndexByDataIndex(STATS.SUM_X2.DATA_REG_INDEX());
            double sumX2Mod = getRegContentsByIndex(index) + stkRegs[STK_REGS.X.INDEX()] * stkRegs[STK_REGS.X.INDEX()];
            setRegContentsByIndex(index, sumX2Mod);

            index = getRegIndexByDataIndex(STATS.SUM_Y.DATA_REG_INDEX());
            double sumYMod = getRegContentsByIndex(index) + stkRegs[STK_REGS.Y.INDEX()];
            setRegContentsByIndex(index, sumYMod);

            index = getRegIndexByDataIndex(STATS.SUM_Y2.DATA_REG_INDEX());
            double sumY2Mod = getRegContentsByIndex(index) + stkRegs[STK_REGS.Y.INDEX()] * stkRegs[STK_REGS.Y.INDEX()];
            setRegContentsByIndex(index, sumY2Mod);

            index = getRegIndexByDataIndex(STATS.SUM_XY.DATA_REG_INDEX());
            double sumXYMod = getRegContentsByIndex(index) + stkRegs[STK_REGS.X.INDEX()] * stkRegs[STK_REGS.Y.INDEX()];
            setRegContentsByIndex(index, sumXYMod);

            stkRegs[STK_REGS.X.INDEX()] = nMod;

            if ((Double.isNaN(sumXMod)) || (Double.isInfinite(sumXMod))) {
                throw new ArithmeticException();
            }
            if ((Double.isNaN(sumX2Mod)) || (Double.isInfinite(sumX2Mod))) {
                throw new ArithmeticException();
            }
            if ((Double.isNaN(sumYMod)) || (Double.isInfinite(sumYMod))) {
                throw new ArithmeticException();
            }
            if ((Double.isNaN(sumY2Mod)) || (Double.isInfinite(sumY2Mod))) {
                throw new ArithmeticException();
            }
            if ((Double.isNaN(sumXYMod)) || (Double.isInfinite(sumXYMod))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String sigmaMinus() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        try {
            int index = getRegIndexByDataIndex(STATS.N.DATA_REG_INDEX());
            int nMod = (int) getRegContentsByIndex(index) - 1;
            setRegContentsByIndex(index, nMod);

            index = getRegIndexByDataIndex(STATS.SUM_X.DATA_REG_INDEX());
            double sumXMod = getRegContentsByIndex(index) - stkRegs[STK_REGS.X.INDEX()];
            setRegContentsByIndex(index, sumXMod);

            index = getRegIndexByDataIndex(STATS.SUM_X2.DATA_REG_INDEX());
            double sumX2Mod = getRegContentsByIndex(index) - stkRegs[STK_REGS.X.INDEX()] * stkRegs[STK_REGS.X.INDEX()];
            setRegContentsByIndex(index, sumX2Mod);

            index = getRegIndexByDataIndex(STATS.SUM_Y.DATA_REG_INDEX());
            double sumYMod = getRegContentsByIndex(index) - stkRegs[STK_REGS.Y.INDEX()];
            setRegContentsByIndex(index, sumYMod);

            index = getRegIndexByDataIndex(STATS.SUM_Y2.DATA_REG_INDEX());
            double sumY2Mod = getRegContentsByIndex(index) - stkRegs[STK_REGS.Y.INDEX()] * stkRegs[STK_REGS.Y.INDEX()];
            setRegContentsByIndex(index, sumY2Mod);

            index = getRegIndexByDataIndex(STATS.SUM_XY.DATA_REG_INDEX());
            double sumXYMod = getRegContentsByIndex(index) - stkRegs[STK_REGS.X.INDEX()] * stkRegs[STK_REGS.Y.INDEX()];
            setRegContentsByIndex(index, sumXYMod);

            stkRegs[STK_REGS.X.INDEX()] = nMod;

            if ((Double.isNaN(sumXMod)) || (Double.isInfinite(sumXMod))) {
                throw new ArithmeticException();
            }
            if ((Double.isNaN(sumX2Mod)) || (Double.isInfinite(sumX2Mod))) {
                throw new ArithmeticException();
            }
            if ((Double.isNaN(sumYMod)) || (Double.isInfinite(sumYMod))) {
                throw new ArithmeticException();
            }
            if ((Double.isNaN(sumY2Mod)) || (Double.isInfinite(sumY2Mod))) {
                throw new ArithmeticException();
            }
            if ((Double.isNaN(sumXYMod)) || (Double.isInfinite(sumXYMod))) {
                throw new ArithmeticException();
            }
        } catch (ArithmeticException | IllegalArgumentException | SecurityException ex) {
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_OVERFLOW;
        }
        return error;
    }

    public String mean() {   //  LASTX non modifié
        String error = "";
        double n = getRegContentsByIndex(getRegIndexByDataIndex(STATS.N.DATA_REG_INDEX()));
        if (n > 0) {
            double sumX = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_X.DATA_REG_INDEX()));
            double sumY = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_Y.DATA_REG_INDEX()));
            double meanX = sumX / n;
            double meanY = sumY / n;
            stkRegs[STK_REGS.X.INDEX()] = meanX;
            stkRegs[STK_REGS.Y.INDEX()] = meanY;
        } else {   //  n <= 0
            error = ERROR_STAT_0;
        }
        return error;
    }

    public String stDev() {   //  LASTX non modifié
        String error = "";
        double n = getRegContentsByIndex(getRegIndexByDataIndex(STATS.N.DATA_REG_INDEX()));
        if (n > 1) {
            double sumX = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_X.DATA_REG_INDEX()));
            double sumX2 = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_X2.DATA_REG_INDEX()));
            double sumY = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_Y.DATA_REG_INDEX()));
            double sumY2 = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_Y2.DATA_REG_INDEX()));
            double mv = n * sumX2 - sumX * sumX;
            double nv = n * sumY2 - sumY * sumY;
            double stDevX = Math.sqrt(mv / (n * (n - 1)));
            double stDevY = Math.sqrt(nv / (n * (n - 1)));
            stkRegs[STK_REGS.X.INDEX()] = stDevX;
            stkRegs[STK_REGS.Y.INDEX()] = stDevY;
        } else {   //  n <= 1
            error = ERROR_STAT_1;
        }
        return error;
    }

    public String lr() {   //  LASTX non modifié
        String error = "";
        double n = getRegContentsByIndex(getRegIndexByDataIndex(STATS.N.DATA_REG_INDEX()));
        if (n > 1) {
            double sumX = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_X.DATA_REG_INDEX()));
            double sumX2 = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_X2.DATA_REG_INDEX()));
            double sumY = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_Y.DATA_REG_INDEX()));
            double sumY2 = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_Y2.DATA_REG_INDEX()));
            double sumXY = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_XY.DATA_REG_INDEX()));
            double mv = n * sumX2 - sumX * sumX;
            double nv = n * sumY2 - sumY * sumY;
            double p = n * sumXY - sumX * sumY;
            double a = p / mv;
            double b = (mv * sumY - p * sumX) / (n * mv);
            stkRegs[STK_REGS.X.INDEX()] = b;
            stkRegs[STK_REGS.Y.INDEX()] = a;
        } else {   //  n <= 1
            error = ERROR_STAT_1;
        }
        return error;
    }

    public String yer() {
        String error = "";
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        double n = getRegContentsByIndex(getRegIndexByDataIndex(STATS.N.DATA_REG_INDEX()));
        if (n > 1) {
            double sumX = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_X.DATA_REG_INDEX()));
            double sumX2 = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_X2.DATA_REG_INDEX()));
            double sumY = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_Y.DATA_REG_INDEX()));
            double sumY2 = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_Y2.DATA_REG_INDEX()));
            double sumXY = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_XY.DATA_REG_INDEX()));
            double mv = n * sumX2 - sumX * sumX;
            double nv = n * sumY2 - sumY * sumY;
            double p = n * sumXY - sumX * sumY;
            double r = p / Math.sqrt(mv * nv);
            double ye = (mv * sumY + p * (n * stkRegs[STK_REGS.X.INDEX()] - sumX)) / (n * mv);
            stkRegs[STK_REGS.X.INDEX()] = ye;
            stkRegs[STK_REGS.Y.INDEX()] = r;
        } else {   //  n <= 1
            stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
            error = ERROR_STAT_1;
        }
        return error;
    }

    public String sumXYToXY() {
        String error = "";
        double sumX = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_X.DATA_REG_INDEX()));
        double sumY = getRegContentsByIndex(getRegIndexByDataIndex(STATS.SUM_Y.DATA_REG_INDEX()));
        stkRegs[STK_REGS.X.INDEX()] = sumX;
        stkRegs[STK_REGS.Y.INDEX()] = sumY;
        return error;
    }

    public String lastXToX() {   //  LASTX non modifié :)
        String error = "";
        stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
        return error;
    }

    public String piToX() {  //  LASTX non modifié
        String error = "";
        stkRegs[STK_REGS.X.INDEX()] = Math.PI;
        return error;
    }

    public String randToX() {  //  LASTX non modifié
        String error = "";
        stkRegs[STK_REGS.X.INDEX()] = Math.random();
        return error;
    }

    public String clX() {   //  T,Z,Y,X -> T,Z,Y,0    LASTX non modifié
        String error = "";
        stkRegs[STK_REGS.X.INDEX()] = 0;
        return error;
    }

    public void xchgXY() {   //  T,Z,Y,X -> T,Z,X,Y
        Double temp = stkRegs[STK_REGS.X.INDEX()];
        stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.Y.INDEX()];
        stkRegs[STK_REGS.Y.INDEX()] = temp;
    }

    public void stackClear() {   //  T,Z,Y,X -> 0,0,0,0
        stkRegs[STK_REGS.X.INDEX()] = 0;
        stkRegs[STK_REGS.Y.INDEX()] = 0;
        stkRegs[STK_REGS.Z.INDEX()] = 0;
        stkRegs[STK_REGS.T.INDEX()] = 0;
        stkRegs[STK_REGS.LX.INDEX()] = 0;
    }

    public void stackRollDown() {   //  T,Z,Y,X -> X,T,Z,Y
        Double temp = stkRegs[STK_REGS.X.INDEX()];
        stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.Y.INDEX()];
        stkRegs[STK_REGS.Y.INDEX()] = stkRegs[STK_REGS.Z.INDEX()];
        stkRegs[STK_REGS.Z.INDEX()] = stkRegs[STK_REGS.T.INDEX()];
        stkRegs[STK_REGS.T.INDEX()] = temp;
    }

    public void stackRollUp() {   //  T,Z,Y,X -> Z,Y,X,T
        Double temp = stkRegs[STK_REGS.T.INDEX()];
        stkRegs[STK_REGS.T.INDEX()] = stkRegs[STK_REGS.Z.INDEX()];
        stkRegs[STK_REGS.Z.INDEX()] = stkRegs[STK_REGS.Y.INDEX()];
        stkRegs[STK_REGS.Y.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        stkRegs[STK_REGS.X.INDEX()] = temp;
    }

    public void stackLift() {   //  T,Z,Y,X -> Z,Y,X,X
        stkRegs[STK_REGS.T.INDEX()] = stkRegs[STK_REGS.Z.INDEX()];
        stkRegs[STK_REGS.Z.INDEX()] = stkRegs[STK_REGS.Y.INDEX()];
        stkRegs[STK_REGS.Y.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
    }

    public void stackMergeDown() {   //  T,Z,Y,X -> T,T,Z,f(X,Y)
        stkRegs[STK_REGS.Y.INDEX()] = stkRegs[STK_REGS.Z.INDEX()];
        stkRegs[STK_REGS.Z.INDEX()] = stkRegs[STK_REGS.T.INDEX()];
    }

    public void saveStack() {
        stkRegs[STK_REGS.LX.INDEX()] = stkRegs[STK_REGS.X.INDEX()];
        stkRegs[STK_REGS.LY.INDEX()] = stkRegs[STK_REGS.Y.INDEX()];
        stkRegs[STK_REGS.LZ.INDEX()] = stkRegs[STK_REGS.Z.INDEX()];
        stkRegs[STK_REGS.LT.INDEX()] = stkRegs[STK_REGS.T.INDEX()];
    }

    public void restoreStack() {
        stkRegs[STK_REGS.X.INDEX()] = stkRegs[STK_REGS.LX.INDEX()];
        stkRegs[STK_REGS.Y.INDEX()] = stkRegs[STK_REGS.LY.INDEX()];
        stkRegs[STK_REGS.Z.INDEX()] = stkRegs[STK_REGS.LZ.INDEX()];
        stkRegs[STK_REGS.T.INDEX()] = stkRegs[STK_REGS.LT.INDEX()];
    }

    public String aToX(String alpha) {
        String error = "";
        if (isDouble(alpha)) {
            stkRegs[STK_REGS.X.INDEX()] = Double.parseDouble(alpha);
        } else {
            error = ERROR_NOT_NUMBER;   //  Echec
        }
        return error;
    }

    private String roundForDisplay(double value) {
        double val = Math.abs(value);
        String res = "";
        int exp = 1;
        double mant = 0;
        if (val != 0) {
            exp = (int) Math.floor(1d + Math.log10(val));
            mant = val / Math.pow(10, exp);   //  Entre 0 et 1
        }
        int expr = 0;
        OPERATIONS rm = roundMode;   //  FIX, SCI, ENG
        if (rm.equals(OPERATIONS.FIX)) {
            expr = 0;
            if (val != 0) {
                if ((val >= Math.pow(10, MAX_DIGITS)) || (val < Math.pow(10, -roundParam))) {   //  Trop grand ou trop petit => Afficher comme SCI
                    rm = OPERATIONS.SCI;
                }
            }
        }
        if (!rm.equals(OPERATIONS.FIX)) {   //  SCI ou ENG
            expr = exp - 1;
            if (rm.equals(OPERATIONS.ENG)) {   //  Ajuster pour obtenir un exposant multiple de 3
                int p = Math.abs(expr) % 3;
                if (p != 0) {
                    expr = expr - (expr < 0 ? 3 - p : p);
                }
            }
        }
        double valr = mant * Math.pow(10, exp - expr);
        res = String.format(Locale.US, "%,." + (Math.min(roundParam, MAX_DIGITS - (exp - expr))) + "f", valr);
        if (!rm.equals(OPERATIONS.FIX)) {   //  SCI ou ENG
            res = res + "E" + expr;
        }
        if (value < 0) {
            res = "-" + res;
        }
        return res;
    }

    private double angleToRad(double value) {
        double res = value;
        switch (angleMode) {
            case DEG:
                res = Math.toRadians(res);
                break;
            case GRAD:
                res = Math.toRadians(res) * 0.9d;
                break;
        }
        return res;
    }

    private double radToAngle(double value) {
        double res = value;
        switch (angleMode) {
            case DEG:
                res = Math.toDegrees(res);
                break;
            case GRAD:
                res = Math.toDegrees(res) / 0.9d;
                break;
        }
        return res;
    }

    private int fact(int m) {
        int res = 1;
        if (m != 0) {
            for (int i = 1; i <= m; i = i + 1) {
                res = res * i;
            }
        }
        return res;
    }

    private int factOver(int m, int n) {
        int res = 1;
        for (int i = m; i > n; i = i - 1)
            res = res * i;
        return res;
    }

    private boolean isDouble(String sNumber) {
        boolean res = true;
        double d = 0;
        try {
            d = Double.parseDouble(sNumber);
        } catch (IllegalArgumentException | SecurityException ex) {
            res = false;   //  Echec
        }
        return res;
    }

    private double gamma(double x) {   //  https://rosettacode.org/wiki/Gamma_function#Java
        double[] p = {0.99999999999980993, 676.5203681218851, -1259.1392167224028,
                771.32342877765313, -176.61502916214059, 12.507343278686905,
                -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7};
        int g = 7;
        if (x < 0.5) return Math.PI / (Math.sin(Math.PI * x) * gamma(1 - x));
        x -= 1;
        double a = p[0];
        double t = x + g + 0.5;
        for (int i = 1; i < p.length; i++) {
            a += p[i] / (x + i);
        }
        return Math.sqrt(2 * Math.PI) * Math.pow(t, x + 0.5) * Math.exp(-t) * a;
    }

    public ProgLine getProgLine(int progLineNumber) {
        return proglines.get(progLineNumber);
    }

    public int getProgLinesSize() {
        return proglines.size();
    }

    public void addProgLineAtNumber(ProgLine progLine, int progLineNumber) {
        proglines.add(progLineNumber, progLine);
    }

    public void removeProgLineAtNumber(int progLineNumber) {
        proglines.remove(progLineNumber);
    }

    public void clearProgLine(ProgLine progLine) {
        int n = progLine.getOpsSize();
        for (int i = 0; i <= (n - 1); i = i + 1) {
            progLine.setOp(i, null);
        }
    }

    public void addStkRetProgLineNumber(int progLineNumber) {   //  PUSH
        stkRet.add(0, progLineNumber);
    }

    public int getLastStkRetProgLineNumber() {   //  POP1
        return stkRet.get(0);
    }

    public void removeLastStkRetProgLineNumber() {   //  POP2
        stkRet.remove(0);
    }

    private void setupReturnStack() {
        stkRet = new ArrayList<Integer>();
        stkRet.add(0, END_RETURN_STACK);
    }

    private void setupProgLines() {
        proglines = new ArrayList<ProgLine>();
        proglines.add(new ProgLine());   //  Index 0
        ProgLine progLine = proglines.get(0);
        progLine.setOp(0, OPERATIONS.BEGIN);
    }

    public String progLineToString(int progLineNumber, boolean displaySymbol) {   //  displaySymbol True => Afficher uniquement symboles ; displaySymbol False => afficher keyCodes (et parfois symbol (p.ex. ".5" ...)
        String res = "";
        ProgLine progLine = proglines.get(progLineNumber);

        OPERATIONS[] ops = progLine.getOps();
        for (int i = 0; i <= (ops.length - 1); i = i + 1) {
            if (ops[i] != null) {
                KEYS key = getKeyByOp(ops[i]);
                if (res.equals("")) {   //  Préfixer de l'éventuelle touche shift F ou G
                    int shiftKeyCode = getShiftKeyCodeByOp(ops[i]);
                    if (shiftKeyCode != UNSHIFTED_KEY_CODE) {   //  Pas Unshifted => Operation avec shift F ou G
                        if (!displaySymbol) {
                            res = String.valueOf(shiftKeyCode);
                        }
                    }
                }
                String s = (!displaySymbol ? String.valueOf(key.CODE()) : ops[i].SYMBOL());   //  keyCode, sans formatage
                if (!displaySymbol) {
                    if (ops[i].equals(OPERATIONS.DOT)) {   //  Si "." est suivi par un chiffre n => afficher .n
                        if (i < (ops.length - 1)) {
                            if (ops[i + 1] != null) {
                                OPERATIONS nextOp = ops[i + 1];
                                if (((nextOp.INDEX() >= OPERATIONS.DIGIT_0.INDEX()) && (nextOp.INDEX() <= OPERATIONS.DIGIT_9.INDEX()))) {
                                    s = OPERATIONS.DOT.SYMBOL();
                                }
                            }
                        }
                    } else {   //  Pas "."
                        OPERATIONS unshiftedOp = key.UNSHIFTED_OP();   //  Opération sans aucune touche Shift
                        if (((unshiftedOp.INDEX() >= OPERATIONS.DIGIT_0.INDEX()) && (unshiftedOp.INDEX() <= OPERATIONS.DIGIT_9.INDEX()))) {   //  Afficher chiffre (même si operation n'est pas chiffre)
                            s = unshiftedOp.SYMBOL();
                        }
                    }
                }
                res = res + (!res.equals("") ? " " : "") + s;
            }
            res = String.format("0000", progLineNumber) + ": " + res;
        }
        return res;
    }

    private void setupMaps() {
        symbolToBaseRegMap = new HashMap<String, BASE_REGS>();
        for (BASE_REGS baseReg : BASE_REGS.values()) {   //  Uniquement pour les registres de base (I, R0 à R9, R.0 à R.9)
            symbolToBaseRegMap.put(baseReg.SYMBOL(), baseReg);
        }

        opToKeyMap = new HashMap<OPERATIONS, KEYS>();
        opToShiftKeyCodeMap = new HashMap<OPERATIONS, Integer>();
        keyCodeToKeyMap = new HashMap<Integer, KEYS>();
        for (KEYS key : KEYS.values()) {
            keyCodeToKeyMap.put(key.CODE(), key);
            OPERATIONS op = key.UNSHIFTED_OP();
            if (!op.equals(OPERATIONS.UNKNOWN)) {
                opToKeyMap.put(op, key);
                opToShiftKeyCodeMap.put(op, UNSHIFTED_KEY_CODE);
            }
            op = key.SHIFT_F_OP();
            if (!op.equals(OPERATIONS.UNKNOWN)) {
                opToKeyMap.put(op, key);
                opToShiftKeyCodeMap.put(op, SHIFT_F_KEY_CODE);
            }
            op = key.SHIFT_G_OP();
            if (!op.equals(OPERATIONS.UNKNOWN)) {
                opToKeyMap.put(op, key);
                opToShiftKeyCodeMap.put(op, SHIFT_G_KEY_CODE);
            }
        }
    }
}
