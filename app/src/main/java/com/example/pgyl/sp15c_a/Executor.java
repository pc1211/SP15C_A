package com.example.pgyl.sp15c_a;

import com.example.pgyl.sp15c_a.ProgLine.LINE_OPS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static com.example.pgyl.pekislib_a.StringDB.TABLE_DATA_INDEX;
import static com.example.pgyl.pekislib_a.StringDB.TABLE_ID_INDEX;

public class Executor {

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
        SOLVE("SOLVE"),
        INTEG("INTEG"),
        DSE("DSE"),
        ISG("ISG"),
        USER("USER"),
        MEM("MEM"),
        ON("ON"),
        PSE("PSE"),
        MATRIX("MATRIX"),
        RESULT("RESULT"),
        REIM("Re<>Im"),
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
        LASTX("LASTx"),
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
        EXP10("10^x"),
        LOG("LOG"),
        POWER("y^x"),
        PC("%"),
        INV("1/x"),
        DPC("DELTA%"),
        ABS("ABS"),
        RND("RND"),
        RAND("RAND"),
        RECT("->R"),
        POL("->P"),
        HMS("->H.MS"),
        H("->H"),
        COMB("Cy,x"),
        PERM("Py,x"),
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
        YER("y^,r"),
        LR("LR"),
        CLEAR_SIGMA("CLSIGMA"),
        CLEAR_PRGM("CLPRGM"),
        CLEAR_REGS("CLREG"),
        CLEAR_PREFIX("PREFIX"),
        XCHG("x<>"),
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
        KEY_10(10, OPS.DIV, OPS.SOLVE, OPS.XLEY),
        KEY_21(21, OPS.SST, OPS.LBL, OPS.BST),
        KEY_22(22, OPS.GTO, OPS.HYP, OPS.AHYP),
        KEY_23(23, OPS.SIN, OPS.DIM, OPS.ASIN),
        KEY_24(24, OPS.COS, OPS.INDI, OPS.ACOS),
        KEY_25(25, OPS.TAN, OPS.I, OPS.ATAN),
        KEY_26(26, OPS.EEX, OPS.RESULT, OPS.PI),   //  RESULT inactif
        KEY_27(27, OPS.DIGIT_4, OPS.XCHG, OPS.SF),
        KEY_28(28, OPS.DIGIT_5, OPS.DSE, OPS.CF),
        KEY_29(29, OPS.DIGIT_6, OPS.ISG, OPS.TF),
        KEY_20(20, OPS.MULT, OPS.INTEG, OPS.XE0),
        KEY_31(31, OPS.RS, OPS.PSE, OPS.PR),
        KEY_32(32, OPS.GSB, OPS.CLEAR_SIGMA, OPS.RTN),
        KEY_33(33, OPS.RDN, OPS.CLEAR_PRGM, OPS.RUP),
        KEY_34(34, OPS.XCHGXY, OPS.CLEAR_REGS, OPS.RND),
        KEY_35(35, OPS.BACK, OPS.CLEAR_PREFIX, OPS.CLX),
        KEY_36(36, OPS.ENTER, OPS.RAND, OPS.LASTX),
        KEY_37(37, OPS.DIGIT_1, OPS.RECT, OPS.POL),
        KEY_38(38, OPS.DIGIT_2, OPS.HMS, OPS.H),
        KEY_39(39, OPS.DIGIT_3, OPS.TO_RAD, OPS.TO_DEG),
        KEY_30(30, OPS.MINUS, OPS.REIM, OPS.TEST),
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

    public enum MODES {
        NORM, EDIT, RUN;

        public int INDEX() {
            return ordinal();
        }
    }

    public enum STACK_REGS {
        X, Y, Z, T, LX;

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

    public enum LABELS {
        L0("0"), L1("1"), L2("2"), L3("3"), L4("4"), L5("5"), L6("6"), L7("7"), L8("8"), L9("9"),
        LDOT0(".0"), LDOT1(".1"), LDOT2(".2"), LDOT3(".3"), LDOT4(".4"), LDOT5(".5"), LDOT6(".6"), LDOT7(".7"), LDOT8(".8"), LDOT9(".9"),
        LA("A"), LB("B"), LC("C"), LD("D"), LE("E");   //    "I" non repris car "LBL I" n'existe pas

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
        }
    }

    public final int MAX_FLAGS = 10;
    public final int DEF_MAX_REGS = 100;    //  Par défaut, inclus les 21 registres de base de BASE_REGS (I, R0 à R9, R.0 à R.9)

    private final int SOLVE_RETURN_CODE = 100000;   //  > 10000 pour ne pas le confondre avec un N° de ligne ordinaire (0000-9999)
    private final int INTEG_RETURN_CODE = 200000;

    public final int END_RETURN_CODE = 300000;   //  Pour assurer le retour après un GSB lancé en mode NORM
    public final String ERROR_GTO_GSB = "Invalid GTO/GSB";

    private final int MAX_DIGITS = 10;
    private final int MAX_PROG_LINES = 9999;
    private final int RET_STACK_SIZE_MAX = 100;
    private final int MAX_REGS = 1000;   //  Max, inclus les 21 registres de base de BASE_REGS (I, R0 à R9, R.0 à R.9)
    private final String ERROR_INF = "Inf Math error";
    private final String ERROR_NAN = "Nan Math error";
    private final String ERROR_STAT = "Stat error";
    private final String ERROR_PERM_COMB = "Invalid Perm/Comb";
    private final String ERROR_INDEX = "Invalid index";
    private final String ERROR_NESTED_SOLVE = "Nested SOLVE";
    private final String ERROR_NESTED_INTEG = "Nested INTEG";
    private final String ERROR_SOLVE_ITER_MAX = "Max iter SOLVE";
    private final String ERROR_INTEG_ITER_MAX = "Max iter INTEG";
    private final String ERROR_RET_STACK_FULL = "Ret stack full";
    private final int END_RETURN_STACK = -1;
    private final int UNSHIFTED_KEY_CODE = 0;
    private final int SHIFT_F_KEY_CODE = 42;
    private final int SHIFT_G_KEY_CODE = 43;
    private final int COMPLEX_FLAG_INDEX = 8;

    private SolveParamSet solveParamSet;
    private IntegParamSet integParamSet;
    private Complex complex;
    private String alpha;
    private double[] stackRegs;
    private double[] imStackRegs;
    private boolean[] flags;
    private ArrayList<Double> regs;   //  Les registres de BASE_REGS puis les suivants (accessibles par (i) )
    private OPS roundMode;
    private int roundParam;
    private OPS angleMode;
    private boolean isComplexMode;
    private boolean isStackLiftEnabled;
    private HashMap<Integer, KEYS> keyCodeToKeyMap;
    private HashMap<String, BASE_REGS> symbolToBaseRegMap;
    private HashMap<OPS, KEYS> opToKeyMap;
    private HashMap<OPS, Integer> opToShiftKeyCodeMap;
    private HashMap<OPS, GHOST_KEYS> opToGhostKeyMap;
    private HashMap<PairOp, OPS> ghostOpsToOpMap;
    private HashMap<String, LABELS> symbolToLabelMap;
    private HashMap<Integer, LABELS> labelIndexToLabelMap;
    private HashMap<LABELS, Integer> labelToprogLineNumberMap;
    private ArrayList<ProgLine> progLines;
    private ArrayList<Integer> retStack;
    private String error;
    private boolean isAutoLine;
    private boolean requestStopAfterSolve;
    private boolean requestStopAfterInteg;
    private boolean inSST;
    private boolean inPSE;
    private boolean isWrapAround;
    private int nextProgLineNumber;
    private int currentProgLineNumber;
    private ProgLine tempProgLine;
    private MODES mode;

    public Executor() {
        init();
    }

    private void init() {
        setupMaps();
        mode = MODES.NORM;
        angleMode = OPS.RAD;
        roundMode = OPS.FIX;
        roundParam = 4;
        complex = new Complex();
        isComplexMode = false;
        alpha = "";
        error = "";
        tempProgLine = new ProgLine();
        currentProgLineNumber = 0;
        nextProgLineNumber = 0;
        isAutoLine = false;
        inSST = false;
        inPSE = false;
        requestStopAfterSolve = false;
        requestStopAfterInteg = false;
        isWrapAround = false;
        isStackLiftEnabled = false;
        solveParamSet = new SolveParamSet();
        integParamSet = new IntegParamSet();
        setupMaps();
    }

    public void close() {
        solveParamSet.close();
        solveParamSet = null;
        integParamSet.close();
        integParamSet = null;
        stackRegs = null;
        imStackRegs = null;
        complex.close();
        complex = null;
        flags = null;
        retStack.clear();
        retStack = null;
        regs.clear();
        regs = null;
        tempProgLine = null;
        progLines.clear();
        progLines = null;
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
        keyCodeToKeyMap.clear();
        keyCodeToKeyMap = null;
        if (labelToprogLineNumberMap != null) {
            labelToprogLineNumberMap.clear();
            labelToprogLineNumberMap = null;
        }
    }

    //  ************************************************************************* PARAMS *************************************************************************


    public void setMode(MODES mode) {
        this.mode = mode;
    }

    public MODES getMode() {
        return mode;
    }

    public OPS getAngleMode() {
        return angleMode;
    }

    public void setRoundMode(OPS roundMode) {
        this.roundMode = roundMode;
    }

    public OPS getRoundMode() {
        return roundMode;
    }

    public int getRoundParam() {
        return roundParam;
    }

    public void setIsComplexMode(boolean isComplexMode) {
        this.isComplexMode = isComplexMode;
        flags[COMPLEX_FLAG_INDEX] = isComplexMode;
    }

    public boolean getIsComplexMode() {
        return isComplexMode;
    }

    public void setInSST(boolean inSST) {
        this.inSST = inSST;
    }

    public boolean getInSST() {
        return inSST;
    }

    public void setInPSE(boolean inPSE) {
        this.inPSE = inPSE;
    }

    public boolean getInPSE() {
        return inPSE;
    }

    public void setIsAutoLine(boolean isAutoLine) {
        this.isAutoLine = isAutoLine;
    }

    public boolean getIsAutoLine() {
        return isAutoLine;
    }

    public void setIsWrapAround(boolean isWrapAround) {
        this.isWrapAround = isWrapAround;
    }

    public String getAlpha() {
        return alpha;
    }

    public void setFlags(boolean[] flags) {
        this.flags = flags;
    }

    public boolean[] getFlags() {
        return flags;
    }

    public void setRequestStopAfterSolve(boolean requestStopAfterSolve) {
        this.requestStopAfterSolve = requestStopAfterSolve;
    }

    public boolean getRequestStopAfterSolve() {
        return (requestStopAfterSolve);
    }

    public void setRequestStopAfterInteg(boolean requestStopAfterInteg) {
        this.requestStopAfterInteg = requestStopAfterInteg;
    }

    public boolean getRequestStopAfterInteg() {
        return (requestStopAfterInteg);
    }

    public IntegParamSet getIntegParamSet() {
        return integParamSet;
    }

    public void clearSolveParamSet() {
        solveParamSet.clear();
    }

    public SolveParamSet getSolveParamSet() {
        return solveParamSet;
    }

    public void clearIntegParamSet() {
        integParamSet.clear();
    }

    public ArrayList<ProgLine> getProgLines() {
        return progLines;
    }

    public int getProgLinesSize() {
        return progLines.size();
    }

    public ProgLine getCurrentProgLine() {
        return getProgLines().get(currentProgLineNumber);
    }

    public void setCurrentProgLineNumber(int currentProgLineNumber) {
        this.currentProgLineNumber = currentProgLineNumber;
    }

    public int getCurrentProgLineNumber() {
        return currentProgLineNumber;
    }

    public void setNextProgLineNumber(int nextProgLineNumber) {
        this.nextProgLineNumber = nextProgLineNumber;
    }

