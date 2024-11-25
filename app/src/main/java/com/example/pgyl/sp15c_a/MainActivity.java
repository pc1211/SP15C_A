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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.pgyl.pekislib_a.ColorBox;
import com.example.pgyl.pekislib_a.ColorPickerActivity;
import com.example.pgyl.pekislib_a.ColorUtils.BUTTON_COLOR_TYPES;
import com.example.pgyl.pekislib_a.DotMatrixDisplayView;
import com.example.pgyl.pekislib_a.HelpActivity;
import com.example.pgyl.pekislib_a.StringDB;
import com.example.pgyl.pekislib_a.StringDBTables.ACTIVITY_START_STATUS;
import com.example.pgyl.pekislib_a.StringDBTables.TABLE_EXTRA_KEYS;
import com.example.pgyl.sp15c_a.Executor.BASE_REGS;
import com.example.pgyl.sp15c_a.Executor.KEYS;
import com.example.pgyl.sp15c_a.Executor.MODES;
import com.example.pgyl.sp15c_a.Executor.OPS;
import com.example.pgyl.sp15c_a.Executor.STACK_REGS;
import com.example.pgyl.sp15c_a.ProgLine.LINE_OPS;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.pgyl.pekislib_a.Constants.COLOR_PREFIX;
import static com.example.pgyl.pekislib_a.Constants.CRLF;
import static com.example.pgyl.pekislib_a.Constants.PEKISLIB_ACTIVITIES;
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
import static com.example.pgyl.pekislib_a.StringDBUtils.createPresetWithDefaultValues;
import static com.example.pgyl.pekislib_a.StringDBUtils.getCurrent;
import static com.example.pgyl.pekislib_a.StringDBUtils.getCurrentsFromActivity;
import static com.example.pgyl.pekislib_a.StringDBUtils.getDefaults;
import static com.example.pgyl.pekislib_a.StringDBUtils.setCurrent;
import static com.example.pgyl.pekislib_a.StringDBUtils.setCurrentsForActivity;
import static com.example.pgyl.pekislib_a.StringDBUtils.setStartStatusOfActivity;
import static com.example.pgyl.pekislib_a.TimeDateUtils.MILLISECONDS_PER_SECOND;
import static com.example.pgyl.sp15c_a.Constants.SP15C_ACTIVITIES;
import static com.example.pgyl.sp15c_a.StringDBTables.DATA_VERSION;
import static com.example.pgyl.sp15c_a.StringDBTables.getFlagsTableName;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorDisp1BackIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorDisp1OffIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorDisp1OnIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorDisp2BackIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorDisp2OffIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorDisp2OnIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorKeyClearTopFrontIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorKeyFLowBackIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorKeyFMidBackIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorKeyFMidFrontIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorKeyGLowBackIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorKeyGMidBackIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorKeyGMidFrontIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorKeyLowBackIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorKeyLowFrontIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorKeyMidBackIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorKeyMidFrontIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorKeyOutlineIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorKeyTopFrontIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorPanelLowIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorPanelTopIndex;
import static com.example.pgyl.sp15c_a.StringDBTables.getPaletteColorsTableName;
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
import static com.example.pgyl.sp15c_a.StringDBUtils.initializeTablePaletteColors;
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

    private final String ERROR_LINE_NUMBER = "Invalid Line num";
    private final String ERROR_KEYBOARD_INTERRUPT = "Keyboard Break";
    private final String ERROR_PROG_LINES_MAX = "Max Prog lines";
    private final long PSE_MS = MILLISECONDS_PER_SECOND;   //  1 seconde
    private final long FLASH_RUN_MS = MILLISECONDS_PER_SECOND / 2;   //  1/2 seconde
    private final long AUTO_UPDATE_INTERVAL_MS = 1;

    public enum SWTIMER_SHP_KEY_NAMES {KEEP_SCREEN}
    //endregion

    //region Variables
    private ImageButtonViewStack[] buttons;
    private DotMatrixDisplayView dotMatrixDisplayView;
    private DotMatrixDisplayView sideDotMatrixDisplayView;
    private String[] paletteColors;
    private Menu menu;
    private MenuItem barMenuItemKeepScreen;
    private boolean keepScreen;
    private StringDB stringDB;
    private String shpFileName;
    private SHIFT_MODES shiftMode;
    private CalcDotMatrixDisplayUpdater dotMatrixDisplayUpdater;
    private SideDotMatrixDisplayUpdater sideDotMatrixDisplayUpdater;
    private Executor executor;
    private String error;
    private ProgLine readProgLine;
    private ProgLine tempProgLine;
    private long updateInterval;
    private long nowmPSE;
    private long nowmRUN;
    private Handler handlerTimeLine;
    private Runnable runnableTimeLine;
    private OPS inOp;
    private OPS shiftFOp;
    private OPS currentOp;
    private boolean isDisplayPressed;
    private boolean user;
    private boolean isAutoL;
    private boolean isKeyboardInterrupt;
    private boolean inExecCurrentProgLine;
    private boolean validReturnFromCalledActivity;
    private String calledActivityName;
    private LinearLayout panelTop;
    private LinearLayout panelLow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String ACTIVITY_TITLE = "SP15C";

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().setTitle(ACTIVITY_TITLE);
        validReturnFromCalledActivity = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        setCurrent(stringDB, getAppInfosTableName(), getAppInfosDataVersionIndex(), String.valueOf(DATA_VERSION));
        setCurrentsForActivity(stringDB, SP15C_ACTIVITIES.MAIN.toString(), getPaletteColorsTableName(), paletteColors);
        saveRowsToDB(stringDB, getStackRegsTableName(), doubleArrayToRows(executor.getStackRegs()));
        saveRowsToDB(stringDB, getFlagsTableName(), booleanArrayToRows(executor.getFlags()));
        saveRowsToDB(stringDB, getRegsTableName(), doubleArrayToRows(doubleListToArray(executor.getRegs())));
        saveRowsToDB(stringDB, getRetStackTableName(), intArrayToRows(intListToArray(executor.getRetStack())));
        saveRowsToDB(stringDB, getProgLinesTableName(), executor.progLinesToRows());
        saveRowsToDB(stringDB, getParamsTableName(), paramsToRows());

        dotMatrixDisplayUpdater.close();
        dotMatrixDisplayUpdater = null;
        sideDotMatrixDisplayUpdater.close();
        sideDotMatrixDisplayUpdater = null;

        executor.close();
        executor = null;

        tempProgLine = null;
        readProgLine = null;
        paletteColors = null;
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

        paletteColors = getCurrentsFromActivity(stringDB, SP15C_ACTIVITIES.MAIN.toString(), getPaletteColorsTableName());

        if (validReturnFromCalledActivity) {
            validReturnFromCalledActivity = false;
            if (calledActivityName.equals(PEKISLIB_ACTIVITIES.COLOR_PICKER.toString())) {   //  Pour Edition de la palette de couleurs
                paletteColors = getCurrentsFromActivity(stringDB, PEKISLIB_ACTIVITIES.COLOR_PICKER.toString(), getPaletteColorsTableName());
            }
        }

        setupPanels();
        setupButtons();
        setupDotMatrixDisplay();
        setupSideDotMatrixDisplay();

        error = "";
        nowmPSE = 0;
        nowmRUN = 0;
        inOp = null;
        shiftFOp = null;
        isAutoL = false;
        user = false;
        isKeyboardInterrupt = false;
        isDisplayPressed = false;
        tempProgLine = new ProgLine();
        readProgLine = new ProgLine();
        updateInterval = AUTO_UPDATE_INTERVAL_MS;
        shiftMode = SHIFT_MODES.UNSHIFTED;

        executor = new Executor();
        executor.setStackRegs(rowsToDoubleArray(loadRowsFromDB(stringDB, getStackRegsTableName()), STACK_REGS.values().length));
        executor.setFlags(rowsToBooleanArray(loadRowsFromDB(stringDB, getFlagsTableName()), executor.MAX_FLAGS));
        executor.setRegs(doubleArrayToList(rowsToDoubleArray(loadRowsFromDB(stringDB, getRegsTableName()), executor.DEF_MAX_REGS)));
        executor.setRetStack(intArrayToList(rowsToIntArray(loadRowsFromDB(stringDB, getRetStackTableName()), 0)));

        setupDotMatrixDisplayUpdater();
        updateDisplayDotMatrixColors();
        updateDisplayPanelColors();
        updateDisplayButtonColors();
        setupSideDotMatrixDisplayUpdater();
        updateSideDotMatrixColors();

        encodeKeyCodesFromProgLinesRows(loadRowsFromDB(stringDB, getProgLinesTableName()));
        rowsToParams(loadRowsFromDB(stringDB, getParamsTableName()));

        dotMatrixDisplayUpdater.displayText(executor.getRoundXForDisplay(), true);
        dotMatrixDisplayView.updateDisplay();
        updateSideDisplay();

        setupRunnableTimeLine();
        updateDisplayKeepScreen();
        invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
        validReturnFromCalledActivity = false;
        if (requestCode == PEKISLIB_ACTIVITIES.COLOR_PICKER.INDEX()) {   //  Pour éditer la palette de couleurs
            calledActivityName = PEKISLIB_ACTIVITIES.COLOR_PICKER.toString();
            if (resultCode == RESULT_OK) {
                validReturnFromCalledActivity = true;
            }
        }
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
        if (item.getItemId() == R.id.EDIT_PALETTE) {
            launchColorPickerActivity();
            return true;
        }
        if (item.getItemId() == R.id.IMPORT) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (clipboard != null) {
                if (clipboard.hasPrimaryClip()) {
                    ClipData cld = clipboard.getPrimaryClip();
                    if (cld != null) {
                        ClipData.Item cldi = cld.getItemAt(0);
                        if (cldi != null) {
                            encodeKeyCodesFromClipboard(cldi.getText().toString());
                            msgBox(executor.getProgLinesSize() + " lines imported", this);
                            dotMatrixDisplayUpdater.displayText((executor.getAlpha().equals("") ? executor.getRoundXForDisplay() : formatAlphaNumber()), true);   //  formatAlphaNumber pour faire apparaître le séparateur de milliers
                            dotMatrixDisplayView.updateDisplay();
                        }
                    }
                }
                clipboard = null;
            }
            return true;
        }
        if (item.getItemId() == R.id.EXPORT) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData clip = ClipData.newPlainText(null, progLinesToClipboard());
                clipboard.setPrimaryClip(clip);
                msgBox(executor.getProgLinesSize() + " lines exported", this);
                clipboard = null;
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
        isDisplayPressed = !isDisplayPressed;
        if (executor.getMode().equals(MODES.EDIT)) {
            dotMatrixDisplayUpdater.displayText(executor.progLineToString(executor.getCurrentProgLineNumber(), isDisplayPressed), false);
            dotMatrixDisplayView.updateDisplay();
        }
    }

    private void onSSTClickDown() {   //  Click Down sur SST => Afficher ProgLine courante
        if ((executor.getMode().equals(MODES.NORM)) && (shiftMode.equals(SHIFT_MODES.UNSHIFTED))) {
            dotMatrixDisplayUpdater.displayText(executor.progLineToString(executor.getCurrentProgLineNumber(), isDisplayPressed), false);
            dotMatrixDisplayView.updateDisplay();
        }
    }

    private void onSSTClickLeave() {   //  Quitter SST sans cliquer => Affichage normal
        if ((executor.getMode().equals(MODES.NORM)) && (shiftMode.equals(SHIFT_MODES.UNSHIFTED))) {
            dotMatrixDisplayUpdater.displayText((executor.getAlpha().equals("") ? executor.getRoundXForDisplay() : formatAlphaNumber()), true);   //  formatAlphaNumber pour faire apparaître le séparateur de milliers
            dotMatrixDisplayView.updateDisplay();
        }
    }

    private void onButtonClick(KEYS key) {
        currentOp = null;   //  Restera null si fonction f ou g activée ou annulée
        if (executor.getMode() != MODES.RUN) {
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
            n = digitToRealKeyCode(keyCode);
        }
        KEYS key = executor.getKeyByKeyCode(n);
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
            executor.setNextProgLineNumber(executor.getCurrentProgLineNumber());   //  Sauf mention contraire en mode NORM ou EDIT (SST, BST, CLEAR_PRGM, ...)
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
                executor.clearProgLine(tempProgLine);   //  Préparer le terrain pour une nouvelle instruction
                if (error.length() == 0) {   //  Pas d'erreur nouvelle
                    executor.setCurrentProgLineNumber(executor.getNextProgLineNumber());
                    if (executor.getMode().equals(MODES.NORM)) {    //  A voir selon alpha si entrée de nombre en cours ou pas
                        dotMatrixDisplayUpdater.displayText((executor.getAlpha().equals("") ? executor.getRoundXForDisplay() : formatAlphaNumber()), true);    //  formatAlphaNumber pour faire apparaître le séparateur de milliers
                    }
                    if (executor.getMode().equals(MODES.EDIT)) {
                        dotMatrixDisplayUpdater.displayText(executor.progLineToString(executor.getCurrentProgLineNumber(), isDisplayPressed), false);
                    }
                }
                if (error.length() != 0) {    //  Erreur (ou Prefix) nouvelle
                    dotMatrixDisplayUpdater.displayText(error, false);
                }
            }
        } else {   //  Erreur (ou Prefix) antérieure
            error = "";
            executor.clearRetStack();
            executor.getSolveParamSet().clear();
            executor.getIntegParamSet().clear();
            inOp = null;
            if (executor.getMode().equals(MODES.NORM)) {
                dotMatrixDisplayUpdater.displayText(executor.getRoundXForDisplay(), true);
            }
            if (executor.getMode().equals(MODES.EDIT)) {
                dotMatrixDisplayUpdater.displayText(executor.progLineToString(executor.getCurrentProgLineNumber(), isDisplayPressed), false);
            }
        }
        dotMatrixDisplayView.updateDisplay();
        updateSideDisplay();
        startOrStopAutomaticLine();
    }

    private void updateSideDisplay() {
        sideDotMatrixDisplayUpdater.displayText(
                executor.getAngleMode().toString().toLowerCase() + " " +
                        executor.getRoundMode().toString().toLowerCase() + executor.getRoundParam() + " " +
                        (user ? "user" : ""), false);
        sideDotMatrixDisplayView.updateDisplay();
    }

    private void swapColorBoxColors(ColorBox colorBox, int index1, int index2) {
        String color1 = colorBox.getColor(index1).RGBString;
        colorBox.setColor(index1, colorBox.getColor(index2).RGBString);
        colorBox.setColor(index2, color1);
    }

    private void updateDisplayPanelColors() {
        int color = Color.parseColor(COLOR_PREFIX + paletteColors[getPaletteColorPanelTopIndex()]);
        panelTop.setBackgroundColor(color);
        color = Color.parseColor(COLOR_PREFIX + paletteColors[getPaletteColorPanelLowIndex()]);
        panelLow.setBackgroundColor(color);
    }

    private void updateDisplayDotMatrixColors() {
        String[] colors = {paletteColors[getPaletteColorDisp1OnIndex()], paletteColors[getPaletteColorDisp1OffIndex()], paletteColors[getPaletteColorDisp1BackIndex()]};    // ON, OFF, BACK

        dotMatrixDisplayUpdater.setColors(colors);
        dotMatrixDisplayUpdater.rebuildStructure();
        dotMatrixDisplayView.updateDisplay();
    }

    private void updateSideDotMatrixColors() {
        String[] colors = {paletteColors[getPaletteColorDisp2OnIndex()], paletteColors[getPaletteColorDisp2OffIndex()], paletteColors[getPaletteColorDisp2BackIndex()]};    // ON, OFF, BACK

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
        ColorBox keyColorBox = buttons[key.INDEX()].getKeyColorBox();
        keyColorBox.setColor(BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), paletteColors[getPaletteColorPanelLowIndex()]);
        keyColorBox.setColor(BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX(), paletteColors[getPaletteColorKeyOutlineIndex()]);
        keyColorBox.setColor(BUTTON_COLOR_TYPES.BACK_SCREEN.INDEX(), paletteColors[getPaletteColorPanelLowIndex()]);

        ColorBox[] imageColorBoxes = buttons[key.INDEX()].getImageColorBoxes();
        for (LEGEND_POS legendPos : LEGEND_POS.values()) {
            switch (legendPos) {
                case MID:
                    switch (key) {
                        case KEY_42:
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), paletteColors[getPaletteColorKeyFMidFrontIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX(), paletteColors[getPaletteColorKeyFMidBackIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX(), paletteColors[getPaletteColorKeyFMidFrontIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX(), paletteColors[getPaletteColorKeyFMidBackIndex()]);
                            break;
                        case KEY_43:
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), paletteColors[getPaletteColorKeyGMidFrontIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX(), paletteColors[getPaletteColorKeyGMidBackIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX(), paletteColors[getPaletteColorKeyGMidFrontIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX(), paletteColors[getPaletteColorKeyGMidBackIndex()]);
                            break;
                        default:
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), paletteColors[getPaletteColorKeyMidFrontIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX(), paletteColors[getPaletteColorKeyMidBackIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX(), paletteColors[getPaletteColorKeyMidFrontIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX(), paletteColors[getPaletteColorKeyMidBackIndex()]);
                            break;
                    }
                    break;
                case TOP:
                    if ((key.equals(KEYS.KEY_32)) || (key.equals(KEYS.KEY_33)) || (key.equals(KEYS.KEY_34)) || (key.equals(KEYS.KEY_35))) {   //  Touches CLEAR
                        imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), paletteColors[getPaletteColorKeyClearTopFrontIndex()]);
                        imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX(), paletteColors[getPaletteColorPanelLowIndex()]);
                        imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX(), paletteColors[getPaletteColorKeyClearTopFrontIndex()]);
                        imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX(), paletteColors[getPaletteColorPanelLowIndex()]);
                    } else {   //  Pas les touches CLEAR
                        imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), paletteColors[getPaletteColorKeyTopFrontIndex()]);
                        imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX(), paletteColors[getPaletteColorPanelLowIndex()]);
                        imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX(), paletteColors[getPaletteColorKeyTopFrontIndex()]);
                        imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX(), paletteColors[getPaletteColorPanelLowIndex()]);
                    }
                    break;
                case LOW:
                    switch (key) {
                        case KEY_42:
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), paletteColors[getPaletteColorKeyFLowBackIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX(), paletteColors[getPaletteColorKeyFLowBackIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX(), paletteColors[getPaletteColorKeyFLowBackIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX(), paletteColors[getPaletteColorKeyFLowBackIndex()]);
                            break;
                        case KEY_43:
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), paletteColors[getPaletteColorKeyGLowBackIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX(), paletteColors[getPaletteColorKeyGLowBackIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX(), paletteColors[getPaletteColorKeyGLowBackIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX(), paletteColors[getPaletteColorKeyGLowBackIndex()]);
                            break;
                        default:
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), paletteColors[getPaletteColorKeyLowFrontIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX(), paletteColors[getPaletteColorKeyLowBackIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX(), paletteColors[getPaletteColorKeyLowFrontIndex()]);
                            imageColorBoxes[legendPos.INDEX()].setColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX(), paletteColors[getPaletteColorKeyLowBackIndex()]);
                            break;
                    }
                    break;
            }
            buttons[key.INDEX()].updateDisplay();
        }
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
                if ((executor.getMode().equals(MODES.NORM)) || (executor.getMode().equals(MODES.EDIT))) {
                    KEYS key = executor.getKeyByOp(inOp);
                    swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());   //  Touche inOp revient à la normale
                    buttons[key.INDEX()].updateDisplay();
                }
            }
        }
    }

    private void interpretGhostOp() {
        if (inOp != null) {   //  Opération MultiOps déjà engagée
            OPS dop = executor.getOpByGhostKeyOps(inOp, currentOp);   //  Pas null pour opérations fantômes (cf GHOST_KEYS) : HYP, AHYP, TEST
            if (dop != null) {   // Cas particuliers: SINH,COSH,TANH,ASINH,ACOSH,ATANH et les 10 tests ("x<0?", ... (TEST n)) sont codées en clair en op0 (pex "ACOSH", "x<0?") et en normal (p.ex. HYP-1 COS, TEST 2) dans les op suivants
                // Suite: Ce qui implique que si Affichage symboles: Afficher uniquement op0, Si Affichage Codes: Afficher à partir de op1
                tempProgLine.ops[LINE_OPS.GHOST1.INDEX()] = inOp;   //  Garder l'opération initiale (AHYP COS , TEST n) après op0; op0 sera fixé dans interpretOp()
                tempProgLine.ops[LINE_OPS.GHOST2.INDEX()] = currentOp;
                currentOp = dop;   //  l'opération est requalifiée en son équivalent direct et sera examinée plus bas
                if ((executor.getMode().equals(MODES.NORM)) || (executor.getMode().equals(MODES.EDIT))) {
                    KEYS key = executor.getKeyByOp(inOp);
                    swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());   //  Touche inOp revient à la normale
                    buttons[key.INDEX()].updateDisplay();
                }
                inOp = null;
            }
        }
    }

    private void prepareMultiOpsProgLine() {
        if (inOp != null) {
            shiftFOp = executor.getKeyByOp(currentOp).SHIFT_F_OP();   //  Pour I, (i), ou A..E;
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
                    tempProgLine.paramAddress = executor.getRegIndexBySymbol(tempProgLine.symbol);
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

                            if ((executor.getMode().equals(MODES.EDIT)) && (tempProgLine.ops[LINE_OPS.CHS.INDEX()] != null)) {   //  GTO CHS nnnnn en mode EDIT et pas en mode de lecture automatique de lignes
                                if (tempProgLine.ops[LINE_OPS.A09.INDEX()] != null) {
                                    if (tempProgLine.paramAddress == 0) {
                                        tempProgLine.paramAddress = 1;   //  cf ci-dessous, permet de multiplier par 10 puis d'ajouter un n (de nnnn), le 1 deviendra 10000 après entrée de nnnn
                                    }
                                    tempProgLine.paramAddress = 10 * tempProgLine.paramAddress + Integer.valueOf(tempProgLine.ops[LINE_OPS.A09.INDEX()].SYMBOL());
                                    if (tempProgLine.paramAddress > 10000) {   //  OK 4 chiffres obligatoires (nnnn)
                                        isComplete = true;
                                        int dpln = tempProgLine.paramAddress - 10000;   //  nnnn
                                        tempProgLine.paramAddress = 0;
                                        executor.setNextProgLineNumber(dpln);
                                        if (dpln > (executor.getProgLinesSize() - 1)) {
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
                    error = executor.exec(tempProgLine);
                }
            }
            if (isComplete) {
                KEYS key = executor.getKeyByOp(inOp);
                swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());   //  Touche inOp revient à la normale
                buttons[key.INDEX()].updateDisplay();
                inOp = null;
            }
        }
    }

    private void interpretDigitOp() {
        if (((currentOp.INDEX() >= OPS.DIGIT_0.INDEX()) && (currentOp.INDEX() <= OPS.DIGIT_9.INDEX())) ||
                (currentOp.equals(OPS.DOT)) || (currentOp.equals(OPS.EEX)) || (currentOp.equals(OPS.CHS))) {

            if (executor.getMode().equals(MODES.NORM)) {
                error = executor.exec(tempProgLine);
            }
            if (executor.getMode().equals(MODES.EDIT)) {
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
                    error = executor.exec(tempProgLine);
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
                    error = executor.exec(tempProgLine);
                }
                break;
            case BACK:
                if (executor.getMode().equals(MODES.NORM)) {
                    error = executor.exec(tempProgLine);
                }
                if (executor.getMode().equals(MODES.EDIT)) {
                    if (executor.getCurrentProgLineNumber() != 0) {   //  Interdiction d'effacer BEGIN
                        executor.removeProgLineAtNumber(executor.getCurrentProgLineNumber());
                        executor.setNextProgLineNumber(executor.dec(executor.getCurrentProgLineNumber()));
                    }
                }
                break;
            case USER:
                if (executor.alphaToX()) {
                    user = !user;
                }
                break;
            case BEGIN:   //  Neutre sur StackLift
                if (executor.alphaToX()) {
                    //  NOP
                }
                break;
            case PR:
                boolean sw = false;
                if (!sw) {
                    if (executor.getMode().equals(MODES.NORM)) {   //  NORM -> EDIT
                        sw = true;
                        if (executor.alphaToX()) {
                            executor.setMode(MODES.EDIT);
                            executor.setNextProgLineNumber(executor.getCurrentProgLineNumber());
                        }
                    }
                }
                if (!sw) {   //  On ne vient pas de passer de NORM à EDIT juste avant
                    if (executor.getMode().equals(MODES.EDIT)) {   //  EDIT -> NORM
                        sw = true;
                        executor.setMode(MODES.NORM);
                    }
                }
                break;
            case RS:
                if (executor.getMode().equals(MODES.NORM)) {   //  NORM -> RUN
                    sw = true;
                    if (executor.alphaToX()) {
                        executor.setMode(MODES.RUN);
                        executor.setIsAutoLine(true);
                        nowmRUN = System.currentTimeMillis();
                        executor.rebuildlabelToProgLineNumberMap();   //  Mettre à jour les lignes existantes
                        executor.linkDestProgLineNumbers();
                    }
                }
                if (executor.getMode().equals(MODES.EDIT)) {
                    if (!inEditModeAfterSavingLine(tempProgLine)) {
                        //  NOP
                    }
                }
                break;
            case SST:
                if (executor.getMode().equals(MODES.NORM)) {   //  Pas de alphaToX() car sinon plusieurs chiffres successifs éventuels dans le programme ne s'assembleront pas en progressant avec SST
                    executor.setMode(MODES.RUN);
                    executor.setIsAutoLine(true);   //  Pour exécuter
                    executor.setInSST(true);
                    nowmRUN = System.currentTimeMillis();
                    executor.rebuildlabelToProgLineNumberMap();   //  Mettre à jour les lignes existantes
                    executor.linkDestProgLineNumbers();
                }
                if (executor.getMode().equals(MODES.EDIT)) {
                    executor.setNextProgLineNumber(executor.inc(executor.getCurrentProgLineNumber()));    //  Pas nextProgLineNumber car égal à currentProgLineNumber en mode NORM ou EDIT
                }
                break;
            case BST:
                executor.setNextProgLineNumber(executor.dec(executor.getCurrentProgLineNumber()));
                if (executor.getMode().equals(MODES.NORM)) {
                    if (executor.alphaToX()) {
                        //  NOP   Uniquement reculer, sans exécuter
                    }
                }
                break;
            case CLEAR_PRGM:
                if (executor.getMode().equals(MODES.NORM)) {
                    if (executor.alphaToX()) {
                        executor.setNextProgLineNumber(0);
                        executor.clearRetStack();
                        executor.clearSolveParamSet();
                        executor.clearIntegParamSet();
                    }
                }
                if (executor.getMode().equals(MODES.EDIT)) {
                    if (executor.alphaToX()) {
                        askAndDeletePrograms();   //  Remet aussi currentProgLineNumber à 0
                    }
                }
                break;
            case PSE:
                if (!inEditModeAfterSavingLine(tempProgLine)) {   //  Neutre sur StackLift ???
                    if (executor.getMode().equals(MODES.NORM)) {
                        if (executor.alphaToX()) {
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
                if ((executor.getMode().equals(MODES.NORM)) || (executor.getMode().equals(MODES.EDIT))) {   //  NORM ou EDIT
                    KEYS key = executor.getKeyByOp(inOp);
                    swapColorBoxColors(buttons[key.INDEX()].getKeyColorBox(), BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX());   //  Touche inOp revient à la normale
                    buttons[key.INDEX()].updateDisplay();
                }
                break;
        }
    }

    public boolean inEditModeAfterSavingLine(ProgLine progLine) {
        boolean res = false;   //  Exécuter la ligne (NORM ou RUN)
        if (executor.getMode().equals(MODES.EDIT)) {   //  EDIT
            int pln = executor.getCurrentProgLineNumber() + 1;   //  Pas incProgLineNumber(currentProgLineNumber), afin d'éviter Wrap around
            if (executor.addProgLineAtNumber(progLine, pln)) {   //  OK nouvelle ligne en mode EDIT
                executor.setNextProgLineNumber(pln);
            } else {
                error = ERROR_PROG_LINES_MAX;
            }
            res = true;   //  Ne pas exécuter la ligne
        }
        return res;
    }

    public void saveOrExecLineWithLabel(boolean setRunMode) {
        if (!inEditModeAfterSavingLine(tempProgLine)) {
            if (executor.getMode().equals(MODES.NORM)) {
                executor.rebuildlabelToProgLineNumberMap();   //  Mettre à jour les lignes existantes
                executor.linkDestProgLineNumbers();
                int dpln = executor.getGTODestProgLineNumber(tempProgLine);   //  L'exec() utilisera le progLine.ref, mis à jour dans les lignes existantes seulement (ou si GTO/GSB I), donc pas dans tempProgLine en mode NORM !
                if (dpln != (-1)) {   //  OK
                    tempProgLine.paramAddress = dpln;
                    if (setRunMode) {
                        switch (tempProgLine.ops[LINE_OPS.BASE.INDEX()]) {
                            case SOLVE:
                                executor.setRequestStopAfterSolve(true);   //  Nécessaire car un SOLVE ou INTEG ne se termine pas par un RTN comme dans un GSB
                                break;
                            case INTEG:
                                executor.setRequestStopAfterInteg(true);
                                break;
                            case GSB:
                                executor.setNextProgLineNumber(executor.END_RETURN_CODE);
                                break;
                        }
                        nowmRUN = System.currentTimeMillis();
                        executor.setMode(MODES.RUN);   //   Exécuter un GSB, SOLVE, INTEG, c'est se mettre en mode RUN car plusieurs lignes à exécuter
                        executor.setIsAutoLine(true);
                    }
                } else {   //  Invalide
                    error = executor.ERROR_GTO_GSB;
                }
            }
            if (error.length() == 0) {
                error = executor.exec(tempProgLine);
            }
        }
    }

    private String progLinesToClipboard() {
        String res = "";
        int n = executor.getProgLines().size();
        if (n > 0) {
            for (int i = 0; i <= (n - 1); i = i + 1) {
                String plc = executor.progLineToString(i, false);   //  Codes
                String pls = executor.progLineToString(i, true);   //  Symbols
                res = res + plc.substring(0, 5) + " { " + plc.substring(6) + " } " + pls.substring(6) + CRLF;   //  Ne pas reprendre de nouveau le n° de ligne de la ligne de symboles
            }
        }
        return res;
    }

    private void encodeKeyCodesFromClipboard(String clipText) {
        if (clipText != null) {
            String[] lines = clipText.split("\\r?\\n");   //  Splitter selon CR/LF
            int n = lines.length;
            if (n > 0) {
                executor.setupProgLines();
                executor.setMode(MODES.EDIT);
                for (int i = 0; i <= (n - 1); i = i + 1) {
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
                executor.setMode(MODES.NORM);
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
        executor.setupProgLines();
        if (progLinesRows != null) {
            int n = rows.length;
            if (n > 0) {
                executor.setMode(MODES.EDIT);
                int m = getSp15cTableDataFieldsCount(getProgLinesTableName());   //  Normalement 3 (après le champ ID (n° de ligne)) : p.ex. ID: "1"  Values:"45"  "23"  "24"
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    for (int j = 1; j <= m; j = j + 1) {   //  Après le champ ID (ProgLineNumber)
                        String kc = rows[i][j];
                        if (kc != null) {
                            handleCodeToEncode(kc);   //  progLines va progressivement se remplir de toutes ses lignes
                        }
                    }
                }
                executor.setMode(MODES.NORM);
            }
        }
    }

    public String[][] paramsToRows() {
        String[][] res = new String[8][2];
        res[0][TABLE_ID_INDEX] = "ROUND_MODE";
        res[0][TABLE_DATA_INDEX] = executor.getRoundMode().toString();
        res[1][TABLE_ID_INDEX] = "ROUND_PARAM";
        res[1][TABLE_DATA_INDEX] = String.valueOf(executor.getRoundParam());
        res[2][TABLE_ID_INDEX] = "ANGLE_MODE";
        res[2][TABLE_DATA_INDEX] = executor.getAngleMode().toString();
        res[3][TABLE_ID_INDEX] = "NEXT_PROG_LINE_NUMBER";
        res[3][TABLE_DATA_INDEX] = String.valueOf(executor.getNextProgLineNumber());
        res[4][TABLE_ID_INDEX] = "CURRENT_PROG_LINE_NUMBER";
        res[4][TABLE_DATA_INDEX] = String.valueOf(executor.getCurrentProgLineNumber());
        res[5][TABLE_ID_INDEX] = "USER";
        res[5][TABLE_DATA_INDEX] = (user ? "1" : "0");
        res[6][TABLE_ID_INDEX] = "DISPLAY_PRESSED";
        res[6][TABLE_DATA_INDEX] = (isDisplayPressed ? "1" : "0");
        res[7][TABLE_ID_INDEX] = "STACK_LIFT_ENABLED";
        res[7][TABLE_DATA_INDEX] = (executor.getStackLiftEnabled() ? "1" : "0");
        return res;
    }

    public void rowsToParams(String[][] paramRows) {
        if (paramRows != null) {
            int n = paramRows.length;
            if (n > 0) {
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    String s = paramRows[i][TABLE_ID_INDEX];
                    if (s.equals("ROUND_MODE")) {
                        executor.setRoundMode(OPS.valueOf(paramRows[i][TABLE_DATA_INDEX]));
                    }
                    if (s.equals("ROUND_PARAM")) {
                        executor.setRoundParam(Integer.parseInt(paramRows[i][TABLE_DATA_INDEX]));
                    }
                    if (s.equals("ANGLE_MODE")) {
                        executor.setAngleMode(OPS.valueOf(paramRows[i][TABLE_DATA_INDEX]));
                    }
                    if (s.equals("NEXT_PROG_LINE_NUMBER")) {
                        executor.setNextProgLineNumber(Integer.parseInt(paramRows[i][TABLE_DATA_INDEX]));
                    }
                    if (s.equals("CURRENT_PROG_LINE_NUMBER")) {
                        executor.setCurrentProgLineNumber(Integer.parseInt(paramRows[i][TABLE_DATA_INDEX]));
                    }
                    if (s.equals("USER")) {
                        user = (paramRows[i][TABLE_DATA_INDEX].equals("1"));
                    }
                    if (s.equals("DISPLAY_PRESSED")) {
                        isDisplayPressed = (paramRows[i][TABLE_DATA_INDEX].equals("1"));
                    }
                    if (s.equals("STACK_LIFT_ENABLED")) {
                        executor.setStackLiftEnabled((paramRows[i][TABLE_DATA_INDEX].equals("1")));
                    }
                }
            }
        }
    }

    private String formatAlphaNumber() {
        String alphaTemp = executor.getAlpha();
        int indMax = alphaTemp.length();   //  Faire apparaître le séparateur de milliers au cours de l'entrée de nombre, avant le 1er "." ou "E"
        int indDot = alphaTemp.indexOf(OPS.DOT.SYMBOL());
        if (indDot != (-1)) {   //  "." détecté
            indMax = Math.min(indMax, indDot);
        }
        int indEex = alphaTemp.indexOf(OPS.EEX.SYMBOL());
        if (indEex != (-1)) {   //  "E" détecté
            indMax = Math.min(indMax, indEex);
        }
        int indMin = (alphaTemp.substring(0, 1).equals(OPS.CHS.SYMBOL()) ? 1 : 0);   //  Tenir compte du "-" initial éventuel
        String res = String.format(Locale.US, "%,d", Long.parseLong(alphaTemp.substring(indMin, indMax)));   //  Séparateur de milliers
        if (indMin != 0) {
            res = OPS.CHS.SYMBOL() + res;   //  Ramener le "-" initial éventuel
        }
        if (indMax < alphaTemp.length()) {
            res = res + alphaTemp.substring(indMax);   //  Le reste
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
                executor.clearProgLines();
                executor.setCurrentProgLineNumber(0);    //  Cf onDismiss pour le reste
            }
        });
        builder.setNegativeButton("No", null);
        Dialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {    // OK pour modifier UI sous-jacente à la boîte de dialogue
                dotMatrixDisplayUpdater.displayText(executor.progLineToString(executor.getCurrentProgLineNumber(), isDisplayPressed), false);
                dotMatrixDisplayView.updateDisplay();
                updateSideDisplay();
            }
        });
        dialog.show();
    }

    private void startOrStopAutomaticLine() {
        if (executor.getIsAutoLine()) {
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
            inExecCurrentProgLine = true;
            readProgLine = executor.getCurrentProgLine();    //  Charger la ligne actuelle
            executor.setNextProgLineNumber(executor.inc(executor.getCurrentProgLineNumber()));    //  Sauf mention contraire (GTO, GSB, RTN, ...)
            if (executor.getNextProgLineNumber() == 0) {
                executor.setIsWrapAround(true);  //  Wrap Around => RTN
            }
            error = executor.exec(readProgLine);
            if (error.length() == 0) {   //  Pas d'erreur nouvelle
                executor.setCurrentProgLineNumber(executor.getNextProgLineNumber());
                if (executor.getInSST()) {   //  STOP après SST
                    executor.setInSST(false);
                    executor.setIsAutoLine(false);
                }
            } else {    //  Erreur nouvelle
                executor.setIsAutoLine(false);
            }
            if (isKeyboardInterrupt) {
                isKeyboardInterrupt = false;
                executor.setIsAutoLine(false);
                error = ERROR_KEYBOARD_INTERRUPT;
            }
            startOrStopAutomaticLine();
            if (!executor.getIsAutoLine()) {
                executor.setMode(MODES.NORM);
                dotMatrixDisplayView.setInvertOn(false);
                if (error.length() == 0) {   //  Pas d'erreur nouvelle
                    dotMatrixDisplayUpdater.displayText((executor.getAlpha().equals("") ? executor.getRoundXForDisplay() : formatAlphaNumber()), true);   //  formatAlphaNumber pour faire apparaître le séparateur de milliers
                } else {   //  Erreur nouvelle
                    dotMatrixDisplayUpdater.displayText(error, false);
                }
                dotMatrixDisplayView.updateDisplay();
                updateSideDisplay();
            }
            inExecCurrentProgLine = false;
        }
        if ((executor.getInPSE()) && (nowmPSE == 0)) {   //  Nouvelle instruction PSE et Pas d'affichage de X en cours suite à instruction PSE antérieure
            executor.setInPSE(false);
            nowmPSE = nowm;
        }
        if (nowmPSE > 0) {   //  Comptage du temps de PSE en cours
            if ((nowm - nowmPSE) >= PSE_MS) {   //  Fin du temps de PSE
                nowmPSE = 0;
                dotMatrixDisplayUpdater.displayText(executor.getRoundXForDisplay(), true);
                dotMatrixDisplayView.updateDisplay();
            }
        } else {   //  Pas de PSE en cours
            if ((nowm - nowmRUN) >= FLASH_RUN_MS) {   //  Fin du temps entre 2 flash
                nowmRUN = nowm;
                dotMatrixDisplayView.invert();
                if (executor.getRequestStopAfterSolve()) {   //  Affiche aussi la situation de INTEG (I:0/0 si non appelée par SOLVE)
                    dotMatrixDisplayUpdater.displayText(isDisplayPressed ? "I:" + executor.getIntegParamSet().countFx + "/" + executor.getIntegParamSet().countFxMax : executor.roundForDisplay(executor.getSolveParamSet().t), true);
                } else {
                    if (executor.getRequestStopAfterInteg()) {
                        dotMatrixDisplayUpdater.displayText(isDisplayPressed ? "I:" + executor.getIntegParamSet().countFx + "/" + executor.getIntegParamSet().countFxMax : executor.roundForDisplay(executor.getIntegParamSet().z), true);
                    } else {
                        dotMatrixDisplayUpdater.displayText("Running...", false);
                    }
                }
                dotMatrixDisplayView.updateDisplay();
            }
        }
    }

    private void setupPanels() {
        panelTop = findViewById(R.id.PANEL_TOP_LAYOUT);
        panelLow = findViewById(R.id.PANEL_LOW_LAYOUT);
    }

    private void setupButtons() {
        final String KEY_FILE_PREFIX = "k";
        final String KEY_FILE_SUFFIX = "_";

        buttons = new ImageButtonViewStack[KEYS.values().length];
        Class rid = R.id.class;
        for (KEYS key : KEYS.values()) {
            try {
                buttons[key.INDEX()] = findViewById(rid.getField(key.toString()).getInt(rid));
                buttons[key.INDEX()].setOutlineStrokeWidthDp(2);
                buttons[key.INDEX()].setPcBackCornerRadius(0);
                buttons[key.INDEX()].setImageCount(LEGEND_POS.values().length);
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
                float imageSizeCoeff = 0;
                int heightWeight = 0;

                if ((key.equals(KEYS.KEY_41)) || (key.equals(KEYS.KEY_42)) || (key.equals(KEYS.KEY_43))) {   //  ON, f ou g
                    for (LEGEND_POS legendPos : LEGEND_POS.values()) {
                        switch (legendPos) {
                            case TOP:
                                heightWeight = 7;
                                buttons[key.INDEX()].setImageVisibilities(legendPos.INDEX(), false);   //  La zone TOP doit être invisible
                                break;
                            case MID:
                                heightWeight = 11;
                                String fileName = KEY_FILE_PREFIX + key.CODE() + KEY_FILE_SUFFIX + legendPos.toString().toLowerCase();   //  Pas de fichier image en position TOP ni LOW
                                int svgResId = getResources().getIdentifier(fileName, "raw", getPackageName());
                                buttons[key.INDEX()].setSVGImageResource(legendPos.INDEX(), svgResId);
                                switch (key) {
                                    case KEY_41:
                                        heightWeight = 17;
                                        imageSizeCoeff = 0.4f;
                                        buttons[key.INDEX()].setImageSizeCoeff(legendPos.INDEX(), imageSizeCoeff);   //  Image ON à adapter
                                        break;
                                }
                                break;
                            case LOW:
                                heightWeight = 8;
                                switch (key) {
                                    case KEY_41:   //  La zone LOW doit être invisible pour la touche ON
                                        heightWeight = 2;
                                        buttons[key.INDEX()].setImageVisibilities(legendPos.INDEX(), false);
                                        break;
                                }
                                break;
                        }
                        buttons[key.INDEX()].setHeightWeight(legendPos.INDEX(), heightWeight);
                    }
                } else {   //  Pas ON, f ou g
                    for (LEGEND_POS legendPos : LEGEND_POS.values()) {
                        String fileName = KEY_FILE_PREFIX + key.CODE() + KEY_FILE_SUFFIX + legendPos.toString().toLowerCase();
                        int svgResId = getResources().getIdentifier(fileName, "raw", getPackageName());
                        buttons[key.INDEX()].setSVGImageResource(legendPos.INDEX(), svgResId);
                        switch (legendPos) {
                            case TOP:
                                imageSizeCoeff = 0.7f;
                                heightWeight = 7;
                                switch (key) {
                                    case KEY_20:
                                        imageSizeCoeff = 0.8f;
                                        break;
                                    case KEY_36:
                                        heightWeight = 9;
                                        break;
                                }
                                break;
                            case MID:
                                imageSizeCoeff = 0.65f;
                                heightWeight = 11;
                                switch (key) {
                                    case KEY_16:
                                        imageSizeCoeff = 0.6f;
                                        break;
                                    case KEY_21:
                                        imageSizeCoeff = 0.59f;
                                        break;
                                    case KEY_22:
                                        imageSizeCoeff = 0.6f;
                                        break;
                                    case KEY_23:
                                        imageSizeCoeff = 0.57f;
                                        break;
                                    case KEY_24:
                                        imageSizeCoeff = 0.63f;
                                        break;
                                    case KEY_25:
                                        imageSizeCoeff = 0.57f;
                                        break;
                                    case KEY_26:
                                        imageSizeCoeff = 0.58f;
                                        break;
                                    case KEY_30:
                                        imageSizeCoeff = 0.34f;
                                        break;
                                    case KEY_31:
                                        imageSizeCoeff = 0.55f;
                                        break;
                                    case KEY_32:
                                        imageSizeCoeff = 0.6f;
                                        break;
                                    case KEY_33:
                                        imageSizeCoeff = 0.55f;
                                        break;
                                    case KEY_35:
                                        imageSizeCoeff = 0.5f;
                                        break;
                                    case KEY_36:
                                        heightWeight = 56;
                                        imageSizeCoeff = 0.85f;
                                        break;
                                    case KEY_44:
                                        imageSizeCoeff = 0.58f;
                                        break;
                                    case KEY_45:
                                        imageSizeCoeff = 0.56f;
                                        break;
                                    case KEY_48:
                                        imageSizeCoeff = 0.3f;
                                        break;
                                }
                                break;
                            case LOW:
                                imageSizeCoeff = 0.6f;
                                heightWeight = 8;
                                switch (key) {
                                    case KEY_36:
                                        heightWeight = 11;
                                        break;
                                }
                                break;
                        }
                        buttons[key.INDEX()].setHeightWeight(legendPos.INDEX(), heightWeight);
                        buttons[key.INDEX()].setImageSizeCoeff(legendPos.INDEX(), imageSizeCoeff);
                    }
                }
            } catch
            (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException
                            ex) {
                Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
        }
        if (!stringDB.tableExists(getPaletteColorsTableName())) {
            createSp15cTableIfNotExists(stringDB, getPaletteColorsTableName());
            initializeTablePaletteColors(stringDB);
            String[] defaults = getDefaults(stringDB, getPaletteColorsTableName());
            setCurrentsForActivity(stringDB, SP15C_ACTIVITIES.MAIN.toString(), getPaletteColorsTableName(), defaults);
            createPresetWithDefaultValues(stringDB, getPaletteColorsTableName(), defaults);   //  => PRESET1 = DEFAULT  dans la table de couleurs
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

    private void launchColorPickerActivity() {
        setCurrentsForActivity(stringDB, PEKISLIB_ACTIVITIES.COLOR_PICKER.toString(), getPaletteColorsTableName(), paletteColors);
        setStartStatusOfActivity(stringDB, PEKISLIB_ACTIVITIES.COLOR_PICKER.toString(), ACTIVITY_START_STATUS.COLD);
        Intent callingIntent = new Intent(this, ColorPickerActivity.class);
        callingIntent.putExtra(TABLE_EXTRA_KEYS.TABLE.toString(), getPaletteColorsTableName());
        startActivityForResult(callingIntent, PEKISLIB_ACTIVITIES.COLOR_PICKER.INDEX());
    }

    private void launchHelpActivity() {
        Intent callingIntent = new Intent(this, HelpActivity.class);
        callingIntent.putExtra(PEKISLIB_ACTIVITY_EXTRA_KEYS.TITLE.toString(), HELP_ACTIVITY_TITLE);
        callingIntent.putExtra(HelpActivity.HELP_ACTIVITY_EXTRA_KEYS.HTML_ID.toString(), R.raw.helpmainactivity);
        startActivity(callingIntent);
    }

}