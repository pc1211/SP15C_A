package com.example.pgyl.sp15c_a;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import com.example.pgyl.pekislib_a.ColorUtils.BUTTON_COLOR_TYPES;
import com.example.pgyl.pekislib_a.DotMatrixDisplayView;
import com.example.pgyl.pekislib_a.StringDB;
import com.example.pgyl.sp15c_a.Alu.BASE_REGS;
import com.example.pgyl.sp15c_a.Alu.KEYS;
import com.example.pgyl.sp15c_a.Alu.OPS;
import com.example.pgyl.sp15c_a.Alu.STK_REGS;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.pgyl.pekislib_a.MiscUtils.msgBox;

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
    private final String ERROR_GTO = "Invalid GTO";
    private final String ERROR_NUMBER = "Invalid number";
    private final String ERROR_RET_STACK_FULL = "Ret stack full";

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
    private boolean createNewProgLine;
    private boolean displaySymbol;
    private boolean user;
    private OPS inOp = null;
    private ProgLine tempProgLine;
    private ProgLine readProgLine;
    private int currentProgLineNumber;

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
        alu.close();
        alu = null;
        tempProgLine = null;
        readProgLine = null;
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
        stackLiftEnabled = true;
        inOp = null;

        setupDotMatrixDisplayUpdater();
        updateDisplayDotMatrixColors();

        setupAlu();
        tempProgLine = new ProgLine();
        readProgLine = new ProgLine();
        currentProgLineNumber = 0;
        displaySymbol = true;
        createNewProgLine = false;
        user = false;

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

    private void onDotMatrixDisplayViewClick() {
        if (mode.equals(MODES.EDIT)) {
            displaySymbol = !displaySymbol;
            String disp = alu.progLineToString(currentProgLineNumber, displaySymbol);
            dotMatrixDisplayUpdater.displayText(disp, false);
            dotMatrixDisplayView.updateDisplay();
            updateSideDisplay();
        }
    }

    private void onButtonClick(KEYS key) {
        //msgBox(String.valueOf(currentKey.ID()), this);
        OPS op = null;   //  Restera null si fonction f ou g activée ou annulée

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
                            if ((key.SHIFT_F_OP().INDEX() >= OPS.A.INDEX()) && (key.SHIFT_F_OP().INDEX() <= OPS.E.INDEX())) {
                                op = (user ? key.SHIFT_F_OP() : key.UNSHIFTED_OP());
                            } else {   //  Pas A..E
                                op = key.UNSHIFTED_OP();
                            }
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
                            if ((key.SHIFT_F_OP().INDEX() >= OPS.A.INDEX()) && (key.SHIFT_F_OP().INDEX() <= OPS.E.INDEX())) {
                                op = (user ? key.UNSHIFTED_OP() : key.SHIFT_F_OP());
                            } else {   //  Pas A..E
                                op = key.SHIFT_F_OP();
                            }
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
                if (op.equals(OPS.UNKNOWN)) {    //  Fonction non encore implémentée
                    msgBox("Function not implemented yet", this);
                } else {   //  Fonction déjà implémentée
                    keyOp(op);
                }
            }
        } else {   //  RUN
            keyOp(OPS.RS);   //  Pour STOP
        }
    }

    private void keyOp(OPS op) {   // Pas de switch car parfois l'opération est requalifiée dans cette procédure
        String disp;
        OPS shiftFOp = null;
        int indEex = 0;
        int indDot = 0;
        if (error.equals("")) {   //  Pas d'erreur (ou Prefix) antérieure
            if (inOp != null) {
                shiftFOp = alu.getKeyByOp(op).SHIFT_F_OP();   //  Pour I, (i), ou A..E;
            }
            if (inOp != null) {   //  Op en attente de paramètre (FIX, SCI, ENG, ...)
                if ((inOp.equals(OPS.FIX)) || (inOp.equals(OPS.SCI)) || (inOp.equals(OPS.ENG))) {
                    if (((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX()))
                            || (shiftFOp.equals(OPS.I))) {   //  Chiffre (entre 0 et 9) ou contenu dans I (Touche TAN)

                        if (shiftFOp.equals(OPS.I)) {
                            op = shiftFOp;
                        }
                        tempProgLine.setOp(1, op);
                        if (canExecAfterHandling(tempProgLine)) {
                            if (alphaToX()) {   //  Si on tape 5 puis 6 puis FIX 4 => On doit voir 56 avec 4 décimales
                                alu.setRoundMode(inOp);
                                int n = (op.equals(OPS.I) ? (int) alu.getRegContentsByIndex(BASE_REGS.RI.INDEX()) : Integer.valueOf(op.SYMBOL()));
                                alu.setRoundParam(n);
                            }
                        }
                        inOp = null;   //  Opération terminée
                        op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        //  FIX/SCI/ENG neutre sur stacklift
                    } else {   //  Opération mais pas chiffre ni I - On annule FIX/SCI/ENG et cette opération sera examinée plus loin dans cette procédure
                        inOp = null;
                    }
                }
                if (inOp != null) {
                    if (inOp.equals(OPS.STO)) {
                        if (((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX())) ||
                                (shiftFOp.equals(OPS.I)) || (shiftFOp.equals(OPS.INDI)) || (op.equals(OPS.DOT)) ||
                                (op.equals(OPS.RAND)) ||
                                (op.equals(OPS.PLUS)) || (op.equals(OPS.MINUS)) || (op.equals(OPS.MULT)) || (op.equals(OPS.DIV))) {  //  Eventuel +/-/*// puis Chiffre (entre 0 et 9) ou "." ou I (TAN)  ou "(i)" (COS) ou RAND

                            if ((shiftFOp.equals(OPS.I)) || (shiftFOp.equals(OPS.INDI))) {
                                op = shiftFOp;
                            }
                            if ((op.equals(OPS.PLUS)) || (op.equals(OPS.MINUS)) || (op.equals(OPS.MULT)) || (op.equals(OPS.DIV))) {   //  +-*/ => On continue à attendre
                                tempProgLine.setOp(1, op);
                                op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                            }
                            if (op.equals(OPS.DOT)) {   //  Normalement, on va continuer à attendre
                                if (tempProgLine.getOp(1) != null) {   //  Le +-*/ ne peut suivre un "."
                                    inOp = null;
                                    error = ERROR_STO;
                                } else {   //  Un "." suit +-*/    OK
                                    tempProgLine.setOp(2, op);
                                }
                                op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                            }
                            if (op.equals(OPS.RAND)) {   //  STO RAN # traité mais sans effet
                                tempProgLine.setOp(1, op);
                                if (canExecAfterHandling(tempProgLine)) {
                                    if (alphaToX()) {
                                        // NOP
                                        stackLiftEnabled = true;
                                    }
                                }
                                inOp = null;
                                op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                            }
                            if ((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX()) ||
                                    (op.equals(OPS.I)) || (op.equals(OPS.INDI))) {   //  Chiffre ou I ou (i) => OK on peut enfin traiter;  (TAN: I) (COS: (i))

                                int index = -1;
                                if (op.equals(OPS.I)) {
                                    index = BASE_REGS.RI.INDEX();
                                }
                                if (op.equals(OPS.INDI)) {   //  (i))
                                    int dataIndex = (int) alu.getRegContentsByIndex(BASE_REGS.RI.INDEX());   //  Valeur dans I
                                    index = alu.getRegIndexByDataIndex(dataIndex);
                                    if ((index < 0) || (index > alu.getRegsMaxIndex())) {
                                        error = ERROR_INDEX;
                                    }
                                }
                                if ((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX())) {   //  Chiffre => Rn ou R.n
                                    String s = op.SYMBOL();
                                    if (tempProgLine.getOp(2) != null) {   //  R.n
                                        s = OPS.DOT.SYMBOL() + s;
                                    }
                                    index = alu.getRegIndexBySymbol(s);
                                }
                                if (error.equals("")) {
                                    tempProgLine.setOp(3, op);
                                    if (canExecAfterHandling(tempProgLine)) {
                                        if (alphaToX()) {
                                            error = (tempProgLine.getOp(1) != null ? alu.xToRegOp(index, tempProgLine.getOp(1)) : alu.xToReg(index));
                                            if (error.equals("")) {
                                                stackLiftEnabled = true;
                                            }
                                        }
                                    }
                                }
                                inOp = null;
                                op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                            }
                        }
                    }
                }
            }
            if (inOp != null) {
                if (inOp.equals(OPS.RCL)) {
                    if (((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX())) ||
                            (op.equals(OPS.DIM)) || (op.equals(OPS.SIGMA_PLUS)) ||
                            (shiftFOp.equals(OPS.I)) || (shiftFOp.equals(OPS.INDI)) || (op.equals(OPS.DOT)) ||
                            (op.equals(OPS.PLUS)) || (op.equals(OPS.MINUS)) || (op.equals(OPS.MULT)) || (op.equals(OPS.DIV))) {  //  Eventuel +-*/ puis Chiffre (entre 0 et 9) ou "." ou I (TAN)  ou "(i)" (COS)

                        if ((shiftFOp.equals(OPS.I)) || (shiftFOp.equals(OPS.I))) {
                            op = shiftFOp;
                        }
                        if ((op.equals(OPS.PLUS)) || (op.equals(OPS.MINUS)) || (op.equals(OPS.MULT)) || (op.equals(OPS.DIV))) {   //  +-*/ => On continue à attendre
                            tempProgLine.setOp(1, op);
                            op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                        if (op.equals(OPS.DIM)) {   //  Attendre maintenant (i)
                            tempProgLine.setOp(3, op);
                            op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                        if (op.equals(OPS.INDI)) {   //  COS: (i), pour RCL DIM (i)
                            if (tempProgLine.getOp(3) != null) {
                                if (tempProgLine.getOp(3).equals(OPS.DIM)) {  //  RCL DIM (i) à traiter
                                    op = OPS.INDI;
                                    tempProgLine.setOp(4, op);
                                    if (canExecAfterHandling(tempProgLine)) {
                                        if (alphaToX()) {
                                            stackLiftIfEnabled();
                                            int n = alu.getRegsMaxIndex();
                                            alu.setStkRegContent(STK_REGS.X, alu.getDataRegIndexByIndex(n));
                                            stackLiftEnabled = true;
                                        }
                                    }
                                    inOp = null;
                                    op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                                }
                            }
                        }
                        if (op.equals(OPS.DOT)) {   //  Normalement, on va continuer à attendre
                            if (tempProgLine.getOp(1) != null) {   //  Le +-*/ ne peut suivre un "."
                                error = ERROR_RCL;
                                inOp = null;
                            } else {   //  Un "." suit +-*/    OK
                                tempProgLine.setOp(2, op);
                            }
                            op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                        if (op.equals(OPS.SIGMA_PLUS)) {   //  RCL SIGMA+
                            tempProgLine.setOp(4, op);
                            if (canExecAfterHandling(tempProgLine)) {
                                if (alphaToX()) {
                                    alu.stackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                                    stackLiftIfEnabled();
                                    error = alu.sumXYToXY();
                                    if (error.equals("")) {
                                        stackLiftEnabled = true;
                                    }
                                }
                            }
                            inOp = null;
                            op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                        if ((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX()) ||
                                (op.equals(OPS.I)) || (op.equals(OPS.INDI))) {   //  Chiffre ou I ou (i) => OK on peut enfin traiter;  (TAN: I) (COS: (i))

                            if (tempProgLine.getOp(3) == null) {   //  cad ne pas interférer avec RCL DIM (i)
                                int index = -1;
                                if (op.equals(OPS.I)) { //  I
                                    index = BASE_REGS.RI.INDEX();
                                }
                                if (op.equals(OPS.INDI)) { //  (i))
                                    int dataIndex = (int) alu.getRegContentsByIndex(BASE_REGS.RI.INDEX());   //  Valeur dans I
                                    index = alu.getRegIndexByDataIndex(dataIndex);
                                    if ((index < 0) || (index > alu.getRegsMaxIndex())) {
                                        error = ERROR_INDEX;
                                    }
                                }
                                if ((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX())) {   //  Chiffre => Rn ou R.n
                                    String s = op.SYMBOL();
                                    if (tempProgLine.getOp(2) != null) {   //  R.n
                                        s = OPS.DOT.SYMBOL() + s;
                                    }
                                    index = alu.getRegIndexBySymbol(s);   //
                                }
                                if (error.equals("")) {
                                    tempProgLine.setOp(4, op);
                                    if (canExecAfterHandling(tempProgLine)) {
                                        if (alphaToX()) {
                                            stackLiftIfEnabled();
                                            error = (tempProgLine.getOp(1) != null ? alu.regToXOp(index, tempProgLine.getOp(1)) : alu.regToX(index));
                                            if (error.equals("")) {
                                                stackLiftEnabled = true;
                                            }
                                        }
                                    }
                                }
                                inOp = null;
                                op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                            }
                        }
                    }
                }
            }
            if (inOp != null) {
                if (inOp.equals(OPS.DIM)) {   //  DIM (i)
                    if (op.equals(OPS.INDI)) {   //  COS: (i) était attendu après DIM
                        op = OPS.INDI;
                        tempProgLine.setOp(1, op);
                        if (canExecAfterHandling(tempProgLine)) {
                            if (alphaToX()) {   //  Si on tape 5 puis 6 puis DIM (i) => On doit voir 56  (dataRegIndex max)
                                int n = (int) alu.getStkRegContents(STK_REGS.X);
                                error = alu.setDataRegsSize(n + 1);   //  n=max Data Index
                                if (error.equals("")) {
                                    stackLiftEnabled = true;
                                } else {   //  Erreur
                                    error = "RANGE 0-" + alu.getDataRegIndexByIndex(alu.getRegsAbsoluteSizeMax() - 1);
                                }
                            }
                        }
                    }
                    inOp = null;   //  Opération terminée
                    op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                }
            }
            if (inOp != null) {
                if (inOp.equals(OPS.XCHG)) {
                    if (((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX())) ||
                            (shiftFOp.equals(OPS.I)) || (shiftFOp.equals(OPS.INDI)) || (op.equals(OPS.DOT))) {  //  Chiffre (entre 0 et 9) ou "." ou I (TAN)  ou "(i)" (COS)

                        if ((shiftFOp.equals(OPS.I)) || (shiftFOp.equals(OPS.INDI))) {
                            op = shiftFOp;
                        }
                        if (op.equals(OPS.DOT)) {   //  Normalement, on va continuer à attendre (un chiffre)
                            tempProgLine.setOp(1, op);
                            op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                        if ((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX()) ||
                                (op.equals(OPS.I)) || (op.equals(OPS.INDI))) {   //  Chiffre ou I ou (i) => OK on peut enfin traiter;  (TAN: I) (COS: (i))

                            int index = -1;
                            if (op.equals(OPS.I)) { //  X<>I
                                index = BASE_REGS.RI.INDEX();
                            }
                            if (op.equals(OPS.INDI)) { //  X<>(i))
                                int dataIndex = (int) alu.getRegContentsByIndex(BASE_REGS.RI.INDEX());   //  Valeur dans I
                                index = alu.getRegIndexByDataIndex(dataIndex);
                                if ((index < 0) || (index > alu.getRegsMaxIndex())) {
                                    error = ERROR_INDEX;
                                }
                            }
                            if ((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX())) {   //  Chiffre => X<>n ou X<>.n
                                String s = op.SYMBOL();
                                if (tempProgLine.getOp(1) != null) {   //  .n
                                    s = OPS.DOT.SYMBOL() + s;
                                }
                                index = alu.getRegIndexBySymbol(s);   //
                            }
                            if (error.equals("")) {
                                tempProgLine.setOp(2, op);
                                if (canExecAfterHandling(tempProgLine)) {
                                    if (alphaToX()) {
                                        error = alu.xXchgReg(index);
                                        if (error.equals("")) {
                                            stackLiftEnabled = true;
                                        }
                                    }
                                }
                            }
                            inOp = null;
                            op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                    }
                }
            }
            if (inOp != null) {
                if (inOp.equals(OPS.GTO)) {
                    if (((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX())) ||
                            ((shiftFOp.INDEX() >= OPS.A.INDEX()) && (shiftFOp.INDEX() <= OPS.E.INDEX())) ||
                            (shiftFOp.equals(OPS.I)) || (op.equals(OPS.DOT)) ||
                            (op.equals(OPS.CHS))) { //  Pour GTO CHS nnnn en mode EDIT   //  Chiffre (entre 0 et 9) (avec "." antérieur éventuel) ou A..E ou I (TAN)

                        if (((shiftFOp.INDEX() >= OPS.A.INDEX()) && (shiftFOp.INDEX() <= OPS.E.INDEX())) || (shiftFOp.equals(OPS.I))) {
                            op = shiftFOp;
                        }
                        if (op.equals(OPS.DOT)) {   //  Normalement, on va continuer à attendre
                            tempProgLine.setOp(1, op);
                            op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                        if (op.equals(OPS.CHS)) {   //  Normalement, on va continuer à attendre
                            if (mode.equals(MODES.EDIT)) {   //  GTO CHS nnnn uniquement en mode EDIT

                                tempProgLine.setOp(2, op);
                                op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                            }
                        }
                        if ((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX()) ||
                                ((op.INDEX() >= OPS.A.INDEX()) && (op.INDEX() <= OPS.E.INDEX())) ||
                                (op.equals(OPS.I))) {   //  [.]Chiffre ou lettre ou I  => OK on peut enfin traiter;  (TAN: I)

                            if ((tempProgLine.getOp(2) != null) && (createNewProgLine)) {    //  GTO CHS nnnn en mode EDIT et pas en mode feedOps
                                if ((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX())) {
                                    if (tempProgLine.getOp(3) == null) {
                                        tempProgLine.setOp(3, op);
                                    } else {  //  Op3 déjà occupé
                                        if (tempProgLine.getOp(4) == null) {
                                            tempProgLine.setOp(4, op);
                                        } else {   //  Op4 déjà occupé
                                            if (tempProgLine.getOp(5) == null) {
                                                tempProgLine.setOp(5, op);
                                            } else {   //  Op5 déjà occupé
                                                if (tempProgLine.getOp(6) == null) {   // OK nnnn
                                                    tempProgLine.setOp(6, op);
                                                    int dpln = Integer.valueOf(tempProgLine.getOp(6).SYMBOL()) + 10 * (
                                                            Integer.valueOf(tempProgLine.getOp(5).SYMBOL()) + 10 * (
                                                                    Integer.valueOf(tempProgLine.getOp(4).SYMBOL()) + 10 * (
                                                                            Integer.valueOf(tempProgLine.getOp(3).SYMBOL()))));
                                                    inOp = null;
                                                    if (dpln <= (alu.getProgLinesSize() - 1)) {
                                                        currentProgLineNumber = dpln;
                                                        createNewProgLine = false;
                                                        readProgLine = alu.getProgLine(currentProgLineNumber);
                                                        feedOps(readProgLine);
                                                        createNewProgLine = true;
                                                    } else {   //  Invalide
                                                        error = ERROR_GTO;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {   //  Pas GTO CHS nnnn en mode EDIT
                                if ((op.INDEX() >= OPS.A.INDEX()) && (op.INDEX() <= OPS.E.INDEX())) {
                                    if (tempProgLine.getOp(1) != null) {   //  .A à .E non admis
                                        error = ERROR_GTO;
                                    }
                                }
                                if (error.equals("")) {
                                    tempProgLine.setOp(2, op);
                                    if (canExecAfterHandling(tempProgLine)) {   //  Neutre sur StackLift ???
                                        if (alphaToX()) {
                                            int dpln = alu.getDestProgLineNumber(tempProgLine);
                                            if (dpln != (-1)) {   //  OK
                                                currentProgLineNumber = dpln;
                                            } else {   //  Invalide
                                                error = ERROR_GTO;
                                            }
                                        }
                                    }
                                }
                                inOp = null;
                            }
                            op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                    }
                }
            }
            if (inOp != null) {
                if (inOp.equals(OPS.LBL)) {
                    if (((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX())) ||
                            ((shiftFOp.INDEX() >= OPS.A.INDEX()) && (shiftFOp.INDEX() <= OPS.E.INDEX())) ||
                            (op.equals(OPS.DOT))) {  //  Chiffre (entre 0 et 9) (avec "." antérieur éventuel) ou A..E

                        if ((shiftFOp.INDEX() >= OPS.A.INDEX()) && (shiftFOp.INDEX() <= OPS.E.INDEX())) {
                            op = shiftFOp;
                        }
                        if (op.equals(OPS.DOT)) {   //  Normalement, on va continuer à attendre
                            tempProgLine.setOp(1, op);
                            op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                        if ((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX()) ||
                                ((op.INDEX() >= OPS.A.INDEX()) && (op.INDEX() <= OPS.E.INDEX())) ||
                                (op.equals(alu.getKeyByOp(OPS.I).UNSHIFTED_OP()))) {   //  [.]Chiffre ou lettre   => OK on peut enfin traiter;

                            if ((op.INDEX() >= OPS.A.INDEX()) && (op.INDEX() <= OPS.E.INDEX())) {
                                if (tempProgLine.getOp(1) != null) {   //  .A à .E non admis
                                    error = ERROR_GTO;
                                }
                            }
                            if (error.equals("")) {
                                tempProgLine.setOp(2, op);
                                if (canExecAfterHandling(tempProgLine)) {    //  Neutre sur StackLift ???
                                    if (alphaToX()) {
                                        //  NOP
                                    }
                                }
                            }
                            inOp = null;
                            op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                    }
                }
            }
            if (inOp != null) {
                if (inOp.equals(OPS.GSB)) {
                    if (((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX())) ||
                            ((shiftFOp.INDEX() >= OPS.A.INDEX()) && (shiftFOp.INDEX() <= OPS.E.INDEX())) ||
                            (shiftFOp.equals(OPS.I)) || (op.equals(OPS.DOT))) {  //  Chiffre (entre 0 et 9) (avec "." antérieur éventuel) ou A..E ou I (TAN)

                        if (((shiftFOp.INDEX() >= OPS.A.INDEX()) && (shiftFOp.INDEX() <= OPS.E.INDEX())) || (shiftFOp.equals(OPS.I))) {
                            op = shiftFOp;
                        }
                        if (op.equals(OPS.DOT)) {   //  Normalement, on va continuer à attendre
                            tempProgLine.setOp(1, op);
                            op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                        if ((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX()) ||
                                ((op.INDEX() >= OPS.A.INDEX()) && (op.INDEX() <= OPS.E.INDEX())) ||
                                (op.equals(OPS.I))) {   //  [.]Chiffre ou lettre ou I  => OK on peut enfin traiter;  (TAN: I)

                            if ((op.INDEX() >= OPS.A.INDEX()) && (op.INDEX() <= OPS.E.INDEX())) {
                                if (tempProgLine.getOp(1) != null) {   //  .A à .E non admis
                                    error = ERROR_GTO;
                                }
                            }
                            if (error.equals("")) {
                                tempProgLine.setOp(2, op);
                                if (canExecAfterHandling(tempProgLine)) {   //  Neutre sur StackLift ???
                                    if (alphaToX()) {
                                        forwardProgLineNumber();
                                        if (alu.addStkRetProgLineNumber(currentProgLineNumber)) {   //  Au retour, revenir à la ligne suivant l'actuelle
                                            int dpln = alu.getDestProgLineNumber(tempProgLine);
                                            if (dpln != (-1)) {   //  OK
                                                currentProgLineNumber = dpln;
                                            } else {   //  Invalide
                                                error = ERROR_GTO;
                                            }
                                        } else {   //  MAX_RETS dépassé
                                            error = ERROR_RET_STACK_FULL;
                                        }
                                    }
                                }
                            }
                            inOp = null;
                            op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                        }
                    }
                }
            }
            if (inOp != null) {
                if ((inOp.equals(OPS.SF)) || (inOp.equals(OPS.CF)) || (inOp.equals(OPS.TF))) {
                    if ((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX())) {  //  Chiffre (entre 0 et 9)

                        tempProgLine.setOp(1, op);
                        int flagIndex = Integer.valueOf(op.SYMBOL());
                        if (canExecAfterHandling(tempProgLine)) {
                            if (alphaToX()) {
                                switch (op) {
                                    case SF:
                                        alu.setFlag(flagIndex);
                                        break;
                                    case CF:
                                        alu.clearFlag(flagIndex);
                                        break;
                                    case TF:
                                        if (!alu.testFlag(flagIndex)) {   //  Skip next line if flag cleared
                                            forwardProgLineNumber();
                                        }
                                        break;
                                }
                                stackLiftEnabled = true;
                            }
                        }
                    }
                    inOp = null;
                    op = OPS.UNKNOWN;   //  Ne pas traiter plus loin dans cette procédure
                }
            }
            if (inOp != null) {
                OPS dop = alu.getOpByIndirectKeyOps(inOp, op);   //  Pas null pour opérations indirectes (cf INDIRECT_KEYS) : HYP, AHYP, TEST
                if (dop != null) {   // Cas particuliers: SINH,COSH,TANH,ASINH,ACOSH,ATANH et les 10 tests ("x<0?", ... (TEST n)) sont codées en clair en op0 (pex "ACOSH", "x<0?") et en normal (p.ex. HYP-1 COS, TEST 2) dans les op suivants
                    // Suite: Ce qui implique que si Affichage symboles: Afficher uniquement op0, Si Affichage Codes: Afficher à partir de op1
                    tempProgLine.setOp(1, inOp);   //  Garder l'opération initiale (AHYP COS , TEST n) après op0
                    tempProgLine.setOp(2, op);
                    op = dop;   //  l'opération est requalifiée en son équivalent direct et sera examinée plus bas
                    inOp = null;
                }
            }
            if (inOp == null) {   //  Pas d'opération en attente de paramètre
                if (!op.equals(OPS.UNKNOWN)) {
                    tempProgLine.setOp(0, op);
                    if (((op.INDEX() >= OPS.DIGIT_0.INDEX()) && (op.INDEX() <= OPS.DIGIT_9.INDEX())) ||
                            (op.equals(OPS.DOT)) || (op.equals(OPS.EEX)) || (op.equals(OPS.CHS))) {

                        if ((mode.equals(MODES.NORM)) || (mode.equals(MODES.RUN))) {
                            if (alpha.equals("")) {   // Début d'entrée de nombre
                                if (!op.equals(OPS.CHS)) {  //  StackLift éventuel uniquement si entrée d'un nombre (ne commençant pas par "-")
                                    if (stackLiftEnabled) {
                                        alu.stackLift();
                                    }
                                }
                            }
                            boolean acceptOp = true;
                            indEex = alpha.indexOf(OPS.EEX.SYMBOL());
                            if (indEex != (-1)) {   //  Ne plus accepter de "." ou "E" après un "E" antérieur
                                if ((op.equals(OPS.DOT)) || (op.equals(OPS.EEX))) {
                                    acceptOp = false;
                                }
                            }
                            indDot = alpha.indexOf(OPS.DOT.SYMBOL());
                            if (indDot != (-1)) {   //  Ne plus accepter de "." après un "." antérieur
                                if (op.equals(OPS.DOT)) {
                                    acceptOp = false;
                                }
                            }
                            if (acceptOp) {
                                if (op.equals(OPS.CHS)) {
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
                                        if (op.equals(OPS.EEX)) {
                                            s = OPS.DIGIT_1.SYMBOL() + s;   //  Ajout de 1 en préfixe si commence par "E"
                                        }
                                        if (op.equals(OPS.DOT)) {
                                            s = OPS.DIGIT_0.SYMBOL() + s;   //  Ajout de 0 en préfixe si commence par "."
                                        }
                                    }
                                    alpha = alpha + s;
                                }
                            }
                        }
                        if (mode.equals(MODES.EDIT)) {
                            if (canExecAfterHandling(tempProgLine)) {
                                //  NOP
                            }
                        }
                    }
                }
            }
            if (op.equals(OPS.BACK)) {    // Désactive stacklift
                if ((mode.equals(MODES.NORM)) || (mode.equals(MODES.RUN))) {
                    if (alpha.length() >= 2) {
                        alpha = alpha.substring(0, alpha.length() - 1);   //  Enlever le dernier caractère
                        if (alpha.equals(OPS.CHS.SYMBOL())) {   //  p.ex. "-0.3" -> "-0." -> "-0" -> "-" : Boum  (cf String.format plus bas, sur la partie avant le "E" et le ".")
                            alu.clX();
                            stackLiftEnabled = false;
                            alpha = "";
                        }
                    } else {   //  alpha a 0 ou 1 caractères
                        alu.clX();
                        stackLiftEnabled = false;
                        alpha = "";
                    }
                }
                if (mode.equals(MODES.EDIT)) {
                    if (currentProgLineNumber != 0) {
                        alu.removeProgLineAtNumber(currentProgLineNumber);
                        backwardProgLineNumber();
                    }
                }
            }
            if (op.equals(OPS.CLX)) {   // Désactive stacklift
                if (canExecAfterHandling(tempProgLine)) {
                    if (alphaToX()) {
                        error = alu.clX();
                        if (error.equals("")) {
                            stackLiftEnabled = false;
                        }
                    }
                }
            }
            if (op.equals(OPS.PI)) {
                if (canExecAfterHandling(tempProgLine)) {
                    if (alphaToX()) {
                        stackLiftIfEnabled();
                        error = alu.piToX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        }
                    }
                }
            }
            if (op.equals(OPS.LASTX)) {
                if (canExecAfterHandling(tempProgLine)) {
                    if (alphaToX()) {
                        stackLiftIfEnabled();
                        error = alu.lastXToX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        }
                    }
                }
            }
            if (op.equals(OPS.RAND)) {
                if (canExecAfterHandling(tempProgLine)) {
                    if (alphaToX()) {
                        stackLiftIfEnabled();
                        error = alu.randToX();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        }
                    }
                }
            }
            if (op.equals(OPS.CLEAR_PREFIX)) {   //  Neutre sur stackLift
                if (canExecAfterHandling(tempProgLine)) {
                    if (alphaToX()) {
                        error = alu.prefX();  //  error sera <>"" dans tous les cas (car représentera la mantisse) et sera donc traité comme une erreur
                    }
                }
            }
            if (op.equals(OPS.CLEAR_REGS)) {   //  Neutre sur stackLift
                if (canExecAfterHandling(tempProgLine)) {
                    if (alphaToX()) {
                        error = alu.clRegs();
                    }
                }
            }
            if (op.equals(OPS.CLEAR_SIGMA)) {   //  Neutre sur stackLift
                if (canExecAfterHandling(tempProgLine)) {
                    if (alphaToX()) {
                        error = alu.clStats();
                        alu.clearStack();
                    }
                }
            }
            if (op.equals(OPS.SIGMA_PLUS)) {   //  Désactive stackLift
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.SIGMA_MINUS)) {   //  Désactive stacklift
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.MEAN)) {
                if (canExecAfterHandling(tempProgLine)) {
                    if (alphaToX()) {
                        alu.saveStack();
                        alu.stackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                        stackLiftIfEnabled();
                        error = alu.mean();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.restoreStack();
                        }
                    }
                }
            }
            if (op.equals(OPS.STDEV)) {
                if (canExecAfterHandling(tempProgLine)) {
                    if (alphaToX()) {
                        alu.saveStack();
                        alu.stackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                        stackLiftIfEnabled();
                        error = alu.stDev();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.restoreStack();
                        }
                    }
                }
            }
            if (op.equals(OPS.LR)) {
                if (canExecAfterHandling(tempProgLine)) {
                    if (alphaToX()) {
                        alu.saveStack();
                        alu.stackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                        stackLiftIfEnabled();
                        error = alu.lr();
                        if (error.equals("")) {
                            stackLiftEnabled = true;
                        } else {
                            alu.restoreStack();
                        }
                    }
                }
            }
            if (op.equals(OPS.YER)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.SQR)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.SQRT)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.TO_RAD)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.TO_DEG)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.EXP)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.LN)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.EXP10)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.LOG)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.POWER)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.PC)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.INV)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.DPC)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.ABS)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.RND)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.POL)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.RECT)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.HMS)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.H)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.COMB)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.PERM)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.FRAC)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.INT)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.SIN)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.COS)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.TAN)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.ASIN)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.ACOS)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.ATAN)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.SINH)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.COSH)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.TANH)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.ASINH)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.ACOSH)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.ATANH)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.FACT)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.ENTER)) {   // Désactive Stacklift
                if (canExecAfterHandling(tempProgLine)) {
                    if (alphaToX()) {
                        alu.stackLift();
                        stackLiftEnabled = false;
                    }
                }
            }
            if (op.equals(OPS.RDN)) {
                if (canExecAfterHandling(tempProgLine)) {
                    if (alphaToX()) {
                        alu.stackRollDown();
                        stackLiftEnabled = true;
                    }
                }
            }
            if (op.equals(OPS.RUP)) {
                if (canExecAfterHandling(tempProgLine)) {
                    if (alphaToX()) {
                        alu.stackRollUp();
                        stackLiftEnabled = true;
                    }
                }
            }
            if (op.equals(OPS.XCHGXY)) {
                if (canExecAfterHandling(tempProgLine)) {
                    if (alphaToX()) {
                        alu.xchgXY();
                        stackLiftEnabled = true;
                    }
                }
            }
            if (op.equals(OPS.PLUS)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.MINUS)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if (op.equals(OPS.MULT)) {
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
            if (op.equals(OPS.DIV)) {
                if (canExecAfterHandling(tempProgLine)) {
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
            if ((op.equals(OPS.DEG)) || (op.equals(OPS.RAD)) || (op.equals(OPS.GRAD))) {   //  Neutre sur stackLift
                if (canExecAfterHandling(tempProgLine)) {
                    if (alphaToX()) {
                        alu.setAngleMode(op);
                    }
                }
            }
            if ((op.equals(OPS.XE0)) || (op.equals(OPS.XLEY)) ||
                    (op.equals(OPS.XNE0)) || (op.equals(OPS.XG0)) || (op.equals(OPS.XL0)) || (op.equals(OPS.XGE0)) ||
                    (op.equals(OPS.XLE0)) || (op.equals(OPS.XEY)) || (op.equals(OPS.XNEY)) || (op.equals(OPS.XGY)) ||
                    (op.equals(OPS.XLY)) || (op.equals(OPS.XGEY))) {    //  Neutre sur StackLift ???

                if (canExecAfterHandling(tempProgLine)) {
                    if (alphaToX()) {
                        if (alu.test(op)) {
                            forwardProgLineNumber();
                        }
                    }
                }
            }
            if (op.equals(OPS.BEGIN)) {
                if (mode.equals(MODES.RUN)) {
                    op = OPS.RTN;    //  Opération requalifiée et à examiner ci-dessous
                }
            }
            if (op.equals(OPS.USER)) {   //  Neutre sur StackLift
                if (alphaToX()) {
                    user = !user;
                }
            }
            if ((op.equals(OPS.A)) || (op.equals(OPS.B)) || (op.equals(OPS.C)) || (op.equals(OPS.D)) || (op.equals(OPS.E))) {
                tempProgLine.setOp(0, OPS.GSB);   //  Convertir en GSB A..E
                tempProgLine.setOp(2, op);
                if (canExecAfterHandling(tempProgLine)) {   //  Neutre sur StackLift ???
                    if (alphaToX()) {
                        forwardProgLineNumber();   //  Au retour, revenir à la ligne suivant l'actuelle
                        alu.addStkRetProgLineNumber(currentProgLineNumber);
                        int dpln = alu.getDestProgLineNumber(tempProgLine);
                        if (dpln != (-1)) {   //  OK
                            currentProgLineNumber = dpln;
                        } else {   //  Invalide
                            error = ERROR_GTO;
                        }
                    }
                }
            }
            if (op.equals(OPS.RTN)) {
                if (canExecAfterHandling(tempProgLine)) {   //  Neutre sur StackLift ???
                    if (alphaToX()) {
                        forwardProgLineNumber();
                        if (!alu.isStkRetEmpty()) {  //  La pile d'appel n'est pas vide
                            int dpln = alu.getLastStkRetProgLineNumber();
                            alu.removeLastStkRetProgLineNumber();
                            currentProgLineNumber = dpln;
                        } else {   //  STOP
                            mode = MODES.NORM;
                            createNewProgLine = true;
                        }
                    }
                }
            }
            if (op.equals(OPS.PR)) {
                boolean sw = false;
                if (!sw) {
                    if (mode.equals(MODES.NORM)) {   //  NORM -> EDIT
                        sw = true;
                        if (alphaToX()) {
                            mode = MODES.EDIT;
                            createNewProgLine = false;
                            readProgLine = alu.getProgLine(currentProgLineNumber);
                            feedOps(readProgLine);
                            createNewProgLine = true;
                        }
                    }
                }
                if (!sw) {   //  On ne vient pas de passer de NORM à EDIT juste avant
                    if (mode.equals(MODES.EDIT)) {   //  EDIT -> NORM
                        sw = true;
                        mode = MODES.NORM;
                        createNewProgLine = true;
                    }
                }
            }
            if (op.equals(OPS.RS)) {
                boolean sw = false;
                if (!sw) {
                    if (mode.equals(MODES.NORM)) {   //  NORM -> RUN
                        sw = true;
                        if (alphaToX()) {
                            mode = MODES.RUN;
                            createNewProgLine = false;
                            alu.rebuildlabelToprogLineNumberMap();   //  Réassocier les labels à leur n° de ligne, le lancement proprement sera effectué plus bas
                        }
                    }
                }
                if (!sw) {   //  On ne vient pas de passer de NORM à RUN juste avant
                    sw = true;
                    if (mode.equals(MODES.RUN)) {   //  RUN -> NORM
                        if (alphaToX()) {
                            mode = MODES.NORM;
                            createNewProgLine = true;
                        }
                    }
                }
                if (mode.equals(MODES.EDIT)) {
                    if (canExecAfterHandling(tempProgLine)) {
                        //  NOP
                    }
                }
            }
            if (op.equals(OPS.SST)) {   //  END -> BEGIN
                if ((mode.equals(MODES.NORM)) || (mode.equals(MODES.EDIT))) {
                    if (alphaToX()) {
                        createNewProgLine = false;
                        forwardProgLineNumber();
                        readProgLine = alu.getProgLine(currentProgLineNumber);
                        feedOps(readProgLine);
                        createNewProgLine = true;
                    }
                }
            }
            if (op.equals(OPS.BST)) {   //  BEGIN -> END
                if ((mode.equals(MODES.NORM)) || (mode.equals(MODES.EDIT))) {
                    if (alphaToX()) {
                        createNewProgLine = false;
                        backwardProgLineNumber();
                        readProgLine = alu.getProgLine(currentProgLineNumber);
                        feedOps(readProgLine);
                        createNewProgLine = true;
                    }
                }
            }
            if (op.equals(OPS.CLEAR_PRGM)) {
                if (mode.equals(MODES.NORM)) {
                    if (alphaToX()) {
                        currentProgLineNumber = 0;
                    }
                }
                if (mode.equals(MODES.EDIT)) {
                    if (alphaToX()) {
                        askAndDeletePrograms();   //  Remet aussi currentProgLineNumber à 0
                    }
                }
            }
            if ((op.equals(OPS.FIX)) || (op.equals(OPS.SCI)) || (op.equals(OPS.ENG)) ||
                    (op.equals(OPS.STO)) || (op.equals(OPS.RCL)) || (op.equals(OPS.XCHG)) ||
                    (op.equals(OPS.HYP)) || (op.equals(OPS.AHYP)) || (op.equals(OPS.TEST)) ||
                    (op.equals(OPS.DIM)) || (op.equals(OPS.GTO)) || (op.equals(OPS.LBL)) ||
                    (op.equals(OPS.SF)) || (op.equals(OPS.CF)) || (op.equals(OPS.TF))) {
                inOp = op;   //  Attente de paramètre
            }
            if (inOp == null) {    //  Ligne terminée (déjà enregistrée dans progLines)
                alu.clearProgLine(tempProgLine);
                if (error.equals("")) {   //  Pas d'erreur nouvelle
                    if (mode.equals(MODES.NORM)) {
                        if (alpha.equals("")) {   //  Pas d'entrée de nombre en cours
                            disp = alu.getRoundXForDisplay();
                        } else {   //  Entrée de nombre en cours => faire apparaître le séparateur de milliers
                            int indMax = alpha.length();   //  Faire apparaître le séparateur de milliers au cours de l'entrée de nombre, avant le 1er "." ou "E"
                            int indMin = 0;
                            indDot = alpha.indexOf(OPS.DOT.SYMBOL());
                            if (indDot != (-1)) {   //  "." détecté
                                indMax = Math.min(indMax, indDot);
                            }
                            indEex = alpha.indexOf(OPS.EEX.SYMBOL());
                            if (indEex != (-1)) {   //  "E" détecté
                                indMax = Math.min(indMax, indEex);
                            }
                            indMin = (alpha.substring(0, 1).equals(OPS.CHS.SYMBOL()) ? 1 : 0);   //  Tenir compte du "-" initial éventuel
                            String s = String.format(Locale.US, "%,d", Integer.parseInt(alpha.substring(indMin, indMax)));   //  Séparateur de milliers
                            if (indMin != 0) {
                                s = OPS.CHS.SYMBOL() + s;   //  Ramener le "-" initial éventuel
                            }
                            if (indMax < alpha.length()) {
                                s = s + alpha.substring(indMax);   //  Le reste
                            }
                            disp = s;
                        }
                        dotMatrixDisplayUpdater.displayText(disp, true);
                    }
                    if (mode.equals(MODES.EDIT)) {
                        disp = alu.progLineToString(currentProgLineNumber, displaySymbol);
                        dotMatrixDisplayUpdater.displayText(disp, false);
                    }
                    if (mode.equals(MODES.RUN)) {   //  On continue dans ce mode tant qu'il n'y a pas d'arrêt
                        forwardProgLineNumber();
                        readProgLine = alu.getProgLine(currentProgLineNumber);
                        feedOps(readProgLine);
                    }
                } else {    //  Erreur (ou Prefix) nouvelle
                    if (mode.equals(MODES.RUN)) {   //  STOP
                        mode = MODES.NORM;
                        createNewProgLine = true;
                    }
                    dotMatrixDisplayUpdater.displayText(error, false);
                    alpha = "";
                }
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
                dotMatrixDisplayUpdater.displayText(disp, false);
            }
        }
        dotMatrixDisplayView.updateDisplay();
        updateSideDisplay();
    }

    private void stackLiftIfEnabled() {
        if (stackLiftEnabled) {
            alu.stackLift();
        }
    }

    private boolean alphaToX() {
        boolean res = true;
        if (!alpha.equals("")) {  //  Entrée de nombre en cours, à valider et terminer
            if (alu.isDouble(alpha)) {
                String error = alu.aToX(alpha);
                if (error.equals("")) {
                    alpha = "";
                    stackLiftEnabled = true;
                } else {   //  Erreur
                    res = false;
                }
            } else {
                error = ERROR_NUMBER;
                res = false;
            }
        }
        return res;
    }

    public boolean canExecAfterHandling(ProgLine progLine) {
        boolean res = true;   //  Exécuter la ligne (NORM ou RUN)
        if (mode.equals(MODES.RUN)) {   //  Une exécution en mode NORM (p.ex 5, COS) ne change normalement pas currentProgLineNumber
            forwardProgLineNumber();
        }
        if (mode.equals(MODES.EDIT)) {   //  EDIT
            if (createNewProgLine) {   //  Créer une nouvelle ligne
                currentProgLineNumber = currentProgLineNumber + 1;   //  Pas forwardProgLineNumber(), afin d'éviter wrap around
                alu.addProgLineAtNumber(progLine, currentProgLineNumber);
            }
            res = false;   //  Ne pas exécuter la ligne
        }
        return res;
    }

    private void feedOps(ProgLine progLine) {
        int n = progLine.getOpsSize();
        for (int i = 0; i <= (n - 1); i = i + 1) {
            OPS op = progLine.getOp(i);
            if (op != null) {
                if ((i == 0) || (!alu.opIsIndirectKey(op))) {   //  Cf Cas particuliers
                    // Cas particuliers: SINH,COSH,TANH,ASINH,ACOSH,ATANH et les 10 tests ("x<0?", ... (TEST n)) sont codées en clair en op0 (pex "ACOSH", "x<0?") et en normal (p.ex. HYP-1 COS, TEST 2) dans les op suivants
                    // Suite: Ce qui implique que si Affichage symboles: Afficher uniquement op0, Si Affichage Codes: Afficher à partir de op1
                    keyOp(op);
                }
            }
        }
    }

    private void forwardProgLineNumber() {
        currentProgLineNumber = currentProgLineNumber + 1;
        if (currentProgLineNumber > (alu.getProgLinesSize() - 1)) {
            currentProgLineNumber = 0;
        }
    }

    private void backwardProgLineNumber() {
        currentProgLineNumber = currentProgLineNumber - 1;
        if (currentProgLineNumber < 0) {
            currentProgLineNumber = alu.getProgLinesSize() - 1;
        }
    }

    private void askAndDeletePrograms() {
        String disp = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete all programs");
        builder.setMessage("Are you sure ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                alu.clearProgLines();
                currentProgLineNumber = 0;   //  Cf onDismiss pour le reste
            }
        });
        builder.setNegativeButton("No", null);
        Dialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {    // OK pour modifier UI sous-jacente à la boîte de dialogue
                String disp = alu.progLineToString(currentProgLineNumber, displaySymbol);
                dotMatrixDisplayUpdater.displayText(disp, false);
                dotMatrixDisplayView.updateDisplay();
                updateSideDisplay();
            }
        });
        dialog.show();
    }

    private void updateSideDisplay() {
        sideDotMatrixDisplayUpdater.displayText(
                alu.getAngleMode().toString().toLowerCase() + " " +
                        alu.getRoundMode().toString().toLowerCase() + alu.getRoundParam() + " " +
                        (user ? "user" : ""), false);
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
        final float BUTTON_MID_23_IMAGE_SIZE_COEFF = 0.6f;
        final float BUTTON_MID_25_IMAGE_SIZE_COEFF = 0.55f;
        final float BUTTON_MID_26_IMAGE_SIZE_COEFF = 0.6f;
        final float BUTTON_MID_30_IMAGE_SIZE_COEFF = 0.3f;
        final float BUTTON_MID_31_IMAGE_SIZE_COEFF = 0.6f;
        final float BUTTON_MID_33_IMAGE_SIZE_COEFF = 0.55f;
        final float BUTTON_MID_35_IMAGE_SIZE_COEFF = 0.5f;
        final float BUTTON_MID_36_IMAGE_SIZE_COEFF = 0.85f;
        final float BUTTON_MID_44_IMAGE_SIZE_COEFF = 0.6f;
        final float BUTTON_MID_45_IMAGE_SIZE_COEFF = 0.6f;
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
                        if ((key.equals(KEYS.KEY_26)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image EEX à adapter
                            imageSizeCoeff = BUTTON_MID_26_IMAGE_SIZE_COEFF;
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
                        if ((key.equals(KEYS.KEY_44)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image STO à adapter
                            imageSizeCoeff = BUTTON_MID_44_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_45)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image RCL à adapter
                            imageSizeCoeff = BUTTON_MID_45_IMAGE_SIZE_COEFF;
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
        dotMatrixDisplayView.setOnCustomClickListener(new DotMatrixDisplayView.onCustomClickListener() {
            @Override
            public void onCustomClick() {
                onDotMatrixDisplayViewClick();
            }
        });
    }

    private void setupSideDotMatrixDisplay() {
        sideDotMatrixDisplayView = findViewById(R.id.DOT_MATRIX_DISPLAY_SIDE);
    }

}