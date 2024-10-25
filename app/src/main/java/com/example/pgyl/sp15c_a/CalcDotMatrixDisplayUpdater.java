package com.example.pgyl.sp15c_a;

import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;

import com.example.pgyl.pekislib_a.ColorBox;
import com.example.pgyl.pekislib_a.DotMatrixDisplayView;
import com.example.pgyl.pekislib_a.DotMatrixFont;
import com.example.pgyl.pekislib_a.DotMatrixFontDefault;

import static com.example.pgyl.pekislib_a.ColorUtils.DOT_MATRIX_COLOR_TYPES;
import static com.example.pgyl.pekislib_a.Constants.UNDEFINED;
import static com.example.pgyl.pekislib_a.DotMatrixDisplayView.SCROLL_DIRECTIONS;
import static com.example.pgyl.pekislib_a.DotMatrixFontUtils.getFontTextDimensions;
import static com.example.pgyl.pekislib_a.MiscUtils.BiDimensions;
import static com.example.pgyl.pekislib_a.TimeDateUtils.MILLISECONDS_PER_SECOND;
//import static com.example.pgyl.swtimer_a.StringDBTables.getDotMatrixDisplayColorsBackIndex;
//import static com.example.pgyl.swtimer_a.StringDBTables.getDotMatrixDisplayColorsOffIndex;
//import static com.example.pgyl.swtimer_a.StringDBTables.getDotMatrixDisplayColorsOnLabelIndex;
//import static com.example.pgyl.swtimer_a.StringDBTables.getDotMatrixDisplayColorsOnTimeIndex;

public class CalcDotMatrixDisplayUpdater {
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
    private ColorBox colorBox;
    private int onTimeColorIndex;
    private int onLabelColorIndex;
    private int offColorIndex;
    private int backColorIndex;
    private int dotsPerSecond;
    private long updateInterval;
    private SCROLL_DIRECTIONS scrollDirection;
    private int scrollCount;
    private boolean automaticScrollOn;
    private boolean inAutomatic;
    private long timeStartAutomatic;
    private Handler handlerTime;
    private Runnable runnableTime;
    //endregion

    public CalcDotMatrixDisplayUpdater(DotMatrixDisplayView dotMatrixDisplayView) {
        super();

        this.dotMatrixDisplayView = dotMatrixDisplayView;
        init();
    }

    private void init() {
        final long DOTS_PER_SECOND_DEFAULT = 25;       //   25 points par seconde => +/- 4 caractères par secondes  (car un caractère avec marge droite a une largeur de 6 points)

        //setupRunnableTime();
        dotMatrixDisplayView.setExternalMarginCoeffs(new RectF(0.01f, UNDEFINED, UNDEFINED, UNDEFINED));
        dotMatrixDisplayView.setInternalMarginCoeffs(new RectF(0.01f, 0.01f, 0.01f, 0.01f));
        setupDefaultFont();
        setupExtraFont();
        setupColorIndexes();
        setupMargins();
        setupDimensions();
        resetScroll();
        setScrollSpeed(String.valueOf(DOTS_PER_SECOND_DEFAULT));
        colorBox = dotMatrixDisplayView.getColorBox();
        inAutomatic = false;
    }

    public void close() {
        stopAutomatic();
        runnableTime = null;
        handlerTime = null;
        defaultFont.close();
        defaultFont = null;
        extraFont.close();
        extraFont = null;
        colorBox.close();
        colorBox = null;
    }

    public void setColors(String[] colors) {
        this.colors = colors;   //  couleurs utilisées plus bas par displayTime() (ON TIME, ON LABEL) ou displayBackground() (OFF)) ... via colorBox de DotMatrixDisplayView
    }

    public void setDotSpacingCoeff(String dotSpacingCoeff) {
        dotMatrixDisplayView.setDotSpacingCoeff(dotSpacingCoeff);
    }

    public void setDotCornerRadiusCoeff(String dotCornerRadiusCoeff) {
        dotMatrixDisplayView.setDotCornerRadiusCoeff(dotCornerRadiusCoeff);
    }

    public void setScrollSpeed(String scrollSpeed) {
        dotsPerSecond = Integer.parseInt(scrollSpeed);
        updateInterval = getUpdateInterval(dotsPerSecond);
    }

