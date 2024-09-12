package com.example.pgyl.sp15c_a;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.pgyl.pekislib_a.ColorBox;
import com.example.pgyl.pekislib_a.DotMatrixDisplayView;
import com.example.pgyl.pekislib_a.StringDB;
import com.example.pgyl.sp15c_a.Alu.KEYS;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.pgyl.pekislib_a.ColorUtils.BUTTON_COLOR_TYPES;
import static com.example.pgyl.pekislib_a.MiscUtils.msgBox;
import static com.example.pgyl.sp15c_a.Alu.BASE_REGS;
import static com.example.pgyl.sp15c_a.Alu.OPERATIONS;
import static com.example.pgyl.sp15c_a.Alu.STK_REGS;

//  MainActivity fait appel à CtRecordShandler pour la gestion des CtRecord (création, suppression, tri, écoute des événements, ...) grâce aux boutons de contrôle agissant sur la sélection des items de la liste, ...
//  MainCtListUpdater maintient la liste de MainActivity (rafraîchissement, scrollbar, ...), fait appel à MainCtListAdapter (pour gérer chaque item) et également à CtRecordShandler (pour leur mise à jour)
//  MainCtListItemAdapter reçoit ses items (CtRecord) de la part de MainCtListUpdater et gère chaque item de la liste (avec ses boutons de contrôle)
//  CtRecordsHandler reçoit les événements onExpiredTimer() des CtRecord (et les relaie à MainCtListUpdater), et aussi leurs onRequestClockAppAlarmSwitch() pour la création/suppression d'alarmes dans Clock App
//  Si un item de liste génère un onExpiredTimer(), MainCtListUpdater le signalera à l'utilisateur
public class MainActivity extends Activity {
    //region Constantes
    private enum SHIFT_MODES {
        UNSHIFTED, F_SHIFT, G_SHIFT;

        public int INDEX() {
            return ordinal();
        }
    }

    public enum LEGEND_POS {
        TOP, MID, LOW;

        public int INDEX() {
            return ordinal();
        }
    }

    private enum MODES {
        NORM, EDIT, RUN, EXPORT;

        public int INDEX() {
            return ordinal();
        }
    }

    private final String ERROR_STO = "STO ?";
    private final String ERROR_RCL = "RCL ?";
    private final String ERROR_INDEX = "Invalid index";
    private final String ERROR_NO_PROG = "Invalid instruction";


    public enum SWTIMER_SHP_KEY_NAMES {SHOW_EXPIRATION_TIME, ADD_NEW_CHRONOTIMER_TO_LIST, SET_CLOCK_APP_ALARM_ON_START_TIMER, KEEP_SCREEN, REQUESTED_CLOCK_APP_ALARM_DISMISSES}
    //endregion

    //region Variables
    private LinearLayout layoutButtonsOnSelection;
    private LinearLayout layoutDotMatrixDisplay;
    private ImageButtonViewStack[] buttons;
    private DotMatrixDisplayView dotMatrixDisplayView;
    private DotMatrixDisplayView sideDotMatrixDisplayView;
    private Menu menu;
    private MenuItem barMenuItemSetClockAppAlarmOnStartTimer;
    private MenuItem barMenuItemKeepScreen;
    private boolean keepScreen;
    private ListView mainCtListView;
    private StringDB stringDB;
    private String shpFileName;
    private SHIFT_MODES shiftMode;
    private CalcDotMatrixDisplayUpdater dotMatrixDisplayUpdater;
    private SideDotMatrixDisplayUpdater sideDotMatrixDisplayUpdater;
    private Alu alu;
    private MODES mode;
    private String error;
    private String alpha;
    private boolean stackLiftEnabled;
    private OPERATIONS inOp = null;
    private ProgLine tProgLine;
    private int currentProgLineNumber;
    private boolean newProgLine;
    private boolean displaySymbol;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String ACTIVITY_TITLE = "SP15C";

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().setTitle(ACTIVITY_TITLE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //setCurrent(stringDB, getAppInfosTableName(), getAppInfosDataVersionIndex(), String.valueOf(DATA_VERSION));
        ///mainCtListUpdater.stopAutomatic();
        //mainCtListUpdater.close();
        //mainCtListUpdater = null;
        //mainCtListItemAdapter.close();
        //mainCtListItemAdapter = null;
        //ctRecordsHandler.saveAndclose();
        //ctRecordsHandler = null;
        dotMatrixDisplayUpdater.close();
        dotMatrixDisplayUpdater = null;
        sideDotMatrixDisplayUpdater.close();
        sideDotMatrixDisplayUpdater = null;
        //stringDB.close();
        //stringDB = null;
        //menu = null;
        //savePreferences();
    }
    //endregion

