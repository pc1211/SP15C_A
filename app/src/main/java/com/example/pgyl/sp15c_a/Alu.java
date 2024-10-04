package com.example.pgyl.sp15c_a;

import com.example.pgyl.sp15c_a.ProgLine.LINE_OPS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Alu {
    public enum OPS {
        BEGIN("BEGIN"),
        PR("P/R"),
        RS("R/S"),
        SST("SST"),
        BST("BST"),
        GTO("GTO"),
        GSB("GSB"),
        LBL("LBL"),
        RTN("RTN"),
        DSE("DSE"),
        ISG("ISG"),
        USER("USER"),
        MEM("MEM"),
        ON("ON"),
        PSE("PSE"),
        MATRIX("MATRIX"),
        RESULT("RESULT"),
        //  ****************************** Début Bloc (à laisser dans l'ordre, pour identifier facilement "de A à E")
        A("A"),
        B("B"),
        C("C"),
        D("D"),
        E("E"),
        //  ****************************** Fin Bloc
        F("F"),
        G("G"),
        I("I"),
        INDI("(i)"),
        //  ****************************** Début Bloc (à laisser dans l'ordre, pour identifier facilement "de 0 à 9")
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
        //  ****************************** Fin Bloc
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
        XNE0("x<>0?"),
        XG0("x>0?"),
        XL0("x<0?"),
        XGE0("x>=0?"),
        XLE0("x<=0?"),
        XEY("x=y?"),
        XNEY("x<>y?"),
        XGY("x>y?"),
        XLY("x<y?"),
        XGEY("x>=y?"),
        XLEY("x<=y?"),
        XE0("x=0?"),
        TEST("TEST"),
        SQRT("SQRT"),
        SQR("x^2"),
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
        RAND("RAND"),
        RECT("->R"),
        POL("->P"),
        HMS("->H.MS"),
        H("->H"),
        COMB("CY,X"),
        PERM("PY,X"),
        FRAC("FRAC"),
        INTEGER("INT"),
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
        CLEAR_PRGM("CLPRGM"),
        CLEAR_REGS("CLREG"),
        CLEAR_PREFIX("PREFIX"),
        XCHG("X<>"),
        SF("SF"),
        CF("CF"),
        TF("TF"),
        UNKNOWN("?");

        private String symbol;

        OPS(String symbol) {
            this.symbol = symbol;
        }

        public String SYMBOL() {
            return symbol;
        }

        public int INDEX() {
            return ordinal();
        }
    }

    public enum KEYS {
        KEY_11(11, OPS.SQRT, OPS.A, OPS.SQR),
        KEY_12(12, OPS.EXP, OPS.B, OPS.LN),
        KEY_13(13, OPS.EXP10, OPS.C, OPS.LOG),
        KEY_14(14, OPS.POWER, OPS.D, OPS.PC),
        KEY_15(15, OPS.INV, OPS.E, OPS.DPC),
        KEY_16(16, OPS.CHS, OPS.MATRIX, OPS.ABS),   //  MATRIX inactif
        KEY_17(17, OPS.DIGIT_7, OPS.FIX, OPS.DEG),
        KEY_18(18, OPS.DIGIT_8, OPS.SCI, OPS.RAD),
        KEY_19(19, OPS.DIGIT_9, OPS.ENG, OPS.GRAD),
        KEY_10(10, OPS.DIV, OPS.UNKNOWN, OPS.XLEY),
        KEY_21(21, OPS.SST, OPS.LBL, OPS.BST),
        KEY_22(22, OPS.GTO, OPS.HYP, OPS.AHYP),
        KEY_23(23, OPS.SIN, OPS.DIM, OPS.ASIN),
        KEY_24(24, OPS.COS, OPS.INDI, OPS.ACOS),
        KEY_25(25, OPS.TAN, OPS.I, OPS.ATAN),
        KEY_26(26, OPS.EEX, OPS.RESULT, OPS.PI),   //  RESULT inactif
        KEY_27(27, OPS.DIGIT_4, OPS.XCHG, OPS.SF),
        KEY_28(28, OPS.DIGIT_5, OPS.DSE, OPS.CF),
        KEY_29(29, OPS.DIGIT_6, OPS.ISG, OPS.TF),
        KEY_20(20, OPS.MULT, OPS.UNKNOWN, OPS.XE0),
        KEY_31(31, OPS.RS, OPS.PSE, OPS.PR),
        KEY_32(32, OPS.GSB, OPS.CLEAR_SIGMA, OPS.RTN),
        KEY_33(33, OPS.RDN, OPS.CLEAR_PRGM, OPS.RUP),
        KEY_34(34, OPS.XCHGXY, OPS.CLEAR_REGS, OPS.RND),
        KEY_35(35, OPS.BACK, OPS.CLEAR_PREFIX, OPS.CLX),
        KEY_36(36, OPS.ENTER, OPS.RAND, OPS.LASTX),
        KEY_37(37, OPS.DIGIT_1, OPS.RECT, OPS.POL),
        KEY_38(38, OPS.DIGIT_2, OPS.HMS, OPS.H),
        KEY_39(39, OPS.DIGIT_3, OPS.TO_RAD, OPS.TO_DEG),
        KEY_30(30, OPS.MINUS, OPS.UNKNOWN, OPS.TEST),
        KEY_41(41, OPS.ON, OPS.ON, OPS.ON),   //  ON inactif
        KEY_42(42, OPS.F, OPS.UNKNOWN, OPS.UNKNOWN),
        KEY_43(43, OPS.G, OPS.UNKNOWN, OPS.UNKNOWN),
        KEY_44(44, OPS.STO, OPS.FRAC, OPS.INTEGER),
        KEY_45(45, OPS.RCL, OPS.USER, OPS.MEM),    //  MEM inactif
        KEY_47(47, OPS.DIGIT_0, OPS.FACT, OPS.MEAN),
        KEY_48(48, OPS.DOT, OPS.YER, OPS.STDEV),
        KEY_49(49, OPS.SIGMA_PLUS, OPS.LR, OPS.SIGMA_MINUS),
        KEY_40(40, OPS.PLUS, OPS.PERM, OPS.COMB);

        private int code;
        private OPS uOp;   //  Unshifted operation
        private OPS fOp;   //  Shift F operation
        private OPS gOp;   //  Shift G operation

        KEYS(int code, OPS uOp, OPS fOp, OPS gOp) {
            this.code = code;
            this.uOp = uOp;
            this.fOp = fOp;
            this.gOp = gOp;
        }

        public int CODE() {
            return code;
        }

        public OPS UNSHIFTED_OP() {
            return uOp;
        }

        public OPS SHIFT_F_OP() {
            return fOp;
        }

        public OPS SHIFT_G_OP() {
            return gOp;
        }

        public int INDEX() {
            return ordinal();
        }
    }

    public enum GHOST_KEYS {
        GHOST_KEY_1(OPS.SINH, OPS.HYP, OPS.SIN),
        GHOST_KEY_2(OPS.COSH, OPS.HYP, OPS.COS),
        GHOST_KEY_3(OPS.TANH, OPS.HYP, OPS.TAN),
        GHOST_KEY_4(OPS.ASINH, OPS.AHYP, OPS.SIN),
        GHOST_KEY_5(OPS.ACOSH, OPS.AHYP, OPS.COS),
        GHOST_KEY_6(OPS.ATANH, OPS.AHYP, OPS.TAN),
        GHOST_KEY_7(OPS.XNE0, OPS.TEST, OPS.DIGIT_0),
        GHOST_KEY_8(OPS.XG0, OPS.TEST, OPS.DIGIT_1),
        GHOST_KEY_9(OPS.XL0, OPS.TEST, OPS.DIGIT_2),
        GHOST_KEY_10(OPS.XGE0, OPS.TEST, OPS.DIGIT_3),
        GHOST_KEY_11(OPS.XLE0, OPS.TEST, OPS.DIGIT_4),
        GHOST_KEY_12(OPS.XEY, OPS.TEST, OPS.DIGIT_5),
        GHOST_KEY_13(OPS.XNEY, OPS.TEST, OPS.DIGIT_6),
        GHOST_KEY_14(OPS.XGY, OPS.TEST, OPS.DIGIT_7),
        GHOST_KEY_15(OPS.XLY, OPS.TEST, OPS.DIGIT_8),
        GHOST_KEY_16(OPS.XGEY, OPS.TEST, OPS.DIGIT_9);

        private OPS op;
        private OPS ghostOp1;
        private OPS ghostOp2;

        GHOST_KEYS(OPS Op, OPS ghostOp1, OPS ghostOp2) {
            this.op = Op;
            this.ghostOp1 = ghostOp1;
            this.ghostOp2 = ghostOp2;
        }

        public OPS OP() {
            return op;
        }

        public OPS GHOST_OP1() {
            return ghostOp1;
        }

        public OPS GHOST_OP2() {
            return ghostOp2;
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

    public enum LABELS {   //  Registres de base (I, R0 à R9, R.0 à R.9) (avec les registres classiques de données (data) à partir de R0)
        L0("0"), L1("1"), L2("2"), L3("3"), L4("4"), L5("5"), L6("6"), L7("7"), L8("8"), L9("9"),
        LDOT0(".0"), LDOT1(".1"), LDOT2(".2"), LDOT3(".3"), LDOT4(".4"), LDOT5(".5"), LDOT6(".6"), LDOT7(".7"), LDOT8(".8"), LDOT9(".9"),
        LA("A"), LB("B"), LC("C"), LD("D"), LE("E");

        private String symbol;

        LABELS(String symbol) {
            this.symbol = symbol;
        }

        public String SYMBOL() {
            return symbol;
        }

        public int INDEX() {
            return ordinal();
        }
    }

    public enum STAT_OPS {
        N(2), SUM_X(3), SUM_X2(4), SUM_Y(5), SUM_Y2(6), SUM_XY(7);

        private int dataRegIndex;

        STAT_OPS(int dataRegIndex) {
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
    final int MAX_PROG_LINES = 9999;
    final int MAX_FLAGS = 10;
    final int MAX_RETS = 100;
    final int MAX_REGS = 1000;   //  Max, inclus les 21 registres de base de BASE_REGS (I, R0 à R9, R.0 à R.9)
    final int DEF_MAX_REGS = 100;    //  Par défaut, inclus les 21 registres de base de BASE_REGS (I, R0 à R9, R.0 à R.9)

    final String ERROR_OVERFLOW = "Overflow";
    final String ERROR_LOG = "Log(Neg or 0)";
    final String ERROR_DIV_BY_0 = "Div By 0";
    final String ERROR_NUMBER = "Invalid number";
    final String ERROR_SQRT_NEG = "Sqrt(Neg)";
    final String ERROR_STAT_0 = "Stat n <= 0";
    final String ERROR_STAT_1 = "Stat n <= 1";
    final String ERROR_PERM_COMB = "Invalid Perm/Comb";
    final String ERROR_GTO_GSB = "Invalid GTO/GSB";

    final int END_RETURN_STACK = -1;
    final int UNSHIFTED_KEY_CODE = 0;
    final int SHIFT_F_KEY_CODE = 42;
    final int SHIFT_G_KEY_CODE = 43;

    private double[] stkRegs;
    private boolean[] flags;
    private ArrayList<Double> regs;   //  Les registres de BASE_REGS puis les suivants (accessibles par (i) )
    private OPS roundMode;
    private int roundParam;
    private OPS angleMode;
    private boolean stackLiftEnabled;
    private HashMap<String, BASE_REGS> symbolToBaseRegMap;
    private HashMap<OPS, KEYS> opToKeyMap;
    private HashMap<OPS, Integer> opToShiftKeyCodeMap;
    private HashMap<OPS, GHOST_KEYS> opToGhostKeyMap;
    private HashMap<PairOp, OPS> ghostOpsToOpMap;
    private HashMap<String, LABELS> symbolToLabelMap;
    private HashMap<Integer, LABELS> labelIndexToLabelMap;
    private HashMap<LABELS, Integer> labelToprogLineNumberMap;
    private ArrayList<ProgLine> proglines;
    private ArrayList<Integer> stkRet;

    public Alu() {
        init();
    }

    private void init() {
        regs = new ArrayList<Double>();
        for (int i = 0; i <= (DEF_MAX_REGS - 1); i = i + 1) {   //  Les 21 registres de base (I, R0 à R9, R.0 à R.9) sont au début
            regs.add(0d);
        }

        setupMaps();
        stkRegs = new double[STK_REGS.values().length];
        clearStack();
        flags = new boolean[MAX_FLAGS];
        clearFlags();
        setupProgLines();
        setupReturnStack();
        angleMode = OPS.RAD;
        roundMode = OPS.FIX;
        roundParam = 4;
        stackLiftEnabled = false;
    }

    public void close() {
        stkRegs = null;
        flags = null;
        stkRet.clear();
        stkRet = null;
        regs.clear();
        regs = null;
        proglines.clear();
        proglines = null;
        symbolToBaseRegMap.clear();
        symbolToBaseRegMap = null;
        opToKeyMap.clear();
        opToKeyMap = null;
        opToShiftKeyCodeMap.clear();
        opToShiftKeyCodeMap = null;
        opToGhostKeyMap.clear();
        opToGhostKeyMap = null;
        ghostOpsToOpMap.clear();
        ghostOpsToOpMap = null;
        symbolToLabelMap.clear();
        symbolToLabelMap = null;
        labelIndexToLabelMap.clear();
        labelIndexToLabelMap = null;
        labelToprogLineNumberMap.clear();
        labelToprogLineNumberMap = null;
    }

    public String setMaxDataRegIndex(int newMaxDataRegIndex) {   //  les registres de données classiques (data) commencent à partir de R0
        String res = "";
        final String RANGE_ERROR = "Out of " + (BASE_REGS.values().length - BASE_REGS.R0.INDEX()) + "-" + (MAX_REGS - 1 - BASE_REGS.R0.INDEX());

        int oldMaxDataRegIndex = regs.size() - 1 - BASE_REGS.R0.INDEX();
        int n = newMaxDataRegIndex - oldMaxDataRegIndex;
        if (n > 0) {   //  Ajouter n registres
            if (newMaxDataRegIndex <= (MAX_REGS - 1 - BASE_REGS.R0.INDEX())) {   //  Respecte Max
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    regs.add(0d);
                }
            } else {  //  > Max
                res = RANGE_ERROR;
            }
        } else {   //  Retirer n registres, en commençant par les derniers de la liste
            if (newMaxDataRegIndex >= (BASE_REGS.values().length - BASE_REGS.R0.INDEX())) {   //   Respecte Min
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    regs.subList(newMaxDataRegIndex + BASE_REGS.R0.INDEX(), oldMaxDataRegIndex + BASE_REGS.R0.INDEX()).clear();
                }
            } else {   //  < Min
                res = RANGE_ERROR;
            }
        }
        return res;
    }

    public String clearRegs() {
        String res = "";
        for (int i = 0; i <= (regs.size() - 1); i = i + 1) {   //  Tout effacer: I, R0 à R9, R.0 à R.9 et suivants
            regs.set(i, 0d);
        }
        return res;
    }

    public int getRegIndexByDataRegIndex(int index) {
        return BASE_REGS.R0.INDEX() + index;
    }   //  les registres de données classiques (data) commencent à partir de R0

    public int getDataRegIndexByIndex(int index) {
        return index - BASE_REGS.R0.INDEX();
    }   //  les registres de données classiques (data) commencent à partir de R0

    public int getRegIndexBySymbol(String symbol) {   //  Pour les premiers registres de regs, cad ceux de BASE_REGS (I, R0 à R9, R.0 à R.9)
        return symbolToBaseRegMap.get(symbol).INDEX();
    }

    public boolean isGhostKey(OPS op) {
        return (opToGhostKeyMap.get(op) != null);
    }

    public OPS getOpByGhostKeyOps(OPS prefixOp, OPS lastOp) {
        PairOp pairOp = new PairOp(prefixOp, lastOp);
        OPS op = ghostOpsToOpMap.get(pairOp);
        pairOp = null;
        return op;
    }

    public KEYS getKeyByOp(OPS op) {
        return opToKeyMap.get(op);
    }

    public ArrayList<Double> getRegs() {
        return regs;
    }

    public void setAngleMode(OPS angleMode) {
        this.angleMode = angleMode;
    }

    public void setRoundMode(OPS roundMode) {
        this.roundMode = roundMode;
    }

    public void setRoundParam(int roundParam) {
        this.roundParam = Math.min(MAX_DIGITS - 1, roundParam);
    }

    public OPS getAngleMode() {
        return angleMode;
    }

    public OPS getRoundMode() {
        return roundMode;
    }

    public int getRoundParam() {
        return roundParam;
    }

    public int getRegsMaxIndex() {
        return regs.size() - 1;
    }

    public int getRegsAbsoluteSizeMax() {
        return MAX_REGS;
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

    public String xToReg4Op(int index, OPS op) {   //  STO+-*/  Reg
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

    public String regToX4Op(int index, OPS op) {   //   RCL+-*/ Reg
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

    public String integerX() {
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

    public boolean test(OPS op) {
        boolean res = false;
        double x = round(stkRegs[STK_REGS.X.INDEX()], MAX_DIGITS + 1);
        double y = round(stkRegs[STK_REGS.Y.INDEX()], MAX_DIGITS + 1);
        switch (op) {
            case XNE0:
                res = (x != 0d);
                break;
            case XG0:
                res = (x > 0d);
                break;
            case XL0:
                res = (x < 0d);
                break;
            case XGE0:
                res = (x >= 0d);
                break;
            case XLE0:
                res = (x <= 0d);
                break;
            case XEY:
                res = (x == y);
                break;
            case XNEY:
                res = (x != y);
                break;
            case XGY:
                res = (x > y);
                break;
            case XLY:
                res = (x < y);
                break;
            case XGEY:
                res = (x >= y);
                break;
            case XLEY:
                res = (x <= y);
                break;
            case XE0:
                res = (x == 0d);
                break;
        }
        return res;
    }

    public void setFlag(int index) {
        flags[index] = true;
    }

    public void clearFlag(int index) {
        flags[index] = false;
    }

    public boolean testFlag(int index) {
        return flags[index];
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

    public String clearStats() {   //  LASTX non modifié
        String error = "";
        for (STAT_OPS stat : STAT_OPS.values()) {
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
            int index = getRegIndexByDataRegIndex(STAT_OPS.N.DATA_REG_INDEX());
            int nMod = (int) getRegContentsByIndex(index) + 1;
            setRegContentsByIndex(index, nMod);

            index = getRegIndexByDataRegIndex(STAT_OPS.SUM_X.DATA_REG_INDEX());
            double sumXMod = getRegContentsByIndex(index) + stkRegs[STK_REGS.X.INDEX()];
            setRegContentsByIndex(index, sumXMod);

            index = getRegIndexByDataRegIndex(STAT_OPS.SUM_X2.DATA_REG_INDEX());
            double sumX2Mod = getRegContentsByIndex(index) + stkRegs[STK_REGS.X.INDEX()] * stkRegs[STK_REGS.X.INDEX()];
            setRegContentsByIndex(index, sumX2Mod);

            index = getRegIndexByDataRegIndex(STAT_OPS.SUM_Y.DATA_REG_INDEX());
            double sumYMod = getRegContentsByIndex(index) + stkRegs[STK_REGS.Y.INDEX()];
            setRegContentsByIndex(index, sumYMod);

            index = getRegIndexByDataRegIndex(STAT_OPS.SUM_Y2.DATA_REG_INDEX());
            double sumY2Mod = getRegContentsByIndex(index) + stkRegs[STK_REGS.Y.INDEX()] * stkRegs[STK_REGS.Y.INDEX()];
            setRegContentsByIndex(index, sumY2Mod);

            index = getRegIndexByDataRegIndex(STAT_OPS.SUM_XY.DATA_REG_INDEX());
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
            int index = getRegIndexByDataRegIndex(STAT_OPS.N.DATA_REG_INDEX());
            int nMod = (int) getRegContentsByIndex(index) - 1;
            setRegContentsByIndex(index, nMod);

            index = getRegIndexByDataRegIndex(STAT_OPS.SUM_X.DATA_REG_INDEX());
            double sumXMod = getRegContentsByIndex(index) - stkRegs[STK_REGS.X.INDEX()];
            setRegContentsByIndex(index, sumXMod);

            index = getRegIndexByDataRegIndex(STAT_OPS.SUM_X2.DATA_REG_INDEX());
            double sumX2Mod = getRegContentsByIndex(index) - stkRegs[STK_REGS.X.INDEX()] * stkRegs[STK_REGS.X.INDEX()];
            setRegContentsByIndex(index, sumX2Mod);

            index = getRegIndexByDataRegIndex(STAT_OPS.SUM_Y.DATA_REG_INDEX());
            double sumYMod = getRegContentsByIndex(index) - stkRegs[STK_REGS.Y.INDEX()];
            setRegContentsByIndex(index, sumYMod);

            index = getRegIndexByDataRegIndex(STAT_OPS.SUM_Y2.DATA_REG_INDEX());
            double sumY2Mod = getRegContentsByIndex(index) - stkRegs[STK_REGS.Y.INDEX()] * stkRegs[STK_REGS.Y.INDEX()];
            setRegContentsByIndex(index, sumY2Mod);

            index = getRegIndexByDataRegIndex(STAT_OPS.SUM_XY.DATA_REG_INDEX());
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
        double n = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.N.DATA_REG_INDEX()));
        if (n > 0) {
            double sumX = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_X.DATA_REG_INDEX()));
            double sumY = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_Y.DATA_REG_INDEX()));
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
        double n = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.N.DATA_REG_INDEX()));
        if (n > 1) {
            double sumX = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_X.DATA_REG_INDEX()));
            double sumX2 = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_X2.DATA_REG_INDEX()));
            double sumY = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_Y.DATA_REG_INDEX()));
            double sumY2 = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_Y2.DATA_REG_INDEX()));
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
        double n = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.N.DATA_REG_INDEX()));
        if (n > 1) {
            double sumX = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_X.DATA_REG_INDEX()));
            double sumX2 = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_X2.DATA_REG_INDEX()));
            double sumY = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_Y.DATA_REG_INDEX()));
            double sumY2 = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_Y2.DATA_REG_INDEX()));
            double sumXY = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_XY.DATA_REG_INDEX()));
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
        double n = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.N.DATA_REG_INDEX()));
        if (n > 1) {
            double sumX = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_X.DATA_REG_INDEX()));
            double sumX2 = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_X2.DATA_REG_INDEX()));
            double sumY = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_Y.DATA_REG_INDEX()));
            double sumY2 = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_Y2.DATA_REG_INDEX()));
            double sumXY = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_XY.DATA_REG_INDEX()));
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
        double sumX = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_X.DATA_REG_INDEX()));
        double sumY = getRegContentsByIndex(getRegIndexByDataRegIndex(STAT_OPS.SUM_Y.DATA_REG_INDEX()));
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

    public void clearStack() {   //  T,Z,Y,X -> 0,0,0,0
        stkRegs[STK_REGS.X.INDEX()] = 0;
        stkRegs[STK_REGS.Y.INDEX()] = 0;
        stkRegs[STK_REGS.Z.INDEX()] = 0;
        stkRegs[STK_REGS.T.INDEX()] = 0;
        stkRegs[STK_REGS.LX.INDEX()] = 0;
    }

    public void clearFlags() {
        int n = flags.length;
        for (int i = 1; i <= (n - 1); i = i + 1) {
            flags[i] = false;
        }
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
            error = ERROR_NUMBER;   //  Echec
        }
        return error;
    }

    public String roundForDisplay(double value) {
        double val = Math.abs(value);
        String res = "";
        int exp = 1;
        double mant = 0;
        if (val != 0) {
            exp = (int) Math.floor(1d + Math.log10(val));
            mant = val / Math.pow(10, exp - 1) / 10d;   //  Entre 0 et 1;    exp-1 pour éviter overflow
        }
        int expr = 0;
        OPS rm = roundMode;   //  FIX, SCI, ENG
        if (rm.equals(OPS.FIX)) {
            expr = 0;
            if (val != 0) {
                if ((val >= Math.pow(10, MAX_DIGITS)) || (val < Math.pow(10, -roundParam))) {   //  Trop grand ou trop petit => Afficher comme SCI
                    rm = OPS.SCI;
                }
            }
        }
        if (!rm.equals(OPS.FIX)) {   //  SCI ou ENG
            expr = exp - 1;
            if (rm.equals(OPS.ENG)) {   //  Ajuster pour obtenir un exposant multiple de 3
                int p = Math.abs(expr) % 3;
                if (p != 0) {
                    expr = expr - (expr < 0 ? 3 - p : p);
                }
            }
        }
        double valr = mant * Math.pow(10, exp - expr);
        res = String.format(Locale.US, "%,." + (Math.min(roundParam, MAX_DIGITS - (exp - expr))) + "f", valr);
        if (!rm.equals(OPS.FIX)) {   //  SCI ou ENG
            res = res + " E" + expr;
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

    public void stackLiftIfEnabled() {
        if (stackLiftEnabled) {
            stackLift();
        }
    }

    public void setStackLiftEnabled(boolean enabled) {
        stackLiftEnabled = enabled;
    }

    public boolean getStackLiftEnabled() {
        return stackLiftEnabled;
    }

    public boolean isDouble(String sNumber) {
        boolean res = true;
        try {
            double d = Double.parseDouble(sNumber);
            if ((Double.isNaN(d)) || (Double.isInfinite(d))) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException | SecurityException ex) {
            res = false;   //  Echec
        }
        return res;
    }

    private double round(double value, int n) {
        int scale = (int) Math.pow(10, n);
        double res = Math.round(Math.abs(value) * scale) / scale;
        if (value < 0) {
            res = -res;
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

    public boolean addProgLineAtNumber(ProgLine progLine, int progLineNumber) {
        boolean res = false;
        if (proglines.size() < MAX_PROG_LINES) {   //  Il y a encore assez de lignes dispponibles
            ProgLine newProgLine = new ProgLine();
            int n = progLine.ops.length;
            for (int i = 0; i <= (n - 1); i = i + 1) {   //  Copier dans la nouvelle ligne
                newProgLine.ops[i] = progLine.ops[i];
            }
            newProgLine.ref = progLine.ref;
            newProgLine.symbol = progLine.symbol;
            proglines.add(progLineNumber, newProgLine);
            res = true;
        }
        return res;
    }

    public void removeProgLineAtNumber(int progLineNumber) {
        proglines.remove(progLineNumber);
    }

    public void clearProgLine(ProgLine progLine) {
        int n = progLine.ops.length;
        for (int i = 0; i <= (n - 1); i = i + 1) {
            progLine.ops[i] = null;
        }
        progLine.ref = 0;
        progLine.symbol = "";
    }

    public void clearProgLines() {
        proglines.clear();
        ProgLine progLine = new ProgLine();
        progLine.ops[LINE_OPS.BASE.INDEX()] = OPS.BEGIN;
        proglines.add(progLine);    // A l'index 0, proglines contient au moins BEGIN
    }

    private void setupProgLines() {
        proglines = new ArrayList<ProgLine>();
        ProgLine progLine = new ProgLine();
        progLine.ops[LINE_OPS.BASE.INDEX()] = OPS.BEGIN;
        proglines.add(progLine);   // A l'index 0, proglines contient au moins BEGIN
    }

    public String progLineToString(int progLineNumber, boolean displaySymbol) {   //  displaySymbol True => Afficher uniquement symboles ; displaySymbol False => afficher keyCodes (et parfois symbol (p.ex. ".5" ...)
        final String SEP = " ";
        String res = "";
        String s = "";
        if (progLineNumber != 0) {
            ProgLine progLine = proglines.get(progLineNumber);
            OPS opBase = progLine.ops[LINE_OPS.BASE.INDEX()];   //  LINE_OPS: BASE, A4OP, DOT, A09, AE, I, DIM, INDI, RAND, SIGMA_PLUS, CHS, GHOST1, GHOST2
            boolean isGhost = (opToGhostKeyMap.get(opBase) != null);
            int iMin = ((isGhost && !displaySymbol) ? LINE_OPS.GHOST1.INDEX() : LINE_OPS.BASE.INDEX());
            int iMax = ((isGhost && !displaySymbol) ? LINE_OPS.GHOST2.INDEX() : LINE_OPS.SIGMA_PLUS.INDEX());   //  On ne prend pas le CHS car n'a été utilisé que pour le GTO CHS nnnnn en mode EDIT
            int i = iMin;
            do {
                if (progLine.ops[i] != null) {
                    String sep = SEP;
                    if (!displaySymbol) {   //  Codes
                        KEYS key = opToKeyMap.get(progLine.ops[i]);
                        s = String.valueOf(key.CODE());
                        OPS unshiftedOp = key.UNSHIFTED_OP();   //  Opération sans aucune touche Shift
                        if (((unshiftedOp.INDEX() >= OPS.DIGIT_0.INDEX()) && (unshiftedOp.INDEX() <= OPS.DIGIT_9.INDEX()))) {   //  Afficher chiffre (même si operation n'est pas chiffre)
                            s = unshiftedOp.SYMBOL();
                        }
                        if (i == LINE_OPS.DOT.INDEX()) {
                            if (progLine.ops[LINE_OPS.A09.INDEX()] != null) {
                                s = OPS.DOT.SYMBOL();
                            }
                        }
                        if (res.equals("")) {
                            int shiftKeyCode = opToShiftKeyCodeMap.get(progLine.ops[i]);   //  Préfixer de l'éventuelle touche shift F ou G
                            if (shiftKeyCode != UNSHIFTED_KEY_CODE) {
                                s = shiftKeyCode + sep + s;
                            }
                        }
                    } else {   //  Symboles
                        if ((progLine.ops[i].equals(OPS.EEX)) || (progLine.ops[i].equals(OPS.CHS))) {
                            s = progLine.ops[i].toString();
                        } else {   //  Pas EEX ni CHS
                            s = progLine.ops[i].SYMBOL();
                        }
                        if ((opBase.equals(OPS.STO)) || (opBase.equals(OPS.RCL))) {
                            if (i == LINE_OPS.A4OP.INDEX()) {
                                sep = "";   //  Pour avoir +-*/ juste à côté de l'op: STO+ ... RCL* ..., et non STO + ... RCL * ...
                            }
                        }
                        if (opBase.equals(OPS.XCHG)) {
                            if (i != LINE_OPS.BASE.INDEX()) {
                                sep = "";   //  Pour avoir X<>1  X<>(i) ...
                            }
                        }
                    }
                    res = res + (!res.equals("") ? sep : "") + s;
                }
                i = i + 1;
            } while (i <= iMax);
        } else {   //  Ligne 0
            if (!displaySymbol) {   //  Codes
                res = "00";
            } else {   //  Symboles
                res = OPS.BEGIN.SYMBOL();
            }
        }
        res = String.format("%04d", progLineNumber) + ": " + res;
        return res;
    }

    public void rebuildlabelToProgLineNumberMap() {
        labelToprogLineNumberMap = new HashMap<LABELS, Integer>();
        int n = proglines.size();
        for (int i = 0; i <= (n - 1); i = i + 1) {
            ProgLine progLine = proglines.get(i);
            OPS op = progLine.ops[LINE_OPS.BASE.INDEX()];
            if (op.equals(OPS.LBL)) {
                labelToprogLineNumberMap.put(symbolToLabelMap.get(progLine.symbol), i);
            }
        }
    }

    public void linkGTOGSBToProgLineNumbers() {
        int n = proglines.size();
        for (int i = 0; i <= (n - 1); i = i + 1) {
            ProgLine progLine = proglines.get(i);
            OPS op = progLine.ops[LINE_OPS.BASE.INDEX()];
            if ((op.equals(OPS.GTO)) || (op.equals(OPS.GSB))) {
                LABELS lbl = symbolToLabelMap.get(progLine.symbol);
                int pln = labelToprogLineNumberMap.get(lbl);
                progLine.ref = pln;
            }
        }
    }

    public int getDestProgLineNumber(ProgLine progLine) {
        int res = -1;
        Integer pln = null;
        OPS op = progLine.ops[LINE_OPS.BASE.INDEX()];   //  GTO, GSB
        if (op.equals(OPS.GTO)) {
            if (progLine.ops[LINE_OPS.I.INDEX()] != null) {   //  GTO I
                int n = (int) getRegContentsByIndex(BASE_REGS.RI.INDEX());   //  Valeur de I
                if (n >= 0) {   //  GTO I positif => GTO label   (n=labelIndex)
                    if (n <= LABELS.values().length - 1) {
                        LABELS lbl = labelIndexToLabelMap.get(n);
                        if (lbl != null) {
                            pln = labelToprogLineNumberMap.get(lbl);
                        }
                    }
                } else {   //  n<0 cad GTO I négatif => GTO ProgLineNumber  (-n=ProgLine number)
                    if ((-n) <= (proglines.size() - 1)) {
                        pln = -n;
                    }
                }
            } else {   //  Pas GTO I => GTO [.]0..9 ou A-E
                LABELS lbl = symbolToLabelMap.get(progLine.symbol);
                if (lbl != null) {
                    pln = labelToprogLineNumberMap.get(lbl);
                }
            }
        }
        if (op.equals(OPS.GSB)) {
            if (progLine.ops[LINE_OPS.I.INDEX()] != null) {   //  GSB I
                int n = (int) getRegContentsByIndex(BASE_REGS.RI.INDEX());   //  Valeur de I
                if (n >= 0) {   //  GSB I positif => GSB label   (n=labelIndex)
                    if (n <= LABELS.values().length - 1) {
                        LABELS lbl = labelIndexToLabelMap.get(n);
                        if (lbl != null) {
                            pln = labelToprogLineNumberMap.get(lbl);
                        }
                    }
                } else {   //  n<0 cad GSB I négatif, non prévu par la HP-15C
                    //  NOP
                }
            } else {   //  Pas GSB I => GSB [.]0..9 ou A-E
                LABELS lbl = symbolToLabelMap.get(progLine.symbol);
                if (lbl != null) {
                    pln = labelToprogLineNumberMap.get(lbl);
                }
            }
        }
        if (pln != null) {
            res = pln;
        }
        return res;
    }

    public boolean pushStkRetProgLineNumber(int progLineNumber) {   //  PUSH
        boolean res = false;
        if (stkRet.size() < MAX_RETS) {
            stkRet.add(0, progLineNumber);
            res = true;
        }
        return res;
    }

    public int popStkRetProgLineNumber() {   //  POP1
        return stkRet.get(0);
    }

    public boolean isStkRetEmpty() {
        boolean res = false;
        if (stkRet.get(0) == END_RETURN_STACK) {
            res = true;
        }
        return res;
    }

    public void removeLastStkRetProgLineNumber() {   //  POP2
        stkRet.remove(0);
    }

    public void clearReturnStack() {
        stkRet.clear();
        stkRet.add(0, END_RETURN_STACK);
    }

    private void setupReturnStack() {
        stkRet = new ArrayList<Integer>();
        stkRet.add(0, END_RETURN_STACK);
    }

    private void setupMaps() {
        opToKeyMap = new HashMap<OPS, KEYS>();
        opToShiftKeyCodeMap = new HashMap<OPS, Integer>();
        for (KEYS key : KEYS.values()) {
            OPS op = key.UNSHIFTED_OP();
            if (!op.equals(OPS.UNKNOWN)) {
                opToKeyMap.put(op, key);
                opToShiftKeyCodeMap.put(op, UNSHIFTED_KEY_CODE);
            }
            op = key.SHIFT_F_OP();
            if (!op.equals(OPS.UNKNOWN)) {
                opToKeyMap.put(op, key);
                opToShiftKeyCodeMap.put(op, SHIFT_F_KEY_CODE);
            }
            op = key.SHIFT_G_OP();
            if (!op.equals(OPS.UNKNOWN)) {
                opToKeyMap.put(op, key);
                opToShiftKeyCodeMap.put(op, SHIFT_G_KEY_CODE);
            }
        }
        // Cas particuliers: SINH,COSH,TANH,ASINH,ACOSH,ATANH et les 10 tests ("x<0?", ... (TEST n)) sont codées en clair en op0 (pex "ACOSH", "x<0?") et en normal (p.ex. HYP-1 COS, TEST 2) dans les op suivants
        // Suite: Ce qui implique que si Affichage symboles: Afficher uniquement op0, Si Affichage Codes: Afficher à partir de op1
        ghostOpsToOpMap = new HashMap<PairOp, OPS>();
        opToGhostKeyMap = new HashMap<OPS, GHOST_KEYS>();
        for (GHOST_KEYS ghostKey : GHOST_KEYS.values()) {
            OPS op = ghostKey.OP();
            OPS prefixOp = ghostKey.GHOST_OP1();
            OPS lastOp = ghostKey.GHOST_OP2();
            opToGhostKeyMap.put(op, ghostKey);
            PairOp pairOp = new PairOp(prefixOp, lastOp);
            ghostOpsToOpMap.put(pairOp, op);
            pairOp = null;
            KEYS key = opToKeyMap.get(prefixOp);
            opToKeyMap.put(op, key);
            if (key.UNSHIFTED_OP().equals(op)) {
                opToShiftKeyCodeMap.put(op, UNSHIFTED_KEY_CODE);
            }
            if (key.SHIFT_F_OP().equals(op)) {
                opToShiftKeyCodeMap.put(op, SHIFT_F_KEY_CODE);
            }
            if (key.SHIFT_G_OP().equals(op)) {
                opToShiftKeyCodeMap.put(op, SHIFT_G_KEY_CODE);
            }
        }
        symbolToLabelMap = new HashMap<String, LABELS>();
        labelIndexToLabelMap = new HashMap<Integer, LABELS>();
        for (LABELS lbl : LABELS.values()) {
            labelIndexToLabelMap.put(lbl.INDEX(), lbl);
            symbolToLabelMap.put(lbl.SYMBOL(), lbl);
        }
        symbolToBaseRegMap = new HashMap<String, BASE_REGS>();
        for (BASE_REGS baseReg : BASE_REGS.values()) {   //  Uniquement pour les registres de base (I, R0 à R9, R.0 à R.9)
            symbolToBaseRegMap.put(baseReg.SYMBOL(), baseReg);
        }
    }
}
