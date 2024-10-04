package com.example.pgyl.sp15c_a;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.pgyl.sp15c_a.ProgLine.LINE_OPS;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.pgyl.pekislib_a.MiscUtils.msgBox;
import static com.example.pgyl.pekislib_a.TimeDateUtils.MILLISECONDS_PER_SECOND;

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

    private final String ERROR_INDEX = "Invalid index";
    private final String ERROR_GTO_GSB = "Invalid GTO/GSB";
    private final String ERROR_LINE_NUMBER = "Invalid Line num";
    private final String ERROR_RET_STACK_FULL = "Ret stack full";
    private final String ERROR_PROG_LINES_FULL = "Prog lines full";
    private final String ERROR_KEYBOARD_INTERRUPT = "Keyboard Break";
    private final long PSE_MS = MILLISECONDS_PER_SECOND;   //  1 seconde
    private final long FLASH_RUN_MS = MILLISECONDS_PER_SECOND / 2;   //  1/2 seconde
    private final long AUTO_UPDATE_INTERVAL_MS = MILLISECONDS_PER_SECOND / 50;   //  20 ms

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
    private boolean displaySymbol;
    private boolean user;
    private OPS inOp = null;
    private ProgLine tempProgLine;
    private ProgLine readProgLine;
    private long updateInterval;
    private long nowmPSE;
    private long nowmRUN;
    private int readProgLineOpIndex;
    private boolean lineIsGhostKey;
    private Handler handlerTime;
    private Runnable runnableTime;
    private boolean isAuto;
    private boolean isAutoLine;
    private boolean isKeyboardInterrupt;
    private boolean inInterpretOp;
    private OPS shiftFOp;
    private OPS currentOp;
    int nextProgLineNumber;
    int currentProgLineNumber;
    private boolean isWrapAround;
    String alpha = "";

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
        isWrapAround = false;
        currentProgLineNumber = 0;
        nextProgLineNumber = 0;
        alpha = "";
        inOp = null;
        isAutoLine = false;
        isAuto = false;
        readProgLineOpIndex = 0;
        lineIsGhostKey = false;
        isKeyboardInterrupt = false;
        tempProgLine = new ProgLine();
        readProgLine = new ProgLine();
        displaySymbol = true;
        user = false;
        updateInterval = AUTO_UPDATE_INTERVAL_MS;
        shiftFOp = null;
        shiftMode = SHIFT_MODES.UNSHIFTED;

        setupDotMatrixDisplayUpdater();
        updateDisplayDotMatrixColors();

        setupAlu();

        dotMatrixDisplayUpdater.displayText(alu.getRoundXForDisplay(), true);
        updateDisplayButtonColors();
        setupSideDotMatrixDisplayUpdater();
        updateSideDotMatrixColors();
        updateSideDisplay();
        setupRunnableTime();
        //setupShowExpirationTime();
        //setupSetClockAppAlarmOnStartTimer();
        //setupAddNewChronoTimerToList();
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
                    interpretOp();
                }
            }
        } else {   //  RUN
            isKeyboardInterrupt = true;
        }
    }

    private void interpretOp() {
        inInterpretOp = true;
        String disp;
        if (isAutoLine) {   //  Op à obtenir automatiquement (p.ex. en mode RUN)
            currentOp = readProgLine.ops[readProgLineOpIndex];
            readProgLineOpIndex = readProgLineOpIndex + 1;
        }
        if (error.equals("")) {   //  Pas d'erreur (ou Prefix) antérieure
            testAndHandleDirectAEOp();   //  Test si A..E: inOp y deviendra OPS.GSB
            testAndHandleGhostOp();   //  Test si Touche fantôme (après opération HYP, AHYP ou TEST déjà engagée): inOp y deviendra null
            nextProgLineNumber = currentProgLineNumber;    //  Sauf mention contraire en mode NORM ou EDIT (SST, BST, CLEAR_PRGM, ...)
            if (mode.equals(MODES.RUN)) {
                nextProgLineNumber = inc(currentProgLineNumber);    //  Sauf mention contraire en RUN (GTO, GSB, RTN, A..E, ...)
                if (nextProgLineNumber == 0) {
                    isWrapAround = true;
                }
            }
            if (inOp != null) {   //  instruction MultiOps déjà engagée
                prepareMultiOpsProgLine();
                testAndHandleMultiOpsEnd();   //  Test si fin d'nstruction MultiOps (Eventuellement inOp y deviendra null si instruction MultiOps complète (=> Nbre en cours copié dans X et vidé, puis exécution)(si EDIT: Nouvelle instruction))
            } else {   //  Pas d'instruction MultiOps déjà engagée
                tempProgLine.ops[LINE_OPS.BASE.INDEX()] = currentOp;   //  Nouvelle instruction commence
                testAndHandleDigitOp();   //  Test si Chiffre entré (ou CHS, EEX, "."): stackLift éventuel et se met en fin du nombre en cours (Si EDIT: Enregistrement instruction avec ce chiffre)
                testAndHandleSingleOp();   //  Test si Opération non MultiOps, normale (=> stackLift éventuel, Nbre en cours copié dans X et vidé, puis exécution)(si EDIT: Enregistrement instruction)
                testAndHandleSpecialOp();   //  Test si Opération non MultiOps, spéciale (=> stackLift éventuel, Nbre en cours copié dans X et vidé, puis exécution éventuelle) (si EDIT: Enregistrement instruction éventuelle)
                testAndHandleMultiOpsBegin();  //  Test si début d'instruction MultiOps: inOp deviendra non null
            }
            if (inOp == null) {    //  Instruction terminée (déjà enregistrée dans progLines si EDIT ou RUN) ou éventuellement annulée pour problème de syntaxe mais sans erreur explicite
                alu.clearProgLine(tempProgLine);   //  Préparer le terrain pour une nouvelle instruction
                if (error.equals("")) {   //  Pas d'erreur nouvelle
                    if (mode.equals(MODES.NORM)) {    //  A voir selon alpha si entrée de nombre en cours ou pas
                        disp = (alpha.equals("") ? alu.getRoundXForDisplay() : formatAlphaNumber());   //  formatAlphaNumber pour faire apparaître le séparateur de milliers
                        dotMatrixDisplayUpdater.displayText(disp, true);
                    }
                    currentProgLineNumber = nextProgLineNumber;
                    if (mode.equals(MODES.EDIT)) {
                        disp = alu.progLineToString(currentProgLineNumber, displaySymbol);
                        dotMatrixDisplayUpdater.displayText(disp, false);
                    }
                    if (isKeyboardInterrupt) {
                        isKeyboardInterrupt = false;
                        error = ERROR_KEYBOARD_INTERRUPT;
                    }
                }
                if (!error.equals("")) {    //  Erreur (ou Prefix) nouvelle
                    if (mode.equals(MODES.RUN)) {   //  STOP
                        mode = MODES.NORM;
                        dotMatrixDisplayView.setInvertOn(false);
                        isAutoLine = false;
                        inOp = null;
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
        if (!mode.equals(MODES.RUN)) {   //  NORM ou EDIT
            dotMatrixDisplayView.updateDisplay();
            updateSideDisplay();
        }
        if (isAutoLine) {   //  Obtenir automatiquement le prochain op de la ligne ou de la suivante
            if (readProgLineOpIndex == 0) {   //  Op0 toujours disponible
                readProgLine = alu.getProgLine(currentProgLineNumber);    //  Ligne suivante
                lineIsGhostKey = alu.isGhostKey(readProgLine.ops[LINE_OPS.BASE.INDEX()]);   //  Si Ligne avec touche fantôme => Ne pas aller au-delà de Op0
            } else {   //  Index > 0
                readProgLineOpIndex = getValidProgLineOpIndex(readProgLine, readProgLineOpIndex);   //  Les ops d'une progLine ne ne sont pas toujours côte à côte
                if ((readProgLineOpIndex == -1) || lineIsGhostKey) {   //  Il n'y a plus d'ops dans la progLine ou Ligne avec touche fantôme
                    if (mode.equals(MODES.RUN)) {
                        readProgLine = alu.getProgLine(currentProgLineNumber);    //  Ligne suivante
                        lineIsGhostKey = alu.isGhostKey(readProgLine.ops[LINE_OPS.BASE.INDEX()]);   //  Si Ligne avec touche fantôme => Ne pas aller au-delà de Op0
                        readProgLineOpIndex = 0;   //  Op0 toujours disponible
                    } else {   //  Pas RUN
                        isAutoLine = false;
                    }
                }
            }
        }
        startOrStopAutomatic();
        inInterpretOp = false;
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

    private void testAndHandleDirectAEOp() {
        if (inOp == null) {
            if ((currentOp.INDEX() >= OPS.A.INDEX()) && (currentOp.INDEX() <= OPS.E.INDEX())) {
                inOp = OPS.GSB;
                tempProgLine.ops[LINE_OPS.BASE.INDEX()] = inOp;    //  Conversion en GSB A..E, à examiner ci-dessous
                if (!mode.equals(MODES.RUN)) {   //  NORM ou EDIT
                    KEYS key = alu.getKeyByOp(inOp);
                    swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());   //  Touche inOp revient à la normale
                    buttons[key.INDEX()].updateDisplay();
                }
            }
        }
    }

    private void testAndHandleGhostOp() {
        if (inOp != null) {   //  Opération MultiOps déjà engagée
            OPS dop = alu.getOpByGhostKeyOps(inOp, currentOp);   //  Pas null pour opérations fantômes (cf GHOST_KEYS) : HYP, AHYP, TEST
            if (dop != null) {   // Cas particuliers: SINH,COSH,TANH,ASINH,ACOSH,ATANH et les 10 tests ("x<0?", ... (TEST n)) sont codées en clair en op0 (pex "ACOSH", "x<0?") et en normal (p.ex. HYP-1 COS, TEST 2) dans les op suivants
                // Suite: Ce qui implique que si Affichage symboles: Afficher uniquement op0, Si Affichage Codes: Afficher à partir de op1
                tempProgLine.ops[LINE_OPS.GHOST1.INDEX()] = inOp;   //  Garder l'opération initiale (AHYP COS , TEST n) après op0; op0 sera fixé dans handleOp()
                tempProgLine.ops[LINE_OPS.GHOST2.INDEX()] = currentOp;
                currentOp = dop;   //  l'opération est requalifiée en son équivalent direct et sera examinée plus bas
                if (!mode.equals(MODES.RUN)) {   //  NORM ou EDIT
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
                    tempProgLine.ref = alu.getRegIndexBySymbol(tempProgLine.symbol);
                }
            }
            if ((currentOp.INDEX() >= OPS.A.INDEX()) && (currentOp.INDEX() <= OPS.E.INDEX())) {
                tempProgLine.ops[LINE_OPS.AE.INDEX()] = currentOp;
                tempProgLine.symbol = currentOp.SYMBOL();
            }
            if (currentOp.equals(OPS.I)) {
                tempProgLine.ref = BASE_REGS.RI.INDEX();
                tempProgLine.symbol = currentOp.SYMBOL();
            }
        }
    }

    private void testAndHandleMultiOpsEnd() {
        if (inOp != null) {
            boolean isComplete = false;
            boolean common = false;

            if (inOp.equals(OPS.GTO)) {
                if ((tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.I.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.AE.INDEX()] != null) ||
                        (tempProgLine.ops[LINE_OPS.CHS.INDEX()] != null)) {   //  A09 tient déjà compte du DOT (cf prepareMultiOpsProgLine())

                    if ((mode.equals(MODES.EDIT)) && (tempProgLine.ops[LINE_OPS.CHS.INDEX()] != null) && (!isAutoLine)) {   //  GTO CHS nnnnn en mode EDIT et pas en mode de lecture automatique de lignes
                        if (tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) {
                            if (tempProgLine.ref == 0) {
                                tempProgLine.ref = 1;
                            }
                            tempProgLine.ref = 10 * tempProgLine.ref + Integer.valueOf(tempProgLine.ops[LINE_OPS.A09.INDEX()].SYMBOL());
                            if (tempProgLine.ref > 10000) {   //  OK 4 chiffres obligatoires (nnnn)
                                isComplete = true;
                                int dpln = tempProgLine.ref - 10000;   //  nnnn
                                tempProgLine.ref = 0;
                                nextProgLineNumber = dpln;
                                if (dpln > (alu.getProgLinesSize() - 1)) {
                                    error = ERROR_LINE_NUMBER;
                                }
                            }
                        }
                    } else {   //  Pas GTO CHS nnnn en mode EDIT
                        isComplete = true;
                        if (!inEditModeAfterSavingLine(tempProgLine)) {
                            if (alphaToX()) {
                                if (mode.equals(MODES.NORM)) {
                                    alu.rebuildlabelToProgLineNumberMap();   //  Mettre à jour les lignes existantes
                                    alu.linkGTOGSBToProgLineNumbers();
                                }
                                error = exec(tempProgLine);

                                if (mode.equals(MODES.NORM)) {
                                    if (nextProgLineNumber == 0) {   //  L'exec() utilise le progLine.ref, mis à jour dans les lignes existantes seulement (ou si GTO I), donc pas dans tempProgLien en mode NORM si pas GTO I!
                                        int dpln = alu.getDestProgLineNumber(tempProgLine);
                                        if (dpln != (-1)) {   //  OK
                                            nextProgLineNumber = dpln;
                                            tempProgLine.ref = dpln;
                                        } else {   //  Invalide
                                            error = ERROR_GTO_GSB;
                                        }
                                    }
                                    KEYS key = alu.getKeyByOp(inOp);
                                    swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());   //  Touche inOp revient à la normale
                                    buttons[key.INDEX()].updateDisplay();
                                }
                            }
                        }
                    }
                }
            }
            if (inOp.equals(OPS.GSB)) {
                if ((tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.I.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.AE.INDEX()] != null)) {   //  A09 tient déjà compte du DOT (cf prepareMultiOpsProgLine())

                    isComplete = true;
                    if (!inEditModeAfterSavingLine(tempProgLine)) {
                        if (alphaToX()) {
                            if (mode.equals(MODES.NORM)) {
                                alu.rebuildlabelToProgLineNumberMap();   //  Mettre à jour les lignes existantes
                                alu.linkGTOGSBToProgLineNumbers();
                            }
                            error = exec(tempProgLine);

                            if (mode.equals(MODES.NORM)) {
                                if (nextProgLineNumber == 0) {   //  L'exec() utilise le progLine.ref, mis à jour dans les lignes existantes seulement (ou si GSB I), donc pas dans tempProgLien en mode NORM si pas GSB I!
                                    int dpln = alu.getDestProgLineNumber(tempProgLine);
                                    if (dpln != (-1)) {   //  OK
                                        nextProgLineNumber = dpln;
                                        tempProgLine.ref = dpln;
                                    } else {   //  Invalide
                                        error = ERROR_GTO_GSB;
                                    }
                                }
                                if (error.equals("")) {
                                    nowmRUN = System.currentTimeMillis();
                                    mode = MODES.RUN;   //   Exécuter un GSB, c'est se mettre en mode RUN car plusieurs lignes à exécuter
                                    isAutoLine = true;
                                    readProgLineOpIndex = 0;
                                }
                                KEYS key = alu.getKeyByOp(inOp);
                                swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());   //  Touche inOp revient à la normale
                                buttons[key.INDEX()].updateDisplay();
                            }
                        }
                    }
                }
            }

            if ((inOp.equals(OPS.FIX)) || (inOp.equals(OPS.SCI)) || (inOp.equals(OPS.ENG))) {
                if ((tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.I.INDEX()] != null)) {
                    common = true;
                }
            }
            if (inOp.equals(OPS.STO)) {
                if ((tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.I.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.INDI.INDEX()] != null) ||
                        (tempProgLine.ops[LINE_OPS.RAND.INDEX()] != null)) {   //  A09 tient déjà compte du DOT (cf prepareMultiOpsProgLine())
                    common = true;
                }
            }
            if (inOp.equals(OPS.RCL)) {
                if ((tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.I.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.INDI.INDEX()] != null) ||
                        (tempProgLine.ops[LINE_OPS.SIGMA_PLUS.INDEX()] != null)) {   //  A09 tient déjà compte du DOT (cf prepareMultiOpsProgLine())
                    common = true;
                }
            }
            if (inOp.equals(OPS.DIM)) {   //  DIM (i)
                if (tempProgLine.ops[LINE_OPS.INDI.INDEX()].equals(OPS.INDI)) {   //  COS: (i) était attendu après DIM
                    common = true;
                }
            }
            if (inOp.equals(OPS.XCHG)) {
                if ((tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.I.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.INDI.INDEX()] != null)) {   //  A09 tient déjà compte du DOT (cf prepareMultiOpsProgLine())
                    common = true;
                }
            }
            if (inOp.equals(OPS.LBL)) {
                if ((tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.AE.INDEX()] != null)) {   //  A09 tient déjà compte du DOT (cf prepareMultiOpsProgLine())
                    common = true;
                }
            }
            if ((inOp.equals(OPS.DSE)) || (inOp.equals(OPS.ISG))) {
                if ((tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.I.INDEX()] != null) || (tempProgLine.ops[LINE_OPS.INDI.INDEX()] != null)) {   //  A09 tient déjà compte du DOT (cf prepareMultiOpsProgLine())
                    common = true;
                }
            }
            if ((inOp.equals(OPS.SF)) || (inOp.equals(OPS.CF)) || (inOp.equals(OPS.TF))) {
                if (tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) {
                    common = true;
                }
            }

            if (common) {
                isComplete = true;
                if (!inEditModeAfterSavingLine(tempProgLine)) {
                    if (alphaToX()) {   //  Si on tape 5 puis 6 puis FIX 4 => On doit voir 56 avec 4 décimales   ;
                        error = exec(tempProgLine);
                    }
                }
            }

            if (isComplete) {
                if (!mode.equals(MODES.RUN)) {   //  NORM ou EDIT
                    KEYS key = alu.getKeyByOp(inOp);
                    swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());   //  Touche inOp revient à la normale
                    buttons[key.INDEX()].updateDisplay();
                }
                inOp = null;   //  Pour la condition !mustWait, il s'agit ici d'annuler cette opération foireuse (incomplet et problème de syntaxe (avec error explicite ou pas))
            }
        }
    }

    private void testAndHandleDigitOp() {
        if (((currentOp.INDEX() >= OPS.DIGIT_0.INDEX()) && (currentOp.INDEX() <= OPS.DIGIT_9.INDEX())) ||
                (currentOp.equals(OPS.DOT)) || (currentOp.equals(OPS.EEX)) || (currentOp.equals(OPS.CHS))) {

            if ((mode.equals(MODES.NORM)) || (mode.equals(MODES.RUN))) {
                error = exec(tempProgLine);
            }
            if (mode.equals(MODES.EDIT)) {
                if (!inEditModeAfterSavingLine(tempProgLine)) {   //  Enregistrer une ligne avec le chiffre
                    //  NOP
                }
            }
        }
    }

    private void testAndHandleSingleOp() {
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
                    if (alphaToX()) {
                        error = exec(tempProgLine);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void testAndHandleSpecialOp() {
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
                    if (alphaToX()) {
                        error = exec(tempProgLine);
                    }
                }
                break;
            default:
                break;
        }

        if (currentOp.equals(OPS.BACK)) {    // Désactive stacklift
            if ((mode.equals(MODES.NORM)) || (mode.equals(MODES.RUN))) {
                error = exec(tempProgLine);
            }
            if (mode.equals(MODES.EDIT)) {
                if (currentProgLineNumber != 0) {   //  Interdiction d'effacer BEGIN
                    alu.removeProgLineAtNumber(currentProgLineNumber);
                    nextProgLineNumber = dec(currentProgLineNumber);
                }
            }
        }
        if (currentOp.equals(OPS.USER)) {   //  Neutre sur StackLift
            if (alphaToX()) {
                user = !user;
            }
        }
        if (currentOp.equals(OPS.BEGIN)) {   //  Neutre sur StackLift
            if (alphaToX()) {
                error = exec(tempProgLine);   //  Pour tenir compte de l'éventuel wrap around
            }
        }
        if (currentOp.equals(OPS.PR)) {
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
        }
        if (currentOp.equals(OPS.RS)) {
            boolean sw = false;
            if (!sw) {
                if (mode.equals(MODES.NORM)) {   //  NORM -> RUN
                    sw = true;
                    if (alphaToX()) {
                        mode = MODES.RUN;
                        isAutoLine = true;
                        readProgLineOpIndex = 0;
                        nowmRUN = System.currentTimeMillis();
                        alu.rebuildlabelToProgLineNumberMap();   //  Réassocier les labels à leur n° de ligne, le lancement proprement sera effectué plus bas
                    }
                }
            }
            if (!sw) {   //  On ne vient pas de passer de NORM à RUN juste avant
                sw = true;
                if (mode.equals(MODES.RUN)) {   //  RUN -> NORM
                    if (alphaToX()) {
                        //error = exec.invokeFunction(tempProgLine);   //  NOP
                        mode = MODES.NORM;
                        isAutoLine = false;
                        dotMatrixDisplayView.setInvertOn(false);
                    }
                }
            }
            if (mode.equals(MODES.EDIT)) {
                if (!inEditModeAfterSavingLine(tempProgLine)) {
                    //  NOP
                }
            }
        }
        if (currentOp.equals(OPS.SST)) {
            nextProgLineNumber = inc(currentProgLineNumber);    //  Pas nextProgLineNumber car égal à currentProgLineNumber en mode NORM ou EDIT
            if (mode.equals(MODES.NORM)) {
                if (alphaToX()) {
                    isAutoLine = true;   //  Pour exécuter
                    readProgLineOpIndex = 0;
                }
            }
        }
        if (currentOp.equals(OPS.BST)) {
            nextProgLineNumber = dec(currentProgLineNumber);
            if (mode.equals(MODES.NORM)) {
                if (alphaToX()) {
                    //  NOP   Uniquement reculer, sans exécuter
                }
            }
        }
        if (currentOp.equals(OPS.CLEAR_PRGM)) {
            if (mode.equals(MODES.NORM)) {
                if (alphaToX()) {
                    nextProgLineNumber = 0;
                }
            }
            if (mode.equals(MODES.EDIT)) {
                if (alphaToX()) {
                    askAndDeletePrograms();   //  Remet aussi currentProgLineNumber à 0
                }
            }
        }
        if (currentOp.equals(OPS.PSE)) {
            if (!inEditModeAfterSavingLine(tempProgLine)) {   //  Neutre sur StackLift ???
                if (alphaToX()) {
                    dotMatrixDisplayUpdater.displayText(alu.getRoundXForDisplay(), true);
                    dotMatrixDisplayView.updateDisplay();
                    nowmPSE = System.currentTimeMillis();
                }
            }
        }
    }

    private void testAndHandleMultiOpsBegin() {
        if ((currentOp.equals(OPS.FIX)) || (currentOp.equals(OPS.SCI)) || (currentOp.equals(OPS.ENG)) ||
                (currentOp.equals(OPS.STO)) || (currentOp.equals(OPS.RCL)) || (currentOp.equals(OPS.XCHG)) ||
                (currentOp.equals(OPS.HYP)) || (currentOp.equals(OPS.AHYP)) || (currentOp.equals(OPS.TEST)) ||
                (currentOp.equals(OPS.DIM)) || (currentOp.equals(OPS.GTO)) || (currentOp.equals(OPS.GSB)) || (currentOp.equals(OPS.LBL)) ||
                (currentOp.equals(OPS.SF)) || (currentOp.equals(OPS.CF)) || (currentOp.equals(OPS.TF)) ||
                (currentOp.equals(OPS.DSE)) || (currentOp.equals(OPS.ISG))) {
            inOp = currentOp;   //  Début d'instruction MultiOps
            if (!mode.equals(MODES.RUN)) {   //  NORM ou EDIT
                KEYS key = alu.getKeyByOp(inOp);
                swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());   //  Touche inOp revient à la normale
                buttons[key.INDEX()].updateDisplay();
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
        if (!alpha.equals("")) {  //  Entrée de nombre en cours, à valider et terminer
            int indLast = alpha.length() - 1;
            if (alpha.substring(indLast).equals("E")) {   //  Enlever un "E" éventuel en dernière position
                alpha = alpha.substring(0, indLast);
            }
            error = alu.aToX(alpha);
            if (error.equals("")) {
                alpha = "";
                alu.setStackLiftEnabled(true);
            } else {   //  Erreur
                res = false;
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

    private int getValidProgLineOpIndex(ProgLine progLine, int fromIndex) {
        int res = -1;
        int n = progLine.ops.length;
        for (int i = fromIndex; i <= (n - 1); i = i + 1) {
            OPS op = progLine.ops[i];
            if (op != null) {
                res = i;
                break;
            }
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
                nextProgLineNumber = 0;    //  Cf onDismiss pour le reste
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

    private void startOrStopAutomatic() {
        if (isAutoLine) {
            if (!isAuto) {
                startAutomatic();
            }
        } else {   //  Pas autoLine
            if (isAuto) {
                stopAutomatic();
            }
        }
    }

    public void startAutomatic() {
        isAuto = true;
        handlerTime.postDelayed(runnableTime, updateInterval);   //  Respecter updateInterval à partir de nowm
    }

    public void stopAutomatic() {
        handlerTime.removeCallbacks(runnableTime);
        isAuto = false;
    }

    private void automatic() {
        handlerTime.postDelayed(runnableTime, updateInterval);
        long nowm = System.currentTimeMillis();
        if (!inInterpretOp) {
            interpretOp();
        }
        if (nowmPSE > 0) {   //  PSE en cours
            if ((nowm - nowmPSE) >= PSE_MS) {   //  Fin du temps de PSE
                nowmPSE = 0;
            }
        } else {   //  Pas PSE en cours
            if (mode.equals(MODES.RUN)) {
                if ((nowm - nowmRUN) >= FLASH_RUN_MS) {   //  Fin du temps entre 2 flash
                    nowmRUN = nowm;
                    dotMatrixDisplayView.invert();
                    dotMatrixDisplayUpdater.displayText("RUNNING...", false);
                    dotMatrixDisplayView.updateDisplay();
                }
            }
        }
    }

    public String exec(ProgLine progLine) {
        OPS opBase = progLine.ops[LINE_OPS.BASE.INDEX()];
        boolean common = false;   //  Sortie classique de fonction: stackLiftEnabled=true, lastx si erreur

        switch (opBase) {   //  The GIANT
            case FIX:
            case SCI:
            case ENG:
                alu.setRoundMode(progLine.ops[LINE_OPS.BASE.INDEX()]);
                int n = (progLine.ops[LINE_OPS.I.INDEX()] != null ? (int) alu.getRegContentsByIndex(progLine.ref) : Integer.valueOf(progLine.symbol));
                alu.setRoundParam(n);
                break;
            case STO:
                if (progLine.ops[LINE_OPS.RAND.INDEX()] == null) {   //  STO RAN# traité mais sans effet
                    int regIndex = progLine.ref;
                    if (progLine.ops[LINE_OPS.INDI.INDEX()] != null) {   //  (i))
                        int dataRegIndex = (int) alu.getRegContentsByIndex(BASE_REGS.RI.INDEX());   //  Valeur dans I
                        regIndex = alu.getRegIndexByDataRegIndex(dataRegIndex);
                        if ((regIndex < 0) || (regIndex > alu.getRegsMaxIndex())) {
                            error = ERROR_INDEX;
                        }
                    }
                    if (error.equals("")) {
                        error = (progLine.ops[LINE_OPS.A4OP.INDEX()] != null ? alu.xToReg4Op(regIndex, progLine.ops[LINE_OPS.A4OP.INDEX()]) : alu.xToReg(regIndex));
                    }
                }
                if (error.equals("")) {
                    alu.setStackLiftEnabled(true);
                }
                break;
            case RCL:
                alu.stackLiftIfEnabled();
                if (progLine.ops[LINE_OPS.DIM.INDEX()] != null) {   //  RCL DIM (i)
                    n = alu.getRegsMaxIndex();
                    alu.setStkRegContent(Alu.STK_REGS.X, alu.getDataRegIndexByIndex(n));
                } else {   //  Pas RCL DIM (i)
                    if (progLine.ops[LINE_OPS.SIGMA_PLUS.INDEX()] != null) {   //  RCL SIGMA_PLUS
                        alu.stackLift();    //  Un stacklift obligatoire + un deuxième (cf supra) si stackLift activé
                        error = alu.sumXYToXY();
                    } else {   //  Pas RCL SIGMA_PLUS
                        int regIndex = progLine.ref;
                        if (progLine.ops[LINE_OPS.INDI.INDEX()] != null) {   //  (i))
                            int dataRegIndex = (int) alu.getRegContentsByIndex(BASE_REGS.RI.INDEX());   //  Valeur dans I
                            regIndex = alu.getRegIndexByDataRegIndex(dataRegIndex);
                            if ((regIndex < 0) || (regIndex > alu.getRegsMaxIndex())) {
                                error = ERROR_INDEX;
                            }
                        }
                        if (error.equals("")) {
                            error = (progLine.ops[LINE_OPS.A4OP.INDEX()] != null ? alu.regToX4Op(regIndex, progLine.ops[LINE_OPS.A4OP.INDEX()]) : alu.regToX(regIndex));
                        }
                    }
                }
                if (error.equals("")) {
                    alu.setStackLiftEnabled(true);
                }
                break;
            case DIM:
                n = (int) alu.getStkRegContents(Alu.STK_REGS.X);
                error = alu.setMaxDataRegIndex(n);
                if (error.equals("")) {
                    if (error.equals("")) {
                        alu.setStackLiftEnabled(true);
                    }
                }
                break;
            case XCHG:
                int regIndex = progLine.ref;
                if (progLine.ops[LINE_OPS.INDI.INDEX()] != null) {   //  (i))
                    int dataRegIndex = (int) alu.getRegContentsByIndex(BASE_REGS.RI.INDEX());   //  Valeur dans I
                    regIndex = alu.getRegIndexByDataRegIndex(dataRegIndex);
                    if ((regIndex < 0) || (regIndex > alu.getRegsMaxIndex())) {
                        error = ERROR_INDEX;
                    }
                }
                if (error.equals("")) {
                    error = alu.xXchgReg(regIndex);
                    if (error.equals("")) {
                        alu.setStackLiftEnabled(true);
                    }
                }
                break;
            case LBL:   //  Neutre sur StackLift ???
                break;   //  NOP
            case GTO:   //  Neutre sur StackLift ???
                if (progLine.ops[LINE_OPS.I.INDEX()] != null) {   //  GTO I => recalculer selon I
                    int dpln = alu.getDestProgLineNumber(progLine);
                    if (dpln != (-1)) {   //  OK
                        nextProgLineNumber = dpln;
                    } else {   //  Invalide
                        error = ERROR_GTO_GSB;
                    }
                } else {   //  Pas GTO I
                    nextProgLineNumber = progLine.ref;
                }
                break;
            case GSB:    //  Neutre sur StackLift ???
                if (!alu.pushStkRetProgLineNumber(nextProgLineNumber)) {   //  Si False => MAX_RETS dépassé
                    alu.clearReturnStack();
                    error = ERROR_RET_STACK_FULL;
                } else {   //  OK Push
                    if (progLine.ops[LINE_OPS.I.INDEX()] != null) {   //  GSB I => recalculer selon I
                        int dpln = alu.getDestProgLineNumber(progLine);
                        if (dpln != (-1)) {   //  OK
                            nextProgLineNumber = dpln;
                            progLine.ref = dpln;
                        } else {   //  Invalide
                            error = ERROR_GTO_GSB;
                        }
                    } else {   //  Pas GSB I
                        nextProgLineNumber = progLine.ref;
                    }
                }
                break;
            case DSE:
            case ISG:
                regIndex = progLine.ref;
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
                break;
            case SF:
            case CF:
            case TF:
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
                }
                alu.setStackLiftEnabled(true);
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
                        alu.stackLiftIfEnabled();
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
                alu.stackLiftIfEnabled();
                error = alu.piToX();
                if (error.equals("")) {
                    alu.setStackLiftEnabled(true);
                }
                break;
            case LASTX:
                alu.stackLiftIfEnabled();
                error = alu.lastXToX();
                if (error.equals("")) {
                    alu.setStackLiftEnabled(true);
                }
                break;
            case RAND:
                alu.stackLiftIfEnabled();
                error = alu.randToX();
                if (error.equals("")) {
                    alu.setStackLiftEnabled(true);
                }
                break;
            case SIGMA_PLUS:   //  Désactive stackLift
                error = alu.sigmaPlus();
                if (error.equals("")) {
                    alu.setStackLiftEnabled(false);
                } else {
                    alu.lastXToX();
                }
                break;
            case SIGMA_MINUS:   //  Désactive stackLift
                error = alu.sigmaMinus();
                if (error.equals("")) {
                    alu.setStackLiftEnabled(false);
                } else {
                    alu.lastXToX();
                }
                break;
            case MEAN:   //  Désactive stackLift
                alu.saveStack();
                alu.stackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                alu.stackLiftIfEnabled();
                error = alu.mean();
                if (error.equals("")) {
                    alu.setStackLiftEnabled(true);
                } else {
                    alu.restoreStack();
                }
                break;
            case STDEV:
                alu.saveStack();
                alu.stackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                alu.stackLiftIfEnabled();
                error = alu.stDev();
                if (error.equals("")) {
                    alu.setStackLiftEnabled(true);
                } else {
                    alu.restoreStack();
                }
                break;
            case LR:
                alu.saveStack();
                alu.stackLift();    //  Un stacklift obligatoire + un deuxième si stackLift activé
                alu.stackLiftIfEnabled();
                error = alu.lr();
                if (error.equals("")) {
                    alu.setStackLiftEnabled(true);
                } else {
                    alu.restoreStack();
                }
                break;
            case YER:
                alu.saveStack();
                alu.stackLift();    //  Un stacklift obligatoire
                error = alu.yer();
                if (error.equals("")) {
                    alu.setStackLiftEnabled(true);
                } else {
                    alu.restoreStack();
                }
                break;
            case SQR:
                error = alu.sqrX();
                common = true;
                break;
            case SQRT:
                error = alu.sqrtX();
                common = true;
                break;
            case TO_RAD:
                error = alu.xToRad();
                common = true;
                break;
            case TO_DEG:
                error = alu.xToDeg();
                common = true;
                break;
            case EXP:
                error = alu.expX();
                common = true;
                break;
            case LN:
                error = alu.lnX();
                common = true;
                break;
            case EXP10:
                error = alu.exp10X();
                common = true;
                break;
            case LOG:
                error = alu.logX();
                common = true;
                break;
            case POWER:
                error = alu.pow();
                if (error.equals("")) {
                    alu.stackMergeDown();
                    alu.setStackLiftEnabled(true);
                } else {
                    alu.lastXToX();
                }
                break;
            case PC:
                error = alu.xPcY();   //  Pas de mergeDown
                common = true;
                break;
            case INV:
                error = alu.invX();
                common = true;
                break;
            case DPC:
                error = alu.xDpcY();   //  Pas de mergeDown
                common = true;
                break;
            case ABS:
                error = alu.absX();
                common = true;
                break;
            case RND:
                error = alu.rndX();
                common = true;
                break;
            case POL:
                error = alu.xyToPol();
                common = true;
                break;
            case RECT:
                error = alu.xyToRect();
                common = true;
                break;
            case HMS:
                error = alu.hmsX();
                common = true;
                break;
            case H:
                error = alu.hX();
                common = true;
                break;
            case COMB:
                error = alu.xyToComb();
                common = true;
                break;
            case PERM:
                error = alu.xyToPerm();
                common = true;
                break;
            case FRAC:
                error = alu.fracX();
                common = true;
                break;
            case INTEGER:
                error = alu.integerX();
                common = true;
                break;
            case SIN:
                error = alu.sinX();
                common = true;
                break;
            case COS:
                error = alu.cosX();
                common = true;
                break;
            case TAN:
                error = alu.tanX();
                common = true;
                break;
            case ASIN:
                error = alu.asinX();
                common = true;
                break;
            case ACOS:
                error = alu.acosX();
                common = true;
                break;
            case ATAN:
                error = alu.atanX();
                common = true;
                break;
            case SINH:
                error = alu.sinhX();
                common = true;
                break;
            case COSH:
                error = alu.coshX();
                common = true;
                break;
            case TANH:
                error = alu.tanhX();
                common = true;
                break;
            case ASINH:
                error = alu.asinhX();
                common = true;
                break;
            case ACOSH:
                error = alu.acoshX();
                common = true;
                break;
            case ATANH:
                error = alu.atanhX();
                common = true;
                break;
            case FACT:
                error = alu.factX();
                common = true;
                break;
            case PLUS:
                error = alu.yPlusX();
                if (error.equals("")) {
                    alu.stackMergeDown();
                    alu.setStackLiftEnabled(true);
                } else {
                    alu.lastXToX();
                }
                break;
            case MINUS:
                error = alu.yMinusX();
                if (error.equals("")) {
                    alu.stackMergeDown();
                    alu.setStackLiftEnabled(true);
                } else {
                    alu.lastXToX();
                }
                break;
            case MULT:
                error = alu.yMultX();
                if (error.equals("")) {
                    alu.stackMergeDown();
                    alu.setStackLiftEnabled(true);
                } else {
                    alu.lastXToX();
                }
                break;
            case DIV:
                error = alu.yDivX();
                if (error.equals("")) {
                    alu.stackMergeDown();
                    alu.setStackLiftEnabled(true);
                } else {
                    alu.lastXToX();
                }
                break;
            case DEG:
            case RAD:
            case GRAD:   //  Neutre sur stackLift
                OPS op = progLine.ops[LINE_OPS.BASE.INDEX()];
                alu.setAngleMode(op);
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
                if (alu.test(opBase)) {
                    nextProgLineNumber = inc(nextProgLineNumber);
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
                    alu.setStackLiftEnabled(false);
                    alpha = "";
                }
                break;
            case CLX:    // Désactive stacklift
                error = alu.clX();
                if (error.equals("")) {
                    alu.setStackLiftEnabled(false);
                }
                break;
            case CLEAR_PREFIX:    //  Neutre sur StackLift
                error = alu.prefX();  //  error sera <>"" dans tous les cas (car représentera la mantisse) et sera donc traité comme une erreur
                break;
            case CLEAR_REGS:   //  Neutre sur StackLift
                error = alu.clearRegs();
                break;
            case CLEAR_SIGMA:   //  Neutre sur StackLift
                error = alu.clearStats();
                alu.clearStack();
                break;
            case ENTER:   // Désactive Stacklift
                alu.stackLift();
                alu.setStackLiftEnabled(false);
                break;
            case RDN:
                alu.stackRollDown();
                alu.setStackLiftEnabled(true);
                break;
            case RUP:
                alu.stackRollUp();
                alu.setStackLiftEnabled(true);
                break;
            case XCHGXY:
                alu.xchgXY();
                alu.setStackLiftEnabled(true);
                break;
            case BEGIN:
                if (isWrapAround) {   //  On est passé de la fin au début => STOP
                    isWrapAround = false;
                    ProgLine progLine1 = new ProgLine();
                    progLine1.ops[LINE_OPS.BASE.INDEX()] = OPS.RTN;   //  Opération requalifiée et à examiner ci-dessous
                    error = rtn(progLine1);
                    progLine1 = null;
                }
                break;
            case RTN:
                error = rtn(progLine);
                break;
            case HYP:   //  Ghost => NOP
            case AHYP:
            case TEST:
            case RS:   //  Déjà réglé dans MainActivity()
            case PSE:
            case PR:   //  Non programmable
            case SST:
            case BST:
            case CLEAR_PRGM:
                break;
        }
        if (common) {
            if (error.equals("")) {
                alu.setStackLiftEnabled(true);
            } else {
                alu.lastXToX();
            }
        }
        return error;
    }

    public String rtn(ProgLine progLine) {   //  Neutre sur StackLift ???
        if (!alu.isStkRetEmpty()) {  //  La pile d'appels n'est pas vide
            int dpln = alu.popStkRetProgLineNumber();
            alu.removeLastStkRetProgLineNumber();
            nextProgLineNumber = dpln;
        } else {   //  Pile d'appels vide => STOP
            error = "";
            mode = MODES.NORM;
            isAutoLine = false;
            dotMatrixDisplayView.setInvertOn(false);
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
        final float BUTTON_MID_21_IMAGE_SIZE_COEFF = 0.6f;
        final float BUTTON_MID_22_IMAGE_SIZE_COEFF = 0.6f;
        final float BUTTON_MID_23_IMAGE_SIZE_COEFF = 0.52f;
        final float BUTTON_MID_24_IMAGE_SIZE_COEFF = 0.64f;
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

    private void setupRunnableTime() {
        handlerTime = new Handler();
        runnableTime = new Runnable() {
            @Override
            public void run() {
                automatic();
            }
        };
    }

}