    @Override
    protected void onResume() {
        super.onResume();

        setContentView(R.layout.main);
        //shpFileName = getPackageName() + SHP_FILE_NAME_SUFFIX;   //  Sans nom d'activité car sera partagé avec CtDisplayActivity
        //keepScreen = getSHPKeepScreen();

        setupButtons();
        setupDotMatrixDisplay();
        setupSideDotMatrixDisplay();
        //setupStringDB();
        //setupCtRecordsHandler();
        //setupMainCtList();
        //setupMainCtListUpdater();

        mode = MODES.NORM;
        error = "";
        alpha = "";
        stackLiftEnabled = false;
        inOp = null;

        setupDotMatrixDisplayUpdater();
        updateDisplayDotMatrixColors();

        setupAlu();
        tProgLine = new ProgLine();
        currentProgLineNumber = 0;
        displaySymbol = true;
        newProgLine = false;

        dotMatrixDisplayUpdater.displayText(alu.getRoundXForDisplay(), true);

        setupSideDotMatrixDisplayUpdater();
        updateSideDotMatrixColors();
        updateSideDisplay();
        //setupShowExpirationTime();
        //setupSetClockAppAlarmOnStartTimer();
        //setupAddNewChronoTimerToList();
        shiftMode = SHIFT_MODES.UNSHIFTED;
        updateDisplayButtonColors();
        //updateDisplayKeepScreen();
        //mainCtListUpdater.reload();
        //mainCtListUpdater.startAutomatic(System.currentTimeMillis(), 0);
        //updateDisplayButtonsAndDotMatrixDisplayVisibility();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {  //  Non appelé après changement d'orientation
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        //setupBarMenuItems();
        //updateDisplaySetClockAppAlarmOnStartTimerBarMenuItemIcon(setClockAppAlarmOnStartTimer);
        //updateDisplayKeepScreenBarMenuItemIcon(keepScreen);
        return true;
    }
    //endregion

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {  // appelé par invalideOptionsMenu après changement d'orientation
        //updateDisplaySetClockAppAlarmOnStartTimerBarMenuItemIcon(setClockAppAlarmOnStartTimer);
        //updateDisplayKeepScreenBarMenuItemIcon(keepScreen);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.HELP) {
            //launchHelpActivity();
            return true;
        }
        if (item.getItemId() == R.id.ABOUT) {
            PackageInfo pInfo = null;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            String version = pInfo.versionName;   //Version Name
            int verCode = pInfo.versionCode;   //Version Code

            msgBox("Version: " + version, this);
            return true;
        }
        if (item.getItemId() == R.id.BAR_MENU_ITEM_KEEP_SCREEN) {
            keepScreen = !keepScreen;
            updateDisplayKeepScreen();
            updateDisplayKeepScreenBarMenuItemIcon(keepScreen);
        }
        return super.onOptionsItemSelected(item);
    }

    private void onButtonClick(KEYS key) {
        //msgBox(String.valueOf(currentKey.ID()), this);
        OPERATIONS op = null;   //  Restera null si fonction f ou g activée ou annulée

        if (mode != MODES.RUN) {
            switch (shiftMode) {
                case UNSHIFTED:
                    switch (key) {
                        case KEY_42:
                            swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());
                            shiftMode = SHIFT_MODES.F_SHIFT;
                            break;
                        case KEY_43:
                            swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());
                            shiftMode = SHIFT_MODES.G_SHIFT;
                            break;
                        default:
                            op = key.UNSHIFTED_OP();
                            break;
                    }
                    break;
                case F_SHIFT:
                    switch (key) {
                        case KEY_43:
                            swapColorBoxColors(buttons[KEYS.KEY_42.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());
                            swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());
                            shiftMode = SHIFT_MODES.G_SHIFT;
                            break;
                        case KEY_42:
                            swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());
                            shiftMode = SHIFT_MODES.UNSHIFTED;
                            break;
                        default:
                            swapColorBoxColors(buttons[KEYS.KEY_42.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());
                            shiftMode = SHIFT_MODES.UNSHIFTED;
                            op = key.SHIFT_F_OP();
                            break;
                    }
                    break;
                case G_SHIFT:
                    switch (key) {
                        case KEY_42:
                            swapColorBoxColors(buttons[KEYS.KEY_43.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());
                            swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());
                            shiftMode = SHIFT_MODES.F_SHIFT;
                            break;
                        case KEY_43:
                            swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());
                            shiftMode = SHIFT_MODES.UNSHIFTED;
                            break;
                        default:
                            swapColorBoxColors(buttons[KEYS.KEY_43.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());
                            shiftMode = SHIFT_MODES.UNSHIFTED;
                            op = key.SHIFT_G_OP();
                            break;
                    }
                    break;
                default:
                    break;
            }
            buttons[KEYS.KEY_42.INDEX()].updateDisplay();
            buttons[KEYS.KEY_43.INDEX()].updateDisplay();

            if (op != null) {   //  Pas Fonction Shift f ou g activée ou annulée
                if (op.equals(OPERATIONS.UNKNOWN)) {    //  Fonction non encore implémentée
                    msgBox("Function not implemented yet", this);
                } else {   //  Fonction déjà implémentée
                    keyOp(op);
                }
            }
        } else {   //  RUN
            keyOp(OPERATIONS.RS);   //  Pour STOP
        }
    }

    private void keyOp(OPERATIONS op) {   // Pas de switch car parfois l'opération est requalifiée dans cette procédure
        String disp;
        int indEex = 0;
        int indDot = 0;
        if (inOp == null) {
            if (mode.equals(MODES.EDIT)) {
                if (newProgLine) {
                    tProgLine = new ProgLine();
                }
            }
        }
        if (error.equals("")) {   //  Pas d'erreur (ou Prefix) antérieure
            if (inOp != null) {   //  Op en attente de paramètre (FIX, SCI, ENG, ...)
                if ((inOp.equals(OPERATIONS.FIX)) || (inOp.equals(OPERATIONS.SCI)) || (inOp.equals(OPERATIONS.ENG))) {
                    if (((op.INDEX() >= OPERATIONS.DIGIT_0.INDEX()) && (op.INDEX() <= OPERATIONS.DIGIT_9.INDEX()))
                            || (op.equals(alu.getKeyByOp(OPERATIONS.I).UNSHIFTED_OP()))) {   //  Chiffre (entre 0 et 9) ou contenu dans I (Touche TAN)
                        if (op.equals(alu.getKeyByOp(OPERATIONS.I).UNSHIFTED_OP())) {   //  I
                            op = OPERATIONS.I;
                        }
                        tProgLine.setOp(1, op);
                        if (canExecAfterHandling(tProgLine)) {
                            alphaToX();   //  Si on tape 5 puis 6 puis FIX 4 => On doit voir 56 avec 4 décimales
                            alu.setRoundMode(inOp);
                            int n = (op.equals(OPERATIONS.TAN) ? (int) alu.getRegContentsByIndex(BASE_REGS.RI.INDEX()) : Integer.valueOf(op.SYMBOL()));
                            alu.setRoundParam(n);
                        }
                        inOp = null;   //  Opération terminée
                        op = OPERATIONS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        //  FIX/SCI/ENG neutre sur stacklift
                    } else {   //  Opération mais pas chiffre ni I - On annule FIX/SCI/ENG et cette opération sera examinée plus loin dans cette procédure
                        inOp = null;
                    }
                }
                if (inOp != null) {
                    if (inOp.equals(OPERATIONS.STO)) {
                        if (((op.INDEX() >= OPERATIONS.DIGIT_0.INDEX()) && (op.INDEX() <= OPERATIONS.DIGIT_9.INDEX())) ||
                                (op.equals(alu.getKeyByOp(OPERATIONS.I).UNSHIFTED_OP())) || (op.equals(alu.getKeyByOp(OPERATIONS.INDI).UNSHIFTED_OP())) || (op.equals(OPERATIONS.DOT)) ||
                                (op.equals(OPERATIONS.RAND)) ||
                                (op.equals(OPERATIONS.PLUS)) || (op.equals(OPERATIONS.MINUS)) || (op.equals(OPERATIONS.MULT)) || (op.equals(OPERATIONS.DIV))) {  //  Eventuel +/-/*// puis Chiffre (entre 0 et 9) ou "." ou I (TAN)  ou "(i)" (COS) ou RAND

                            if ((op.equals(OPERATIONS.PLUS)) || (op.equals(OPERATIONS.MINUS)) || (op.equals(OPERATIONS.MULT)) || (op.equals(OPERATIONS.DIV))) {   //  +-*/ => On continue à attendre
                                tProgLine.setOp(1, op);
                                op = OPERATIONS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                            }
                            if (op.equals(OPERATIONS.DOT)) {   //  Normalement, on va continuer à attendre
                                if (tProgLine.getOp(1) != null) {   //  Le +-*/ ne peut suivre un "."
                                    inOp = null;
                                    error = ERROR_STO;
                                } else {   //  Un "." suit +-*/    OK
                                    tProgLine.setOp(2, op);
                                }
                                op = OPERATIONS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                            }
                            if (op.equals(OPERATIONS.RAND)) {   //  STO RAN #
                                tProgLine.setOp(1, op);
                                if (canExecAfterHandling(tProgLine)) {
                                    if (alphaToX()) {
                                        // NOP
                                        stackLiftEnabled = true;
                                    }
                                }
                                inOp = null;
                                op = OPERATIONS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                            }
                            if ((op.INDEX() >= OPERATIONS.DIGIT_0.INDEX()) && (op.INDEX() <= OPERATIONS.DIGIT_9.INDEX()) ||
                                    (op.equals(alu.getKeyByOp(OPERATIONS.I).UNSHIFTED_OP())) || (op.equals(alu.getKeyByOp(OPERATIONS.INDI).UNSHIFTED_OP()))) {   //  Chiffre ou I ou (i) => OK on peut enfin traiter;  (TAN: I) (COS: (i))

                                int index = -1;
                                if (op.equals(alu.getKeyByOp(OPERATIONS.I).UNSHIFTED_OP())) {   //  I
                                    op = OPERATIONS.I;
                                    index = BASE_REGS.RI.INDEX();
                                }
                                if (op.equals(alu.getKeyByOp(OPERATIONS.INDI).UNSHIFTED_OP())) {   //  (i))
                                    op = OPERATIONS.INDI;
                                    int dataIndex = (int) alu.getRegContentsByIndex(BASE_REGS.RI.INDEX());   //  Valeur dans I
                                    index = alu.getRegIndexByDataIndex(dataIndex);
                                    if ((index < 0) || (index > alu.getRegsMaxIndex())) {
                                        error = ERROR_INDEX;
                                    }
                                }
                                if ((op.INDEX() >= OPERATIONS.DIGIT_0.INDEX()) && (op.INDEX() <= OPERATIONS.DIGIT_9.INDEX())) {   //  Chiffre => Rn ou R.n
                                    String s = op.SYMBOL();
                                    if (tProgLine.getOp(2) != null) {   //  R.n
                                        s = OPERATIONS.DOT.SYMBOL() + s;
                                    }
                                    index = alu.getRegIndexBySymbol(s);   //
                                }
                                if (error.equals("")) {
                                    tProgLine.setOp(3, op);
                                    if (canExecAfterHandling(tProgLine)) {
                                        if (alphaToX()) {
                                            error = (tProgLine.getOp(1) != null ? alu.xToRegOp(index, tProgLine.getOp(1)) : alu.xToReg(index));
                                            if (error.equals("")) {
                                                stackLiftEnabled = true;
                                            }
                                        }
                                    }
                                }
                            }
                            inOp = null;
                            op = OPERATIONS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                    }
                }
            }
            if (inOp != null) {
                if (inOp.equals(OPERATIONS.RCL)) {
                    if (((op.INDEX() >= OPERATIONS.DIGIT_0.INDEX()) && (op.INDEX() <= OPERATIONS.DIGIT_9.INDEX())) ||
                            (op.equals(OPERATIONS.DIM)) || (op.equals(OPERATIONS.SIGMA_PLUS)) ||
                            (op.equals(alu.getKeyByOp(OPERATIONS.I).UNSHIFTED_OP())) || (op.equals(alu.getKeyByOp(OPERATIONS.INDI).UNSHIFTED_OP())) || (op.equals(OPERATIONS.DOT)) ||
                            (op.equals(OPERATIONS.PLUS)) || (op.equals(OPERATIONS.MINUS)) || (op.equals(OPERATIONS.MULT)) || (op.equals(OPERATIONS.DIV))) {  //  Eventuel +-*/ puis Chiffre (entre 0 et 9) ou "." ou I (TAN)  ou "(i)" (COS)

                        if ((op.equals(OPERATIONS.PLUS)) || (op.equals(OPERATIONS.MINUS)) || (op.equals(OPERATIONS.MULT)) || (op.equals(OPERATIONS.DIV))) {   //  +-*/ => On continue à attendre
                            tProgLine.setOp(1, op);
                            op = OPERATIONS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                        if (op.equals(OPERATIONS.DIM)) {   //  Attendre maintenant (i)
                            tProgLine.setOp(3, op);
                            op = OPERATIONS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                        if (op.equals(alu.getKeyByOp(OPERATIONS.INDI).UNSHIFTED_OP())) {   //  COS: (i), pour RCL DIM (i)
                            if (tProgLine.getOp(3) != null) {
                                if (tProgLine.getOp(3).equals(OPERATIONS.DIM)) {  //  RCL DIM (i) à traiter
                                    op = OPERATIONS.INDI;
                                    tProgLine.setOp(4, op);
                                    if (canExecAfterHandling(tProgLine)) {
                                        if (alphaToX()) {
                                            if (stackLiftEnabled) {
                                                alu.stackLift();
                                            }
                                            int n = alu.getRegsMaxIndex();
                                            alu.setStkRegContent(STK_REGS.X, alu.getDataRegIndexByIndex(n));
                                            stackLiftEnabled = true;
                                        }
                                    }
                                    inOp = null;
                                    op = OPERATIONS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                                }
                            }
                        }
                        if (op.equals(OPERATIONS.DOT)) {   //  Normalement, on va continuer à attendre
                            if (tProgLine.getOp(1) != null) {   //  Le +-*/ ne peut suivre un "."
                                inOp = null;
                                error = ERROR_RCL;
                            } else {   //  Un "." suit +-*/    OK
                                tProgLine.setOp(2, op);
                            }
                            op = OPERATIONS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                        if (op.equals(OPERATIONS.SIGMA_PLUS)) {   //  RCL SIGMA+
                            tProgLine.setOp(4, op);
                            if (canExecAfterHandling(tProgLine)) {
                                if (alphaToX()) {
                                    alu.stackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                                    if (stackLiftEnabled) {
                                        alu.stackLift();
                                    }
                                    error = alu.sumXYToXY();
                                    if (error.equals("")) {
                                        stackLiftEnabled = true;
                                    }
                                }
                            }
                            inOp = null;
                            op = OPERATIONS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                        if ((op.INDEX() >= OPERATIONS.DIGIT_0.INDEX()) && (op.INDEX() <= OPERATIONS.DIGIT_9.INDEX()) ||
                                (op.equals(alu.getKeyByOp(OPERATIONS.I).UNSHIFTED_OP())) || (op.equals(alu.getKeyByOp(OPERATIONS.INDI).UNSHIFTED_OP()))) {   //  Chiffre ou I ou (i) => OK on peut enfin traiter;  (TAN: I) (COS: (i))

                            if (tProgLine.getOp(3) == null) {   //  cad ne pas interférer avec RCL DIM (i)
                                int index = -1;
                                if (op.equals(alu.getKeyByOp(OPERATIONS.I).UNSHIFTED_OP())) { //  I
                                    op = OPERATIONS.I;
                                    index = BASE_REGS.RI.INDEX();
                                }
                                if (op.equals(alu.getKeyByOp(OPERATIONS.INDI).UNSHIFTED_OP())) { //  (i))
                                    op = OPERATIONS.INDI;
                                    int dataIndex = (int) alu.getRegContentsByIndex(BASE_REGS.RI.INDEX());   //  Valeur dans I
                                    index = alu.getRegIndexByDataIndex(dataIndex);
                                    if ((index < 0) || (index > alu.getRegsMaxIndex())) {
                                        error = ERROR_INDEX;
                                    }
                                }
                                if ((op.INDEX() >= OPERATIONS.DIGIT_0.INDEX()) && (op.INDEX() <= OPERATIONS.DIGIT_9.INDEX())) {   //  Chiffre => Rn ou R.n
                                    String s = op.SYMBOL();
                                    if (tProgLine.getOp(2) != null) {   //  R.n
                                        s = OPERATIONS.DOT.SYMBOL() + s;
                                    }
                                    index = alu.getRegIndexBySymbol(s);   //
                                }
                                if (error.equals("")) {
                                    tProgLine.setOp(4, op);
                                    if (canExecAfterHandling(tProgLine)) {
                                        if (alphaToX()) {
                                            if (stackLiftEnabled) {
                                                alu.stackLift();
                                            }
                                            error = (tProgLine.getOp(1) != null ? alu.regToXOp(index, tProgLine.getOp(1)) : alu.regToX(index));
                                            if (error.equals("")) {
                                                stackLiftEnabled = true;
                                            }
                                        }
                                    }
                                }
                                inOp = null;
                                op = OPERATIONS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                            }
                        }
                    }
                }
            }
            if (inOp != null) {
                if (inOp.equals(OPERATIONS.DIM)) {   //  DIM (i)
                    if (op.equals(alu.getKeyByOp(OPERATIONS.INDI).UNSHIFTED_OP())) {   //  COS: (i) était attendu après DIM
                        op = OPERATIONS.INDI;
                        tProgLine.setOp(1, op);
                        if (canExecAfterHandling(tProgLine)) {
                            alphaToX();   //  Si on tape 5 puis 6 puis DIM (i) => On doit voir 56
                            int n = (int) alu.getStkRegContents(STK_REGS.X);
                            error = alu.setDataRegsSize(n + 1);   //  n=max Data Index
                            if (error.equals("")) {
                                stackLiftEnabled = true;
                            } else {   //  Erreur
                                error = "RANGE 0-" + alu.getDataRegIndexByIndex(alu.getRegsAbsoluteSizeMax() - 1);
                            }
                        }
                        inOp = null;   //  Opération terminée
                        op = OPERATIONS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        //  FIX/SCI/ENG neutre sur stacklift
                    } else {   //  Opération mais pas chiffre ni I - On annule FIX/SCI/ENG et cette opération sera examinée plus loin dans cette procédure
                        inOp = null;
                    }
                }
            }
            if (inOp != null) {
                if (inOp.equals(OPERATIONS.XCHG)) {
                    if (((op.INDEX() >= OPERATIONS.DIGIT_0.INDEX()) && (op.INDEX() <= OPERATIONS.DIGIT_9.INDEX())) ||
                            (op.equals(alu.getKeyByOp(OPERATIONS.I).UNSHIFTED_OP())) || (op.equals(alu.getKeyByOp(OPERATIONS.INDI).UNSHIFTED_OP())) || (op.equals(OPERATIONS.DOT))) {  //  Chiffre (entre 0 et 9) ou "." ou I (TAN)  ou "(i)" (COS)

                        if (op.equals(OPERATIONS.DOT)) {   //  Normalement, on va continuer à attendre (un chiffre)
                            tProgLine.setOp(1, op);
                            op = OPERATIONS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                        if ((op.INDEX() >= OPERATIONS.DIGIT_0.INDEX()) && (op.INDEX() <= OPERATIONS.DIGIT_9.INDEX()) ||
                                (op.equals(alu.getKeyByOp(OPERATIONS.I).UNSHIFTED_OP())) || (op.equals(alu.getKeyByOp(OPERATIONS.INDI).UNSHIFTED_OP()))) {   //  Chiffre ou I ou (i) => OK on peut enfin traiter;  (TAN: I) (COS: (i))

                            int index = -1;
                            if (op.equals(alu.getKeyByOp(OPERATIONS.I).UNSHIFTED_OP())) { //  X<>I
                                op = OPERATIONS.I;
                                index = BASE_REGS.RI.INDEX();
                            }
                            if (op.equals(alu.getKeyByOp(OPERATIONS.INDI).UNSHIFTED_OP())) { //  X<>(i))
                                op = OPERATIONS.INDI;
                                int dataIndex = (int) alu.getRegContentsByIndex(BASE_REGS.RI.INDEX());   //  Valeur dans I
                                index = alu.getRegIndexByDataIndex(dataIndex);
                                if ((index < 0) || (index > alu.getRegsMaxIndex())) {
                                    error = ERROR_INDEX;
                                }
                            }
                            if ((op.INDEX() >= OPERATIONS.DIGIT_0.INDEX()) && (op.INDEX() <= OPERATIONS.DIGIT_9.INDEX())) {   //  Chiffre => X<>n ou X<>.n
                                String s = op.SYMBOL();
                                if (tProgLine.getOp(1) != null) {   //  .n
                                    s = OPERATIONS.DOT.SYMBOL() + s;
                                }
                                index = alu.getRegIndexBySymbol(s);   //
                            }
                            if (error.equals("")) {
                                tProgLine.setOp(2, op);
                                if (canExecAfterHandling(tProgLine)) {
                                    if (alphaToX()) {
                                        error = alu.xXchgReg(index);
                                        if (error.equals("")) {
                                            stackLiftEnabled = true;
                                        }
                                    }
                                }
                            }
                            inOp = null;
                            op = OPERATIONS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                    }
                }
            }
            if (inOp != null) {
                if ((inOp.equals(OPERATIONS.HYP)) || (inOp.equals(OPERATIONS.AHYP))) {
                    switch (inOp) {
                        case HYP:
                            switch (op) {   //  Opération à requalifier et examiner plus bas
                                case SIN:
                                    op = OPERATIONS.SINH;
                                    break;
                                case COS:
                                    op = OPERATIONS.COSH;
                                    break;
                                case TAN:
                                    op = OPERATIONS.TANH;
                                    break;
                            }
                        case AHYP:
                            switch (op) {   //  Opération à requalifier et examiner plus bas
                                case SIN:
                                    op = OPERATIONS.ASINH;
                                    break;
                                case COS:
                                    op = OPERATIONS.ACOSH;
                                    break;
                                case TAN:
                                    op = OPERATIONS.ATANH;
                                    break;
                            }
                    }
                    inOp = null;
                }
            }
            if (inOp == null) {   //  Pas d'opération en attente de paramètre
                alu.clearProgLine(tProgLine);
                tProgLine.setOp(0, op);
                if (((op.INDEX() >= OPERATIONS.DIGIT_0.INDEX()) && (op.INDEX() <= OPERATIONS.DIGIT_9.INDEX())) ||
                        (op.equals(OPERATIONS.DOT)) || (op.equals(OPERATIONS.EEX)) || (op.equals(OPERATIONS.CHS))) {

                    if (!mode.equals(MODES.EDIT)) {   //  NORM ou RUN
                        if (alpha.equals("")) {   // Début d'entrée de nombre
                            if (!op.equals(OPERATIONS.CHS)) {  //  StackLift éventuel uniquement si entrée d'un nombre (ne commençant pas par "-")
                                if (stackLiftEnabled) {
                                    alu.stackLift();
                                }
                            }
                        }
                        boolean acceptOp = true;
                        indEex = alpha.indexOf(OPERATIONS.EEX.SYMBOL());
                        if (indEex != (-1)) {   //  Ne plus accepter de "." ou "E" après un "E" antérieur
                            if ((op.equals(OPERATIONS.DOT)) || (op.equals(OPERATIONS.EEX))) {
                                acceptOp = false;
                            }
                        }
                        indDot = alpha.indexOf(OPERATIONS.DOT.SYMBOL());
                        if (indDot != (-1)) {   //  Ne plus accepter de "." après un "." antérieur
                            if (op.equals(OPERATIONS.DOT)) {
                                acceptOp = false;
                            }
                        }
                        if (acceptOp) {
                            if (op.equals(OPERATIONS.CHS)) {
                                if (alpha.equals("")) {   //  Un nombre ne peut commencer par "-" => Changer le signe de X
                                    alu.negX();
                                    stackLiftEnabled = true;
                                } else {   //  Entrée de nombre en cours
                                    int indChs1 = alpha.indexOf(op.SYMBOL());   //  Un "-" existe peut-être déjà => -x ou xE-x ou -xEx ou -xE-x
                                    int indChs2 = -1;
                                    if ((indChs1 != -1) && (indChs1 < (alpha.length() - 1))) {   //  Un 2e "-" est possible   => -xE-x
                                        indChs2 = alpha.indexOf(op.SYMBOL(), indChs1 + 1);   //  après le 1er
                                    }
                                    if (indChs1 != (-1)) {   //   -x ou xE-x ou -xEx ou -xE-x
                                        if (indEex != -1) {   //  xE-x ou -xEx ou -xE-x
                                            if (indChs1 < indEex) {   //  -xEx ou -xE-x
                                                if (indChs2 != -1) {   //  -xE-x
                                                    alpha = alpha.substring(0, indChs2) + alpha.substring(indChs2 + 1);   //  => -xEx
                                                } else {   //  -xEx
                                                    alpha = alpha.substring(0, indEex + 1) + op.SYMBOL() + alpha.substring(indEex + 1);   //  => -xE-x
                                                }
                                            } else {   //  xE-x
                                                alpha = alpha.substring(0, indChs1) + alpha.substring(indChs1 + 1);   //  => xEx
                                            }
                                        } else {   //  -x
                                            alpha = alpha.substring(indChs1 + 1);   //  => x
                                        }
                                    } else {   //  x ou xEx
                                        if (indEex != (-1)) {   //  xEx
                                            alpha = alpha.substring(0, indEex + 1) + op.SYMBOL() + alpha.substring(indEex + 1);   //  => xE-x
                                        } else {   //  x
                                            alpha = op.SYMBOL() + alpha;   //  => -x
                                        }
                                    }
                                }
                            } else {   //  Pas CHS
                                String s = op.SYMBOL();
                                if (alpha.equals("")) {
                                    if (op.equals(OPERATIONS.EEX)) {
                                        s = OPERATIONS.DIGIT_1.SYMBOL() + s;   //  Ajout de 1 en préfixe si commence par "E"
                                    }
                                    if (op.equals(OPERATIONS.DOT)) {
                                        s = OPERATIONS.DIGIT_0.SYMBOL() + s;   //  Ajout de 0 en préfixe si commence par "."
                                    }
                                }
                                alpha = alpha + s;
                            }
                        }
                    } else {   //  EDIT
                        if (canExecAfterHandling(tProgLine)) {
                            //  KEEP IT
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.BACK)) {    // Désactive stacklift
                if (!mode.equals(MODES.EDIT)) {   //  NORM ou RUN
                    if (alpha.length() >= 2) {
                        alpha = alpha.substring(0, alpha.length() - 1);   //  Enlever le dernier caractère
                        if (alpha.equals(OPERATIONS.CHS.SYMBOL())) {   //  p.ex. "-0.3" -> "-0." -> "-0" -> "-" : Boum  (cf String.format plus bas, sur la partie avant le "E" et le ".")
                            alu.clX();
                            stackLiftEnabled = false;
                            alpha = "";
                        }
                    } else {   //  alpha a 0 ou 1 caractères
                        alu.clX();
                        stackLiftEnabled = false;
                        alpha = "";
                    }
                } else {   //  EDIT
                    alu.removeProgLineAtNumber(currentProgLineNumber);
                    currentProgLineNumber = currentProgLineNumber - 1;
                }
            }
            if (op.equals(OPERATIONS.CLX)) {   // Désactive stacklift
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.clX();
                        if (error.equals("")) {
                            stackLiftEnabled = false;
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.PI)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        if (stackLiftEnabled) {
                            alu.stackLift();
                        }
                        error = alu.piToX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.LASTX)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        if (stackLiftEnabled) {
                            alu.stackLift();
                        }
                        error = alu.lastXToX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.RAND)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        if (stackLiftEnabled) {
                            alu.stackLift();
                        }
                        error = alu.randToX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.CLEAR_PREFIX)) {   //  Neutre sur stackLift
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.prefX();  //  error sera <>"" dans tous les cas (car représentera la mantisse) et sera donc traité comme une erreur
                    }
                }
            }
            if (op.equals(OPERATIONS.CLEAR_REGS)) {   //  Neutre sur stackLift
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.clRegs();
                    }
                }
            }
            if (op.equals(OPERATIONS.CLEAR_SIGMA)) {   //  Neutre sur stackLift
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.clStats();
                        alu.stackClear();
                    }
                }
            }
            if (op.equals(OPERATIONS.SIGMA_PLUS)) {   //  Désactive stackLift
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.sigmaPlus();
                        if (error.equals("")) {
                            stackLiftEnabled = false;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.SIGMA_MINUS)) {   //  Désactive stacklift
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.sigmaMinus();
                        if (error.equals("")) {
                            stackLiftEnabled = false;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.MEAN)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        alu.saveStack();
                        alu.stackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                        if (stackLiftEnabled) {
                            alu.stackLift();
                        }
                        error = alu.mean();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.restoreStack();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.STDEV)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        alu.saveStack();
                        alu.stackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                        if (stackLiftEnabled) {
                            alu.stackLift();
                        }
                        error = alu.stDev();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.restoreStack();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.LR)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        alu.saveStack();
                        alu.stackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                        if (stackLiftEnabled) {
                            alu.stackLift();
                        }
                        error = alu.lr();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.restoreStack();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.YER)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        alu.saveStack();
                        alu.stackLift();    //  Un stacklift obligatoire
                        error = alu.yer();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.restoreStack();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.SQR)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.sqrX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.SQRT)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.sqrtX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.TO_RAD)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.xToRad();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.TO_DEG)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.xToDeg();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.EXP)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.expX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.LN)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.lnX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.EXP10)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.exp10X();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.LOG)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.logX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.POWER)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.pow();
                        if (error.equals("")) {
                            alu.stackMergeDown();
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.PC)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.xPcY();   //  Pas de mergeDown
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.INV)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.invX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.DPC)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.xDpcY();   //  Pas de mergeDown
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.ABS)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.absX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.RND)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.rndX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.POL)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.xyToPol();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.RECT)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.xyToRect();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.HMS)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.hmsX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.H)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.hX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.COMB)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.xyToComb();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.PERM)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.xyToPerm();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.FRAC)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.fracX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.INT)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.intX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.SIN)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.sinX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.COS)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.cosX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.TAN)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.tanX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.ASIN)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.asinX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.ACOS)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.acosX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.ATAN)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.atanX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.SINH)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.sinhX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.COSH)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.coshX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.TANH)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.tanhX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.ASINH)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.asinhX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.ACOSH)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.acoshX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.ATANH)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.atanhX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.FACT)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.factX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.ENTER)) {   // Désactive Stacklift
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        alu.stackLift();
                        stackLiftEnabled = false;
                    }
                }
            }
            if (op.equals(OPERATIONS.RDN)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        alu.stackRollDown();
                        stackLiftEnabled = true;
                    }
                }
            }
            if (op.equals(OPERATIONS.RUP)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        alu.stackRollUp();
                        stackLiftEnabled = true;
                    }
                }
            }
            if (op.equals(OPERATIONS.XCHGXY)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        alu.xchgXY();
                        stackLiftEnabled = true;
                    }
                }
            }
            if (op.equals(OPERATIONS.PLUS)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.yPlusX();
                        if (error.equals("")) {
                            alu.stackMergeDown();
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.MINUS)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.yMinusX();
                        if (error.equals("")) {
                            alu.stackMergeDown();
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if (op.equals(OPERATIONS.MULT)) {
                if (alphaToX()) {
                    error = alu.yMultX();
                    if (error.equals("")) {
                        alu.stackMergeDown();
                        stackLiftEnabled = true;
                    } else {
                        alu.lastXToX();
                    }
                }
            }
            if (op.equals(OPERATIONS.DIV)) {
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        error = alu.yDivX();
                        if (error.equals("")) {
                            alu.stackMergeDown();
                            stackLiftEnabled = true;
                        } else {
                            alu.lastXToX();
                        }
                    }
                }
            }
            if ((op.equals(OPERATIONS.DEG)) || (op.equals(OPERATIONS.RAD)) || (op.equals(OPERATIONS.GRAD))) {   //  Neutre sur stackLift
                if (canExecAfterHandling(tProgLine)) {
                    if (alphaToX()) {
                        alu.setAngleMode(op);
                    }
                }
            }
            if (op.equals(OPERATIONS.BEGIN)) {
                if (mode.equals(MODES.RUN)) {
                    error = ERROR_NO_PROG;
                    mode = MODES.NORM;
                    newProgLine = true;
                }
            }
            if (op.equals(OPERATIONS.PR)) {
                if (mode.equals(MODES.NORM)) {   //  NORM -> EDIT
                    mode = MODES.EDIT;
                    newProgLine = false;
                    tProgLine = alu.getProgLine(currentProgLineNumber);
                    feedOps(tProgLine);
                    newProgLine = true;
                }
                if (mode.equals(MODES.EDIT)) {   //  EDIT -> NORM
                    mode = MODES.NORM;
                    newProgLine = true;
                }
            }
            if (op.equals(OPERATIONS.RS)) {
                if (mode.equals(MODES.NORM)) {   //  NORM -> RUN
                    mode = MODES.RUN;
                    newProgLine = false;
                    tProgLine = alu.getProgLine(currentProgLineNumber);
                    feedOps(tProgLine);
                    newProgLine = true;
                }
                if (mode.equals(MODES.RUN)) {   //  RUN -> NORM
                    mode = MODES.NORM;
                    newProgLine = true;
                }
            }
            if (op.equals(OPERATIONS.SST)) {   //  END -> BEGIN
                if ((mode.equals(MODES.NORM)) || (mode.equals(MODES.EDIT))) {
                    newProgLine = false;
                    currentProgLineNumber = currentProgLineNumber + 1;
                    if (currentProgLineNumber > (alu.getProgLinesSize() - 1)) {
                        currentProgLineNumber = 0;
                    }
                    tProgLine = alu.getProgLine(currentProgLineNumber);
                    feedOps(tProgLine);
                    newProgLine = true;
                }
            }
            if (op.equals(OPERATIONS.BST)) {   //  BEGIN -> END
                if ((mode.equals(MODES.NORM)) || (mode.equals(MODES.EDIT))) {
                    newProgLine = false;
                    currentProgLineNumber = currentProgLineNumber - 1;
                    if (currentProgLineNumber < 0) {
                        currentProgLineNumber = alu.getProgLinesSize() - 1;
                    }
                    tProgLine = alu.getProgLine(currentProgLineNumber);
                    feedOps(tProgLine);
                    newProgLine = true;
                }
            }
            if ((op.equals(OPERATIONS.HYP)) || (op.equals(OPERATIONS.AHYP))) {
                inOp = op;   //  Attente de paramètre (SIN, COS, TAN)
            }
            if ((op.equals(OPERATIONS.FIX)) || (op.equals(OPERATIONS.SCI)) || (op.equals(OPERATIONS.ENG))) {
                inOp = op;
            }
            if ((op.equals(OPERATIONS.STO)) || (op.equals(OPERATIONS.RCL)) || (op.equals(OPERATIONS.XCHG))) {
                inOp = op;
            }
            if (op.equals(OPERATIONS.DIM)) {
                inOp = op;
            }
            if (error.equals("")) {   //  Pas d'erreur nouvelle
                if (mode.equals(MODES.NORM)) {
                    if (alpha.equals("")) {   //  Pas d'entrée de nombre en cours
                        disp = alu.getRoundXForDisplay();
                    } else {   //  Entrée de nombre en cours => faire apparaître le séparateur de milliers
                        int indMax = alpha.length();   //  Faire apparaître le séparateur de milliers au cours de l'entrée de nombre, avant le 1er "." ou "E"
                        int indMin = 0;
                        indDot = alpha.indexOf(OPERATIONS.DOT.SYMBOL());
                        if (indDot != (-1)) {   //  "." détecté
                            indMax = Math.min(indMax, indDot);
                        }
                        indEex = alpha.indexOf(OPERATIONS.EEX.SYMBOL());
                        if (indEex != (-1)) {   //  "E" détecté
                            indMax = Math.min(indMax, indEex);
                        }
                        indMin = (alpha.substring(0, 1).equals(OPERATIONS.CHS.SYMBOL()) ? 1 : 0);   //  Tenir compte du "-" initial éventuel
                        String s = String.format(Locale.US, "%,d", Integer.parseInt(alpha.substring(indMin, indMax)));   //  Séparateur de milliers
                        if (indMin != 0) {
                            s = OPERATIONS.CHS.SYMBOL() + s;   //  Ramener le "-" initial éventuel
                        }
                        if (indMax < alpha.length()) {
                            s = s + alpha.substring(indMax);   //  Le reste
                        }
                        disp = s;
                    }
                    dotMatrixDisplayUpdater.displayText(disp, true);
                }
                if (mode.equals(MODES.EDIT)) {
                    if (inOp == null) {   //  Ligne terminée (déjà enregistrée dans progLines)
                        disp = alu.progLineToString(currentProgLineNumber, displaySymbol);
                        dotMatrixDisplayUpdater.displayText(disp, true);
                    }
                }
                if (mode.equals(MODES.RUN)) {
                    newProgLine = false;
                    tProgLine = alu.getProgLine(currentProgLineNumber);
                    feedOps(tProgLine);
                }
            } else {    //  Erreur (ou Prefix) nouvelle
                if (mode.equals(MODES.RUN)) {
                    mode = MODES.NORM;
                    newProgLine = true;
                }
                dotMatrixDisplayUpdater.displayText(error, false);
                alpha = "";
            }
        } else {   //  Erreur (ou Prefix) antérieure
            error = "";
            inOp = null;
            if (mode.equals(MODES.NORM)) {
                disp = alu.getRoundXForDisplay();
                dotMatrixDisplayUpdater.displayText(disp, true);
            }
            if (mode.equals(MODES.EDIT)) {
                disp = alu.progLineToString(currentProgLineNumber, displaySymbol);
                dotMatrixDisplayUpdater.displayText(disp, true);
            }
        }
        dotMatrixDisplayView.updateDisplay();
        updateSideDisplay();
    }

    private boolean alphaToX() {
        boolean res = true;
        if (!alpha.equals("")) {  //  //  Entrée de nombre en cours, à valider et terminer
            String error = alu.aToX(alpha);
            if (error.equals("")) {
                alpha = "";
                stackLiftEnabled = true;
            } else {   //  Erreur
                res = false;
            }
        }
        return res;
    }

    public boolean canExecAfterHandling(ProgLine progLine) {
        boolean res = true;   //  Exécuter la ligne
        if (mode.equals(MODES.RUN)) {
            currentProgLineNumber = currentProgLineNumber + 1;   //  Par défaut
            if (currentProgLineNumber > (alu.getProgLinesSize() - 1)) {
                currentProgLineNumber = 0;
            }
        }
        if (mode.equals(MODES.EDIT)) {   //  EDIT
            if (newProgLine) {   //  Créer une nouvelle ligne
                currentProgLineNumber = currentProgLineNumber + 1;
                alu.addProgLineAtNumber(progLine, currentProgLineNumber);
                res = false;   //  Ne pas exécuter la ligne
            }
        }
        return res;
    }

    private void feedOps(ProgLine progLine) {
        int n = progLine.getOpsSize();
        for (int i = 0; i <= (n - 1); i = i + 1) {
            OPERATIONS op = progLine.getOp(i);
            if (op != null) {
                keyOp(op);
            }
        }
    }

    private void updateSideDisplay() {
        sideDotMatrixDisplayUpdater.displayText(alu.getAngleMode().toString().toLowerCase() + " " + alu.getRoundMode().toString().toLowerCase() + alu.getRoundParam(), false);
        sideDotMatrixDisplayView.updateDisplay();
    }

    private void swapColorBoxColors(ColorBox colorBox, int index1, int index2) {
        String color1 = colorBox.getColor(index1).RGBString;
        colorBox.setColor(index1, colorBox.getColor(index2).RGBString);
        colorBox.setColor(index2, color1);
    }

    private void updateDisplayDotMatrixColors() {
        String[] colors = {"000000", "8F8100", "A49300"};    // ON, OFF, BACK

        dotMatrixDisplayUpdater.setColors(colors);
        dotMatrixDisplayUpdater.rebuildStructure();
        dotMatrixDisplayView.updateDisplay();
    }

    private void updateSideDotMatrixColors() {
        String[] colors = {"000000", "8F8100", "A49300"};    // ON, OFF, BACK

        sideDotMatrixDisplayUpdater.setColors(colors);
        sideDotMatrixDisplayUpdater.rebuildStructure();
        sideDotMatrixDisplayView.updateDisplay();
    }

    private void updateDisplayButtonColors() {
        for (KEYS key : KEYS.values()) {
            for (LEGEND_POS legendPos : LEGEND_POS.values()) {
                if (((!key.equals(KEYS.KEY_41)) && (!key.equals(KEYS.KEY_42)) && (!key.equals(KEYS.KEY_43))) || (legendPos.equals(LEGEND_POS.MID))) {
                    updateDisplayButtonColor(key);
                }
            }
        }
    }

    private void updateDisplayButtonColor(KEYS key) {
        final String MID_COLOR_FRONT_UNPRESSED = "FFFFFF";
        final String TOP_COLOR_FRONT_UNPRESSED = "EEBD34";
        final String LOW_COLOR_FRONT_UNPRESSED = "A1BBFF";
        final String K42_COLOR_BACK_UNPRESSED = "EEBD34";
        final String K43_COLOR_BACK_UNPRESSED = "A1BBFF";
        final String MID_COLOR_BACK_UNPPRESSED = "606060";
        final String LOW_COLOR_BACK_UNPPRESSED = "404040";
        final String TOP_K32_TO_K35_COLOR_FRONT_UNPRESSED = "FF0000";   //  Pour les touches "CLEAR"
        final String BACKGROUND_COLOR = "303030";
        final String OUTLINE_COLOR_UNPRESSED = "303030";
        final String OUTLINE_COLOR_PRESSED = "FF9A22";
        final String BACK_SCREEN_COLOR = "303030";

        ColorBox[] imageColorBoxes = buttons[key.INDEX()].getImageColorBoxes();
        for (LEGEND_POS legendPos : LEGEND_POS.values()) {
            switch (legendPos) {
                case MID:
                    switch (key) {
                        case KEY_42:
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), BACKGROUND_COLOR);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX(), K42_COLOR_BACK_UNPRESSED);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX(), BACKGROUND_COLOR);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX(), K42_COLOR_BACK_UNPRESSED);
                            break;
                        case KEY_43:
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), BACKGROUND_COLOR);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX(), K43_COLOR_BACK_UNPRESSED);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX(), BACKGROUND_COLOR);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX(), K43_COLOR_BACK_UNPRESSED);
                            break;
                        default:
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), MID_COLOR_FRONT_UNPRESSED);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX(), MID_COLOR_BACK_UNPPRESSED);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX(), MID_COLOR_FRONT_UNPRESSED);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX(), MID_COLOR_BACK_UNPPRESSED);
                            break;
                    }
                    break;
                case TOP:
                    if ((key.equals(KEYS.KEY_32)) || (key.equals(KEYS.KEY_33)) || (key.equals(KEYS.KEY_34)) || (key.equals(KEYS.KEY_35))) {   //  Touches CLEAR
                        imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), TOP_K32_TO_K35_COLOR_FRONT_UNPRESSED);
                        imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX(), BACKGROUND_COLOR);
                        imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX(), TOP_K32_TO_K35_COLOR_FRONT_UNPRESSED);
                        imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX(), BACKGROUND_COLOR);
                    } else {   //  Pas les touches CLEAR
                        imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), TOP_COLOR_FRONT_UNPRESSED);
                        imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX(), BACKGROUND_COLOR);
                        imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX(), TOP_COLOR_FRONT_UNPRESSED);
                        imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX(), BACKGROUND_COLOR);
                    }
                    break;
                case LOW:
                    imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), LOW_COLOR_FRONT_UNPRESSED);
                    imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX(), LOW_COLOR_BACK_UNPPRESSED);
                    imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX(), LOW_COLOR_FRONT_UNPRESSED);
                    imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX(), LOW_COLOR_BACK_UNPPRESSED);
                    break;
            }
        }

        ColorBox keyColorBox = buttons[key.INDEX()].getKeyColorBox();
        keyColorBox.setColor(BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), OUTLINE_COLOR_UNPRESSED);
        keyColorBox.setColor(BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX(), OUTLINE_COLOR_PRESSED);
        keyColorBox.setColor(BUTTON_COLOR_TYPES.BACK_SCREEN.INDEX(), BACK_SCREEN_COLOR);

        buttons[key.INDEX()].updateDisplay();
    }

    private void updateDisplaySetClockAppAlarmOnStartTimerBarMenuItemIcon(
            boolean setClockAppAlarmOnStartTimer) {
        barMenuItemSetClockAppAlarmOnStartTimer.setIcon((setClockAppAlarmOnStartTimer ? R.drawable.main_bell_start_on : R.drawable.main_bell_start_off));
    }

    private void updateDisplayKeepScreenBarMenuItemIcon(boolean keepScreen) {
        barMenuItemKeepScreen.setIcon((keepScreen ? R.drawable.main_light_on : R.drawable.main_light_off));
    }

    private void updateDisplayKeepScreen() {
        if (keepScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void savePreferences() {
        SharedPreferences shp = getSharedPreferences(shpFileName, MODE_PRIVATE);
        SharedPreferences.Editor shpEditor = shp.edit();
        shpEditor.putBoolean(SWTIMER_SHP_KEY_NAMES.KEEP_SCREEN.toString(), keepScreen);
        shpEditor.commit();
    }

    private boolean getSHPShowExpirationTime() {
        final boolean SHOW_EXPIRATION_TIME_DEFAULT_VALUE = false;

        SharedPreferences shp = getSharedPreferences(shpFileName, MODE_PRIVATE);
        return shp.getBoolean(SWTIMER_SHP_KEY_NAMES.SHOW_EXPIRATION_TIME.toString(), SHOW_EXPIRATION_TIME_DEFAULT_VALUE);
    }

    private boolean getSHPKeepScreen() {
        final boolean KEEP_SCREEN_DEFAULT_VALUE = false;

        SharedPreferences shp = getSharedPreferences(shpFileName, MODE_PRIVATE);
        return shp.getBoolean(SWTIMER_SHP_KEY_NAMES.KEEP_SCREEN.toString(), KEEP_SCREEN_DEFAULT_VALUE);
    }

    private void setupAlu() {
        alu = new Alu();
    }

    private void setupButtons() {
        final String KEY_FILE_PREFIX = "k";
        final String KEY_FILE_SUFFIX = "_";
        final long BUTTON_MIN_CLICK_TIME_INTERVAL_MS = 500;
        final int BUTTON_TOP_IMAGE_HEIGHT_WEIGHT = 1;
        final int BUTTON_MID_IMAGE_HEIGHT_WEIGHT = 2;
        final int BUTTON_LOW_IMAGE_HEIGHT_WEIGHT = 1;
        final float BUTTON_TOP_IMAGE_SIZE_COEFF = 0.7f;
        final float BUTTON_MID_IMAGE_SIZE_COEFF = 0.65f;
        final float BUTTON_LOW_IMAGE_SIZE_COEFF = 0.6f;
        final float BUTTON_MID_23_IMAGE_SIZE_COEFF = 0.55f;
        final float BUTTON_MID_25_IMAGE_SIZE_COEFF = 0.55f;
        final float BUTTON_MID_30_IMAGE_SIZE_COEFF = 0.40f;
        final float BUTTON_MID_31_IMAGE_SIZE_COEFF = 0.6f;
        final float BUTTON_MID_33_IMAGE_SIZE_COEFF = 0.5f;
        final float BUTTON_MID_35_IMAGE_SIZE_COEFF = 0.5f;
        final float BUTTON_MID_36_IMAGE_SIZE_COEFF = 0.8f;
        final float BUTTON_MID_48_IMAGE_SIZE_COEFF = 0.3f;

        buttons = new ImageButtonViewStack[KEYS.values().length];
        Class rid = R.id.class;
        for (KEYS key : KEYS.values()) {
            try {
                buttons[key.INDEX()] = findViewById(rid.getField(key.toString()).getInt(rid));
                buttons[key.INDEX()].setOutlineStrokeWidthDp(2);
                buttons[key.INDEX()].setPcBackCornerRadius(0);
                buttons[key.INDEX()].setImageCount(LEGEND_POS.values().length);
                for (LEGEND_POS legendPos : LEGEND_POS.values()) {
                    if (((!key.equals(KEYS.KEY_41)) && (!key.equals(KEYS.KEY_42)) && (!key.equals(KEYS.KEY_43))) || (legendPos.equals(LEGEND_POS.MID))) {
                        String fileName = KEY_FILE_PREFIX + key.CODE() + KEY_FILE_SUFFIX + legendPos.toString().toLowerCase();
                        int svgResId = getResources().getIdentifier(fileName, "raw", getPackageName());
                        buttons[key.INDEX()].setSVGImageResource(legendPos.INDEX(), svgResId);
                        float imageSizeCoeff = 0;
                        int heightWeight = 0;
                        switch (legendPos) {
                            case TOP:
                                imageSizeCoeff = BUTTON_TOP_IMAGE_SIZE_COEFF;
                                heightWeight = BUTTON_TOP_IMAGE_HEIGHT_WEIGHT;
                                break;
                            case MID:
                                imageSizeCoeff = BUTTON_MID_IMAGE_SIZE_COEFF;
                                heightWeight = BUTTON_MID_IMAGE_HEIGHT_WEIGHT;
                                break;
                            case LOW:
                                imageSizeCoeff = BUTTON_LOW_IMAGE_SIZE_COEFF;
                                heightWeight = BUTTON_LOW_IMAGE_HEIGHT_WEIGHT;
                                break;
                        }
                        if (key.equals(KEYS.KEY_36)) {   // Cas particulier de la touche ENTER
                            switch (legendPos) {
                                case TOP:
                                    heightWeight = 2;
                                    break;
                                case MID:
                                    heightWeight = 14;
                                    break;
                                case LOW:
                                    heightWeight = 2;
                                    break;
                            }
                        }
                        buttons[key.INDEX()].setHeightWeight(legendPos.INDEX(), heightWeight);
                        if ((key.equals(KEYS.KEY_23)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image SIN à adapter
                            imageSizeCoeff = BUTTON_MID_23_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_25)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image TAN à adapter
                            imageSizeCoeff = BUTTON_MID_25_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_30)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image Minus à adapter
                            imageSizeCoeff = BUTTON_MID_30_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_31)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image RDN à adapter
                            imageSizeCoeff = BUTTON_MID_31_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_33)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image RDN à adapter
                            imageSizeCoeff = BUTTON_MID_33_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_35)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image BACK à adapter
                            imageSizeCoeff = BUTTON_MID_35_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_36)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image Enter à adapter
                            imageSizeCoeff = BUTTON_MID_36_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_48)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image Dot à adapter
                            imageSizeCoeff = BUTTON_MID_48_IMAGE_SIZE_COEFF;
                        }
                        buttons[key.INDEX()].setImageSizeCoeff(legendPos.INDEX(), imageSizeCoeff);
                        final KEYS fkey = key;
                        buttons[key.INDEX()].setOnCustomClickListener(new ImageButtonViewStack.onCustomClickListener() {
                            @Override
                            public void onCustomClick() {
                                onButtonClick(fkey);
                            }
                        });
                    } else {   //  ON, f ou g => Seul le MID est visible
                        buttons[key.INDEX()].setImageVisibilities(legendPos.INDEX(), false);
                    }
                }
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex) {
                Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //layoutButtonsOnSelection = findViewById(R.id.LAY_BUTTONS_ON_SELECTION);
    }

    private void setupDotMatrixDisplayUpdater() {
        dotMatrixDisplayUpdater = new CalcDotMatrixDisplayUpdater(dotMatrixDisplayView);
    }

    private void setupSideDotMatrixDisplayUpdater() {
        sideDotMatrixDisplayUpdater = new SideDotMatrixDisplayUpdater(sideDotMatrixDisplayView);
    }

    private void setupDotMatrixDisplay() {
        dotMatrixDisplayView = findViewById(R.id.DOT_MATRIX_DISPLAY);
    }

    private void setupSideDotMatrixDisplay() {
        sideDotMatrixDisplayView = findViewById(R.id.DOT_MATRIX_DISPLAY_SIDE);
    }

}