    public void resetScroll() {
        scrollCount = 0;
        scrollDirection = SCROLL_DIRECTIONS.LEFT;
        dotMatrixDisplayView.resetScrollOffset();
    }

    public void rebuildStructure() {
        colorBox.setColor(DOT_MATRIX_COLOR_TYPES.BACK_SCREEN.INDEX(), colors[backColorIndex]);   //  Nécessaire pour DotMatrixDisplayView.createDotFormStencilBitmap()
        dotMatrixDisplayView.rebuildStructure();
    }   //  A appeler uniquement si MAJ en temps réel

    public void startAutomatic(long nowm, boolean automaticScrollOn) {
        this.automaticScrollOn = automaticScrollOn;
        //updateInterval = automaticScrollOn ? getUpdateInterval(dotsPerSecond) : APP_TIME_UNIT_PRECISION.DURATION_MS();   //  Si pas de scroll => Rafraichissement à la fréquence correspondant à la précision du chrono/timer
        timeStartAutomatic = nowm;
        handlerTime.postDelayed(runnableTime, updateInterval - (System.currentTimeMillis() - nowm));   //  Respecter updateInterval à partir de nowm
    }

    public void stopAutomatic() {
        if (handlerTime != null) {
            handlerTime.removeCallbacks(runnableTime);
        }
    }

    private void automatic() {   // automatic() continue d'être appelé même si dotsPerSecond = 0 (cf getUpdateInterval()) mais ne doit pas scroller
        handlerTime.postDelayed(runnableTime, updateInterval);
        long nowm = System.currentTimeMillis();
        if ((!inAutomatic) && (!dotMatrixDisplayView.isDrawing())) {   //  OK pour rafraîchir l'affichage
            inAutomatic = true;
            automaticDisplay(nowm);
            timeStartAutomatic = nowm;   //  Mettre à jour le moment du dernier rafraichissement d'affichage
            inAutomatic = false;
        }
    }

    private void automaticDisplay(long nowm) {
        final int MAX_SCROLL_COUNT = 2 * gridRect.width();   //  Scroll de 2 grilles complètes avant changement de sens

        if (automaticScrollOn) {
            if (dotsPerSecond != 0) {   //  Scroll à effectuer
                int dotsElapsed = (int) ((nowm - timeStartAutomatic + (updateInterval / 2)) / updateInterval);   //  Arrondir le nombre de points écoulés depuis timeStart
                int scrollDiff = dotsElapsed % MAX_SCROLL_COUNT;
                scrollCount = scrollCount + scrollDiff;
                if (scrollCount > MAX_SCROLL_COUNT) {
                    scrollCount = scrollCount - MAX_SCROLL_COUNT;
                    scrollDiff = 2 * scrollCount - scrollDiff;   //  Poursuivre au max dans le même sens (scrollDiff - scrollCount) et le reste dans l'autre sens (scrollCount) => en net: 2 * scrollCount - scrollDiff, dans l'autre sens
                    scrollDirection = (scrollDirection.equals(SCROLL_DIRECTIONS.LEFT)) ? SCROLL_DIRECTIONS.RIGHT : SCROLL_DIRECTIONS.LEFT;   //  Changer le sens du scroll
                }
                dotMatrixDisplayView.scroll(scrollDirection, scrollDiff);
                dotMatrixDisplayView.updateDisplay();
            }
        } else {
            displayBackground();
            //displayTime(msToTimeFormatD(currentCtRecord.getTimeDisplay(nowm), APP_TIME_UNIT_PRECISION, APP_TIME_UNIT_PRECISION));
            dotMatrixDisplayView.updateDisplay();
        }
    }

    private long getUpdateInterval(int dotsPerSecond) {
        return (dotsPerSecond != 0) ? MILLISECONDS_PER_SECOND / dotsPerSecond : MILLISECONDS_PER_SECOND;   //  Continuer à appeler automatic() même si dotsPerSecond = 0, mais sans scroll
    }