    public int getNextProgLineNumber() {
        return nextProgLineNumber;
    }

    public int getRegIndexByDataRegIndex(int index) {
        return BASE_REGS.R0.INDEX() + index;   //  les registres de données classiques (data) commencent à partir de R0
    }

    public int getDataRegIndexByIndex(int index) {
        return index - BASE_REGS.R0.INDEX();
    }

    public int getRegsMaxIndex() {
        return regs.size() - 1;
    }

    public void setRoundParam(int roundParam) {
        this.roundParam = Math.min(MAX_DIGITS - 1, roundParam);
    }

    public void setAngleMode(OPS angleMode) {
        this.angleMode = angleMode;
    }

    public void setStackLiftEnabled(boolean enabled) {
        isStackLiftEnabled = enabled;
    }

    public boolean getIsStackLiftEnabled() {
        return isStackLiftEnabled;
    }

    public int getRegIndexBySymbol(String symbol) {   //  Pour les premiers registres de regs, cad ceux de BASE_REGS (I, R0 à R9, R.0 à R.9)
        return symbolToBaseRegMap.get(symbol).INDEX();
    }

    public void setRegs(ArrayList<Double> regs) {
        this.regs = regs;
    }

    public ArrayList<Double> getRegs() {
        return regs;
    }

    public void setStackRegs(double[] stackRegs) {
        this.stackRegs = stackRegs;
    }

    public void setImStackRegs(double[] imStackRegs) {
        this.imStackRegs = imStackRegs;
    }

    public double[] getStackRegs() {
        return stackRegs;
    }

    public double[] getImStackRegs() {
        return imStackRegs;
    }

    public ArrayList<Integer> getRetStack() {
        return retStack;
    }

    public void setRetStack(ArrayList<Integer> argRetStack) {
        this.retStack = argRetStack;
        if (retStack != null) {
            int n = retStack.size();
            if (n > 0) {  //  Vérifier le 1er élémebt de la pile
                if (retStack.get(n - 1) != END_RETURN_STACK) {   //  Absent
                    retStack.add(n, END_RETURN_STACK);   //  Ajouter le 1er élément, tout au-dessus de la pile
                }
            } else {   //  Pile vide, définier le 1er élément
                retStack.add(0, END_RETURN_STACK);
            }
        } else {   //  Pas de pile => La définir
            retStack = new ArrayList<Integer>();
            retStack.add(0, END_RETURN_STACK);
        }
    }

    public OPS getOpByGhostKeyOps(OPS prefixOp, OPS lastOp) {
        PairOp pairOp = new PairOp(prefixOp, lastOp);
        OPS op = ghostOpsToOpMap.get(pairOp);
        pairOp = null;
        return op;
    }

    public KEYS getKeyByKeyCode(int keyCode) {
        return keyCodeToKeyMap.get(keyCode);
    }

    public KEYS getKeyByOp(OPS op) {
        return opToKeyMap.get(op);
    }

    //  ************************************************************************* DIVERS *************************************************************************

    private String solveConfigForEvalUserFx(double x) {
        fillStack(x);   //  C'est ainsi que procède la HP-15C
        if (!pushProgLineNumber(SOLVE_RETURN_CODE)) {   //  Push Code de retour spécial après évaluation de UserFx => Retour à SOLVE  cf (RTN);    Si False => MAX_RETS dépassé
            error = ERROR_RET_STACK_FULL;
        } else {  //  OK Push
            nextProgLineNumber = solveParamSet.userFxLineNumber;   //  Emplacement de UserFx à exécuter
        }
        return error;
    }

    private String integConfigForEvalUserFx(double x) {
        fillStack(x);   //  C'est ainsi que procède la HP-15C
        if (!pushProgLineNumber(INTEG_RETURN_CODE)) {   //  Push Code de retour spécial après évaluation de UserFx => Retour à INTEG  cf (RTN);    Si False => MAX_RETS dépassé
            error = ERROR_RET_STACK_FULL;
        } else {  //  OK Push
            nextProgLineNumber = integParamSet.userFxLineNumber;   //  Emplacement de UserFx à exécuter
        }
        return error;
    }

    public void incNextProgLineNumber() {
        nextProgLineNumber = nextProgLineNumber + 1;
        if (nextProgLineNumber == progLines.size()) {   //  N'existe pas => Premier
            nextProgLineNumber = 0;
        }
    }

    public int inc(int progLineNumber) {
        int res = progLineNumber + 1;
        if (res == progLines.size()) {   //  N'existe pas => Premier
            res = 0;
        }
        return res;
    }

    public int dec(int progLineNumber) {
        int res = progLineNumber - 1;
        if (res < 0) {
            res = progLines.size() - 1;   //  BEGIN => Dernier
        }
        return res;
    }

    public void fillStack(double value) {
        stackRegs[STACK_REGS.X.INDEX()] = value;
        stackRegs[STACK_REGS.Y.INDEX()] = value;
        stackRegs[STACK_REGS.Z.INDEX()] = value;
        stackRegs[STACK_REGS.T.INDEX()] = value;
    }

    public void fillImStack(double value) {
        imStackRegs[STACK_REGS.X.INDEX()] = value;
        imStackRegs[STACK_REGS.Y.INDEX()] = value;
        imStackRegs[STACK_REGS.Z.INDEX()] = value;
        imStackRegs[STACK_REGS.T.INDEX()] = value;
    }

    public void stackRollDown() {   //  T,Z,Y,X -> X,T,Z,Y
        Double temp = stackRegs[STACK_REGS.X.INDEX()];
        stackRegs[STACK_REGS.X.INDEX()] = stackRegs[STACK_REGS.Y.INDEX()];
        stackRegs[STACK_REGS.Y.INDEX()] = stackRegs[STACK_REGS.Z.INDEX()];
        stackRegs[STACK_REGS.Z.INDEX()] = stackRegs[STACK_REGS.T.INDEX()];
        stackRegs[STACK_REGS.T.INDEX()] = temp;
        if (isComplexMode) {
            temp = imStackRegs[STACK_REGS.X.INDEX()];
            imStackRegs[STACK_REGS.X.INDEX()] = imStackRegs[STACK_REGS.Y.INDEX()];
            imStackRegs[STACK_REGS.Y.INDEX()] = imStackRegs[STACK_REGS.Z.INDEX()];
            imStackRegs[STACK_REGS.Z.INDEX()] = imStackRegs[STACK_REGS.T.INDEX()];
            imStackRegs[STACK_REGS.T.INDEX()] = temp;
        }
    }

