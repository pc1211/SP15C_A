package com.example.pgyl.sp15c_a;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.pgyl.pekislib_a.ColorBox;
import com.example.pgyl.pekislib_a.ColorUtils.BUTTON_COLOR_TYPES;
import com.example.pgyl.pekislib_a.DotMatrixDisplayView;
import com.example.pgyl.pekislib_a.HelpActivity;
import com.example.pgyl.pekislib_a.StringDB;
import com.example.pgyl.sp15c_a.Alu.BASE_REGS;
import com.example.pgyl.sp15c_a.Alu.KEYS;
import com.example.pgyl.sp15c_a.Alu.OPS;
import com.example.pgyl.sp15c_a.Alu.STACK_REGS;
import com.example.pgyl.sp15c_a.ProgLine.LINE_OPS;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static com.example.pgyl.pekislib_a.Constants.CRLF;
import static com.example.pgyl.pekislib_a.Constants.PEKISLIB_ACTIVITY_EXTRA_KEYS;
import static com.example.pgyl.pekislib_a.Constants.SHP_FILE_NAME_SUFFIX;
import static com.example.pgyl.pekislib_a.HelpActivity.HELP_ACTIVITY_TITLE;
import static com.example.pgyl.pekislib_a.MiscUtils.msgBox;
import static com.example.pgyl.pekislib_a.StringDB.TABLE_DATA_INDEX;
import static com.example.pgyl.pekislib_a.StringDB.TABLE_ID_INDEX;
import static com.example.pgyl.pekislib_a.StringDBTables.getActivityInfosTableName;
import static com.example.pgyl.pekislib_a.StringDBTables.getAppInfosDataVersionIndex;
import static com.example.pgyl.pekislib_a.StringDBTables.getAppInfosTableName;
import static com.example.pgyl.pekislib_a.StringDBUtils.createPekislibTableIfNotExists;
import static com.example.pgyl.pekislib_a.StringDBUtils.getCurrent;
import static com.example.pgyl.pekislib_a.StringDBUtils.setCurrent;
import static com.example.pgyl.pekislib_a.TimeDateUtils.MILLISECONDS_PER_SECOND;
import static com.example.pgyl.sp15c_a.StringDBTables.DATA_VERSION;
import static com.example.pgyl.sp15c_a.StringDBTables.getFlagsTableName;
import static com.example.pgyl.sp15c_a.StringDBTables.getParamsTableName;
import static com.example.pgyl.sp15c_a.StringDBTables.getProgLinesTableName;
import static com.example.pgyl.sp15c_a.StringDBTables.getRegsTableName;
import static com.example.pgyl.sp15c_a.StringDBTables.getRetStackTableName;
import static com.example.pgyl.sp15c_a.StringDBTables.getSp15cTableDataFieldsCount;
import static com.example.pgyl.sp15c_a.StringDBTables.getStackRegsTableName;
import static com.example.pgyl.sp15c_a.StringDBUtils.booleanArrayToRows;
import static com.example.pgyl.sp15c_a.StringDBUtils.createSp15cTableIfNotExists;
import static com.example.pgyl.sp15c_a.StringDBUtils.doubleArrayToList;
import static com.example.pgyl.sp15c_a.StringDBUtils.doubleArrayToRows;
import static com.example.pgyl.sp15c_a.StringDBUtils.doubleListToArray;
import static com.example.pgyl.sp15c_a.StringDBUtils.intArrayToList;
import static com.example.pgyl.sp15c_a.StringDBUtils.intArrayToRows;
import static com.example.pgyl.sp15c_a.StringDBUtils.intListToArray;
import static com.example.pgyl.sp15c_a.StringDBUtils.loadRowsFromDB;
import static com.example.pgyl.sp15c_a.StringDBUtils.rowsToBooleanArray;
import static com.example.pgyl.sp15c_a.StringDBUtils.rowsToDoubleArray;
import static com.example.pgyl.sp15c_a.StringDBUtils.rowsToIntArray;
import static com.example.pgyl.sp15c_a.StringDBUtils.saveRowsToDB;

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
        NORM, EDIT, RUN;

        public int INDEX() {
            return ordinal();
        }
    }

    private final String ERROR_INDEX = "Invalid index";
    private final String ERROR_GTO_GSB = "Invalid GTO/GSB";
    private final String ERROR_LINE_NUMBER = "Invalid Line num";
    private final String ERROR_RET_STACK_FULL = "Ret stack full";
    private final String ERROR_PROG_LINES_FULL = "Prog lines full";
    private final String ERROR_KEYBOARD_INTERRUPT = "Keyboard Break";
    private final String ERROR_NESTED_SOLVE = "Nested SOLVE";
    private final String ERROR_NESTED_INTEG = "Nested INTEG";
    private final String ERROR_SOLVE_ITER_MAX = "Max Iter SOLVE";
    private final String ERROR_INTEG_ITER_MAX = "Max Iter INTEG";
    private final long PSE_MS = MILLISECONDS_PER_SECOND;   //  1 seconde
    private final long FLASH_RUN_MS = MILLISECONDS_PER_SECOND / 2;   //  1/2 seconde
    private final long AUTO_UPDATE_INTERVAL_MS = 1;
    private final int SOLVE_RETURN_CODE = 100000;   //  > 10000 pour ne pas le confondre avec un N° de ligne ordinaire (0000-9999)
    private final int INTEG_RETURN_CODE = 200000;
    private final int END_RETURN_CODE = 300000;   //  Pour assurer le retour après un GSB lancé en mode NORM

    public enum SWTIMER_SHP_KEY_NAMES {KEEP_SCREEN}
    //endregion

    //region Variables
    private ClipboardManager clipboard;
    private ImageButtonViewStack[] buttons;
    private DotMatrixDisplayView dotMatrixDisplayView;
    private DotMatrixDisplayView sideDotMatrixDisplayView;
    private Menu menu;
    private MenuItem barMenuItemKeepScreen;
    private boolean keepScreen;
    private StringDB stringDB;
    private String shpFileName;
    private SHIFT_MODES shiftMode;
    private CalcDotMatrixDisplayUpdater dotMatrixDisplayUpdater;
    private SideDotMatrixDisplayUpdater sideDotMatrixDisplayUpdater;
    private Alu alu;
    private MODES mode;
    private String error;
    private ProgLine tempProgLine;
    private ProgLine readProgLine;
    private long updateInterval;
    private long nowmPSE;
    private long nowmRUN;
    private Handler handlerTimeLine;
    private Runnable runnableTimeLine;
    private OPS inOp;
    private OPS shiftFOp;
    private OPS currentOp;
    private SolveParamSet solveParamSet;
    private IntegParamSet integParamSet;
    private int nextProgLineNumber;
    private int currentProgLineNumber;
    private String alpha;
    private boolean displaySymbol;
    private boolean user;
    private boolean isWrapAround;
    private boolean isAutoL;
    private boolean isAutoLine;
    private boolean isKeyboardInterrupt;
    private boolean inSST;
    private boolean inExecCurrentProgLine;
    private boolean inPSE;
    private boolean requestStopAfterSolve;
    private boolean requestStopAfterInteg;

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

        setCurrent(stringDB, getAppInfosTableName(), getAppInfosDataVersionIndex(), String.valueOf(DATA_VERSION));
        saveRowsToDB(stringDB, getStackRegsTableName(), doubleArrayToRows(alu.getStackRegs()));
        saveRowsToDB(stringDB, getFlagsTableName(), booleanArrayToRows(alu.getFlags()));
        saveRowsToDB(stringDB, getRegsTableName(), doubleArrayToRows(doubleListToArray(alu.getRegs())));
        saveRowsToDB(stringDB, getRetStackTableName(), intArrayToRows(intListToArray(alu.getRetStack())));
        saveRowsToDB(stringDB, getProgLinesTableName(), alu.progLinesToRows());
        saveRowsToDB(stringDB, getParamsTableName(), paramsToRows());
        clipboard = null;
        dotMatrixDisplayUpdater.close();
        dotMatrixDisplayUpdater = null;
        sideDotMatrixDisplayUpdater.close();
        sideDotMatrixDisplayUpdater = null;
        alu.close();
        alu = null;
        solveParamSet.close();
        solveParamSet = null;
        integParamSet.close();
        integParamSet = null;
        tempProgLine = null;
        readProgLine = null;
        stringDB.close();
        stringDB = null;
        menu = null;
        savePreferences();
    }
    //endregion

    @Override
    protected void onResume() {
        super.onResume();

        setContentView(R.layout.main);
        shpFileName = getPackageName() + "." + getClass().getSimpleName() + SHP_FILE_NAME_SUFFIX;
        keepScreen = getSHPKeepScreen();

        setupStringDB();
        setupButtons();
        setupDotMatrixDisplay();
        setupSideDotMatrixDisplay();

        mode = MODES.NORM;
        error = "";
        alpha = "";
        currentProgLineNumber = 0;
        nextProgLineNumber = 0;
        nowmPSE = 0;
        nowmRUN = 0;
        inOp = null;
        shiftFOp = null;
        inSST = false;
        isWrapAround = false;
        isAutoLine = false;
        isAutoL = false;
        inPSE = false;
        user = false;
        isKeyboardInterrupt = false;
        displaySymbol = true;
        requestStopAfterSolve = false;
        requestStopAfterInteg = false;
        tempProgLine = new ProgLine();
        readProgLine = new ProgLine();
        updateInterval = AUTO_UPDATE_INTERVAL_MS;
        shiftMode = SHIFT_MODES.UNSHIFTED;

        alu = new Alu();
        solveParamSet = new SolveParamSet();
        integParamSet = new IntegParamSet();

        alu.setStackRegs(rowsToDoubleArray(loadRowsFromDB(stringDB, getStackRegsTableName()), STACK_REGS.values().length));
        alu.setFlags(rowsToBooleanArray(loadRowsFromDB(stringDB, getFlagsTableName()), alu.MAX_FLAGS));
        alu.setRegs(doubleArrayToList(rowsToDoubleArray(loadRowsFromDB(stringDB, getRegsTableName()), alu.DEF_MAX_REGS)));
        alu.setRetStack(intArrayToList(rowsToIntArray(loadRowsFromDB(stringDB, getRetStackTableName()), 0)));

        setupDotMatrixDisplayUpdater();
        updateDisplayDotMatrixColors();
        updateDisplayButtonColors();
        setupSideDotMatrixDisplayUpdater();
        updateSideDotMatrixColors();

        encodeKeyCodesFromProgLinesRows(loadRowsFromDB(stringDB, getProgLinesTableName()));
        rowsToParams(loadRowsFromDB(stringDB, getParamsTableName()));

        dotMatrixDisplayUpdater.displayText(alu.getRoundXForDisplay(), true);
        dotMatrixDisplayView.updateDisplay();
        updateSideDisplay();

        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        setupRunnableTimeLine();
        updateDisplayKeepScreen();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {  //  Non appelé après changement d'orientation
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        setupBarMenuItems();
        updateDisplayKeepScreenBarMenuItemIcon(keepScreen);
        return true;
    }
    //endregion

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {  // appelé par invalideOptionsMenu après changement d'orientation
        updateDisplayKeepScreenBarMenuItemIcon(keepScreen);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.HELP) {
            launchHelpActivity();
            return true;
        }
        if (item.getItemId() == R.id.IMPORT) {
            if (clipboard != null) {
                if (clipboard.hasPrimaryClip()) {
                    if (clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)) {
                        ClipData cld = clipboard.getPrimaryClip();
                        if (cld != null) {
                            ClipData.Item cldi = cld.getItemAt(0);
                            if (cldi != null) {
                                encodeKeyCodesFromClipBoard(cldi.getText().toString());
                                msgBox(alu.getProgLinesSize() + " lines imported", this);
                                dotMatrixDisplayUpdater.displayText((alpha.equals("") ? alu.getRoundXForDisplay() : formatAlphaNumber()), true);   //  formatAlphaNumber pour faire apparaître le séparateur de milliers
                                dotMatrixDisplayView.updateDisplay();
                            }
                        }
                    }
                }
            }
            return true;
        }
        if (item.getItemId() == R.id.EXPORT) {
            if (clipboard != null) {
                ClipData clip = ClipData.newPlainText(null, progLinesToClipBoard());
                clipboard.setPrimaryClip(clip);
                msgBox(alu.getProgLinesSize() + " lines exported", this);
            }
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
            dotMatrixDisplayUpdater.displayText(alu.progLineToString(currentProgLineNumber, displaySymbol), false);
            dotMatrixDisplayView.updateDisplay();
        }
    }

    private void onSSTClickDown() {   //  Click Down sur SST => Afficher ProgLine courante
        if ((mode.equals(MODES.NORM)) && (shiftMode.equals(SHIFT_MODES.UNSHIFTED))) {
            dotMatrixDisplayUpdater.displayText(alu.progLineToString(currentProgLineNumber, displaySymbol), false);
            dotMatrixDisplayView.updateDisplay();
        }
    }

    private void onSSTClickLeave() {   //  Quitter SST sans cliquer => Affichage normal
        if ((mode.equals(MODES.NORM)) && (shiftMode.equals(SHIFT_MODES.UNSHIFTED))) {
            dotMatrixDisplayUpdater.displayText((alpha.equals("") ? alu.getRoundXForDisplay() : formatAlphaNumber()), true);   //  formatAlphaNumber pour faire apparaître le séparateur de milliers
            dotMatrixDisplayView.updateDisplay();
        }
    }

    private void onButtonClick(KEYS key) {
        currentOp = null;   //  Restera null si fonction f ou g activée ou annulée
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
                                currentOp = (user ? key.SHIFT_F_OP() : key.UNSHIFTED_OP());
                            } else {   //  Pas A..E
                                currentOp = key.UNSHIFTED_OP();
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
                                currentOp = (user ? key.UNSHIFTED_OP() : key.SHIFT_F_OP());
                            } else {   //  Pas A..E
                                currentOp = key.SHIFT_F_OP();
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
                            currentOp = key.SHIFT_G_OP();
                            break;
                    }
                    break;
                default:
                    break;
            }
            buttons[KEYS.KEY_42.INDEX()].updateDisplay();
            buttons[KEYS.KEY_43.INDEX()].updateDisplay();

            if (currentOp != null) {   //  Pas Fonction Shift f ou g activée ou annulée
                if (currentOp.equals(OPS.UNKNOWN)) {    //  Fonction non encore implémentée
                    msgBox("Function not implemented yet", this);
                } else {   //  Fonction déjà implémentée
                    interpretAndSaveOrExecOp();
                }
            }
        } else {   //  RUN
            isKeyboardInterrupt = true;
        }
    }

    private int digitToRealKeyCode(int kc) {
        int res = kc;
        switch (kc) {
            case 0:
                res = res + 47;
                break;
            case 1:
            case 2:
            case 3:
                res = res + 36;
                break;
            case 4:
            case 5:
            case 6:
                res = res + 23;
                break;
            case 7:
            case 8:
            case 9:
                res = res + 10;
                break;
        }
        return res;
    }

    private void encodeProgKeyCode(int keyCode) {
        currentOp = null;   //  Restera null si fonction f ou g activée ou annulée
        int n = keyCode;
        if (keyCode < 10) {   //  Retrouver le vrai keyCode (au lieu du chiffre repris dans la liste de codes du programme)
            keyCode = digitToRealKeyCode(keyCode);
        }
        KEYS key = alu.getKeyByKeyCode(keyCode);
        switch (shiftMode) {
            case UNSHIFTED:
                switch (key) {
                    case KEY_42:
                        shiftMode = SHIFT_MODES.F_SHIFT;
                        break;
                    case KEY_43:
                        shiftMode = SHIFT_MODES.G_SHIFT;
                        break;
                    default:
                        if ((key.SHIFT_F_OP().INDEX() >= OPS.A.INDEX()) && (key.SHIFT_F_OP().INDEX() <= OPS.E.INDEX())) {
                            currentOp = (user ? key.SHIFT_F_OP() : key.UNSHIFTED_OP());
                        } else {   //  Pas A..E
                            currentOp = key.UNSHIFTED_OP();
                        }
                        break;
                }
                break;
            case F_SHIFT:
                shiftMode = SHIFT_MODES.UNSHIFTED;
                if ((key.SHIFT_F_OP().INDEX() >= OPS.A.INDEX()) && (key.SHIFT_F_OP().INDEX() <= OPS.E.INDEX())) {
                    currentOp = (user ? key.UNSHIFTED_OP() : key.SHIFT_F_OP());
                } else {   //  Pas A..E
                    currentOp = key.SHIFT_F_OP();
                }
                break;
            case G_SHIFT:
                shiftMode = SHIFT_MODES.UNSHIFTED;
                currentOp = key.SHIFT_G_OP();
                break;
        }
        if (currentOp != null) {   //  Pas Fonction Shift f ou g activée ou annulée
            if (currentOp.equals(OPS.UNKNOWN)) {    //  Fonction non encore implémentée
                msgBox("Function not implemented yet", this);
            } else {   //  Fonction déjà implémentée
                interpretAndSaveOrExecOp();
            }
        }
    }

    private void interpretAndSaveOrExecOp() {
        if (error.length() == 0) {   //  Pas d'erreur (ou Prefix) antérieure
            interpretDirectAEOp();   //  Test si A..E: inOp y deviendra OPS.GSB
            interpretGhostOp();      //  Test si Touche fantôme (après opération HYP, AHYP ou TEST déjà engagée): inOp y deviendra null
            nextProgLineNumber = currentProgLineNumber;    //  Sauf mention contraire en mode NORM ou EDIT (SST, BST, CLEAR_PRGM, ...)
            if (inOp != null) {   //  instruction MultiOps déjà engagée
                prepareMultiOpsProgLine();
                interpretMultiOpsEnd();   //  Test si fin d'nstruction MultiOps (Eventuellement inOp y deviendra null si instruction MultiOps complète (=> Nbre en cours copié dans X et vidé, puis exécution)(si EDIT: Nouvelle instruction))
            } else {   //  Pas d'instruction MultiOps déjà engagée
                tempProgLine.ops[LINE_OPS.BASE.INDEX()] = currentOp;   //  Nouvelle instruction commence
                interpretDigitOp();   //  Test si Chiffre entré (ou CHS, EEX, "."): stackLift éventuel et se met en fin du nombre en cours (Si EDIT: Enregistrement instruction avec ce chiffre)
                interpretSingleOp();   //  Test si Opération non MultiOps, normale (=> stackLift éventuel, Nbre en cours copié dans X et vidé, puis exécution)(si EDIT: Enregistrement instruction)
                interpretSpecialOp();   //  Test si Opération non MultiOps, spéciale (=> stackLift éventuel, Nbre en cours copié dans X et vidé, puis exécution éventuelle) (si EDIT: Enregistrement instruction éventuelle)
                interpretMultiOpsBegin();  //  Test si début d'instruction MultiOps: inOp deviendra non null
            }
            if (inOp == null) {    //  Instruction terminée (déjà enregistrée dans progLines si EDIT ou RUN) ou éventuellement annulée pour problème de syntaxe mais sans erreur explicite
                alu.clearProgLine(tempProgLine);   //  Préparer le terrain pour une nouvelle instruction
                if (error.length() == 0) {   //  Pas d'erreur nouvelle
                    currentProgLineNumber = nextProgLineNumber;
                    if (mode.equals(MODES.NORM)) {    //  A voir selon alpha si entrée de nombre en cours ou pas
                        dotMatrixDisplayUpdater.displayText((alpha.equals("") ? alu.getRoundXForDisplay() : formatAlphaNumber()), true);    //  formatAlphaNumber pour faire apparaître le séparateur de milliers
                    }
                    if (mode.equals(MODES.EDIT)) {
                        dotMatrixDisplayUpdater.displayText(alu.progLineToString(currentProgLineNumber, displaySymbol), false);
                    }
                }
                if (error.length() != 0) {    //  Erreur (ou Prefix) nouvelle
                    dotMatrixDisplayUpdater.displayText(error, false);
                }
            }
        } else {   //  Erreur (ou Prefix) antérieure
            error = "";
            alu.clearRetStack();
            solveParamSet.clear();
            integParamSet.clear();
            inOp = null;
            if (mode.equals(MODES.NORM)) {
                dotMatrixDisplayUpdater.displayText(alu.getRoundXForDisplay(), true);
            }
            if (mode.equals(MODES.EDIT)) {
                dotMatrixDisplayUpdater.displayText(alu.progLineToString(currentProgLineNumber, displaySymbol), false);
            }
        }
        dotMatrixDisplayView.updateDisplay();
        updateSideDisplay();
        startOrStopAutomaticLine();
    }

    private void execCurrentProgLine() {
        inExecCurrentProgLine = true;
        readProgLine = alu.getProgLine(currentProgLineNumber);    //  Charger la ligne actuelle
        nextProgLineNumber = inc(currentProgLineNumber);    //  Sauf mention contraire (GTO, GSB, RTN, ...)
        if (nextProgLineNumber == 0) {
            isWrapAround = true;  //  Wrap Around => RTN
        }
        error = exec(readProgLine);
        if (error.length() == 0) {   //  Pas d'erreur nouvelle
            currentProgLineNumber = nextProgLineNumber;
            if (inSST) {   //  STOP après SST
                inSST = false;
                isAutoLine = false;
            }
        } else {    //  Erreur nouvelle
            isAutoLine = false;
        }
        if (isKeyboardInterrupt) {
            isKeyboardInterrupt = false;
            isAutoLine = false;
            error = ERROR_KEYBOARD_INTERRUPT;
        }
        startOrStopAutomaticLine();
        if (!isAutoLine) {
            mode = MODES.NORM;
            dotMatrixDisplayView.setInvertOn(false);
            if (error.length() == 0) {   //  Pas d'erreur nouvelle
                dotMatrixDisplayUpdater.displayText((alpha.equals("") ? alu.getRoundXForDisplay() : formatAlphaNumber()), true);   //  formatAlphaNumber pour faire apparaître le séparateur de milliers
            } else {   //  Erreur nouvelle
                dotMatrixDisplayUpdater.displayText(error, false);
            }
            dotMatrixDisplayView.updateDisplay();
            updateSideDisplay();
        }
        inExecCurrentProgLine = false;
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
        String[] colors = {"000000", "BFAF00", "D9C700"};    // ON, OFF, BACK

        dotMatrixDisplayUpdater.setColors(colors);
        dotMatrixDisplayUpdater.rebuildStructure();
        dotMatrixDisplayView.updateDisplay();
    }

    private void updateSideDotMatrixColors() {
        String[] colors = {"000000", "BFAF00", "D9C700"};    // ON, OFF, BACK

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

    private boolean getSHPKeepScreen() {
        final boolean KEEP_SCREEN_DEFAULT_VALUE = false;

        SharedPreferences shp = getSharedPreferences(shpFileName, MODE_PRIVATE);
        return shp.getBoolean(SWTIMER_SHP_KEY_NAMES.KEEP_SCREEN.toString(), KEEP_SCREEN_DEFAULT_VALUE);
    }

    private void interpretDirectAEOp() {
        if (inOp == null) {
            if ((currentOp.INDEX() >= OPS.A.INDEX()) && (currentOp.INDEX() <= OPS.E.INDEX())) {
                inOp = OPS.GSB;
                tempProgLine.ops[LINE_OPS.BASE.INDEX()] = inOp;    //  Conversion en GSB A..E, à examiner ci-dessous
                if ((mode.equals(MODES.NORM)) || (mode.equals(MODES.EDIT))) {
                    KEYS key = alu.getKeyByOp(inOp);
                    swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());   //  Touche inOp revient à la normale
                    buttons[key.INDEX()].updateDisplay();
                }
            }
        }
    }

    private void interpretGhostOp() {
        if (inOp != null) {   //  Opération MultiOps déjà engagée
            OPS dop = alu.getOpByGhostKeyOps(inOp, currentOp);   //  Pas null pour opérations fantômes (cf GHOST_KEYS) : HYP, AHYP, TEST
            if (dop != null) {   // Cas particuliers: SINH,COSH,TANH,ASINH,ACOSH,ATANH et les 10 tests ("x<0?", ... (TEST n)) sont codées en clair en op0 (pex "ACOSH", "x<0?") et en normal (p.ex. HYP-1 COS, TEST 2) dans les op suivants
                // Suite: Ce qui implique que si Affichage symboles: Afficher uniquement op0, Si Affichage Codes: Afficher à partir de op1
                tempProgLine.ops[LINE_OPS.GHOST1.INDEX()] = inOp;   //  Garder l'opération initiale (AHYP COS , TEST n) après op0; op0 sera fixé dans interpretOp()
                tempProgLine.ops[LINE_OPS.GHOST2.INDEX()] = currentOp;
                currentOp = dop;   //  l'opération est requalifiée en son équivalent direct et sera examinée plus bas
                if ((mode.equals(MODES.NORM)) || (mode.equals(MODES.EDIT))) {
                    KEYS key = alu.getKeyByOp(inOp);
                    swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());   //  Touche inOp revient à la normale
                    buttons[key.INDEX()].updateDisplay();
                }
                inOp = null;
            }
        }
    }

    private void prepareMultiOpsProgLine() {
        if (inOp != null) {
            shiftFOp = alu.getKeyByOp(currentOp).SHIFT_F_OP();   //  Pour I, (i), ou A..E;
            if ((shiftFOp.equals(OPS.DIM)) || (shiftFOp.equals(OPS.I)) || (shiftFOp.equals(OPS.INDI)) || ((shiftFOp.INDEX() >= OPS.A.INDEX()) && (shiftFOp.INDEX() <= OPS.E.INDEX()))) {
                currentOp = shiftFOp;
            }
            LINE_OPS lineOp = ProgLine.getLineOpByOp(currentOp);
            if (lineOp != null) {
                tempProgLine.ops[lineOp.INDEX()] = currentOp;   //  Les LINE_OPS doivent porter le même nom que les OPS auxquels ils font référence (cf ProgLine)
            }
            if ((currentOp.equals(OPS.PLUS)) || (currentOp.equals(OPS.MINUS)) || (currentOp.equals(OPS.MULT)) || (currentOp.equals(OPS.DIV))) {
                tempProgLine.ops[LINE_OPS.A4OP.INDEX()] = currentOp;
            }
            if ((currentOp.INDEX() >= OPS.DIGIT_0.INDEX()) && (currentOp.INDEX() <= OPS.DIGIT_9.INDEX())) {
                tempProgLine.ops[LINE_OPS.A09.INDEX()] = currentOp;
                tempProgLine.symbol = (tempProgLine.ops[LINE_OPS.DOT.INDEX()] != null ? OPS.DOT.SYMBOL() + currentOp.SYMBOL() : currentOp.SYMBOL());
                if ((inOp.equals(OPS.STO)) || (inOp.equals(OPS.RCL)) || (inOp.equals(OPS.XCHG)) || (inOp.equals(OPS.DSE)) || (inOp.equals(OPS.ISG))) {
                    tempProgLine.paramAddress = alu.getRegIndexBySymbol(tempProgLine.symbol);
                }
            }
            if ((currentOp.INDEX() >= OPS.A.INDEX()) && (currentOp.INDEX() <= OPS.E.INDEX())) {
                tempProgLine.ops[LINE_OPS.AE.INDEX()] = currentOp;
                tempProgLine.symbol = currentOp.SYMBOL();
            }
            if (currentOp.equals(OPS.I)) {
                tempProgLine.paramAddress = BASE_REGS.RI.INDEX();
                tempProgLine.symbol = currentOp.SYMBOL();
            }
        }
    }

    private void interpretMultiOpsEnd() {
        if (inOp != null) {
            boolean isComplete = false;
            boolean common = false;

            if (currentOp.equals(OPS.BACK)) {   //  On annule l'opération MultiOps en cours avec la flèche gauche
                currentOp = OPS.UNKNOWN;
                isComplete = true;
            } else {   //  Pas BACK après début d'une opération MultiOps
                switch (inOp) {
                    case GTO:
                        if ((tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.I.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.AE.INDEX()] != null) ||
                                (tempProgLine.ops[LINE_OPS.CHS.INDEX()] != null)) {   //  A09 tient déjà compte du DOT (cf prepareMultiOpsProgLine())

                            if ((mode.equals(MODES.EDIT)) && (tempProgLine.ops[LINE_OPS.CHS.INDEX()] != null)) {   //  GTO CHS nnnnn en mode EDIT et pas en mode de lecture automatique de lignes
                                if (tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) {
                                    if (tempProgLine.paramAddress == 0) {
                                        tempProgLine.paramAddress = 1;   //  cf ci-dessous, permet de multiplier par 10 puis d'ajouter un n (de nnnn), le 1 deviendra 10000 après entrée de nnnn
                                    }
                                    tempProgLine.paramAddress = 10 * tempProgLine.paramAddress + Integer.valueOf(tempProgLine.ops[LINE_OPS.A09.INDEX()].SYMBOL());
                                    if (tempProgLine.paramAddress > 10000) {   //  OK 4 chiffres obligatoires (nnnn)
                                        isComplete = true;
                                        int dpln = tempProgLine.paramAddress - 10000;   //  nnnn
                                        tempProgLine.paramAddress = 0;
                                        nextProgLineNumber = dpln;
                                        if (dpln > (alu.getProgLinesSize() - 1)) {
                                            error = ERROR_LINE_NUMBER;
                                        }
                                    }
                                }
                            } else {   //  Pas GTO CHS nnnn en mode EDIT
                                isComplete = true;
                                saveOrExecLineWithLabel(false);
                            }
                        }
                        break;
                    case GSB:
                        if ((tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.I.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.AE.INDEX()] != null)) {   //  A09 tient déjà compte du DOT (cf prepareMultiOpsProgLine())
                            isComplete = true;
                            saveOrExecLineWithLabel(true);
                        }
                        break;
                    case SOLVE:
                    case INTEG:
                        if ((tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.AE.INDEX()] != null)) {   //  A09 tient déjà compte du DOT (cf prepareMultiOpsProgLine())
                            isComplete = true;
                            saveOrExecLineWithLabel(true);
                        }
                        break;
                    case FIX:
                    case SCI:
                    case ENG:
                        if ((tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.I.INDEX()] != null)) {
                            common = true;
                        }
                        break;
                    case STO:
                        if ((tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.I.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.INDI.INDEX()] != null) ||
                                (tempProgLine.ops[LINE_OPS.RAND.INDEX()] != null)) {   //  A09 tient déjà compte du DOT (cf prepareMultiOpsProgLine())
                            common = true;
                        }
                        break;
                    case RCL:
                        if ((tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.I.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.INDI.INDEX()] != null) ||
                                (tempProgLine.ops[LINE_OPS.SIGMA_PLUS.INDEX()] != null)) {   //  A09 tient déjà compte du DOT (cf prepareMultiOpsProgLine())
                            common = true;
                        }
                        break;
                    case DIM:   //  DIM (i)
                        if (tempProgLine.ops[LINE_OPS.INDI.INDEX()].equals(OPS.INDI)) {   //  COS: (i) était attendu après DIM
                            common = true;
                        }
                        break;
                    case LBL:
                        if ((tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.AE.INDEX()] != null)) {   //  A09 tient déjà compte du DOT (cf prepareMultiOpsProgLine())
                            common = true;
                        }
                        break;
                    case DSE:
                    case ISG:
                    case XCHG:
                        if ((tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.I.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.INDI.INDEX()] != null)) {   //  A09 tient déjà compte du DOT (cf prepareMultiOpsProgLine())
                            common = true;
                        }
                        break;
                    case SF:
                    case CF:
                    case TF:
                        if (tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) {
                            common = true;
                        }
                        break;
                }
            }

            if (common) {
                isComplete = true;
                if (!inEditModeAfterSavingLine(tempProgLine)) {
                    error = exec(tempProgLine);
                }
            }
            if (isComplete) {
                KEYS key = alu.getKeyByOp(inOp);
                swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());   //  Touche inOp revient à la normale
                buttons[key.INDEX()].updateDisplay();
                inOp = null;
            }
        }
    }

    private void saveOrExecLineWithLabel(boolean setRunMode) {
        if (!inEditModeAfterSavingLine(tempProgLine)) {
            if (mode.equals(MODES.NORM)) {
                alu.rebuildlabelToProgLineNumberMap();   //  Mettre à jour les lignes existantes
                alu.linkDestProgLineNumbers();
                int dpln = alu.getGTODestProgLineNumber(tempProgLine);   //  L'exec() utilisera le progLine.ref, mis à jour dans les lignes existantes seulement (ou si GTO/GSB I), donc pas dans tempProgLine en mode NORM !
                if (dpln != (-1)) {   //  OK
                    tempProgLine.paramAddress = dpln;
                    if (setRunMode) {
                        switch (tempProgLine.ops[LINE_OPS.BASE.INDEX()]) {
                            case SOLVE:
                                requestStopAfterSolve = true;   //  Nécessaire car un SOLVE ou INTEG ne se termine pas par un RTN comme dans un GSB
                                break;
                            case INTEG:
                                requestStopAfterInteg = true;
                                break;
                            case GSB:
                                nextProgLineNumber = END_RETURN_CODE;
                                break;
                        }
                        nowmRUN = System.currentTimeMillis();
                        mode = MODES.RUN;   //   Exécuter un GSB, SOLVE, INTEG, c'est se mettre en mode RUN car plusieurs lignes à exécuter
                        isAutoLine = true;
                    }
                } else {   //  Invalide
                    error = ERROR_GTO_GSB;
                }
            }
            error = exec(tempProgLine);
        }
    }

    private void interpretDigitOp() {
        if (((currentOp.INDEX() >= OPS.DIGIT_0.INDEX()) && (currentOp.INDEX() <= OPS.DIGIT_9.INDEX())) ||
                (currentOp.equals(OPS.DOT)) || (currentOp.equals(OPS.EEX)) || (currentOp.equals(OPS.CHS))) {

            if (mode.equals(MODES.NORM)) {
                error = exec(tempProgLine);
            }
            if (mode.equals(MODES.EDIT)) {
                if (!inEditModeAfterSavingLine(tempProgLine)) {   //  Enregistrer une ligne avec le chiffre
                    //  NOP
                }
            }
        }
    }

    private void interpretSingleOp() {
        switch (currentOp) {
            case PI:
            case LASTX:
            case RAND:
            case SIGMA_PLUS:
            case SIGMA_MINUS:
            case MEAN:
            case STDEV:
            case LR:
            case YER:
            case SQR:
            case SQRT:
            case TO_RAD:
            case TO_DEG:
            case EXP:
            case LN:
            case EXP10:
            case LOG:
            case POWER:
            case PC:
            case INV:
            case DPC:
            case ABS:
            case RND:
            case POL:
            case RECT:
            case HMS:
            case H:
            case COMB:
            case PERM:
            case FRAC:
            case INTEGER:
            case SIN:
            case COS:
            case TAN:
            case ASIN:
            case ACOS:
            case ATAN:
            case SINH:
            case COSH:
            case TANH:
            case ASINH:
            case ACOSH:
            case ATANH:
            case FACT:
            case PLUS:
            case MINUS:
            case MULT:
            case DIV:
            case DEG:
            case RAD:
            case GRAD:
            case XE0:
            case XLEY:
            case XNE0:
            case XG0:
            case XL0:
            case XGE0:
            case XLE0:
            case XEY:
            case XNEY:
            case XGY:
            case XLY:
            case XGEY:
                if (!inEditModeAfterSavingLine(tempProgLine)) {
                    error = exec(tempProgLine);
                }
                break;
        }
    }

    private void interpretSpecialOp() {
        switch (currentOp) {
            case CLX:
            case CLEAR_PREFIX:
            case CLEAR_REGS:
            case CLEAR_SIGMA:
            case ENTER:
            case RDN:
            case RUP:
            case XCHGXY:
            case RTN:
                if (!inEditModeAfterSavingLine(tempProgLine)) {
                    error = exec(tempProgLine);
                }
                break;
            case BACK:
                if (mode.equals(MODES.NORM)) {
                    error = exec(tempProgLine);
                }
                if (mode.equals(MODES.EDIT)) {
                    if (currentProgLineNumber != 0) {   //  Interdiction d'effacer BEGIN
                        alu.removeProgLineAtNumber(currentProgLineNumber);
                        nextProgLineNumber = dec(currentProgLineNumber);
                    }
                }
                break;
            case USER:
                if (alphaToX()) {
                    user = !user;
                }
                break;
            case BEGIN:   //  Neutre sur StackLift
                if (alphaToX()) {
                    //  NOP
                }
                break;
            case PR:
                boolean sw = false;
                if (!sw) {
                    if (mode.equals(MODES.NORM)) {   //  NORM -> EDIT
                        sw = true;
                        if (alphaToX()) {
                            mode = MODES.EDIT;
                            nextProgLineNumber = currentProgLineNumber;
                        }
                    }
                }
                if (!sw) {   //  On ne vient pas de passer de NORM à EDIT juste avant
                    if (mode.equals(MODES.EDIT)) {   //  EDIT -> NORM
                        sw = true;
                        mode = MODES.NORM;
                    }
                }
                break;
            case RS:
                if (mode.equals(MODES.NORM)) {   //  NORM -> RUN
                    sw = true;
                    if (alphaToX()) {
                        mode = MODES.RUN;
                        isAutoLine = true;
                        nowmRUN = System.currentTimeMillis();
                        alu.rebuildlabelToProgLineNumberMap();   //  Mettre à jour les lignes existantes
                        alu.linkDestProgLineNumbers();
                    }
                }
                if (mode.equals(MODES.EDIT)) {
                    if (!inEditModeAfterSavingLine(tempProgLine)) {
                        //  NOP
                    }
                }
                break;
            case SST:
                if (mode.equals(MODES.NORM)) {
                    if (alphaToX()) {
                        mode = MODES.RUN;
                        isAutoLine = true;   //  Pour exécuter
                        inSST = true;
                        nowmRUN = System.currentTimeMillis();
                        alu.rebuildlabelToProgLineNumberMap();   //  Mettre à jour les lignes existantes
                        alu.linkDestProgLineNumbers();
                    }
                }
                if (mode.equals(MODES.EDIT)) {
                    nextProgLineNumber = inc(currentProgLineNumber);    //  Pas nextProgLineNumber car égal à currentProgLineNumber en mode NORM ou EDIT
                }
                break;
            case BST:
                nextProgLineNumber = dec(currentProgLineNumber);
                if (mode.equals(MODES.NORM)) {
                    if (alphaToX()) {
                        //  NOP   Uniquement reculer, sans exécuter
                    }
                }
                break;
            case CLEAR_PRGM:
                if (mode.equals(MODES.NORM)) {
                    if (alphaToX()) {
                        nextProgLineNumber = 0;
                        alu.clearRetStack();
                        solveParamSet.clear();
                        integParamSet.clear();
                    }
                }
                if (mode.equals(MODES.EDIT)) {
                    if (alphaToX()) {
                        askAndDeletePrograms();   //  Remet aussi currentProgLineNumber à 0
                    }
                }
                break;
            case PSE:
                if (!inEditModeAfterSavingLine(tempProgLine)) {   //  Neutre sur StackLift ???
                    if (mode.equals(MODES.NORM)) {
                        if (alphaToX()) {
                            //   NOP
                        }
                    }
                }
                break;
        }
    }

    private void interpretMultiOpsBegin() {
        switch (currentOp) {
            case FIX:
            case SCI:
            case ENG:
            case STO:
            case RCL:
            case XCHG:
            case HYP:
            case AHYP:
            case TEST:
            case DIM:
            case GTO:
            case GSB:
            case INTEG:
            case SOLVE:
            case LBL:
            case SF:
            case CF:
            case TF:
            case DSE:
            case ISG:
                inOp = currentOp;   //  Début d'instruction MultiOps
                if ((mode.equals(MODES.NORM)) || (mode.equals(MODES.EDIT))) {   //  NORM ou EDIT
                    KEYS key = alu.getKeyByOp(inOp);
                    swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());   //  Touche inOp revient à la normale
                    buttons[key.INDEX()].updateDisplay();
                }
                break;
        }
    }

    private String progLinesToClipBoard() {
        String res = "";
        int n = alu.getProgLines().size();
        if (n > 0) {
            for (int i = 0; i <= (n - 1); i = i + 1) {
                String plc = alu.progLineToString(i, false);   //  Codes
                String pls = alu.progLineToString(i, true);   //  Symbols
                res = res + plc.substring(0, 5) + " { " + plc.substring(6) + " } " + pls.substring(6) + CRLF;   //  Ne pas reprendre de nouveau le n° de ligne de la ligne de symboles
            }
        }
        return res;
    }

    private void encodeKeyCodesFromClipBoard(String clipText) {
        if (clipText != null) {
            String[] lines = clipText.split("\\r?\\n");   //  Splitter selon CR/LF
            int n = lines.length - 1;   //  Pas la ligne 0
            if (n > 0) {
                alu.setupProgLines();
                mode = MODES.EDIT;
                for (int i = 0; i <= n; i = i + 1) {
                    String[] codes = lines[i].split("\\s+");   //   "0001:" "{" "45" "23" "14" "}" "etc"   (les espaces simples ou multiples sont éliminés)
                    int j = 0;
                    int parBeg = -1;
                    int parEnd = -1;
                    while (j <= (codes.length - 1)) {
                        if (codes[j].equals("{")) {
                            parBeg = j;
                        }
                        if (codes[j].equals("}")) {
                            parEnd = j;
                        }
                        if ((parBeg > 0) && (parEnd == (-1)) && (j != parBeg)) {   //   On est dans les opCodes
                            handleCodeToEncode(codes[j]);   //   progLines va progressivement se remplir de toutes ses lignes
                        }
                        j = j + 1;
                    }
                }
                mode = MODES.NORM;
            }
        }
    }

    private void handleCodeToEncode(String code) {
        if (!code.equals("")) {
            if ((code.length() > 1) && (code.substring(0, 1).equals("."))) {   //  .8 ou .9 , ...  => Envoyer Point puis envoyer chiffre
                encodeProgKeyCode(Integer.parseInt("48"));   //  keycode de Point
                encodeProgKeyCode(Integer.parseInt(code.substring(1)));   //  Le reste
            } else {   //  Code normal
                encodeProgKeyCode(Integer.parseInt(code));
            }
        }
    }

    public void encodeKeyCodesFromProgLinesRows(String[][] progLinesRows) {
        String[][] rows = progLinesRows;
        alu.setupProgLines();
        if (progLinesRows != null) {
            int n = rows.length;
            if (n > 0) {
                mode = MODES.EDIT;
                int m = getSp15cTableDataFieldsCount(getProgLinesTableName());   //  Normalement 3 (après le champ ID (n° de ligne)) : p.ex. ID: "1"  Values:"45"  "23"  "24"
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    for (int j = 1; j <= m; j = j + 1) {   //  Après le champ ID (ProgLineNumber)
                        String kc = rows[i][j];
                        if (kc != null) {
                            handleCodeToEncode(kc);   //  progLines va progressivement se remplir de toutes ses lignes
                        }
                    }
                }
                mode = MODES.NORM;
            }
        }
    }

    public String[][] paramsToRows() {
        String[][] res = new String[8][2];
        res[0][TABLE_ID_INDEX] = "ROUND_MODE";
        res[0][TABLE_DATA_INDEX] = alu.getRoundMode().toString();
        res[1][TABLE_ID_INDEX] = "ROUND_PARAM";
        res[1][TABLE_DATA_INDEX] = String.valueOf(alu.getRoundParam());
        res[2][TABLE_ID_INDEX] = "ANGLE_MODE";
        res[2][TABLE_DATA_INDEX] = alu.getAngleMode().toString();
        res[3][TABLE_ID_INDEX] = "NEXT_PROG_LINE_NUMBER";
        res[3][TABLE_DATA_INDEX] = String.valueOf(nextProgLineNumber);
        res[4][TABLE_ID_INDEX] = "CURRENT_PROG_LINE_NUMBER";
        res[4][TABLE_DATA_INDEX] = String.valueOf(currentProgLineNumber);
        res[5][TABLE_ID_INDEX] = "USER";
        res[5][TABLE_DATA_INDEX] = (user ? "1" : "0");
        res[6][TABLE_ID_INDEX] = "DISPLAY_SYMBOL";
        res[6][TABLE_DATA_INDEX] = (displaySymbol ? "1" : "0");
        res[7][TABLE_ID_INDEX] = "STACK_LIFT_ENABLED";
        res[7][TABLE_DATA_INDEX] = (alu.getStackLiftEnabled() ? "1" : "0");
        return res;
    }

    public void rowsToParams(String[][] paramRows) {
        if (paramRows != null) {
            int n = paramRows.length;
            if (n > 0) {
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    String s = paramRows[i][TABLE_ID_INDEX];
                    if (s.equals("ROUND_MODE")) {
                        alu.setRoundMode(OPS.valueOf(paramRows[i][TABLE_DATA_INDEX]));
                    }
                    if (s.equals("ROUND_PARAM")) {
                        alu.setRoundParam(Integer.parseInt(paramRows[i][TABLE_DATA_INDEX]));
                    }
                    if (s.equals("ANGLE_MODE")) {
                        alu.setAngleMode(OPS.valueOf(paramRows[i][TABLE_DATA_INDEX]));
                    }
                    if (s.equals("NEXT_PROG_LINE_NUMBER")) {
                        nextProgLineNumber = Integer.parseInt(paramRows[i][TABLE_DATA_INDEX]);
                    }
                    if (s.equals("CURRENT_PROG_LINE_NUMBER")) {
                        currentProgLineNumber = Integer.parseInt(paramRows[i][TABLE_DATA_INDEX]);
                    }
                    if (s.equals("USER")) {
                        user = (paramRows[i][TABLE_DATA_INDEX].equals("1"));
                    }
                    if (s.equals("DISPLAY_SYMBOL")) {
                        displaySymbol = (paramRows[i][TABLE_DATA_INDEX].equals("1"));
                    }
                    if (s.equals("STACK_LIFT_ENABLED")) {
                        alu.setStackLiftEnabled((paramRows[i][TABLE_DATA_INDEX].equals("1")));
                    }
                }
            }
        }
    }

    private String formatAlphaNumber() {
        int indMax = alpha.length();   //  Faire apparaître le séparateur de milliers au cours de l'entrée de nombre, avant le 1er "." ou "E"
        int indDot = alpha.indexOf(OPS.DOT.SYMBOL());
        if (indDot != (-1)) {   //  "." détecté
            indMax = Math.min(indMax, indDot);
        }
        int indEex = alpha.indexOf(OPS.EEX.SYMBOL());
        if (indEex != (-1)) {   //  "E" détecté
            indMax = Math.min(indMax, indEex);
        }
        int indMin = (alpha.substring(0, 1).equals(OPS.CHS.SYMBOL()) ? 1 : 0);   //  Tenir compte du "-" initial éventuel
        String res = String.format(Locale.US, "%,d", Long.parseLong(alpha.substring(indMin, indMax)));   //  Séparateur de milliers
        if (indMin != 0) {
            res = OPS.CHS.SYMBOL() + res;   //  Ramener le "-" initial éventuel
        }
        if (indMax < alpha.length()) {
            res = res + alpha.substring(indMax);   //  Le reste
        }
        return res;
    }

    private boolean alphaToX() {
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
                alu.setStackRegContent(STACK_REGS.X, d);
                alu.setStackLiftEnabled(true);
                alpha = "";
            } catch (IllegalArgumentException | SecurityException ex) {
                res = false;   //  Echec
            }
        }
        return res;
    }

    public boolean inEditModeAfterSavingLine(ProgLine progLine) {
        boolean res = false;   //  Exécuter la ligne (NORM ou RUN)
        if (mode.equals(MODES.EDIT)) {   //  EDIT
            int pln = currentProgLineNumber + 1;   //  Pas incProgLineNumber(currentProgLineNumber), afin d'éviter Wrap around
            if (alu.addProgLineAtNumber(progLine, pln)) {   //  OK nouvelle ligne en mode EDIT
                nextProgLineNumber = pln;
            } else {
                error = ERROR_PROG_LINES_FULL;
            }
            res = true;   //  Ne pas exécuter la ligne
        }
        return res;
    }

    public int inc(int progLineNumber) {
        int res = progLineNumber + 1;
        if (res == alu.getProgLinesSize()) {   //  N'existe pas => Premier
            res = 0;
        }
        return res;
    }

    public int dec(int progLineNumber) {
        int res = progLineNumber - 1;
        if (res < 0) {
            res = alu.getProgLinesSize() - 1;   //  BEGIN => Dernier
        }
        return res;
    }

    private void askAndDeletePrograms() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete all programs");
        builder.setMessage("Are you sure ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                alu.clearProgLines();
                currentProgLineNumber = 0;    //  Cf onDismiss pour le reste
            }
        });
        builder.setNegativeButton("No", null);
        Dialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {    // OK pour modifier UI sous-jacente à la boîte de dialogue
                dotMatrixDisplayUpdater.displayText(alu.progLineToString(currentProgLineNumber, displaySymbol), false);
                dotMatrixDisplayView.updateDisplay();
                updateSideDisplay();
            }
        });
        dialog.show();
    }

    private void startOrStopAutomaticLine() {
        if (isAutoLine) {
            if (!isAutoL) {
                startAutomaticLine();
            }
        } else {   //  Pas isAutoLine
            if (isAutoL) {
                stopAutomaticLine();
            }
        }
    }

    public void startAutomaticLine() {
        isAutoL = true;
        handlerTimeLine.postDelayed(runnableTimeLine, updateInterval);
    }

    public void stopAutomaticLine() {
        handlerTimeLine.removeCallbacks(runnableTimeLine);
        isAutoL = false;
    }

    private void automaticLine() {
        handlerTimeLine.postDelayed(runnableTimeLine, updateInterval);
        long nowm = System.currentTimeMillis();
        if (!inExecCurrentProgLine) {
            execCurrentProgLine();
        }
        if ((inPSE) && (nowmPSE == 0)) {   //  Nouvelle instruction PSE et Pas d'affichage de X en cours suite à instruction PSE antérieure
            inPSE = false;
            nowmPSE = nowm;
        }
        if (nowmPSE > 0) {   //  Comptage du temps de PSE en cours
            if ((nowm - nowmPSE) >= PSE_MS) {   //  Fin du temps de PSE
                nowmPSE = 0;
                dotMatrixDisplayUpdater.displayText(alu.getRoundXForDisplay(), true);
                dotMatrixDisplayView.updateDisplay();
            }
        } else {   //  Pas de PSE en cours
            if ((nowm - nowmRUN) >= FLASH_RUN_MS) {   //  Fin du temps entre 2 flash
                nowmRUN = nowm;
                dotMatrixDisplayView.invert();
                if (requestStopAfterSolve) {
                    dotMatrixDisplayUpdater.displayText(alu.roundForDisplay(solveParamSet.t), true);
                } else {
                    if (requestStopAfterInteg) {
                        dotMatrixDisplayUpdater.displayText(alu.roundForDisplay(integParamSet.z), true);
                    } else {
                        dotMatrixDisplayUpdater.displayText("Running...", false);
                    }
                }
                dotMatrixDisplayView.updateDisplay();
            }
        }
    }

    public String exec(ProgLine progLine) {
        OPS opBase = progLine.ops[LINE_OPS.BASE.INDEX()];
        boolean common = false;   //  Si True: Sortie classique de fonction: stackLiftEnabled=true, lastx si erreur

        switch (opBase) {   //  Le GIANT
            case FIX:
            case SCI:
            case ENG:
                if (alphaToX()) {
                    alu.setRoundMode(progLine.ops[LINE_OPS.BASE.INDEX()]);
                    int n = (progLine.ops[LINE_OPS.I.INDEX()] != null ? (int) alu.getRegContentsByIndex(progLine.paramAddress) : Integer.valueOf(progLine.symbol));
                    alu.setRoundParam(n);
                }
                break;
            case STO:
                if (alphaToX()) {
                    if (progLine.ops[LINE_OPS.RAND.INDEX()] == null) {   //  STO RAN# sans effet
                        int regIndex = progLine.paramAddress;
                        if (progLine.ops[LINE_OPS.INDI.INDEX()] != null) {   //  (i))
                            int dataRegIndex = (int) alu.getRegContentsByIndex(BASE_REGS.RI.INDEX());   //  Valeur dans I
                            regIndex = alu.getRegIndexByDataRegIndex(dataRegIndex);
                            if ((regIndex < 0) || (regIndex > alu.getRegsMaxIndex())) {
                                error = ERROR_INDEX;
                            }
                        }
                        if (error.length() == 0) {
                            error = (progLine.ops[LINE_OPS.A4OP.INDEX()] != null ? alu.xToReg4Op(regIndex, progLine.ops[LINE_OPS.A4OP.INDEX()]) : alu.xToReg(regIndex));
                        }
                    }
                    if (error.length() == 0) {
                        alu.setStackLiftEnabled(true);
                    }
                }
                break;
            case RCL:
                if (alphaToX()) {
                    alu.doStackLiftIfEnabled();
                    if (progLine.ops[LINE_OPS.DIM.INDEX()] != null) {   //  RCL DIM (i)
                        int n = alu.getRegsMaxIndex();
                        alu.setStackRegContent(STACK_REGS.X, alu.getDataRegIndexByIndex(n));
                    } else {   //  Pas RCL DIM (i)
                        if (progLine.ops[LINE_OPS.SIGMA_PLUS.INDEX()] != null) {   //  RCL SIGMA_PLUS
                            alu.doStackLift();    //  Un stacklift obligatoire + un deuxième (cf supra) si stackLift activé
                            error = alu.sumXYToXY();
                        } else {   //  Pas RCL SIGMA_PLUS
                            int regIndex = progLine.paramAddress;
                            if (progLine.ops[LINE_OPS.INDI.INDEX()] != null) {   //  (i))
                                int dataRegIndex = (int) alu.getRegContentsByIndex(BASE_REGS.RI.INDEX());   //  Valeur dans I
                                regIndex = alu.getRegIndexByDataRegIndex(dataRegIndex);
                                if ((regIndex < 0) || (regIndex > alu.getRegsMaxIndex())) {
                                    error = ERROR_INDEX;
                                }
                            }
                            if (error.length() == 0) {
                                error = (progLine.ops[LINE_OPS.A4OP.INDEX()] != null ? alu.regToX4Op(regIndex, progLine.ops[LINE_OPS.A4OP.INDEX()]) : alu.regToX(regIndex));
                            }
                        }
                    }
                    if (error.length() == 0) {
                        alu.setStackLiftEnabled(true);
                    }
                }
                break;
            case DIM:
                if (alphaToX()) {
                    int n = (int) alu.getStackRegContents(STACK_REGS.X);
                    error = alu.setMaxDataRegIndex(n);
                    if (error.length() == 0) {
                        alu.setStackLiftEnabled(true);
                    }
                }
                break;
            case XCHG:
                if (alphaToX()) {
                    int regIndex = progLine.paramAddress;
                    if (progLine.ops[LINE_OPS.INDI.INDEX()] != null) {   //  (i))
                        int dataRegIndex = (int) alu.getRegContentsByIndex(BASE_REGS.RI.INDEX());   //  Valeur dans I
                        regIndex = alu.getRegIndexByDataRegIndex(dataRegIndex);
                        if ((regIndex < 0) || (regIndex > alu.getRegsMaxIndex())) {
                            error = ERROR_INDEX;
                        }
                    }
                    if (error.length() == 0) {
                        error = alu.xXchgReg(regIndex);
                        if (error.length() == 0) {
                            alu.setStackLiftEnabled(true);
                        }
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
                        int dpln = alu.getGTODestProgLineNumber(progLine);
                        if (dpln != (-1)) {   //  OK
                            nextProgLineNumber = dpln;
                        } else {   //  Invalide
                            error = ERROR_GTO_GSB;
                        }
                    } else {   //  Pas GTO I
                        nextProgLineNumber = progLine.paramAddress;
                    }
                }
                break;
            case GSB:    //  Neutre sur StackLift ???
                if (alphaToX()) {
                    if (!alu.pushProgLineNumber(nextProgLineNumber)) {   //  Si False => MAX_RETS dépassé
                        error = ERROR_RET_STACK_FULL;
                    } else {   //  OK Push
                        if (progLine.ops[LINE_OPS.I.INDEX()] != null) {   //  GSB I => recalculer selon I
                            int dpln = alu.getGTODestProgLineNumber(progLine);
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
                        solveParamSet.retLevel = alu.getRetStackSize();
                        solveParamSet.tol = Math.pow(10, -alu.MAX_DIGITS - 2);
                        solveParamSet.iterCount = 0;
                        solveParamSet.a = alu.getStackRegContents(STACK_REGS.Y);   //  Guess 1
                        solveParamSet.b = alu.getStackRegContents(STACK_REGS.X);   //  Guess 2
                        solveParamSet.separateAB();   //   Si a = b (à 1E-14 max près) => Séparer a et b avec une différence de 1E-6
                        error = solveConfigForEvalUserFx(solveParamSet.a);
                    }
                    if (solveParamSet.count == 2) {
                        if (alu.getRetStackSize() != solveParamSet.retLevel) {
                            error = ERROR_NESTED_SOLVE;
                        } else {   //  Pas de Solve imbriqués, on continue
                            solveParamSet.r = alu.getStackRegContents(STACK_REGS.X);   //  f(a)
                            error = solveConfigForEvalUserFx(solveParamSet.b);
                        }
                    }
                    if (solveParamSet.count == 3) {
                        solveParamSet.s = alu.getStackRegContents(STACK_REGS.X);   //  f(b)
                        error = solveParamSet.transform();
                        if (error.length() == 0) {
                            solveParamSet.c = solveParamSet.t;
                            error = solveConfigForEvalUserFx(solveParamSet.c);
                        }
                    }
                    if (solveParamSet.count >= 4) {
                        solveParamSet.q = alu.getStackRegContents(STACK_REGS.X);   //  f(c)
                        solveParamSet.setNextLevel();
                        error = solveParamSet.transform();   //  Nouvelle estimation dans t
                        if (error.length() == 0) {
                            double newX = solveParamSet.t;
                            if (Math.abs(newX - solveParamSet.c) <= solveParamSet.tol) {   //  OK c'est bon
                                alu.setStackRegContent(STACK_REGS.X, newX);
                                alu.setStackRegContent(STACK_REGS.Y, solveParamSet.c);
                                alu.setStackRegContent(STACK_REGS.Z, solveParamSet.q);
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
                common = true;
                break;
            case INTEG:
                if (alphaToX()) {
                    integParamSet.count = integParamSet.count + 1;
                    if (integParamSet.count == 1) {  //  Initialisation et début de traitement
                        integParamSet.oldNextProgLineNumber = nextProgLineNumber;
                        integParamSet.userFxLineNumber = progLine.paramAddress;
                        integParamSet.retLevel = alu.getRetStackSize();
                        integParamSet.tol = Math.pow(10, -alu.getRoundParam() - 2);
                        integParamSet.iterCount = 0;
                        integParamSet.a = alu.getStackRegContents(STACK_REGS.Y);   //  a
                        integParamSet.b = alu.getStackRegContents(STACK_REGS.X);   //  b
                        integParamSet.h = integParamSet.b - integParamSet.a;
                        integParamSet.n = 1;
                        integParamSet.l = integParamSet.n;
                        integParamSet.u = 0;
                        integParamSet.z = 1e99;
                        error = integConfigForEvalUserFx(integParamSet.a);
                    }
                    if (integParamSet.count == 2) {
                        if (alu.getRetStackSize() != integParamSet.retLevel) {
                            error = ERROR_NESTED_INTEG;
                        } else {   //  Pas de Integ imbriqués, on continue
                            integParamSet.p = alu.getStackRegContents(STACK_REGS.X);   //  f(a)
                            error = integConfigForEvalUserFx(integParamSet.b);
                        }
                    }
                    if (integParamSet.count >= 3) {
                        if (integParamSet.n == 1) {
                            integParamSet.p = integParamSet.p + alu.getStackRegContents(STACK_REGS.X);   //  f(a) + f(b)
                            integParamSet.setNextLevel();
                            integParamSet.x = integParamSet.a + integParamSet.h;    //  1er point impair
                            error = integConfigForEvalUserFx(integParamSet.x);
                        } else {   //  n > 1
                            integParamSet.sumFx = integParamSet.sumFx + alu.getStackRegContents(STACK_REGS.X);   //   Mettre à jour la somme des y des points impairs
                            integParamSet.countFx = integParamSet.countFx + 1;
                            if (integParamSet.countFx >= integParamSet.countFxMax) {   //  La somme est complète, on peut calculer la prochaine estimation
                                double oldInteg = integParamSet.z;
                                integParamSet.calc();   //  Nouvelle estimation dans z
                                double diff = Math.abs(integParamSet.z - oldInteg);
                                if (diff <= integParamSet.tol) {   //  OK c'est bon
                                    alu.setStackRegContent(STACK_REGS.X, integParamSet.z);
                                    alu.setStackRegContent(STACK_REGS.Y, diff);
                                    alu.setStackRegContent(STACK_REGS.Z, integParamSet.b);
                                    alu.setStackRegContent(STACK_REGS.T, integParamSet.a);
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
                            } else {   //  La somme n'est pas encore complète
                                integParamSet.x = integParamSet.x + integParamSet.h * 2.0;    //  Prochain point impair
                                error = integConfigForEvalUserFx(integParamSet.x);
                            }
                        }
                    }
                }
                common = true;
                break;
            case DSE:
            case ISG:
                if (alphaToX()) {
                    int regIndex = progLine.paramAddress;
                    if (progLine.ops[LINE_OPS.INDI.INDEX()] != null) {   //  (i))
                        int dataRegIndex = (int) alu.getRegContentsByIndex(BASE_REGS.RI.INDEX());   //  Valeur dans I
                        regIndex = alu.getRegIndexByDataRegIndex(dataRegIndex);
                        if ((regIndex < 0) || (regIndex > alu.getRegsMaxIndex())) {
                            error = ERROR_INDEX;
                        }
                    }
                    double value = alu.getRegContentsByIndex(regIndex);   //  value: counter(nnnnn).goal(nnn)step(nn)
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
                    if (opBase.equals(OPS.DSE)) {
                        counter = counter - step2;
                        if (counter <= goal) {
                            nextProgLineNumber = inc(nextProgLineNumber);
                        }
                    }
                    if (opBase.equals(OPS.ISG)) {
                        counter = counter + step2;
                        if (counter > goal) {
                            nextProgLineNumber = inc(nextProgLineNumber);
                        }
                    }
                    value = Math.abs((double) counter) + ((double) goal + (double) step / 100d) / 1000d;
                    if (counter < 0) {
                        value = -value;
                    }
                    alu.setRegContentsByIndex(regIndex, value);   //  Update register
                    alu.setStackLiftEnabled(true);
                }
                break;
            case SF:
            case CF:
            case TF:
                if (alphaToX()) {
                    int flagIndex = Integer.valueOf(progLine.ops[LINE_OPS.A09.INDEX()].SYMBOL());
                    if (opBase.equals(OPS.SF)) {
                        alu.setFlag(flagIndex);
                    }
                    if (opBase.equals(OPS.CF)) {
                        alu.clearFlag(flagIndex);
                    }
                    if (opBase.equals(OPS.TF)) {
                        if (!alu.testFlag(flagIndex)) {   //  Skip next line if flag cleared
                            nextProgLineNumber = inc(nextProgLineNumber);
                        }
                        if (mode.equals(MODES.NORM)) {   //  Afficher sa valeur ("True" ou "False"))
                            error = (alu.testFlag(flagIndex) ? "True" : "False");
                        }
                    }
                    alu.setStackLiftEnabled(true);
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
                    if (!opBase.equals(OPS.CHS)) {  //  StackLift éventuel uniquement si entrée d'un nombre (ne commençant pas par "-")
                        alu.doStackLiftIfEnabled();
                    }
                }
                boolean acceptOp = true;
                int indEex = beta.indexOf(OPS.EEX.SYMBOL());
                if (indEex != (-1)) {   //  Ne plus accepter de "." ou "E" après un "E" antérieur
                    if ((opBase.equals(OPS.DOT)) || (opBase.equals(OPS.EEX))) {
                        acceptOp = false;
                    }
                }
                int indDot = beta.indexOf(OPS.DOT.SYMBOL());
                if (indDot != (-1)) {   //  Ne plus accepter de "." après un "." antérieur
                    if (opBase.equals(OPS.DOT)) {
                        acceptOp = false;
                    }
                }
                if (acceptOp) {
                    if (opBase.equals(OPS.CHS)) {
                        if (beta.equals("")) {   //  Un nombre ne peut commencer par "-" => Changer le signe de X
                            alu.negX();
                            alu.setStackLiftEnabled(true);
                        } else {   //  Entrée de nombre en cours
                            int indChs1 = beta.indexOf(opBase.SYMBOL());   //  Un "-" existe peut-être déjà => -x ou xE-x ou -xEx ou -xE-x
                            int indChs2 = -1;
                            if ((indChs1 != -1) && (indChs1 < (beta.length() - 1))) {   //  Un 2e "-" est possible   => -xE-x
                                indChs2 = beta.indexOf(opBase.SYMBOL(), indChs1 + 1);   //  après le 1er
                            }
                            if (indChs1 != (-1)) {   //   -x ou xE-x ou -xEx ou -xE-x
                                if (indEex != -1) {   //  xE-x ou -xEx ou -xE-x
                                    if (indChs1 < indEex) {   //  -xEx ou -xE-x
                                        if (indChs2 != -1) {   //  -xE-x
                                            beta = beta.substring(0, indChs2) + beta.substring(indChs2 + 1);   //  => -xEx
                                        } else {   //  -xEx
                                            beta = beta.substring(0, indEex + 1) + opBase.SYMBOL() + beta.substring(indEex + 1);   //  => -xE-x
                                        }
                                    } else {   //  xE-x
                                        beta = beta.substring(0, indChs1) + beta.substring(indChs1 + 1);   //  => xEx
                                    }
                                } else {   //  -x
                                    beta = beta.substring(indChs1 + 1);   //  => x
                                }
                            } else {   //  x ou xEx
                                if (indEex != (-1)) {   //  xEx
                                    beta = beta.substring(0, indEex + 1) + opBase.SYMBOL() + beta.substring(indEex + 1);   //  => xE-x
                                } else {   //  x
                                    beta = opBase.SYMBOL() + beta;   //  => -x
                                }
                            }
                        }
                    } else {   //  Pas CHS
                        String s = opBase.SYMBOL();
                        if (beta.equals("")) {
                            if (opBase.equals(OPS.EEX)) {
                                s = OPS.DIGIT_1.SYMBOL() + s;   //  Ajout de 1 en préfixe si commence par "E"
                            }
                            if (opBase.equals(OPS.DOT)) {
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
                    alu.doStackLiftIfEnabled();
                    error = alu.piToX();
                    if (error.length() == 0) {
                        alu.setStackLiftEnabled(true);
                    }
                }
                break;
            case LASTX:
                if (alphaToX()) {
                    alu.doStackLiftIfEnabled();
                    error = alu.lastXToX();
                    if (error.length() == 0) {
                        alu.setStackLiftEnabled(true);
                    }
                }
                break;
            case RAND:
                if (alphaToX()) {
                    alu.doStackLiftIfEnabled();
                    error = alu.randToX();
                    if (error.length() == 0) {
                        alu.setStackLiftEnabled(true);
                    }
                }
                break;
            case SIGMA_PLUS:   //  Désactive stackLift
                if (alphaToX()) {
                    error = alu.sigmaPlus();
                    if (error.length() == 0) {
                        alu.setStackLiftEnabled(false);
                    } else {
                        alu.lastXToX();
                    }
                }
                break;
            case SIGMA_MINUS:   //  Désactive stackLift
                if (alphaToX()) {
                    error = alu.sigmaMinus();
                    if (error.length() == 0) {
                        alu.setStackLiftEnabled(false);
                    } else {
                        alu.lastXToX();
                    }
                }
                break;
            case MEAN:   //  Désactive stackLift
                if (alphaToX()) {
                    alu.saveStack();
                    alu.doStackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                    alu.doStackLiftIfEnabled();
                    error = alu.mean();
                    if (error.length() == 0) {
                        alu.setStackLiftEnabled(true);
                    } else {
                        alu.restoreStack();
                    }
                }
                break;
            case STDEV:
                if (alphaToX()) {
                    alu.saveStack();
                    alu.doStackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                    alu.doStackLiftIfEnabled();
                    error = alu.stDev();
                    if (error.length() == 0) {
                        alu.setStackLiftEnabled(true);
                    } else {
                        alu.restoreStack();
                    }
                }
                break;
            case LR:
                if (alphaToX()) {
                    alu.saveStack();
                    alu.doStackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                    alu.doStackLiftIfEnabled();
                    error = alu.lr();
                    if (error.length() == 0) {
                        alu.setStackLiftEnabled(true);
                    } else {
                        alu.restoreStack();
                    }
                }
                break;
            case YER:
                if (alphaToX()) {
                    alu.saveStack();
                    alu.doStackLift();    //  Un stacklift obligatoire
                    error = alu.yer();
                    if (error.length() == 0) {
                        alu.setStackLiftEnabled(true);
                    } else {
                        alu.restoreStack();
                    }
                }
                break;
            case SQR:
                if (alphaToX()) {
                    error = alu.sqrX();
                    common = true;
                }
                break;
            case SQRT:
                if (alphaToX()) {
                    error = alu.sqrtX();
                    common = true;
                }
                break;
            case TO_RAD:
                if (alphaToX()) {
                    error = alu.xToRad();
                    common = true;
                }
                break;
            case TO_DEG:
                if (alphaToX()) {
                    error = alu.xToDeg();
                    common = true;
                }
                break;
            case EXP:
                if (alphaToX()) {
                    error = alu.expX();
                    common = true;
                }
                break;
            case LN:
                if (alphaToX()) {
                    error = alu.lnX();
                    common = true;
                }
                break;
            case EXP10:
                if (alphaToX()) {
                    error = alu.exp10X();
                    common = true;
                }
                break;
            case LOG:
                if (alphaToX()) {
                    error = alu.logX();
                    common = true;
                }
                break;
            case POWER:
                if (alphaToX()) {
                    error = alu.pow();
                    if (error.length() == 0) {
                        alu.stackMergeDown();
                        alu.setStackLiftEnabled(true);
                    } else {
                        alu.lastXToX();
                    }
                }
                break;
            case PC:
                if (alphaToX()) {
                    error = alu.xPcY();   //  Pas de mergeDown
                    common = true;
                }
                break;
            case INV:
                if (alphaToX()) {
                    error = alu.invX();
                    common = true;
                }
                break;
            case DPC:
                if (alphaToX()) {
                    error = alu.xDpcY();   //  Pas de mergeDown
                    common = true;
                }
                break;
            case ABS:
                if (alphaToX()) {
                    error = alu.absX();
                    common = true;
                }
                break;
            case RND:
                if (alphaToX()) {
                    error = alu.rndX();
                    common = true;
                }
                break;
            case POL:
                if (alphaToX()) {
                    error = alu.xyToPol();
                    common = true;
                }
                break;
            case RECT:
                if (alphaToX()) {
                    error = alu.xyToRect();
                    common = true;
                }
                break;
            case HMS:
                if (alphaToX()) {
                    error = alu.hmsX();
                    common = true;
                }
                break;
            case H:
                if (alphaToX()) {
                    error = alu.hX();
                    common = true;
                }
                break;
            case COMB:
                if (alphaToX()) {
                    error = alu.xyToComb();
                    common = true;
                }
                break;
            case PERM:
                if (alphaToX()) {
                    error = alu.xyToPerm();
                    common = true;
                }
                break;
            case FRAC:
                if (alphaToX()) {
                    error = alu.fracX();
                    common = true;
                }
                break;
            case INTEGER:
                if (alphaToX()) {
                    error = alu.integerX();
                    common = true;
                }
                break;
            case SIN:
                if (alphaToX()) {
                    error = alu.sinX();
                    common = true;
                }
                break;
            case COS:
                if (alphaToX()) {
                    error = alu.cosX();
                    common = true;
                }
                break;
            case TAN:
                if (alphaToX()) {
                    error = alu.tanX();
                    common = true;
                }
                break;
            case ASIN:
                if (alphaToX()) {
                    error = alu.asinX();
                    common = true;
                }
                break;
            case ACOS:
                if (alphaToX()) {
                    error = alu.acosX();
                    common = true;
                }
                break;
            case ATAN:
                if (alphaToX()) {
                    error = alu.atanX();
                    common = true;
                }
                break;
            case SINH:
                if (alphaToX()) {
                    error = alu.sinhX();
                    common = true;
                }
                break;
            case COSH:
                if (alphaToX()) {
                    error = alu.coshX();
                    common = true;
                }
                break;
            case TANH:
                if (alphaToX()) {
                    error = alu.tanhX();
                    common = true;
                }
                break;
            case ASINH:
                if (alphaToX()) {
                    error = alu.asinhX();
                    common = true;
                }
                break;
            case ACOSH:
                if (alphaToX()) {
                    error = alu.acoshX();
                    common = true;
                }
                break;
            case ATANH:
                if (alphaToX()) {
                    error = alu.atanhX();
                    common = true;
                }
                break;
            case FACT:
                if (alphaToX()) {
                    error = alu.factX();
                    common = true;
                }
                break;
            case PLUS:
                if (alphaToX()) {
                    error = alu.yPlusX();
                    if (error.length() == 0) {
                        alu.stackMergeDown();
                        alu.setStackLiftEnabled(true);
                    } else {
                        alu.lastXToX();
                    }
                }
                break;
            case MINUS:
                if (alphaToX()) {
                    error = alu.yMinusX();
                    if (error.length() == 0) {
                        alu.stackMergeDown();
                        alu.setStackLiftEnabled(true);
                    } else {
                        alu.lastXToX();
                    }
                }
                break;
            case MULT:
                if (alphaToX()) {
                    error = alu.yMultX();
                    if (error.length() == 0) {
                        alu.stackMergeDown();
                        alu.setStackLiftEnabled(true);
                    } else {
                        alu.lastXToX();
                    }
                }
                break;
            case DIV:
                if (alphaToX()) {
                    error = alu.yDivX();
                    if (error.length() == 0) {
                        alu.stackMergeDown();
                        alu.setStackLiftEnabled(true);
                    } else {
                        alu.lastXToX();
                    }
                }
                break;
            case DEG:
            case RAD:
            case GRAD:   //  Neutre sur stackLift
                if (alphaToX()) {
                    OPS op = progLine.ops[LINE_OPS.BASE.INDEX()];
                    alu.setAngleMode(op);
                }
                break;
            case XE0:
            case XLEY:
            case XNE0:
            case XG0:
            case XL0:
            case XGE0:
            case XLE0:
            case XEY:
            case XNEY:
            case XGY:
            case XLY:
            case XGEY:
                if (alphaToX()) {
                    if (alu.test(opBase)) {
                        nextProgLineNumber = inc(nextProgLineNumber);
                    }
                }
                break;
            case BACK:
                if (alpha.length() >= 2) {
                    alpha = alpha.substring(0, alpha.length() - 1);   //  Enlever le dernier caractère
                    if (alpha.equals(OPS.CHS.SYMBOL())) {   //  p.ex. "-0.3" -> "-0." -> "-0" -> "-" : Boum  (cf String.format plus bas, sur la partie avant le "E" et le ".")
                        alu.clX();
                        alu.setStackLiftEnabled(false);
                        alpha = "";
                    }
                } else {   //  alpha a 0 ou 1 caractères
                    alu.clX();
                    alu.setStackLiftEnabled(false);     // Désactive stacklift
                    alpha = "";
                }
                break;
            case CLX:    // Désactive stacklift
                if (alphaToX()) {
                    error = alu.clX();
                    if (error.length() == 0) {
                        alu.setStackLiftEnabled(false);
                    }
                }
                break;
            case CLEAR_PREFIX:    //  Neutre sur StackLift
                if (alphaToX()) {
                    error = alu.prefX();
                }  //  error sera <>"" dans tous les cas (car représentera la mantisse) et sera donc traité comme une erreur
                break;
            case CLEAR_REGS:   //  Neutre sur StackLift
                if (alphaToX()) {
                    error = alu.clearRegs();
                }
                break;
            case CLEAR_SIGMA:   //  Neutre sur StackLift
                if (alphaToX()) {
                    error = alu.clearStats();
                    alu.clearStackRegs();
                }
                break;
            case ENTER:   // Désactive Stacklift
                if (alphaToX()) {
                    alu.doStackLift();
                    alu.setStackLiftEnabled(false);
                }
                break;
            case RDN:
                if (alphaToX()) {
                    alu.stackRollDown();
                    alu.setStackLiftEnabled(true);
                }
                break;
            case RUP:
                if (alphaToX()) {
                    alu.stackRollUp();
                    alu.setStackLiftEnabled(true);
                }
                break;
            case XCHGXY:
                if (alphaToX()) {
                    alu.xchgXY();
                    alu.setStackLiftEnabled(true);
                }
                break;
            case BEGIN:
                if (alphaToX()) {
                    if (isWrapAround) {   //  On est passé de la fin au début => STOP
                        isWrapAround = false;
                        mode = MODES.NORM;
                        isAutoLine = false;
                        alu.clearRetStack();
                    }
                }
                break;
            case RTN:   //  Neutre sur StackLift ???
                if (alphaToX()) {
                    if (!alu.isRetStackEmpty()) {  //  La pile d'appels n'est pas vide
                        int dpln = alu.popProgLineNumber();
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
        if (common) {
            if (error.length() == 0) {
                alu.setStackLiftEnabled(true);
            } else {
                alu.lastXToX();
            }
        }
        return error;
    }

    private String solveConfigForEvalUserFx(double x) {
        alu.fillStack(x);   //  C'est ainsi que procède la HP-15C
        if (!alu.pushProgLineNumber(SOLVE_RETURN_CODE)) {   //  Push Code de retour spécial après évaluation de UserFx => Retour à SOLVE  cf (RTN);    Si False => MAX_RETS dépassé
            error = ERROR_RET_STACK_FULL;
        } else {  //  OK Push
            nextProgLineNumber = solveParamSet.userFxLineNumber;   //  Emplacement de UserFx à exécuter
        }
        return error;
    }

    private String integConfigForEvalUserFx(double x) {
        alu.fillStack(x);   //  C'est ainsi que procède la HP-15C
        if (!alu.pushProgLineNumber(INTEG_RETURN_CODE)) {   //  Push Code de retour spécial après évaluation de UserFx => Retour à INTEG  cf (RTN);    Si False => MAX_RETS dépassé
            error = ERROR_RET_STACK_FULL;
        } else {  //  OK Push
            nextProgLineNumber = integParamSet.userFxLineNumber;   //  Emplacement de UserFx à exécuter
        }
        return error;
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
        final float BUTTON_MID_16_IMAGE_SIZE_COEFF = 0.6f;
        final float BUTTON_TOP_20_IMAGE_SIZE_COEFF = 0.8f;
        final float BUTTON_MID_21_IMAGE_SIZE_COEFF = 0.59f;
        final float BUTTON_MID_22_IMAGE_SIZE_COEFF = 0.6f;
        final float BUTTON_MID_23_IMAGE_SIZE_COEFF = 0.57f;
        final float BUTTON_MID_24_IMAGE_SIZE_COEFF = 0.63f;
        final float BUTTON_MID_25_IMAGE_SIZE_COEFF = 0.57f;
        final float BUTTON_MID_26_IMAGE_SIZE_COEFF = 0.58f;
        final float BUTTON_MID_30_IMAGE_SIZE_COEFF = 0.38f;
        final float BUTTON_MID_31_IMAGE_SIZE_COEFF = 0.55f;
        final float BUTTON_MID_32_IMAGE_SIZE_COEFF = 0.6f;
        final float BUTTON_MID_33_IMAGE_SIZE_COEFF = 0.55f;
        final float BUTTON_MID_35_IMAGE_SIZE_COEFF = 0.5f;
        final float BUTTON_MID_36_IMAGE_SIZE_COEFF = 0.85f;
        final float BUTTON_MID_41_IMAGE_SIZE_COEFF = 0.57f;
        final float BUTTON_MID_44_IMAGE_SIZE_COEFF = 0.58f;
        final float BUTTON_MID_45_IMAGE_SIZE_COEFF = 0.56f;
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

                        if ((key.equals(KEYS.KEY_16)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image CHS à adapter
                            imageSizeCoeff = BUTTON_MID_16_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_20)) && (legendPos.equals(LEGEND_POS.TOP))) {   //  Image INTEG à adapter
                            imageSizeCoeff = BUTTON_TOP_20_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_21)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image SST à adapter
                            imageSizeCoeff = BUTTON_MID_21_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_22)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image GTO à adapter
                            imageSizeCoeff = BUTTON_MID_22_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_23)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image SIN à adapter
                            imageSizeCoeff = BUTTON_MID_23_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_24)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image COS à adapter
                            imageSizeCoeff = BUTTON_MID_24_IMAGE_SIZE_COEFF;
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
                        if ((key.equals(KEYS.KEY_31)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image R/S à adapter
                            imageSizeCoeff = BUTTON_MID_31_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_32)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image GSB à adapter
                            imageSizeCoeff = BUTTON_MID_32_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_33)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image RDN à adapter
                            imageSizeCoeff = BUTTON_MID_33_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_35)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image BACK à adapter
                            imageSizeCoeff = BUTTON_MID_35_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_36)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image ENTER à adapter
                            imageSizeCoeff = BUTTON_MID_36_IMAGE_SIZE_COEFF;
                        }
                        if ((key.equals(KEYS.KEY_41)) && (legendPos.equals(LEGEND_POS.MID))) {   //  Image ON à adapter
                            imageSizeCoeff = BUTTON_MID_41_IMAGE_SIZE_COEFF;
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
                        if (key.equals(KEYS.KEY_21)) {   //  Click Down sur SST => Afficher ProgLine courante
                            buttons[key.INDEX()].setOnCustomClickDownListener(new ImageButtonViewStack.onCustomClickDownListener() {
                                @Override
                                public void onCustomClickDown() {
                                    onSSTClickDown();
                                }
                            });
                            buttons[key.INDEX()].setOnCustomClickLeaveListener(new ImageButtonViewStack.onCustomClickLeaveListener() {
                                @Override
                                public void onCustomClickLeave() {   //  Quitter SST sans cliquer => Affichage normal
                                    onSSTClickLeave();
                                }
                            });
                        }
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

    private void setupBarMenuItems() {
        final String BAR_MENU_ITEM_KEEP_SCREEN_NAME = "BAR_MENU_ITEM_KEEP_SCREEN";

        Class rid = R.id.class;
        try {
            barMenuItemKeepScreen = menu.findItem(rid.getField(BAR_MENU_ITEM_KEEP_SCREEN_NAME).getInt(rid));
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex) {
            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    private void setupRunnableTimeLine() {
        handlerTimeLine = new Handler();
        runnableTimeLine = new Runnable() {
            @Override
            public void run() {
                automaticLine();
            }
        };
    }

    private void setupStringDB() {
        stringDB = new StringDB(this);
        stringDB.open();

        String DBDataVersion = (stringDB.tableExists(getAppInfosTableName())) ? getCurrent(stringDB, getAppInfosTableName(), getAppInfosDataVersionIndex()) : null;
        int ver = (DBDataVersion != null) ? Integer.parseInt(DBDataVersion) : 0;
        if (ver != DATA_VERSION) {   //  Données invalides => Tout réinitialiser, avec données par défaut
            stringDB.deleteTableIfExists(getAppInfosTableName());
            stringDB.deleteTableIfExists(getActivityInfosTableName());
            stringDB.deleteTableIfExists(getStackRegsTableName());
            stringDB.deleteTableIfExists(getFlagsTableName());
            stringDB.deleteTableIfExists(getRegsTableName());
            stringDB.deleteTableIfExists(getRetStackTableName());
            stringDB.deleteTableIfExists(getProgLinesTableName());
            msgBox("All Data Deleted (Invalid)", this);
        }

        if (!stringDB.tableExists(getAppInfosTableName())) {
            createPekislibTableIfNotExists(stringDB, getAppInfosTableName());    //  Réinitialiser
            setCurrent(stringDB, getAppInfosTableName(), getAppInfosDataVersionIndex(), String.valueOf(DATA_VERSION));
        }
        if (!stringDB.tableExists(getActivityInfosTableName())) {
            createPekislibTableIfNotExists(stringDB, getActivityInfosTableName());
        }
        if (!stringDB.tableExists(getStackRegsTableName())) {
            createSp15cTableIfNotExists(stringDB, getStackRegsTableName());
            //initializeTableStackRegs(stringDB);
            //String[] defaults = getDefaults(stringDB, getStackRegsTableName());
            //setCurrentsForActivity(stringDB, SP15C_ACTIVITIES.CT_DISPLAY.toString(), getDotMatrixDisplayColorsTableName(), defaults);
            //createPresetWithDefaultValues(getDotMatrixDisplayColorsTableName(), defaults);   //  => PRESET1 = DEFAULT  dans la table de couleurs de DotMatrixDisplay
        }
        if (!stringDB.tableExists(getFlagsTableName())) {
            createSp15cTableIfNotExists(stringDB, getFlagsTableName());
        }
        if (!stringDB.tableExists(getRegsTableName())) {
            createSp15cTableIfNotExists(stringDB, getRegsTableName());
        }
        if (!stringDB.tableExists(getRetStackTableName())) {
            createSp15cTableIfNotExists(stringDB, getRetStackTableName());
        }
        if (!stringDB.tableExists(getProgLinesTableName())) {
            createSp15cTableIfNotExists(stringDB, getProgLinesTableName());
        }
        if (!stringDB.tableExists(getParamsTableName())) {
            createSp15cTableIfNotExists(stringDB, getParamsTableName());
        }
    }

    private void launchHelpActivity() {
        Intent callingIntent = new Intent(this, HelpActivity.class);
        callingIntent.putExtra(PEKISLIB_ACTIVITY_EXTRA_KEYS.TITLE.toString(), HELP_ACTIVITY_TITLE);
        callingIntent.putExtra(HelpActivity.HELP_ACTIVITY_EXTRA_KEYS.HTML_ID.toString(), R.raw.helpmainactivity);
        startActivity(callingIntent);
    }

}