    private void displayBackground() {
        colorBox.setColor(DOT_MATRIX_COLOR_TYPES.UNPRESSED_BACK.INDEX(), colors[offColorIndex]);
        colorBox.setColor(DOT_MATRIX_COLOR_TYPES.PRESSED_BACK.INDEX(), colors[onTimeColorIndex]);
        dotMatrixDisplayView.drawBackRect(displayRect);
    }

    public void displayText(String text, boolean includeExtraFont) {
        displayBackground();
        colorBox.setColor(DOT_MATRIX_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), colors[onTimeColorIndex]);
        colorBox.setColor(DOT_MATRIX_COLOR_TYPES.PRESSED_FRONT.INDEX(), colors[offColorIndex]);
        dotMatrixDisplayView.setSymbolPos(displayRect.left + margins.left, displayRect.top + margins.top);
        dotMatrixDisplayView.drawFrontText(text, (includeExtraFont ? extraFont : null), defaultFont);   //  Temps avec police extra prioritaire
    }

    private void setupColorIndexes() {
        onTimeColorIndex = 0;
        offColorIndex = 1;
        backColorIndex = 2;
    }

    private void setupDefaultFont() {
        defaultFont = new DotMatrixFontDefault();
    }

    private void setupExtraFont() {
        extraFont = new CalcExtraDotMatrixFont(defaultFont);
    }

    private void setupMargins() {    // Marges autour de l'affichage proprement dit
        final int MARGIN_LEFT = 1;
        final int MARGIN_RIGHT = 1;
        //final int MARGIN_TOP = 1;

        int marginBottom = extraFont.getSymbolByCode(46).getDimensions().height;  // Le  "." est affiché dans la marge inférieure
        int marginTop = extraFont.getSymbolByCode(44).getDimensions().height;  // Le  point séparateur de milliers (appelé avec le caractère ",") est affiché dans la marge supérieure
        margins = new Rect(MARGIN_LEFT, marginTop, MARGIN_RIGHT, marginBottom);
    }

    private void setupDimensions() {       //  La grille (gridRect) contient le temps et le label, et seule une partie est affichée (gridDisplayRect, glissant en cas de scroll)
        final String DIM_STRING = "123456789012345678";

        BiDimensions timeTextDimensions = getFontTextDimensions(DIM_STRING, extraFont, defaultFont);  // timeText mélange de l'extraFont (pour les ":" et ".") et defaultFont (pour les chiffres de 0 à 9). getTimeDefInit() utilisé juste pour avoir un temps
        //BiDimensions labelTextDimensions = getFontTextDimensions(currentCtRecord.getLabel(), null, defaultFont);   //  labelText est uniquement affiché en defaultFont

        int displayRectWidth = margins.left + timeTextDimensions.width - defaultFont.getRightMargin() + margins.right;   //   Affichage sur la largeur du temps, avec margins.right remplaçant la dernière marge droite)
        int displayRectHeight = margins.top + timeTextDimensions.height + margins.bottom;   //  Affichage du temps uniquement ou (via scroll) du temps et du label , sur la hauteur nécessaire
        int gridRectWidth = displayRectWidth;
        //if (labelTextDimensions.width > 0) {   // La grille doit pouvoir contenir le temps et le label sur toute sa largeur ...
        //    gridRectWidth = gridRectWidth + labelTextDimensions.width - defaultFont.getRightMargin();
        //}

        gridRect = new Rect(0, 0, gridRectWidth, displayRectHeight);   //  ... et la même hauteur que la fenêtre d'affichage
        displayRect = new Rect(gridRect.left, gridRect.top, displayRectWidth, displayRectHeight);  //  Affichage au début de la grille
        //halfDisplayRect = new Rect(displayRect.right / 2, displayRect.top, displayRect.right, displayRect.bottom);  //  Pour affichage partagé dans CtDisplayColorsActivity
        //labelRect = new Rect(displayRect.right, gridRect.top, gridRect.right, gridRect.bottom);   //  Espace restant de la grille
        Rect scrollRect = new Rect(gridRect);   //  On scrolle la grille entière (margins.left servira de margins.right)

        dotMatrixDisplayView.setGridRect(gridRect);
        dotMatrixDisplayView.setDisplayRect(displayRect);
        dotMatrixDisplayView.setScrollRect(scrollRect);
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