package com.example.pgyl.swtimer_a;

import android.graphics.Rect;
import android.os.Handler;

import com.example.pgyl.pekislib_a.DotMatrixDisplayView;
import com.example.pgyl.pekislib_a.DotMatrixFont;
import com.example.pgyl.pekislib_a.DotMatrixFontUtils;
import com.example.pgyl.pekislib_a.DotMatrixSymbol;

import static com.example.pgyl.pekislib_a.DotMatrixDisplayView.SCROLL_DIRECTIONS;
import static com.example.pgyl.pekislib_a.DotMatrixFontUtils.getFontTextDimensions;
import static com.example.pgyl.pekislib_a.MiscUtils.BiDimensions;
import static com.example.pgyl.pekislib_a.TimeDateUtils.msToTimeFormatD;
import static com.example.pgyl.swtimer_a.Constants.TIME_UNIT_PRECISION;
import static com.example.pgyl.swtimer_a.StringShelfDatabaseTables.getDotMatrixDisplayBackIndex;
import static com.example.pgyl.swtimer_a.StringShelfDatabaseTables.getDotMatrixDisplayOffIndex;
import static com.example.pgyl.swtimer_a.StringShelfDatabaseTables.getDotMatrixDisplayOnLabelIndex;
import static com.example.pgyl.swtimer_a.StringShelfDatabaseTables.getDotMatrixDisplayOnTimeIndex;

public class CtDisplayDotMatrixDisplayUpdater {
    public interface onExpiredTimerListener {
        void onExpiredTimer();
    }

    public void setOnExpiredTimerListener(onExpiredTimerListener listener) {
        mOnExpiredTimerListener = listener;
    }

    private onExpiredTimerListener mOnExpiredTimerListener;

    //region Variables
    private DotMatrixDisplayView dotMatrixDisplayView;
    private DotMatrixFont defaultFont;
    private DotMatrixFont extraFont;
    private Rect margins;
    private Rect gridRect;
    private Rect displayRect;
    private Rect halfDisplayRect;
    private Rect labelRect;
    private String[] colors;
    private int onTimeColorIndex;
    private int onLabelColorIndex;
    private int offColorIndex;
    private int backColorIndex;
    private CtRecord currentCtRecord;
    private long updateInterval;
    private SCROLL_DIRECTIONS scrollDirection;
    private int scrollCount;
    private int invertCount;
    private boolean automaticScrollOn;
    private boolean inAutomatic;
    private Handler handlerTime;
    private Runnable runnableTime;
    //endregion

    public CtDisplayDotMatrixDisplayUpdater(DotMatrixDisplayView dotMatrixDisplayView, CtRecord currentCtRecord) {
        super();

        this.dotMatrixDisplayView = dotMatrixDisplayView;
        this.currentCtRecord = currentCtRecord;
        init();
    }

    private void init() {
        setupRunnableTime();
        setupDefaultFont();
        setupExtraFont();
        setupColorIndexes();
        setupMargins();
        setupGridDimensions();
        inAutomatic = false;
        mOnExpiredTimerListener = null;
    }

    public void close() {
        handlerTime.removeCallbacks(runnableTime);
        runnableTime = null;
        handlerTime = null;
        dotMatrixDisplayView = null;
        defaultFont.close();
        defaultFont = null;
        extraFont.close();
        extraFont = null;
        currentCtRecord = null;
    }

    public void setColors(String[] colors) {
        this.colors = colors;
        dotMatrixDisplayView.setBackColor(colors[backColorIndex]);
    }

    public void displayTimeAndLabel(String timeText, String labelText) {
        dotMatrixDisplayView.fillRect(displayRect, colors[onTimeColorIndex], colors[offColorIndex]);    //  Pressed=ON TIME  Unpressed=OFF
        dotMatrixDisplayView.setSymbolPos(displayRect.left + margins.left, displayRect.top + margins.top);
        dotMatrixDisplayView.writeText(timeText, colors[onTimeColorIndex], extraFont, defaultFont);   //  Temps avec police extra prioritaire
        dotMatrixDisplayView.fillRect(labelRect, colors[onLabelColorIndex], colors[offColorIndex]);   //  Pressed=ON LABEL  Unpressed=OFF
        dotMatrixDisplayView.setSymbolPos(labelRect.left, labelRect.top + margins.top);
        dotMatrixDisplayView.writeText(labelText, colors[onLabelColorIndex], defaultFont);   //  Label avec police par défaut
        dotMatrixDisplayView.updateDisplay();
    }