    public void stackRollUp() {   //  T,Z,Y,X -> Z,Y,X,T
        Double temp = stackRegs[STACK_REGS.T.INDEX()];
        stackRegs[STACK_REGS.T.INDEX()] = stackRegs[STACK_REGS.Z.INDEX()];
        stackRegs[STACK_REGS.Z.INDEX()] = stackRegs[STACK_REGS.Y.INDEX()];
        stackRegs[STACK_REGS.Y.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
        stackRegs[STACK_REGS.X.INDEX()] = temp;
        if (isComplexMode) {
            temp = imStackRegs[STACK_REGS.T.INDEX()];
            imStackRegs[STACK_REGS.T.INDEX()] = imStackRegs[STACK_REGS.Z.INDEX()];
            imStackRegs[STACK_REGS.Z.INDEX()] = imStackRegs[STACK_REGS.Y.INDEX()];
            imStackRegs[STACK_REGS.Y.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
            imStackRegs[STACK_REGS.X.INDEX()] = temp;
        }
    }

    public void doStackLift() {   //  T,Z,Y,X -> Z,Y,X,X
        stackRegs[STACK_REGS.T.INDEX()] = stackRegs[STACK_REGS.Z.INDEX()];
        stackRegs[STACK_REGS.Z.INDEX()] = stackRegs[STACK_REGS.Y.INDEX()];
        stackRegs[STACK_REGS.Y.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
        if (isComplexMode) {
            imStackRegs[STACK_REGS.T.INDEX()] = imStackRegs[STACK_REGS.Z.INDEX()];
            imStackRegs[STACK_REGS.Z.INDEX()] = imStackRegs[STACK_REGS.Y.INDEX()];
            imStackRegs[STACK_REGS.Y.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
        }
    }

    public void stackMergeDown() {   //  T,Z,Y,X -> T,T,Z,X
        stackRegs[STACK_REGS.Y.INDEX()] = stackRegs[STACK_REGS.Z.INDEX()];
        stackRegs[STACK_REGS.Z.INDEX()] = stackRegs[STACK_REGS.T.INDEX()];
        if (isComplexMode) {
            imStackRegs[STACK_REGS.Y.INDEX()] = imStackRegs[STACK_REGS.Z.INDEX()];
            imStackRegs[STACK_REGS.Z.INDEX()] = imStackRegs[STACK_REGS.T.INDEX()];
        }
    }

    public String getRoundXForDisplay() {
        return getRoundForDisplay(stackRegs[STACK_REGS.X.INDEX()]);
    }

    public String getRoundXImForDisplay() {
        return getRoundForDisplay(imStackRegs[STACK_REGS.X.INDEX()]);
    }

    public String getRoundForDisplay(double value) {
        double val = Math.abs(value);
        String res = "";
        int exp = 1;
        double mant = 0;
        if (val != 0) {
            exp = (int) Math.floor(1d + Math.log10(val));
            mant = val / Math.pow(10, exp - 1.0) / 10.0;   //  Entre 0 et 1;    exp-1 pour éviter overflow
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
                res = Math.toRadians(res) * 0.9;
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
                res = Math.toDegrees(res) / 0.9;
                break;
        }
        return res;
    }

    private double fact(int m) {
        double res = 1;
        if (m != 0) {
            for (int i = 1; i <= m; i = i + 1) {
                res = res * (double) i;
            }
        }
        return res;
    }

    private double factOver(int m, int n) {
        double res = 1;
        for (int i = m; i > n; i = i - 1)
            res = res * (double) i;
        return res;
    }

    public void doStackLiftIfEnabled() {
        if (isStackLiftEnabled) {
            doStackLift();
            imStackRegs[STACK_REGS.X.INDEX()] = 0.0;
        }
    }

    private double round(double value, int n) {
        int scale = (int) Math.pow(10, n);
        double res = Math.round(Math.abs(value) * (double) scale) / (double) scale;
        if (value < 0) {
            res = -res;
        }
        return res;
    }

    public String setMaxDataRegIndex(int newMaxDataRegIndex) {   //  les registres de données classiques (data) commencent à partir de R0
        String res = "";
        final String RANGE_ERROR = "Out of " + (BASE_REGS.values().length - BASE_REGS.R0.INDEX()) + "-" + (MAX_REGS - 1 - BASE_REGS.R0.INDEX());

        int oldMaxDataRegIndex = regs.size() - 1 - BASE_REGS.R0.INDEX();
        int n = newMaxDataRegIndex - oldMaxDataRegIndex;
        if (n > 0) {   //  Ajouter n registres
            if (newMaxDataRegIndex <= (MAX_REGS - 1 - BASE_REGS.R0.INDEX())) {   //  Respecte Max
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    regs.add(0.0);
                }
            } else {  //  > Max
                res = RANGE_ERROR;
            }
        } else {   //  Retirer n registres, en commençant par les derniers de la liste
            if (newMaxDataRegIndex >= (BASE_REGS.values().length - BASE_REGS.R0.INDEX())) {   //   Respecte Min
                regs.subList(newMaxDataRegIndex + BASE_REGS.R0.INDEX(), oldMaxDataRegIndex + BASE_REGS.R0.INDEX()).clear();
            } else {   //  < Min
                res = RANGE_ERROR;
            }
        }
        return res;
    }

    public void setupProgLines() {
        progLines = new ArrayList<ProgLine>();   //  progLines va progressivement se remplir progressivement avec les encodeKeyCode plus bas
        ProgLine progLine = new ProgLine();
        progLine.ops[LINE_OPS.BASE.INDEX()] = OPS.BEGIN;   //  Ajouter le 1er élément, tout en bas de la pile
        progLines.add(0, progLine);
    }

    public String[][] progLinesToRows() {
        String[][] res = null;
        if (progLines != null) {
            int n = progLines.size();
            if (n > 0) {
                res = new String[n][4];   //  4: champ ID + champ VALUE1,2,3
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    String pl = progLineToString(i, true);   //  une string avec max 3 opcodes; p.ex. "0001: 45 23 24"
                    String[] plc = pl.split("\\s+");   //   "0001:"  "45"  "23"  "24"   (les espaces simples ou multiples sont éliminés)
                    res[i][TABLE_ID_INDEX] = String.valueOf(i);   //  "1"
                    res[i][TABLE_DATA_INDEX] = (plc.length >= 2 ? plc[1] : null);   //  "45"
                    res[i][TABLE_DATA_INDEX + 1] = (plc.length >= 3 ? plc[2] : null);   //  "23"
                    res[i][TABLE_DATA_INDEX + 2] = (plc.length >= 4 ? plc[3] : null);   //  "24"
                }
            }
        }
        return res;
    }

    public boolean alphaToX() {
        boolean res = true;
        if (alpha.length() != 0) {
            int indLast = alpha.length() - 1;
            if (alpha.substring(indLast).equals("E")) {   //  Enlever un "E" éventuel en dernière position
                alpha = alpha.substring(0, indLast);
            }
            try {
                double d = Double.parseDouble(alpha);
                if ((Double.isNaN(d)) || (Double.isInfinite(d))) {
                    throw new IllegalArgumentException();
                }
                stackRegs[STACK_REGS.X.INDEX()] = d;
                setStackLiftEnabled(true);
                alpha = "";
            } catch (IllegalArgumentException | SecurityException ex) {
                res = false;   //  Echec
            }
        }
        return res;
    }

    public void rebuildlabelToProgLineNumberMap() {
        labelToprogLineNumberMap = new HashMap<Executor.LABELS, Integer>();
        int n = progLines.size();
        for (int i = 0; i <= (n - 1); i = i + 1) {
            ProgLine progLine = progLines.get(i);
            OPS op = progLine.ops[LINE_OPS.BASE.INDEX()];
            if (op.equals(OPS.LBL)) {
                labelToprogLineNumberMap.put(symbolToLabelMap.get(progLine.symbol), i);
            }
        }
    }

    public void linkDestProgLineNumbers() {
        int n = progLines.size();
        for (int i = 0; i <= (n - 1); i = i + 1) {
            ProgLine progLine = progLines.get(i);
            OPS op = progLine.ops[LINE_OPS.BASE.INDEX()];
            if ((op.equals(OPS.GTO)) || (op.equals(OPS.GSB)) || (op.equals(OPS.SOLVE)) || (op.equals(OPS.INTEG))) {
                if (!progLine.symbol.equals(OPS.I.SYMBOL())) {   //   GTO I ou GSB I existent, mais la destination est le label (ou N° de ligne) contenu dans I !
                    LABELS lbl = symbolToLabelMap.get(progLine.symbol);
                    int pln = labelToprogLineNumberMap.get(lbl);
                    progLine.paramAddress = pln;
                }
            }
        }
    }

    public int getGTODestProgLineNumber(ProgLine progLine) {
        int res = -1;
        Integer pln = null;
        if (progLine.ops[LINE_OPS.I.INDEX()] != null) {   //  GTO I
            int n = regs.get(BASE_REGS.RI.INDEX()).intValue();   //  Valeur de I
            if (n >= 0) {   //  GTO I positif => GTO label   (n=labelIndex)
                if (n <= LABELS.values().length - 1) {
                    LABELS lbl = labelIndexToLabelMap.get(n);
                    if (lbl != null) {
                        pln = labelToprogLineNumberMap.get(lbl);
                    }
                }
            } else {   //  n<0 cad GTO I négatif => GTO ProgLineNumber  (-n=ProgLine number)
                if ((-n) <= (progLines.size() - 1)) {
                    pln = -n;
                }
            }
        } else {   //  Pas GTO I => GTO [.]0..9 ou A-E
            LABELS lbl = symbolToLabelMap.get(progLine.symbol);
            if (lbl != null) {
                pln = labelToprogLineNumberMap.get(lbl);
            }
        }
        if (pln != null) {
            res = pln;
        }
        return res;
    }

    public boolean isRetStackEmpty() {
        boolean res = false;
        if (retStack.get(0) == END_RETURN_STACK) {
            res = true;
        }
        return res;
    }

    public void clearRetStack() {
        retStack.clear();
        retStack.add(0, END_RETURN_STACK);
    }

    public void testErrVal(Double value) {
        if (Double.isNaN(value)) {
            error = ERROR_NAN;
            stackRegs[STACK_REGS.X.INDEX()] = 0;
        }
        if (Double.isInfinite(value)) {
            error = ERROR_INF;
            stackRegs[STACK_REGS.X.INDEX()] = 0;
        }
    }

    public boolean pushProgLineNumber(int progLineNumber) {   //  PUSH
        boolean res = false;
        if (retStack.size() < RET_STACK_SIZE_MAX) {
            retStack.add(0, progLineNumber);
            res = true;
        }
        return res;
    }

    public int popProgLineNumber() {
        int res = retStack.get(0);
        retStack.remove(0);
        return res;
    }

    public ProgLine getProgLine(int progLineNumber) {
        return progLines.get(progLineNumber);
    }

    public boolean addProgLineAtNumber(ProgLine progLine, int progLineNumber) {
        boolean res = false;
        if (progLines.size() < MAX_PROG_LINES) {   //  Il y a encore assez de lignes dispponibles
            ProgLine newProgLine = new ProgLine();
            int n = progLine.ops.length;
            for (int i = 0; i <= (n - 1); i = i + 1) {   //  Copier dans la nouvelle ligne
                newProgLine.ops[i] = progLine.ops[i];
            }
            newProgLine.paramAddress = progLine.paramAddress;
            newProgLine.symbol = progLine.symbol;
            progLines.add(progLineNumber, newProgLine);
            res = true;
        }
        return res;
    }

    public void removeProgLineAtNumber(int progLineNumber) {
        progLines.remove(progLineNumber);
    }

    public void clearProgLine(ProgLine progLine) {
        int n = progLine.ops.length;
        for (int i = 0; i <= (n - 1); i = i + 1) {
            progLine.ops[i] = null;
        }
        progLine.paramAddress = 0;
        progLine.symbol = "";
    }

    public void clearProgLines() {
        progLines.clear();
        ProgLine progLine = new ProgLine();
        progLine.ops[LINE_OPS.BASE.INDEX()] = OPS.BEGIN;
        progLines.add(progLine);    // A l'index 0, proglines contient au moins BEGIN
    }

    public String progLineToString(int progLineNumber, boolean displayKeyCodes) {    //  displayKeyCodes False => Afficher uniquement symboles ; displayKeyCodes True => afficher keyCodes (et parfois symbol (p.ex. ".5" ...)
        final String SEP = " ";
        String res = "";
        String s = "";
        if (progLineNumber != 0) {
            ProgLine progLine = progLines.get(progLineNumber);
            OPS opBase = progLine.ops[LINE_OPS.BASE.INDEX()];   //  LINE_OPS: BASE, A4OP, DOT, A09, AE, I, DIM, INDI, RAND, SIGMA_PLUS, CHS, GHOST1, GHOST2
            boolean isGhost = (opToGhostKeyMap.get(opBase) != null);
            int iMin = ((isGhost && displayKeyCodes) ? LINE_OPS.GHOST1.INDEX() : LINE_OPS.BASE.INDEX());
            int iMax = ((isGhost && displayKeyCodes) ? LINE_OPS.GHOST2.INDEX() : LINE_OPS.SIGMA_PLUS.INDEX());   //  On ne prend pas le CHS car n'a été utilisé que pour le GTO CHS nnnnn en mode EDIT
            int i = iMin;
            do {
                if (progLine.ops[i] != null) {
                    String sep = SEP;
                    if (displayKeyCodes) {   //  Codes
                        KEYS key = opToKeyMap.get(progLine.ops[i]);
                        s = String.valueOf(key.CODE());
                        OPS unshiftedOp = key.UNSHIFTED_OP();   //  Opération sans aucune touche Shift
                        if (((unshiftedOp.INDEX() >= OPS.DIGIT_0.INDEX()) && (unshiftedOp.INDEX() <= OPS.DIGIT_9.INDEX()))) {   //  Afficher chiffre (même si operation n'est pas chiffre)
                            s = unshiftedOp.SYMBOL();
                        }
                        if (i == LINE_OPS.DOT.INDEX()) {
                            if (progLine.ops[LINE_OPS.A09.INDEX()] != null) {    //  Si A09 => Tient déjà compte du point (cf prepareMultiOpsProgLine())
                                s = "";
                                sep = "";
                            }
                        }
                        if (i == LINE_OPS.A09.INDEX()) {
                            s = progLine.symbol;   //   8,9, ou .8, .9, ...
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
                        if (i == LINE_OPS.DOT.INDEX()) {
                            if (progLine.ops[LINE_OPS.A09.INDEX()] != null) {   //  Si A09 => Tient déjà compte du point (cf prepareMultiOpsProgLine())
                                s = "";
                                sep = "";
                            }
                        }
                        if (i == LINE_OPS.A09.INDEX()) {
                            s = progLine.symbol;   //   8,9, ou .8, .9, ...
                        }
                        if ((opBase.equals(OPS.STO)) || (opBase.equals(OPS.RCL))) {
                            if (i == LINE_OPS.A4OP.INDEX()) {
                                sep = "";   //  Pour avoir +-*/ juste à côté de l'op: STO+ ... RCL* ..., et non STO + ... RCL * ...
                            }
                        }
                        if (opBase.equals(OPS.XCHG)) {
                            if (i != LINE_OPS.BASE.INDEX()) {
                                sep = "";   //  Pour avoir X<>1,  X<>(i) ...
                            }
                        }
                    }
                    res = res + (!res.equals("") ? sep : "") + s;
                }
                i = i + 1;
            } while (i <= iMax);
        } else {   //  Ligne 0
            if (displayKeyCodes) {   //  Codes
                res = "";
            } else {   //  Symboles
                res = OPS.BEGIN.SYMBOL();
            }
        }
        res = String.format(Locale.US, "%04d", progLineNumber) + ": " + res;
        return res;
    }

    private double gamma(double x) {   //  https://rosettacode.org/wiki/Gamma_function#Java
        double[] p = {0.99999999999980993, 676.5203681218851, -1259.1392167224028,
                771.32342877765313, -176.61502916214059, 12.507343278686905,
                -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7};
        int g = 7;
        if (x < 0.5) return Math.PI / (Math.sin(Math.PI * x) * gamma(1.0 - x));
        x -= 1.0;
        double a = p[0];
        double t = x + g + 0.5;
        for (int i = 1; i < p.length; i++) {
            a += p[i] / (x + i);
        }
        return Math.sqrt(2 * Math.PI) * Math.pow(t, x + 0.5) * Math.exp(-t) * a;
    }

    private void setupMaps() {
        opToKeyMap = new HashMap<OPS, KEYS>();
        opToShiftKeyCodeMap = new HashMap<OPS, Integer>();
        keyCodeToKeyMap = new HashMap<Integer, KEYS>();
        for (KEYS key : KEYS.values()) {
            keyCodeToKeyMap.put(key.CODE(), key);
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

    private void errorStandardHandle() {
        if (error.length() == 0) {
            setStackLiftEnabled(true);
        } else {
            stackRegs[STACK_REGS.X.INDEX()] = 0.0;
            imStackRegs[STACK_REGS.X.INDEX()] = 0.0;
        }
    }

    //  ************************************************************************* EXEC *************************************************************************

    public String exec(ProgLine progLine) {
        OPS baseOp = progLine.ops[LINE_OPS.BASE.INDEX()];
        error = "";

        switch (baseOp) {   //  Le GIANT
            case FIX:
            case SCI:
            case ENG:
                if (alphaToX()) {
                    roundMode = progLine.ops[LINE_OPS.BASE.INDEX()];
                    int n = (progLine.ops[LINE_OPS.I.INDEX()] != null ? regs.get(BASE_REGS.RI.INDEX()).intValue() : Integer.valueOf(progLine.symbol));
                    setRoundParam(n);
                }
                break;
            case STO:
                if (alphaToX()) {
                    if (progLine.ops[LINE_OPS.RAND.INDEX()] == null) {   //  STO RAN# sans effet
                        int regIndex = progLine.paramAddress;
                        if (progLine.ops[LINE_OPS.INDI.INDEX()] != null) {   //  (i))
                            int dataRegIndex = regs.get(BASE_REGS.RI.INDEX()).intValue();   //  Valeur dans I
                            regIndex = getRegIndexByDataRegIndex(dataRegIndex);
                            if ((regIndex < 0) || (regIndex > getRegsMaxIndex())) {
                                error = ERROR_INDEX;
                            }
                        }
                        if (error.length() == 0) {
                            OPS a4Op = progLine.ops[LINE_OPS.A4OP.INDEX()];
                            if (a4Op != null) {   //  STO +-*/ reg
                                switch (a4Op) {
                                    case PLUS:
                                        regs.set(regIndex, regs.get(regIndex) + stackRegs[STACK_REGS.X.INDEX()]);
                                        break;
                                    case MINUS:
                                        regs.set(regIndex, regs.get(regIndex) - stackRegs[STACK_REGS.X.INDEX()]);
                                        break;
                                    case MULT:
                                        regs.set(regIndex, regs.get(regIndex) * stackRegs[STACK_REGS.X.INDEX()]);
                                        break;
                                    case DIV:
                                        regs.set(regIndex, regs.get(regIndex) / stackRegs[STACK_REGS.X.INDEX()]);
                                        break;
                                }
                                testErrVal(regs.get(regIndex));
                                if (error.length() != 0) {
                                    regs.set(regIndex, 0.0);   //  Plutôt qu'un NAN ou INF
                                }
                            } else {   //  STO reg
                                regs.set(regIndex, stackRegs[STACK_REGS.X.INDEX()]);
                            }
                        }
                    }
                    errorStandardHandle();
                }
                break;
            case RCL:
                if (alphaToX()) {
                    if (progLine.ops[LINE_OPS.A4OP.INDEX()] == null) {
                        doStackLiftIfEnabled();
                    }
                    if (progLine.ops[LINE_OPS.DIM.INDEX()] != null) {   //  RCL DIM (i)
                        if (progLine.ops[LINE_OPS.INDI.INDEX()] != null) {
                            stackRegs[STACK_REGS.X.INDEX()] = getDataRegIndexByIndex(getRegsMaxIndex());
                        }
                    } else {   //  Pas RCL DIM (i)
                        if (progLine.ops[LINE_OPS.SIGMA_PLUS.INDEX()] != null) {   //  RCL SIGMA_PLUS
                            doStackLift();    //  Un stacklift obligatoire + un deuxième (cf supra) si stackLift activé
                            stackRegs[STACK_REGS.X.INDEX()] = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_X.DATA_REG_INDEX()));
                            stackRegs[STACK_REGS.Y.INDEX()] = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_Y.DATA_REG_INDEX()));
                        } else {   //  Pas RCL SIGMA_PLUS
                            int regIndex = progLine.paramAddress;
                            if (progLine.ops[LINE_OPS.INDI.INDEX()] != null) {   //  (i))
                                int dataRegIndex = regs.get(BASE_REGS.RI.INDEX()).intValue();   //  Valeur dans I
                                regIndex = getRegIndexByDataRegIndex(dataRegIndex);
                                if ((regIndex < 0) || (regIndex > getRegsMaxIndex())) {
                                    error = ERROR_INDEX;
                                }
                            }
                            if (error.length() == 0) {
                                OPS a4Op = progLine.ops[LINE_OPS.A4OP.INDEX()];
                                if (a4Op != null) {   //  RCL +-*/ reg
                                    switch (a4Op) {
                                        case PLUS:
                                            stackRegs[STACK_REGS.X.INDEX()] = stackRegs[STACK_REGS.X.INDEX()] + regs.get(regIndex);
                                            break;
                                        case MINUS:
                                            stackRegs[STACK_REGS.X.INDEX()] = stackRegs[STACK_REGS.X.INDEX()] - regs.get(regIndex);
                                            break;
                                        case MULT:
                                            stackRegs[STACK_REGS.X.INDEX()] = stackRegs[STACK_REGS.X.INDEX()] * regs.get(regIndex);
                                            break;
                                        case DIV:
                                            stackRegs[STACK_REGS.X.INDEX()] = stackRegs[STACK_REGS.X.INDEX()] / regs.get(regIndex);
                                            break;
                                    }
                                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                                } else {   //  RCL reg
                                    stackRegs[STACK_REGS.X.INDEX()] = regs.get(regIndex);
                                }
                            }
                        }
                    }
                    errorStandardHandle();
                }
                break;
            case DIM:
                if (alphaToX()) {
                    int n = (int) stackRegs[STACK_REGS.X.INDEX()];
                    error = setMaxDataRegIndex(n);
                    errorStandardHandle();
                }
                break;
            case XCHG:
                if (alphaToX()) {
                    int regIndex = progLine.paramAddress;
                    if (progLine.ops[LINE_OPS.INDI.INDEX()] != null) {   //  (i))
                        int dataRegIndex = regs.get(BASE_REGS.RI.INDEX()).intValue();   //  Valeur dans I
                        regIndex = getRegIndexByDataRegIndex(dataRegIndex);
                        if ((regIndex < 0) || (regIndex > getRegsMaxIndex())) {
                            error = ERROR_INDEX;
                        }
                    }
                    if (error.length() == 0) {
                        double reg = regs.get(regIndex);
                        regs.set(regIndex, stackRegs[STACK_REGS.X.INDEX()]);
                        stackRegs[STACK_REGS.X.INDEX()] = reg;
                        if (error.length() == 0) {
                            setStackLiftEnabled(true);
                        } else {
                            stackRegs[STACK_REGS.X.INDEX()] = 0;
                        }
                    } else {
                        stackRegs[STACK_REGS.X.INDEX()] = 0;
                    }
                }
                break;
            case LBL:   //  Neutre sur StackLift ???
                if (alphaToX()) {
                    //  NOP
                }
                break;
            case GTO:   //  Neutre sur StackLift ???
                if (alphaToX()) {
                    if (progLine.ops[LINE_OPS.I.INDEX()] != null) {   //  GTO I => recalculer selon I
                        int dpln = getGTODestProgLineNumber(progLine);
                        if (dpln != (-1)) {   //  OK
                            nextProgLineNumber = dpln;
                        } else {   //  Invalide
                            error = ERROR_GTO_GSB;
                        }
                    } else {   //  Pas GTO I
                        nextProgLineNumber = (progLine.paramAddress);
                    }
                }
                break;
            case GSB:    //  Neutre sur StackLift ???
                if (alphaToX()) {
                    if (!pushProgLineNumber(nextProgLineNumber)) {   //  Si False => MAX_RETS dépassé
                        error = ERROR_RET_STACK_FULL;
                    } else {   //  OK Push
                        if (progLine.ops[LINE_OPS.I.INDEX()] != null) {   //  GSB I => recalculer selon I
                            int dpln = getGTODestProgLineNumber(progLine);
                            if (dpln != (-1)) {   //  OK
                                nextProgLineNumber = dpln;
                            } else {   //  Invalide
                                error = ERROR_GTO_GSB;
                            }
                        } else {   //  Pas GSB I
                            nextProgLineNumber = progLine.paramAddress;
                        }
                    }
                }
                break;
            case SOLVE:   //  SOLVE est rappelé après chaque évaluation de UserFx via un push sur StkRet d'un code de retour spécial (cf solveConfigForEvalUserFx() et RTN)
                if (alphaToX()) {
                    solveParamSet.count = solveParamSet.count + 1;
                    if (solveParamSet.count == 1) {  //  Initialisation et début de traitement
                        solveParamSet.oldNextProgLineNumber = nextProgLineNumber;
                        solveParamSet.userFxLineNumber = progLine.paramAddress;
                        solveParamSet.retLevel = retStack.size();
                        solveParamSet.tol = Math.pow(10, -MAX_DIGITS - 2);
                        solveParamSet.iterCount = 0;
                        solveParamSet.a = stackRegs[STACK_REGS.Y.INDEX()];   //  Guess 1
                        solveParamSet.b = stackRegs[STACK_REGS.X.INDEX()];   //  Guess 2
                        solveParamSet.separateAB();   //   Si a = b (à 1E-14 max près) => Séparer a et b avec une différence de 1E-6
                        error = solveConfigForEvalUserFx(solveParamSet.a);
                    }
                    if (solveParamSet.count == 2) {
                        if (retStack.size() != solveParamSet.retLevel) {
                            error = ERROR_NESTED_SOLVE;
                        } else {   //  Pas de Solve imbriqués, on continue
                            solveParamSet.r = stackRegs[STACK_REGS.X.INDEX()];   //  f(a)
                            error = solveConfigForEvalUserFx(solveParamSet.b);
                        }
                    }
                    if (solveParamSet.count == 3) {
                        solveParamSet.s = stackRegs[STACK_REGS.X.INDEX()];   //  f(b)
                        error = solveParamSet.transform();
                        if (error.length() == 0) {
                            solveParamSet.c = solveParamSet.t;
                            error = solveConfigForEvalUserFx(solveParamSet.c);
                        }
                    }
                    if (solveParamSet.count >= 4) {
                        solveParamSet.q = stackRegs[STACK_REGS.X.INDEX()];   //  f(c)
                        solveParamSet.setNextLevel();
                        error = solveParamSet.transform();   //  Nouvelle estimation dans t
                        if (error.length() == 0) {
                            double newX = solveParamSet.t;
                            if (Math.abs(newX - solveParamSet.c) <= solveParamSet.tol) {   //  OK c'est bon
                                stackRegs[STACK_REGS.X.INDEX()] = newX;
                                stackRegs[STACK_REGS.Y.INDEX()] = solveParamSet.c;
                                stackRegs[STACK_REGS.Z.INDEX()] = solveParamSet.q;
                                nextProgLineNumber = solveParamSet.oldNextProgLineNumber;
                                solveParamSet.clear();
                                if (requestStopAfterSolve) {   //  Forcer le STOP (comme en mode SST) si SOLVE a été lancé au départ de mode NORM
                                    requestStopAfterSolve = false;
                                    inSST = true;
                                }
                            } else {   //  Tolérance toujours pas respectée => On continue ?
                                solveParamSet.iterCount = solveParamSet.iterCount + 1;
                                if (solveParamSet.iterCount > solveParamSet.ITER_COUNT_MAX) {   //  Il n'y a plus d'espoir
                                    error = ERROR_SOLVE_ITER_MAX;
                                } else {  //  On continue !
                                    solveParamSet.c = newX;
                                    error = solveConfigForEvalUserFx(solveParamSet.c);
                                }
                            }
                        }
                    }
                }
                errorStandardHandle();
                break;
            case INTEG:
                if (alphaToX()) {
                    integParamSet.count = integParamSet.count + 1;
                    if (integParamSet.count == 1) {  //  Initialisation et début de traitement
                        integParamSet.oldNextProgLineNumber = nextProgLineNumber;
                        integParamSet.userFxLineNumber = progLine.paramAddress;
                        integParamSet.retLevel = retStack.size();
                        integParamSet.tol = Math.pow(10, -roundParam - 2);
                        integParamSet.iterCount = 0;
                        integParamSet.a = stackRegs[STACK_REGS.Y.INDEX()];   //  a
                        integParamSet.b = stackRegs[STACK_REGS.X.INDEX()];   //  b
                        integParamSet.h = integParamSet.b - integParamSet.a;
                        integParamSet.n = 1;
                        integParamSet.l = integParamSet.n;
                        integParamSet.u = 0;
                        integParamSet.z = 1e99;
                        error = integConfigForEvalUserFx(integParamSet.a);
                    }
                    if (integParamSet.count == 2) {
                        if (retStack.size() != integParamSet.retLevel) {
                            error = ERROR_NESTED_INTEG;
                        } else {   //  Pas de Integ imbriqués, on continue
                            integParamSet.p = stackRegs[STACK_REGS.X.INDEX()];   //  f(a)
                            error = integConfigForEvalUserFx(integParamSet.b);
                        }
                    }
                    if (integParamSet.count >= 3) {
                        if (integParamSet.n == 1) {
                            integParamSet.p = integParamSet.p + stackRegs[STACK_REGS.X.INDEX()];   //  f(a) + f(b)
                            integParamSet.setNextLevel();
                            integParamSet.x = integParamSet.a + integParamSet.h;    //  1er point impair
                            error = integConfigForEvalUserFx(integParamSet.x);
                        } else {   //  n > 1
                            integParamSet.sumFx = integParamSet.sumFx + stackRegs[STACK_REGS.X.INDEX()];   //   Mettre à jour la somme des y des points impairs
                            integParamSet.countFx = integParamSet.countFx + 1;
                            if (integParamSet.countFx >= integParamSet.countFxMax) {   //  La somme est complète, on peut calculer la prochaine estimation
                                double oldInteg = integParamSet.z;
                                error = integParamSet.calc();   //  Nouvelle estimation dans z
                                stackRegs[STACK_REGS.LX.INDEX()] = integParamSet.z;   //  HP15C le fait
                                if (error.length() == 0) {
                                    double diff = Math.abs(integParamSet.z - oldInteg);
                                    if (diff <= integParamSet.tol) {   //  OK c'est bon
                                        stackRegs[STACK_REGS.X.INDEX()] = integParamSet.z;
                                        stackRegs[STACK_REGS.Y.INDEX()] = diff;
                                        stackRegs[STACK_REGS.Z.INDEX()] = integParamSet.b;
                                        stackRegs[STACK_REGS.T.INDEX()] = integParamSet.a;
                                        nextProgLineNumber = integParamSet.oldNextProgLineNumber;
                                        integParamSet.clear();
                                        if (requestStopAfterInteg) {   //  Forcer le STOP (comme en mode SST) si INTEG a été lancé au départ de mode NORM
                                            requestStopAfterInteg = false;
                                            inSST = true;
                                        }
                                    } else {   //  Tolérance toujours pas respectée => On continue ?
                                        integParamSet.iterCount = integParamSet.iterCount + 1;
                                        if (integParamSet.iterCount > integParamSet.ITER_COUNT_MAX) {   //  Il n'y a plus d'espoir
                                            error = ERROR_INTEG_ITER_MAX;
                                        } else {  //  On continue !
                                            integParamSet.setNextLevel();
                                            integParamSet.x = integParamSet.a + integParamSet.h;    //  1er point impair
                                            error = integConfigForEvalUserFx(integParamSet.x);
                                        }
                                    }
                                }
                            } else {   //  La somme n'est pas encore complète
                                integParamSet.x = integParamSet.x + integParamSet.h * 2.0;    //  Prochain point impair
                                error = integConfigForEvalUserFx(integParamSet.x);
                            }
                        }
                    }
                }
                errorStandardHandle();
                break;
            case DSE:
            case ISG:
                if (alphaToX()) {
                    int regIndex = progLine.paramAddress;
                    if (progLine.ops[LINE_OPS.INDI.INDEX()] != null) {   //  (i))
                        int dataRegIndex = regs.get(BASE_REGS.RI.INDEX()).intValue();   //  Valeur dans I
                        regIndex = getRegIndexByDataRegIndex(dataRegIndex);
                        if ((regIndex < 0) || (regIndex > getRegsMaxIndex())) {
                            error = ERROR_INDEX;
                        }
                    }
                    if (error.length() == 0) {
                        double value = regs.get(regIndex);   //  value: counter(nnnnn).goal(nnn)step(nn)
                        double v = Math.abs(value);
                        int counter = (int) v;
                        double g = (v - (double) counter) * 1000d;
                        int goal = (int) (g + 0.5d);
                        double st = (g - (double) goal) * 100d;
                        int step = (int) (st + 0.5d);
                        int step2 = step;
                        if (step2 == 0) {
                            step2 = 1;
                        }
                        if (value < 0) {
                            counter = -counter;
                        }
                        if (baseOp.equals(OPS.DSE)) {
                            counter = counter - step2;
                            if (counter <= goal) {
                                incNextProgLineNumber();
                            }
                        }
                        if (baseOp.equals(OPS.ISG)) {
                            counter = counter + step2;
                            if (counter > goal) {
                                incNextProgLineNumber();
                            }
                        }
                        value = Math.abs((double) counter) + ((double) goal + (double) step / 100.0) / 1000.0;
                        if (counter < 0) {
                            value = -value;
                        }
                        regs.set(regIndex, value);  //  Update register
                        setStackLiftEnabled(true);
                    }
                }
                break;
            case SF:
            case CF:
            case TF:
                if (alphaToX()) {
                    int index = Integer.valueOf(progLine.ops[LINE_OPS.A09.INDEX()].SYMBOL());
                    if (baseOp.equals(OPS.SF)) {
                        flags[index] = true;
                        if (index == COMPLEX_FLAG_INDEX) {
                            if (!isComplexMode) {
                                isComplexMode = true;
                                fillImStack(0.0);
                            }
                        }
                    }
                    if (baseOp.equals(OPS.CF)) {
                        flags[index] = false;
                        if (index == COMPLEX_FLAG_INDEX) {
                            if (isComplexMode) {
                                isComplexMode = false;
                                fillImStack(0.0);   //  On nettoie et on éteint la lumière
                            }
                        }
                    }
                    if (baseOp.equals(OPS.TF)) {
                        if (!flags[index]) {   //  Skip next line if flag cleared
                            incNextProgLineNumber();
                        }
                        if (mode.equals(MODES.NORM)) {   //  Afficher sa valeur ("True" ou "False"))
                            error = (flags[index] ? "True" : "False");
                        }
                    }
                    setStackLiftEnabled(true);
                }
                break;
            case DIGIT_0:
            case DIGIT_1:
            case DIGIT_2:
            case DIGIT_3:
            case DIGIT_4:
            case DIGIT_5:
            case DIGIT_6:
            case DIGIT_7:
            case DIGIT_8:
            case DIGIT_9:
            case DOT:
            case CHS:
            case EEX:
                final int MAX_INPUT_LENGTH = 17;

                String beta = alpha;
                if (beta.equals("")) {   // Début d'entrée de nombre
                    if (!baseOp.equals(OPS.CHS)) {  //  StackLift éventuel uniquement si entrée d'un nombre (ne commençant pas par "-")
                        doStackLiftIfEnabled();
                    }
                }
                boolean acceptOp = true;
                int indEex = beta.indexOf(OPS.EEX.SYMBOL());
                if (indEex != (-1)) {   //  Ne plus accepter de "." ou "E" après un "E" antérieur
                    if ((baseOp.equals(OPS.DOT)) || (baseOp.equals(OPS.EEX))) {
                        acceptOp = false;
                    }
                }
                int indDot = beta.indexOf(OPS.DOT.SYMBOL());
                if (indDot != (-1)) {   //  Ne plus accepter de "." après un "." antérieur
                    if (baseOp.equals(OPS.DOT)) {
                        acceptOp = false;
                    }
                }
                if (acceptOp) {
                    if (baseOp.equals(OPS.CHS)) {
                        if (beta.equals("")) {   //  Un nombre ne peut commencer par "-" => Changer le signe de X
                            stackRegs[STACK_REGS.X.INDEX()] = -stackRegs[STACK_REGS.X.INDEX()];
                            setStackLiftEnabled(true);
                        } else {   //  Entrée de nombre en cours
                            int indChs1 = beta.indexOf(baseOp.SYMBOL());   //  Un "-" existe peut-être déjà => -x ou xE-x ou -xEx ou -xE-x
                            int indChs2 = -1;
                            if ((indChs1 != -1) && (indChs1 < (beta.length() - 1))) {   //  Un 2e "-" est possible   => -xE-x
                                indChs2 = beta.indexOf(baseOp.SYMBOL(), indChs1 + 1);   //  après le 1er
                            }
                            if (indChs1 != (-1)) {   //   -x ou xE-x ou -xEx ou -xE-x
                                if (indEex != -1) {   //  xE-x ou -xEx ou -xE-x
                                    if (indChs1 < indEex) {   //  -xEx ou -xE-x
                                        if (indChs2 != -1) {   //  -xE-x
                                            beta = beta.substring(0, indChs2) + beta.substring(indChs2 + 1);   //  => -xEx
                                        } else {   //  -xEx
                                            beta = beta.substring(0, indEex + 1) + baseOp.SYMBOL() + beta.substring(indEex + 1);   //  => -xE-x
                                        }
                                    } else {   //  xE-x
                                        beta = beta.substring(0, indChs1) + beta.substring(indChs1 + 1);   //  => xEx
                                    }
                                } else {   //  -x
                                    beta = beta.substring(indChs1 + 1);   //  => x
                                }
                            } else {   //  x ou xEx
                                if (indEex != (-1)) {   //  xEx
                                    beta = beta.substring(0, indEex + 1) + baseOp.SYMBOL() + beta.substring(indEex + 1);   //  => xE-x
                                } else {   //  x
                                    beta = baseOp.SYMBOL() + beta;   //  => -x
                                }
                            }
                        }
                    } else {   //  Pas CHS
                        String s = baseOp.SYMBOL();
                        if (beta.equals("")) {
                            if (baseOp.equals(OPS.EEX)) {
                                s = OPS.DIGIT_1.SYMBOL() + s;   //  Ajout de 1 en préfixe si commence par "E"
                            }
                            if (baseOp.equals(OPS.DOT)) {
                                s = OPS.DIGIT_0.SYMBOL() + s;   //  Ajout de 0 en préfixe si commence par "."
                            }
                        }
                        beta = beta + s;
                    }
                    if (beta.length() <= MAX_INPUT_LENGTH) {
                        alpha = beta;
                    }
                }
                break;
            case PI:
                if (alphaToX()) {
                    doStackLiftIfEnabled();
                    stackRegs[STACK_REGS.X.INDEX()] = Math.PI;
                    errorStandardHandle();
                }
                break;
            case LASTX:
                if (alphaToX()) {
                    doStackLiftIfEnabled();
                    stackRegs[STACK_REGS.X.INDEX()] = stackRegs[STACK_REGS.LX.INDEX()];
                    imStackRegs[STACK_REGS.X.INDEX()] = imStackRegs[STACK_REGS.LX.INDEX()];
                    errorStandardHandle();
                }
                break;
            case RAND:
                if (alphaToX()) {
                    doStackLiftIfEnabled();
                    stackRegs[STACK_REGS.X.INDEX()] = Math.random();
                    errorStandardHandle();
                }
                break;
            case SIGMA_PLUS:   //  Désactive stackLift
                if (alphaToX()) {
                    String error = "";
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    int index = getRegIndexByDataRegIndex(STAT_OPS.N.DATA_REG_INDEX());
                    int nMod = (int) (regs.get(index) + 1);
                    regs.set(index, (double) nMod);

                    index = getRegIndexByDataRegIndex(STAT_OPS.SUM_X.DATA_REG_INDEX());
                    double sumXMod = regs.get(index) + stackRegs[STACK_REGS.X.INDEX()];
                    regs.set(index, sumXMod);

                    index = getRegIndexByDataRegIndex(STAT_OPS.SUM_X2.DATA_REG_INDEX());
                    double sumX2Mod = regs.get(index) + stackRegs[STACK_REGS.X.INDEX()] * stackRegs[STACK_REGS.X.INDEX()];
                    regs.set(index, sumX2Mod);

                    index = getRegIndexByDataRegIndex(STAT_OPS.SUM_Y.DATA_REG_INDEX());
                    double sumYMod = regs.get(index) + stackRegs[STACK_REGS.Y.INDEX()];
                    regs.set(index, sumYMod);

                    index = getRegIndexByDataRegIndex(STAT_OPS.SUM_Y2.DATA_REG_INDEX());
                    double sumY2Mod = regs.get(index) + stackRegs[STACK_REGS.Y.INDEX()] * stackRegs[STACK_REGS.Y.INDEX()];
                    regs.set(index, sumY2Mod);

                    index = getRegIndexByDataRegIndex(STAT_OPS.SUM_XY.DATA_REG_INDEX());
                    double sumXYMod = regs.get(index) + stackRegs[STACK_REGS.X.INDEX()] * stackRegs[STACK_REGS.Y.INDEX()];
                    regs.set(index, sumXYMod);

                    stackRegs[STACK_REGS.X.INDEX()] = nMod;

                    testErrVal(sumXMod);
                    testErrVal(sumX2Mod);
                    testErrVal(sumYMod);
                    testErrVal(sumY2Mod);
                    testErrVal(sumXYMod);

                    if (error.length() == 0) {
                        setStackLiftEnabled(false);
                    } else {
                        regs.set(index, 0.0);   //  Plutôt qu'un NAN ou INF
                        stackRegs[STACK_REGS.X.INDEX()] = 0.0;
                    }
                }
                break;
            case SIGMA_MINUS:   //  Désactive stackLift
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    int index = getRegIndexByDataRegIndex(STAT_OPS.N.DATA_REG_INDEX());
                    int nMod = (int) (regs.get(index) + 1);
                    regs.set(index, (double) nMod);

                    index = getRegIndexByDataRegIndex(STAT_OPS.SUM_X.DATA_REG_INDEX());
                    double sumXMod = regs.get(index) - stackRegs[STACK_REGS.X.INDEX()];
                    regs.set(index, sumXMod);

                    index = getRegIndexByDataRegIndex(STAT_OPS.SUM_X2.DATA_REG_INDEX());
                    double sumX2Mod = regs.get(index) - stackRegs[STACK_REGS.X.INDEX()] * stackRegs[STACK_REGS.X.INDEX()];
                    regs.set(index, sumX2Mod);

                    index = getRegIndexByDataRegIndex(STAT_OPS.SUM_Y.DATA_REG_INDEX());
                    double sumYMod = regs.get(index) - stackRegs[STACK_REGS.Y.INDEX()];
                    regs.set(index, sumYMod);

                    index = getRegIndexByDataRegIndex(STAT_OPS.SUM_Y2.DATA_REG_INDEX());
                    double sumY2Mod = regs.get(index) - stackRegs[STACK_REGS.Y.INDEX()] * stackRegs[STACK_REGS.Y.INDEX()];
                    regs.set(index, sumY2Mod);

                    index = getRegIndexByDataRegIndex(STAT_OPS.SUM_XY.DATA_REG_INDEX());
                    double sumXYMod = regs.get(index) - stackRegs[STACK_REGS.X.INDEX()] * stackRegs[STACK_REGS.Y.INDEX()];
                    regs.set(index, sumXYMod);

                    stackRegs[STACK_REGS.X.INDEX()] = nMod;

                    testErrVal(sumXMod);
                    testErrVal(sumX2Mod);
                    testErrVal(sumYMod);
                    testErrVal(sumY2Mod);
                    testErrVal(sumXYMod);

                    if (error.length() == 0) {
                        setStackLiftEnabled(false);
                    } else {
                        regs.set(index, 0.0);   //  Plutôt qu'un NAN ou INF
                        stackRegs[STACK_REGS.X.INDEX()] = 0.0;
                    }
                }
                break;
            case MEAN:   //  Désactive stackLift
                if (alphaToX()) {
                    doStackLiftIfEnabled();
                    doStackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                    double n = regs.get(getRegIndexByDataRegIndex(STAT_OPS.N.DATA_REG_INDEX()));
                    if (n > 0) {
                        double sumX = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_X.DATA_REG_INDEX()));
                        double sumY = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_Y.DATA_REG_INDEX()));
                        double meanX = sumX / n;
                        double meanY = sumY / n;
                        stackRegs[STACK_REGS.X.INDEX()] = meanX;
                        stackRegs[STACK_REGS.Y.INDEX()] = meanY;
                        testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                        testErrVal(stackRegs[STACK_REGS.Y.INDEX()]);
                    } else {   //  n <= 0
                        error = ERROR_STAT;
                    }
                    if (error.length() == 0) {
                        setStackLiftEnabled(true);
                    } else {
                        stackRegs[STACK_REGS.X.INDEX()] = 0.0;
                        stackRegs[STACK_REGS.Y.INDEX()] = 0.0;   //  Plutôt qu'un NAN ou INF
                    }
                }
                break;
            case STDEV:
                if (alphaToX()) {
                    doStackLiftIfEnabled();
                    doStackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                    double n = regs.get(getRegIndexByDataRegIndex(STAT_OPS.N.DATA_REG_INDEX()));
                    if (n > 1) {
                        double sumX = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_X.DATA_REG_INDEX()));
                        double sumX2 = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_X2.DATA_REG_INDEX()));
                        double sumY = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_Y.DATA_REG_INDEX()));
                        double sumY2 = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_Y2.DATA_REG_INDEX()));
                        double mv = n * sumX2 - sumX * sumX;
                        double nv = n * sumY2 - sumY * sumY;
                        double stDevX = Math.sqrt(mv / (n * (n - 1)));
                        double stDevY = Math.sqrt(nv / (n * (n - 1)));
                        stackRegs[STACK_REGS.X.INDEX()] = stDevX;
                        stackRegs[STACK_REGS.Y.INDEX()] = stDevY;
                    } else {   //  n <= 1
                        error = ERROR_STAT;
                    }
                    if (error.length() == 0) {
                        setStackLiftEnabled(true);
                    } else {
                        stackRegs[STACK_REGS.X.INDEX()] = 0.0;
                        stackRegs[STACK_REGS.Y.INDEX()] = 0.0;   //  Plutôt qu'un NAN ou INF
                    }
                }
                break;
            case LR:
                if (alphaToX()) {
                    doStackLiftIfEnabled();
                    doStackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                    double n = regs.get(getRegIndexByDataRegIndex(STAT_OPS.N.DATA_REG_INDEX()));
                    if (n > 1) {
                        double sumX = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_X.DATA_REG_INDEX()));
                        double sumX2 = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_X2.DATA_REG_INDEX()));
                        double sumY = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_Y.DATA_REG_INDEX()));
                        double sumY2 = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_Y2.DATA_REG_INDEX()));
                        double sumXY = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_XY.DATA_REG_INDEX()));
                        double mv = n * sumX2 - sumX * sumX;
                        double nv = n * sumY2 - sumY * sumY;
                        double p = n * sumXY - sumX * sumY;
                        double a = p / mv;
                        double b = (mv * sumY - p * sumX) / (n * mv);
                        stackRegs[STACK_REGS.X.INDEX()] = b;
                        stackRegs[STACK_REGS.Y.INDEX()] = a;
                    } else {   //  n <= 1
                        error = ERROR_STAT;
                    }
                    if (error.length() == 0) {
                        setStackLiftEnabled(true);
                    } else {
                        stackRegs[STACK_REGS.X.INDEX()] = 0.0;
                        stackRegs[STACK_REGS.Y.INDEX()] = 0.0;   //  Plutôt qu'un NAN ou INF
                    }
                }
                break;
            case YER:
                if (alphaToX()) {
                    doStackLift();    //  Un stacklift obligatoire
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    double n = regs.get(getRegIndexByDataRegIndex(STAT_OPS.N.DATA_REG_INDEX()));
                    if (n > 1) {
                        double sumX = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_X.DATA_REG_INDEX()));
                        double sumX2 = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_X2.DATA_REG_INDEX()));
                        double sumY = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_Y.DATA_REG_INDEX()));
                        double sumY2 = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_Y2.DATA_REG_INDEX()));
                        double sumXY = regs.get(getRegIndexByDataRegIndex(STAT_OPS.SUM_XY.DATA_REG_INDEX()));
                        double mv = n * sumX2 - sumX * sumX;
                        double nv = n * sumY2 - sumY * sumY;
                        double p = n * sumXY - sumX * sumY;
                        double r = p / Math.sqrt(mv * nv);
                        double ye = (mv * sumY + p * (n * stackRegs[STACK_REGS.X.INDEX()] - sumX)) / (n * mv);
                        stackRegs[STACK_REGS.X.INDEX()] = ye;
                        stackRegs[STACK_REGS.Y.INDEX()] = r;
                    } else {   //  n <= 1
                        error = ERROR_STAT;
                    }
                    if (error.length() == 0) {
                        setStackLiftEnabled(true);
                    } else {
                        stackRegs[STACK_REGS.X.INDEX()] = 0.0;
                        stackRegs[STACK_REGS.Y.INDEX()] = 0.0;   //  Plutôt qu'un NAN ou INF
                    }
                }
                break;
            case SQR:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = stackRegs[STACK_REGS.X.INDEX()] * stackRegs[STACK_REGS.X.INDEX()];
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.sqr();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case SQRT:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = Math.sqrt(stackRegs[STACK_REGS.X.INDEX()]);
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.sqrt();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case TO_RAD:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    stackRegs[STACK_REGS.X.INDEX()] = Math.toRadians(stackRegs[STACK_REGS.X.INDEX()]);
                    imStackRegs[STACK_REGS.X.INDEX()] = 0.0;
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case TO_DEG:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    stackRegs[STACK_REGS.X.INDEX()] = stackRegs[STACK_REGS.X.INDEX()] * (180.0 / Math.PI);
                    imStackRegs[STACK_REGS.X.INDEX()] = 0.0;
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case EXP:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = Math.exp(stackRegs[STACK_REGS.X.INDEX()]);
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.exp();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case LN:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = Math.log(stackRegs[STACK_REGS.X.INDEX()]);   //  Math.log est en fait ln
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.ln();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case EXP10:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = Math.exp(Math.log(10) * stackRegs[STACK_REGS.X.INDEX()]);   //  e^(x*ln(10)) cad 10^x
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.exp10();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case LOG:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = Math.log10(stackRegs[STACK_REGS.X.INDEX()]);
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.log10();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case INV:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = 1.0 / stackRegs[STACK_REGS.X.INDEX()];
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.inv();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case PC:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    stackRegs[STACK_REGS.X.INDEX()] = stackRegs[STACK_REGS.Y.INDEX()] * (stackRegs[STACK_REGS.X.INDEX()] / 100.0);
                    imStackRegs[STACK_REGS.X.INDEX()] = 0.0;
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();  //  Pas de mergeDown
                }
                break;
            case DPC:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    stackRegs[STACK_REGS.X.INDEX()] = (stackRegs[STACK_REGS.X.INDEX()] / stackRegs[STACK_REGS.Y.INDEX()] - 1.0) * 100.0;
                    imStackRegs[STACK_REGS.X.INDEX()] = 0.0;
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();  //  Pas de mergeDown
                }
                break;
            case ABS:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = Math.abs(stackRegs[STACK_REGS.X.INDEX()]);
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.abs();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    errorStandardHandle();
                }
                break;
            case RND:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    stackRegs[STACK_REGS.X.INDEX()] = Double.parseDouble(getRoundForDisplay(stackRegs[STACK_REGS.X.INDEX()]));
                    errorStandardHandle();
                }
                break;
            case POL:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        double x = stackRegs[STACK_REGS.X.INDEX()];
                        stackRegs[STACK_REGS.X.INDEX()] = Math.hypot(x, stackRegs[STACK_REGS.Y.INDEX()]);
                        stackRegs[STACK_REGS.Y.INDEX()] = radToAngle(Math.atan2(stackRegs[STACK_REGS.Y.INDEX()], x));
                        testErrVal(stackRegs[STACK_REGS.Y.INDEX()]);
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.pol();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = radToAngle(complex.getXIm());
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    if (error.length() == 0) {
                        setStackLiftEnabled(true);
                    } else {
                        stackRegs[STACK_REGS.X.INDEX()] = 0.0;   //  Plutôt qu'un NAN ou INF
                        stackRegs[STACK_REGS.Y.INDEX()] = 0.0;
                        imStackRegs[STACK_REGS.X.INDEX()] = 0.0;
                    }
                }
                break;
            case RECT:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        double x = stackRegs[STACK_REGS.X.INDEX()];
                        stackRegs[STACK_REGS.X.INDEX()] = x * Math.cos(angleToRad(stackRegs[STACK_REGS.Y.INDEX()]));
                        stackRegs[STACK_REGS.Y.INDEX()] = x * Math.sin(angleToRad(stackRegs[STACK_REGS.Y.INDEX()]));
                        testErrVal(stackRegs[STACK_REGS.Y.INDEX()]);
                    } else {    //  Complex
                        complex.setX(angleToRad(stackRegs[STACK_REGS.X.INDEX()]), angleToRad(imStackRegs[STACK_REGS.X.INDEX()]));
                        complex.rect();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    if (error.length() == 0) {
                        setStackLiftEnabled(true);
                    } else {
                        stackRegs[STACK_REGS.X.INDEX()] = 0.0;   //  Plutôt qu'un NAN ou INF
                        stackRegs[STACK_REGS.Y.INDEX()] = 0.0;
                        imStackRegs[STACK_REGS.X.INDEX()] = 0.0;
                    }
                }
                break;
            case HMS:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    stackRegs[STACK_REGS.X.INDEX()] = (90.0 * stackRegs[STACK_REGS.X.INDEX()] + (int) (60.0 * stackRegs[STACK_REGS.X.INDEX()]) + 100.0 * (int) stackRegs[STACK_REGS.X.INDEX()]) / 250.0;
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case H:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    stackRegs[STACK_REGS.X.INDEX()] = (250.0 * stackRegs[STACK_REGS.X.INDEX()] - (int) (100.0 * stackRegs[STACK_REGS.X.INDEX()]) - 60.0 * (int) stackRegs[STACK_REGS.X.INDEX()]) / 90.0;
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case COMB:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    int m = (int) stackRegs[STACK_REGS.Y.INDEX()];
                    int n = (int) stackRegs[STACK_REGS.X.INDEX()];
                    if ((m >= 0) && (n >= 0) && (n <= m)) {
                        stackRegs[STACK_REGS.X.INDEX()] = factOver(m, m - n) / fact(n);
                        imStackRegs[STACK_REGS.X.INDEX()] = 0.0;
                        stackMergeDown();
                        testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    } else {   //  Erreur
                        error = ERROR_PERM_COMB;
                    }
                    errorStandardHandle();
                }
                break;
            case PERM:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    int m = (int) stackRegs[STACK_REGS.Y.INDEX()];
                    int n = (int) stackRegs[STACK_REGS.X.INDEX()];
                    if ((m >= 0) && (n >= 0) && (n <= m)) {
                        stackRegs[STACK_REGS.X.INDEX()] = factOver(m, m - n);
                        imStackRegs[STACK_REGS.X.INDEX()] = 0.0;
                        stackMergeDown();
                        testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    } else {   //  Erreur
                        error = ERROR_PERM_COMB;
                    }
                    errorStandardHandle();
                }
                break;
            case FRAC:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    double val = Math.abs(stackRegs[STACK_REGS.X.INDEX()]);
                    val = val - (int) val;
                    val = (stackRegs[STACK_REGS.X.INDEX()] >= 0 ? val : -val);
                    stackRegs[STACK_REGS.X.INDEX()] = val;
                    errorStandardHandle();
                }
                break;
            case INTEGER:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    double val = (int) Math.abs(stackRegs[STACK_REGS.X.INDEX()]);
                    val = (stackRegs[STACK_REGS.X.INDEX()] >= 0 ? val : -val);
                    stackRegs[STACK_REGS.X.INDEX()] = val;
                    errorStandardHandle();
                }
                break;
            case SIN:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = Math.sin(angleToRad(stackRegs[STACK_REGS.X.INDEX()]));
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.sin();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case COS:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = Math.cos(angleToRad(stackRegs[STACK_REGS.X.INDEX()]));
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.cos();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case TAN:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = Math.tan(angleToRad(stackRegs[STACK_REGS.X.INDEX()]));
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.tan();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case ASIN:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = radToAngle(Math.asin(stackRegs[STACK_REGS.X.INDEX()]));
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.asin();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case ACOS:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = radToAngle(Math.acos(stackRegs[STACK_REGS.X.INDEX()]));
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.acos();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case ATAN:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = radToAngle(Math.atan(stackRegs[STACK_REGS.X.INDEX()]));
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.atan();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case SINH:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = Math.sinh(stackRegs[STACK_REGS.X.INDEX()]);
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.sinh();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case COSH:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = Math.cosh(stackRegs[STACK_REGS.X.INDEX()]);
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.cosh();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case TANH:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = Math.tanh(stackRegs[STACK_REGS.X.INDEX()]);
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.tanh();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case ASINH:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        double t = stackRegs[STACK_REGS.X.INDEX()];
                        stackRegs[STACK_REGS.X.INDEX()] = Math.log(t + Math.sqrt(t * t + 1.0));
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.asinh();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case ACOSH:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        double t = stackRegs[STACK_REGS.X.INDEX()];
                        stackRegs[STACK_REGS.X.INDEX()] = Math.log(t + Math.sqrt(t * t - 1.0));
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.acosh();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case ATANH:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];   //  HP15C le fait
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        double t = stackRegs[STACK_REGS.X.INDEX()];
                        stackRegs[STACK_REGS.X.INDEX()] = Math.log((1.0 + t) / (1.0 - t)) / 2.0;
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.atanh();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case FACT:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    stackRegs[STACK_REGS.X.INDEX()] = gamma(1 + stackRegs[STACK_REGS.X.INDEX()]);
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    errorStandardHandle();
                }
                break;
            case POWER:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = Math.pow(stackRegs[STACK_REGS.Y.INDEX()], stackRegs[STACK_REGS.X.INDEX()]);
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.setY(stackRegs[STACK_REGS.Y.INDEX()], imStackRegs[STACK_REGS.Y.INDEX()]);
                        complex.pow();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    if (error.length() == 0) {
                        stackMergeDown();
                        setStackLiftEnabled(true);
                    } else {
                        stackRegs[STACK_REGS.X.INDEX()] = 0.0;
                        imStackRegs[STACK_REGS.X.INDEX()] = 0.0;
                    }
                }
                break;
            case PLUS:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = stackRegs[STACK_REGS.Y.INDEX()] + stackRegs[STACK_REGS.X.INDEX()];
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.setY(stackRegs[STACK_REGS.Y.INDEX()], imStackRegs[STACK_REGS.Y.INDEX()]);
                        complex.plus();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    if (error.length() == 0) {
                        stackMergeDown();
                        setStackLiftEnabled(true);
                    } else {
                        stackRegs[STACK_REGS.X.INDEX()] = 0.0;
                        imStackRegs[STACK_REGS.X.INDEX()] = 0.0;
                    }
                }
                break;
            case MINUS:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = stackRegs[STACK_REGS.Y.INDEX()] - stackRegs[STACK_REGS.X.INDEX()];
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.setY(stackRegs[STACK_REGS.Y.INDEX()], imStackRegs[STACK_REGS.Y.INDEX()]);
                        complex.minus();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    if (error.length() == 0) {
                        stackMergeDown();
                        setStackLiftEnabled(true);
                    } else {
                        stackRegs[STACK_REGS.X.INDEX()] = 0.0;
                        imStackRegs[STACK_REGS.X.INDEX()] = 0.0;
                    }
                }
                break;
            case MULT:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = stackRegs[STACK_REGS.Y.INDEX()] * stackRegs[STACK_REGS.X.INDEX()];
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.setY(stackRegs[STACK_REGS.Y.INDEX()], imStackRegs[STACK_REGS.Y.INDEX()]);
                        complex.mult();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    if (error.length() == 0) {
                        stackMergeDown();
                        setStackLiftEnabled(true);
                    } else {
                        stackRegs[STACK_REGS.X.INDEX()] = 0.0;
                        imStackRegs[STACK_REGS.X.INDEX()] = 0.0;
                    }
                }
                break;
            case DIV:
                if (alphaToX()) {
                    stackRegs[STACK_REGS.LX.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                    imStackRegs[STACK_REGS.LX.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                    if (!isComplexMode) {
                        stackRegs[STACK_REGS.X.INDEX()] = stackRegs[STACK_REGS.Y.INDEX()] / stackRegs[STACK_REGS.X.INDEX()];
                    } else {    //  Complex
                        complex.setX(stackRegs[STACK_REGS.X.INDEX()], imStackRegs[STACK_REGS.X.INDEX()]);
                        complex.setY(stackRegs[STACK_REGS.Y.INDEX()], imStackRegs[STACK_REGS.Y.INDEX()]);
                        complex.div();
                        stackRegs[STACK_REGS.X.INDEX()] = complex.getXRe();
                        imStackRegs[STACK_REGS.X.INDEX()] = complex.getXIm();
                        testErrVal(imStackRegs[STACK_REGS.X.INDEX()]);
                    }
                    testErrVal(stackRegs[STACK_REGS.X.INDEX()]);
                    if (error.length() == 0) {
                        stackMergeDown();
                        setStackLiftEnabled(true);
                    } else {
                        stackRegs[STACK_REGS.X.INDEX()] = 0.0;
                        imStackRegs[STACK_REGS.X.INDEX()] = 0.0;
                    }
                }
                break;
            case I:
                if (alphaToX()) {
                    if (!isComplexMode) {
                        isComplexMode = true;
                        flags[COMPLEX_FLAG_INDEX] = true;
                        fillImStack(0.0);
                    }
                    if (isComplexMode) {
                        imStackRegs[STACK_REGS.X.INDEX()] = stackRegs[STACK_REGS.X.INDEX()];
                        stackRegs[STACK_REGS.X.INDEX()] = stackRegs[STACK_REGS.Y.INDEX()];
                        stackMergeDown();
                    }
                    setStackLiftEnabled(true);
                }
                break;
            case REIM:
                if (alphaToX()) {
                    if (!isComplexMode) {
                        isComplexMode = true;
                        flags[COMPLEX_FLAG_INDEX] = true;
                        fillImStack(0.0);
                    }
                    if (isComplexMode) {
                        double t = stackRegs[STACK_REGS.X.INDEX()];
                        stackRegs[STACK_REGS.X.INDEX()] = imStackRegs[STACK_REGS.X.INDEX()];
                        imStackRegs[STACK_REGS.X.INDEX()] = t;
                    }
                    setStackLiftEnabled(true);
                }
                break;
            case DEG:
            case RAD:
            case GRAD:   //  Neutre sur stackLift
                if (alphaToX()) {
                    OPS op = progLine.ops[LINE_OPS.BASE.INDEX()];
                    setAngleMode(op);
                }
                break;
            case XE0:   //  Tests neutres sur stackLift ?
                if (alphaToX()) {
                    double x = round(stackRegs[STACK_REGS.X.INDEX()], MAX_DIGITS + 1);
                    if (!isComplexMode) {
                        if (x != 0) {   //  cad Skip if False
                            incNextProgLineNumber();
                        }
                    } else {   //  Complex
                        double xi = round(imStackRegs[STACK_REGS.X.INDEX()], MAX_DIGITS + 1);
                        if ((x != 0) || (xi != 0)) {   //  cad Skip if False
                            incNextProgLineNumber();
                        }
                    }
                    break;
                }
            case XNE0:
                if (alphaToX()) {
                    double x = round(stackRegs[STACK_REGS.X.INDEX()], MAX_DIGITS + 1);
                    if (!isComplexMode) {
                        if (x == 0) {   //  cad Skip if False
                            incNextProgLineNumber();
                        }
                    } else {   //  Complex
                        double xi = round(imStackRegs[STACK_REGS.X.INDEX()], MAX_DIGITS + 1);
                        if ((x == 0) && (xi == 0)) {   //  cad Skip if False
                            incNextProgLineNumber();
                        }
                    }
                    break;
                }
            case XG0:
                if (alphaToX()) {
                    double x = round(stackRegs[STACK_REGS.X.INDEX()], MAX_DIGITS + 1);
                    if (x <= 0) {   //  cad Skip if False
                        incNextProgLineNumber();
                    }
                    break;
                }
            case XL0:
                if (alphaToX()) {
                    double x = round(stackRegs[STACK_REGS.X.INDEX()], MAX_DIGITS + 1);
                    if (x >= 0) {   //  cad Skip if False
                        incNextProgLineNumber();
                    }
                    break;
                }
            case XGE0:
                if (alphaToX()) {
                    double x = round(stackRegs[STACK_REGS.X.INDEX()], MAX_DIGITS + 1);
                    if (x < 0) {   //  cad Skip if False
                        incNextProgLineNumber();
                    }
                    break;
                }
            case XLE0:
                if (alphaToX()) {
                    double x = round(stackRegs[STACK_REGS.X.INDEX()], MAX_DIGITS + 1);
                    if (x > 0) {   //  cad Skip if False
                        incNextProgLineNumber();
                    }
                    break;
                }
            case XEY:
                if (alphaToX()) {
                    double x = round(stackRegs[STACK_REGS.X.INDEX()], MAX_DIGITS + 1);
                    double y = round(stackRegs[STACK_REGS.Y.INDEX()], MAX_DIGITS + 1);
                    if (!isComplexMode) {
                        if (x != y) {   //  cad Skip if False
                            incNextProgLineNumber();
                        }
                    } else {   //  Complex
                        double xi = round(imStackRegs[STACK_REGS.X.INDEX()], MAX_DIGITS + 1);
                        double yi = round(imStackRegs[STACK_REGS.Y.INDEX()], MAX_DIGITS + 1);
                        if ((x != xi) || (y != yi)) {   //  cad Skip if False
                            incNextProgLineNumber();
                        }
                    }
                    break;
                }
            case XNEY:
                if (alphaToX()) {
                    double x = round(stackRegs[STACK_REGS.X.INDEX()], MAX_DIGITS + 1);
                    double y = round(stackRegs[STACK_REGS.Y.INDEX()], MAX_DIGITS + 1);
                    if (!isComplexMode) {
                        if (x == y) {   //  cad Skip if False
                            incNextProgLineNumber();
                        }
                    } else {   //  Complex
                        double xi = round(imStackRegs[STACK_REGS.X.INDEX()], MAX_DIGITS + 1);
                        double yi = round(imStackRegs[STACK_REGS.Y.INDEX()], MAX_DIGITS + 1);
                        if ((x == xi) && (y == yi)) {   //  cad Skip if False
                            incNextProgLineNumber();
                        }
                    }
                    break;
                }
            case XGY:
                if (alphaToX()) {
                    double x = round(stackRegs[STACK_REGS.X.INDEX()], MAX_DIGITS + 1);
                    double y = round(stackRegs[STACK_REGS.Y.INDEX()], MAX_DIGITS + 1);
                    if (x <= y) {   //  cad Skip if False
                        incNextProgLineNumber();
                    }
                    break;
                }
            case XLY:
                if (alphaToX()) {
                    double x = round(stackRegs[STACK_REGS.X.INDEX()], MAX_DIGITS + 1);
                    double y = round(stackRegs[STACK_REGS.Y.INDEX()], MAX_DIGITS + 1);
                    if (x >= y) {   //  cad Skip if False
                        incNextProgLineNumber();
                    }
                    break;
                }
            case XGEY:
                if (alphaToX()) {
                    double x = round(stackRegs[STACK_REGS.X.INDEX()], MAX_DIGITS + 1);
                    double y = round(stackRegs[STACK_REGS.Y.INDEX()], MAX_DIGITS + 1);
                    if (x < y) {   //  cad Skip if False
                        incNextProgLineNumber();
                    }
                    break;
                }
            case XLEY:
                if (alphaToX()) {
                    double x = round(stackRegs[STACK_REGS.X.INDEX()], MAX_DIGITS + 1);
                    double y = round(stackRegs[STACK_REGS.Y.INDEX()], MAX_DIGITS + 1);
                    if (x > y) {   //  cad Skip if False
                        incNextProgLineNumber();
                    }
                    break;
                }
            case BACK:
                String alphaTemp = alpha;
                if (alphaTemp.length() >= 2) {
                    alphaTemp = alphaTemp.substring(0, alphaTemp.length() - 1);   //  Enlever le dernier caractère
                    if (alphaTemp.equals(OPS.CHS.SYMBOL())) {   //  p.ex. "-0.3" -> "-0." -> "-0" -> "-" : Boum  (cf String.format plus bas, sur la partie avant le "E" et le ".")
                        stackRegs[STACK_REGS.X.INDEX()] = 0;
                        setStackLiftEnabled(false);   // Désactive stacklift
                        alphaTemp = "";
                    }
                } else {   //  alpha a 0 ou 1 caractères
                    stackRegs[STACK_REGS.X.INDEX()] = 0;
                    setStackLiftEnabled(false);     // Désactive stacklift
                    alphaTemp = "";
                }
                alpha = alphaTemp;
                break;
            case CLX:    // Désactive stacklift
                if (alphaToX()) {
                    stackRegs[STACK_REGS.X.INDEX()] = 0;  //  T,Z,Y,X -> T,Z,Y,0    LASTX non modifié
                    setStackLiftEnabled(false);
                }
                break;
            case CLEAR_PREFIX:    //  Neutre sur StackLift
                if (alphaToX()) {
                    double val = Math.abs(stackRegs[STACK_REGS.X.INDEX()]);
                    int exp = 0;
                    double mant = 0;
                    if (val != 0) {
                        exp = (int) Math.floor(1.0 + Math.log10(val));
                        mant = val / Math.pow(10, exp);   //  Entre 0 et 1
                    }
                    double valr = mant * Math.pow(10, MAX_DIGITS);
                    error = String.format(Locale.US, "%.0f", valr);   //  error sera <>"" dans tous les cas (car représentera la mantisse) et sera donc traité comme une "erreur"
                }
                break;
            case CLEAR_REGS:   //  Neutre sur StackLift
                if (alphaToX()) {
                    for (int i = 0; i <= (regs.size() - 1); i = i + 1) {   //  Tout effacer: I, R0 à R9, R.0 à R.9 et suivants
                        regs.set(i, 0.0);
                    }
                }
                break;
            case CLEAR_SIGMA:   //  Neutre sur StackLift
                if (alphaToX()) {
                    for (STAT_OPS statOp : STAT_OPS.values()) {
                        int index = getRegIndexByDataRegIndex(statOp.DATA_REG_INDEX());
                        regs.set(index, 0.0);
                    }
                    fillStack(0);     //  Pas LASTX
                    fillImStack(0);   //  Pas LASTX
                }
                break;
            case ENTER:   // Désactive Stacklift
                if (alphaToX()) {
                    doStackLift();
                    setStackLiftEnabled(false);
                }
                break;
            case RDN:
                if (alphaToX()) {
                    stackRollDown();
                    setStackLiftEnabled(true);
                }
                break;
            case RUP:
                if (alphaToX()) {
                    stackRollUp();
                    setStackLiftEnabled(true);
                }
                break;
            case XCHGXY:
                if (alphaToX()) {
                    Double temp = stackRegs[STACK_REGS.X.INDEX()];
                    stackRegs[STACK_REGS.X.INDEX()] = stackRegs[STACK_REGS.Y.INDEX()];
                    stackRegs[STACK_REGS.Y.INDEX()] = temp;
                    if (isComplexMode) {
                        temp = imStackRegs[STACK_REGS.X.INDEX()];
                        imStackRegs[STACK_REGS.X.INDEX()] = imStackRegs[STACK_REGS.Y.INDEX()];
                        imStackRegs[STACK_REGS.Y.INDEX()] = temp;
                    }
                    setStackLiftEnabled(true);
                }
                break;
            case BEGIN:
                if (alphaToX()) {
                    if (isWrapAround) {   //  On est passé de la fin au début => STOP
                        isWrapAround = false;
                        mode = MODES.NORM;
                        isAutoLine = false;
                        clearRetStack();
                    }
                }
                break;
            case RTN:   //  Neutre sur StackLift ???
                if (alphaToX()) {
                    if (!isRetStackEmpty()) {  //  La pile d'appels n'est pas vide
                        int dpln = popProgLineNumber();
                        switch (dpln) {
                            case SOLVE_RETURN_CODE:    //  Si Code de retour SOLVE ou INTEG => UserFx a été évaluée et donc Retour à SOLVE ou INTEG qui avait demandé l'évaluation
                                tempProgLine.ops[LINE_OPS.BASE.INDEX()] = OPS.SOLVE;   //  Rappeler Solve pour continuer
                                tempProgLine.paramAddress = solveParamSet.userFxLineNumber;
                                error = exec(tempProgLine);
                                break;
                            case INTEG_RETURN_CODE:
                                tempProgLine.ops[LINE_OPS.BASE.INDEX()] = OPS.INTEG;
                                tempProgLine.paramAddress = integParamSet.userFxLineNumber;
                                error = exec(tempProgLine);
                                break;
                            case END_RETURN_CODE:   //  RTN de GSB appelé depuis mode NORM (cf saveOrExecLineWithLabel(runMode))
                                mode = MODES.NORM;
                                isAutoLine = false;
                                break;
                            default:   //  Normal, Pas Code de retour à SOLVE ou INTEG, et pas de RTN de GSB appelé depuis mode NORM
                                nextProgLineNumber = dpln;
                                break;
                        }
                    } else {   //  Pile d'appels vide => STOP sans erreur
                        mode = MODES.NORM;
                        isAutoLine = false;
                    }
                }
                break;
            case RS:
                if (alphaToX()) {
                    mode = MODES.NORM;
                    isAutoLine = false;
                }
                break;
            case HYP:   //  Ghost => NOP
            case AHYP:
            case TEST:
            case PR:   //  Non programmable
            case SST:
            case BST:
            case CLEAR_PRGM:
                if (alphaToX()) {
                    //  NOP
                }
                break;
            case PSE:
                if (alphaToX()) {
                    inPSE = true;
                }
        }
        return error;
    }
}