    public void displayTime(String timeText) {
        dotMatrixDisplayView.fillRect(displayRect, colors[onTimeColorIndex], colors[offColorIndex]);   //  Pressed=ON TIME  Unpressed=OFF
        dotMatrixDisplayView.setSymbolPos(displayRect.left + margins.left, displayRect.top + margins.top);
        dotMatrixDisplayView.writeText(timeText, colors[onTimeColorIndex], extraFont, defaultFont);   //  Temps avec police extra prioritaire
        dotMatrixDisplayView.updateDisplay();
    }

    public void displayHalfTimeAndLabel(String timeText, String labelText) {   //  Partager l'affichage entre Temps et Label (utilisé pour le réglage des couleurs dans CtDisplayColorsActivity)
        dotMatrixDisplayView.fillRect(displayRect, colors[onTimeColorIndex], colors[offColorIndex]);   //  Pressed=ON TIME  Unpressed=OFF
        dotMatrixDisplayView.setSymbolPos(displayRect.left + margins.left, displayRect.top + margins.top);
        dotMatrixDisplayView.writeText(timeText, colors[onTimeColorIndex], extraFont, defaultFont);   //  Temps avec police extra prioritaire
        dotMatrixDisplayView.fillRect(halfDisplayRect, colors[onLabelColorIndex], colors[offColorIndex]);   //  Effacer la 2e moitié du temps    Pressed=ON LABEL  Unpressed=OFF
        dotMatrixDisplayView.setSymbolPos(halfDisplayRect.left, halfDisplayRect.top + margins.top);
        dotMatrixDisplayView.writeText(labelText, colors[onLabelColorIndex], defaultFont);   //  Label avec police par défaut
        dotMatrixDisplayView.updateDisplay();
    }

    public void startAutomatic() {
        final long UPDATE_INTERVAL_RESET_MS = 40;       //   40 ms => 25 scrolls par seconde cad +/- 4 caractères par secondes  (car un caractère avec marge droite nécessite 6 scrolls)

        if (currentCtRecord.isReset()) {
            automaticScrollOn = true;
            invertCount = 0;
            scrollCount = 0;
            scrollDirection = SCROLL_DIRECTIONS.LEFT;
            updateInterval = UPDATE_INTERVAL_RESET_MS;
        } else {
            automaticScrollOn = false;
            updateInterval = TIME_UNIT_PRECISION.DURATION_MS();
        }
        dotMatrixDisplayView.resetScrollStart();
        handlerTime.removeCallbacks(runnableTime);   //  On efface tout et on recommence
        handlerTime.postDelayed(runnableTime, updateInterval);
    }

    public void stopAutomatic() {
        handlerTime.removeCallbacks(runnableTime);
    }

    private void automatic() {
        handlerTime.postDelayed(runnableTime, updateInterval);
        if ((!inAutomatic) && (!dotMatrixDisplayView.isDrawing())) {
            inAutomatic = true;
            long nowm = System.currentTimeMillis();
            if (!currentCtRecord.updateTime(nowm)) {    //  Mise à jour du temps et signaler si le timer a expiré (ce qui génèrera un nouveau startAutomatic())
                if (mOnExpiredTimerListener != null) {
                    mOnExpiredTimerListener.onExpiredTimer();
                }
            }
            automaticDisplay();
            inAutomatic = false;
        }
    }

    private void automaticDisplay() {
        final int MAX_SCROLL_COUNT = 2 * gridRect.width();   //  Scroll de 2 grilles complètes
        final int MAX_INVERT_COUNT = 6;   //  3 flashes avant scroll

        if (automaticScrollOn) {
            if (scrollCount == MAX_SCROLL_COUNT) {
                scrollCount = 0;
                scrollDirection = (scrollDirection.equals(SCROLL_DIRECTIONS.LEFT)) ? SCROLL_DIRECTIONS.RIGHT : SCROLL_DIRECTIONS.LEFT;   //  Changer le sens du scroll
            }
            if (invertCount < MAX_INVERT_COUNT) {   //  Flashes avant le scroll
                dotMatrixDisplayView.invert();
                invertCount = invertCount + 1;
            } else {
                dotMatrixDisplayView.scroll(scrollDirection);
                scrollCount = scrollCount + 1;
            }
            dotMatrixDisplayView.updateDisplay();
        } else {
            displayTime(msToTimeFormatD(currentCtRecord.getTimeDisplay(), TIME_UNIT_PRECISION));
        }
    }

    private void setupColorIndexes() {
        onTimeColorIndex = getDotMatrixDisplayOnTimeIndex();
        onLabelColorIndex = getDotMatrixDisplayOnLabelIndex();
        offColorIndex = getDotMatrixDisplayOffIndex();
        backColorIndex = getDotMatrixDisplayBackIndex();
    }

    private void setupDefaultFont() {
        defaultFont = DotMatrixFontUtils.getDefaultFont();
    }

    private void setupExtraFont() {
        //  Caractères redéfinis pour l'affichage du temps ("." et ":") (plus fins que la fonte par défaut de DotMatrixDisplayView)
        final DotMatrixSymbol[] EXTRA_FONT_SYMBOLS = {
                new DotMatrixSymbol('.', new int[][]{{1}}),
                new DotMatrixSymbol(':', new int[][]{{0}, {0}, {1}, {0}, {1}, {0}, {0}})
        };

        final int EXTRA_FONT_RIGHT_MARGIN = 1;
        DotMatrixSymbol symbol;

        extraFont = new DotMatrixFont();
        extraFont.setSymbols(EXTRA_FONT_SYMBOLS);
        extraFont.setRightMargin(EXTRA_FONT_RIGHT_MARGIN);
        symbol = extraFont.getSymbol('.');
        symbol.setOverwrite(true);   //  Le "." surcharge le symbole précédent (en-dessous dans sa marge droite)
        symbol.setPosOffset(-symbol.getDimensions().width, defaultFont.getDimensions().height);
        symbol = null;
    }

    private void setupMargins() {    // Marges autour de l'affichage proprement dit
        final int MARGIN_LEFT = 1;
        final int MARGIN_RIGHT = 1;
        final int MARGIN_TOP = 1;

        int marginBottom = extraFont.getSymbol('.').getDimensions().height;  // Le  "." est affiché dans la marge inférieure
        margins = new Rect(MARGIN_LEFT, MARGIN_TOP, MARGIN_RIGHT, marginBottom);
    }

    private void setupGridDimensions() {       //  La grille (gridRect) contient le temps et le label, et seule une partie est affichée (gridDisplayRect, glissant en cas de scroll)
        BiDimensions timeTextDimensions = getFontTextDimensions(msToTimeFormatD(currentCtRecord.getTimeDisplay(), TIME_UNIT_PRECISION), extraFont, defaultFont);  // timeText mélange de l'extraFont (pour les ":" et ".") et defaultFont (pour les chiffres de 0 à 9)
        BiDimensions labelTextDimensions = getFontTextDimensions(currentCtRecord.getLabel(), defaultFont);   //  labelText est uniquement affiché en defaultFont

        int displayRectWidth = margins.left + timeTextDimensions.width - defaultFont.getRightMargin() + margins.right;   //   Affichage sur la largeur du temps, avec margins.right remplaçant la dernière marge droite)
        int displayRectHeight = margins.top + Math.max(timeTextDimensions.height, labelTextDimensions.height) + margins.bottom;   //  Affichage du temps uniquement ou (via scroll) du temps et du label , sur la hauteur nécessaire
        int gridRectWidth = displayRectWidth + labelTextDimensions.width - defaultFont.getRightMargin();   // La grille doit pouvoir contenir le temps et le label sur toute sa largeur ...
        int gridRectHeight = displayRectHeight;   //  ... et la même hauteur que la fenêtre d'affichage

        gridRect = new Rect(0, 0, gridRectWidth, gridRectHeight);
        displayRect = new Rect(gridRect.left, gridRect.top, displayRectWidth, displayRectHeight);  //  Affichage au début de la grille
        halfDisplayRect = new Rect(displayRect.right / 2, displayRect.top, displayRect.right, displayRect.bottom);  //  Pour affichage partagé dans CtDisplayColorsActivity
        labelRect = new Rect(displayRect.right, gridRect.top, gridRect.right, gridRect.bottom);   //  Espace restant de la grille
        dotMatrixDisplayView.setGridRect(gridRect);
        dotMatrixDisplayView.setDisplayRect(displayRect);
        dotMatrixDisplayView.setScrollRect(gridRect);   //  On scrolle la grille entière (margins.left servira de margins.right)
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