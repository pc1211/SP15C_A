package com.example.pgyl.sp15c_a;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.pgyl.pekislib_a.ColorBox;
import com.larvalabs.svgandroid.SVGParser;

import static com.example.pgyl.pekislib_a.ColorUtils.BUTTON_COLOR_TYPES;
import static com.example.pgyl.pekislib_a.Constants.BUTTON_STATES;
import static com.example.pgyl.pekislib_a.MiscUtils.DpToPixels;
import static com.example.pgyl.pekislib_a.MiscUtils.getPictureFromDrawable;
import static com.example.pgyl.pekislib_a.PointRectUtils.ALIGN_WIDTH_HEIGHT;
import static com.example.pgyl.pekislib_a.PointRectUtils.getMaxSubRect;

public final class ImageButtonViewStack extends TextView {

    public interface onCustomClickListener {
        void onCustomClick();
    }

    public void setOnCustomClickListener(onCustomClickListener listener) {
        mOnCustomClickListener = listener;
    }

    private onCustomClickListener mOnCustomClickListener;
    //region Variables
    private int imageCount;
    private long minClickTimeInterval;
    private int pcBackCornerRadius;
    private int backCornerRadius;
    private long lastClickUpTime;
    private BUTTON_STATES buttonState;
    private float outlineStrokeWidthPx;
    private ColorBox[] imageColorBoxes;
    private ColorBox[] defaultImageColorBoxes;
    private ColorBox defautKeyColorBox;
    private ColorBox keyColorBox;
    private boolean[] hasFrontColorFilters;
    private boolean[] hasBackColorFilters;
    private boolean clickDownInButtonZone;
    private RectF buttonZone;
    private Paint imageFrontPaint;
    private Paint imageBackPaint;
    private Paint buttonBackPaint;
    private Paint buttonOutlinePaint;
    private int[] heightWeights;
    private Picture[] pictures;
    private boolean[] imageVisibilities;
    private RectF[] imageRelativePositionCoeffs;
    private float[] imageAspectRatios;
    private float[] imageSizeCoeffs;
    private RectF viewCanvasRectExceptOutline;
    private RectF[] viewCanvasSubRects;
    private RectF[] viewCanvasImageSubRects;
    private boolean dirtyRects;
    private Context context;
    //endregion

    public ImageButtonViewStack(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        init();
    }

    public void init() {   //  Le bouton par défaut est un rectangle arrondi gris, entouré de gris clair, devenant orange si pressé :), avec un texte noir
        final int OUTLINE_STROKE_WIDTH_DP_DEFAULT = 2;   //  dp
        final String TEXT_DEFAULT = "";
        final int PC_BACK_CORNER_RADIUS_DEFAULT = 35;    //  % appliqué à 1/2 largeur ou hauteur pour déterminer le rayon du coin arrondi
        final long MIN_CLICK_TIME_INTERVAL_DEFAULT_VALUE = 0;   //   Interval de temps (ms) minimum imposé entre 2 click

        imageCount = 0;
        buttonZone = new RectF();
        buttonState = BUTTON_STATES.UNPRESSED;
        pcBackCornerRadius = PC_BACK_CORNER_RADIUS_DEFAULT;
        outlineStrokeWidthPx = (int) DpToPixels(OUTLINE_STROKE_WIDTH_DP_DEFAULT, context);
        minClickTimeInterval = MIN_CLICK_TIME_INTERVAL_DEFAULT_VALUE;
        lastClickUpTime = 0;
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onButtonTouch(v, event);
            }
        });
        setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        setupImageFrontPaint();
        setupImageBackPaint();
        setupButtonBackPaint();
        setupButtonOutlinePaint();
        setupDefaultKeyColorBox();
        setupKeyColorBox();
        setupTextColor();
        setText(TEXT_DEFAULT);
        dirtyRects = false;   //  Attendre setImageCount pour recalculer les rectangles
    }

    public void setImageCount(int count) {
        final float SIZE_COEFF_DEFAULT = 0.8f;   //  (0..1)
        final int HEIGHT_WEIGHT_DEFAULT = 1;

        imageCount = count;
        pictures = new Picture[imageCount];
        imageVisibilities = new boolean[imageCount];
        heightWeights = new int[imageCount];
        imageRelativePositionCoeffs = new RectF[imageCount];
        imageAspectRatios = new float[imageCount];
        imageSizeCoeffs = new float[imageCount];
        viewCanvasSubRects = new RectF[imageCount];
        viewCanvasImageSubRects = new RectF[imageCount];
        for (int i = 0; i < imageCount; i = i + 1) {
            pictures[i] = null;   //  En attendant l'affectation éventuelle via setSVGImageResource ou setPNGImageResource
            imageVisibilities[i] = true;
            heightWeights[i] = HEIGHT_WEIGHT_DEFAULT;
            imageSizeCoeffs[i] = SIZE_COEFF_DEFAULT;
            imageRelativePositionCoeffs[i] = ALIGN_WIDTH_HEIGHT;
        }
        setupDefaultKeyColorBox();
        setupKeyColorBox();
        setupDefaultImageColorBoxes();
        setupImageColorBoxes();
        dirtyRects = true;
    }

    public void setSVGImageResource(int index, int resId) {
        pictures[index] = SVGParser.getSVGFromResource(getResources(), resId).getPicture();
        imageAspectRatios[index] = (float) pictures[index].getHeight() / (float) pictures[index].getWidth();
        dirtyRects = true;
    }

    public void setPNGImageResource(int index, int resId) {
        pictures[index] = getPictureFromDrawable((BitmapDrawable) getResources().getDrawable(resId, context.getTheme()));
        imageAspectRatios[index] = (float) pictures[index].getHeight() / (float) pictures[index].getWidth();
        dirtyRects = true;
    }

    public void setImageVisibilities(int index, boolean visibility) {
        imageVisibilities[index] = visibility;
    }

    public void setHeightWeight(int index, int heightWeight) {
        heightWeights[index] = heightWeight;
        dirtyRects = true;
    }

    public void setHasFrontColorFilter(int index, boolean hasFrontColorFilter) {
        hasFrontColorFilters[index] = hasFrontColorFilter;
    }

    public void setHasBackColorFilter(int index, boolean hasBackColorFilter) {
        hasBackColorFilters[index] = hasBackColorFilter;
    }

    public void setImageRelativePositionCoeff(int index, RectF imageRelativePositionCoeff) {
        imageRelativePositionCoeffs[index] = imageRelativePositionCoeff;
        dirtyRects = true;
    }

    public void setImageSizeCoeff(int index, float imageSizeCoeff) {
        imageSizeCoeffs[index] = imageSizeCoeff;
        dirtyRects = true;
    }

    public void setOutlineStrokeWidthDp(int outlineStrokeWidthDp) {
        outlineStrokeWidthPx = (int) DpToPixels(outlineStrokeWidthDp, context);
        dirtyRects = true;
    }

    public void setMinClickTimeInterval(long minClickTimeInterval) {
        this.minClickTimeInterval = minClickTimeInterval;
    }

    public void setPcBackCornerRadius(int pcBackCornerRadius) {
        this.pcBackCornerRadius = pcBackCornerRadius;
    }

    public ColorBox[] getImageColorBoxes() {   //   On peut alors modifier les couleurs (colorBox.setColor...), puis faire updateDisplayColors() pour mettre à jour l'affichage
        return imageColorBoxes;
    }

    public ColorBox[] getDefaultImageColorBoxes() {   //   On peut alors modifier les couleurs (colorBox.setColor...), puis faire updateDisplayColors() pour mettre à jour l'affichage
        return defaultImageColorBoxes;
    }

    public ColorBox getKeyColorBox() {   //   On peut alors modifier les couleurs (colorBox.setColor...), puis faire updateDisplayColors() pour mettre à jour l'affichage
        return keyColorBox;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        imageFrontPaint = null;
        imageBackPaint = null;
        buttonBackPaint = null;
        buttonOutlinePaint = null;
        for (int i = 0; i < imageCount; i = i + 1) {
            imageColorBoxes[i].close();
            imageColorBoxes[i] = null;
            pictures[i] = null;
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        dirtyRects = true;
        buttonZone.set(getLeft(), getTop(), getLeft() + w, getTop() + h);
        backCornerRadius = (Math.min(w, h) * pcBackCornerRadius) / 200;    //  Rayon pour coin arrondi (% appliqué à la moitié de la largeur ou hauteur)
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (dirtyRects) {
            dirtyRects = false;
            setupViewCanvasRects(getWidth(), getHeight());
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);
        for (int i = 0; i < imageCount; i = i + 1) {
            if (imageVisibilities[i]) {
                int frontColor = (buttonState.equals(BUTTON_STATES.PRESSED)) ? imageColorBoxes[i].getColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX()).RGBInt : imageColorBoxes[i].getColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX()).RGBInt;
                int backColor = (buttonState.equals(BUTTON_STATES.PRESSED)) ? imageColorBoxes[i].getColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX()).RGBInt : imageColorBoxes[i].getColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX()).RGBInt;

                if (pictures[i] != null) {
                    canvas.drawPicture(pictures[i], viewCanvasImageSubRects[i]);
                    if (hasFrontColorFilters[i]) {
                        imageFrontPaint.setColor(frontColor);
                        canvas.drawRect(viewCanvasSubRects[i], imageFrontPaint);
                    }
                    if (hasBackColorFilters[i]) {
                        imageBackPaint.setColor(backColor);
                        canvas.drawRect(viewCanvasSubRects[i], imageBackPaint);
                    }
                } else {   //  Pas de Picture => Un ractangle simple suffit; Le texte (avec sa couleur) viendra au-dessus
                    if (hasBackColorFilters[i]) {
                        buttonBackPaint.setColor(backColor);
                        canvas.drawRect(viewCanvasSubRects[i], buttonBackPaint);
                    }
                }
            }
        }
        if (outlineStrokeWidthPx != 0) {
            int outlineColor = (buttonState.equals(BUTTON_STATES.PRESSED)) ? keyColorBox.getColor(BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX()).RGBInt : keyColorBox.getColor(BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX()).RGBInt;
            buttonOutlinePaint.setStrokeWidth(outlineStrokeWidthPx);
            buttonOutlinePaint.setColor(outlineColor);
            canvas.drawRect(viewCanvasRectExceptOutline, buttonOutlinePaint);
        }
        canvas.drawColor(keyColorBox.getColor(BUTTON_COLOR_TYPES.BACK_SCREEN.INDEX()).RGBInt, PorterDuff.Mode.DST_OVER);

        super.onDraw(canvas);   //  Dessinera le texte (avec sa couleur) au-dessus
    }

    public boolean onButtonTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            clickDownInButtonZone = true;
            buttonState = BUTTON_STATES.PRESSED;
            v.getParent().requestDisallowInterceptTouchEvent(true);   //  Une listView éventuelle (qui contient des items avec ce contrôle et voudrait scroller) ne pourra voler l'événement ACTION_MOVE de ce contrôle
            invalidate();
            return true;
        }
        if ((action == MotionEvent.ACTION_MOVE) || (action == MotionEvent.ACTION_UP)) {
            if (clickDownInButtonZone) {
                if (buttonZone.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
                    if (action == MotionEvent.ACTION_UP) {
                        long nowm = System.currentTimeMillis();
                        buttonState = BUTTON_STATES.UNPRESSED;
                        invalidate();
                        if ((nowm - lastClickUpTime) >= minClickTimeInterval) {   //  OK pour traiter le click
                            lastClickUpTime = nowm;
                            if (mOnCustomClickListener != null) {
                                mOnCustomClickListener.onCustomClick();
                            }
                        } else {   //  Attendre pour pouvoir traiter un autre click
                            clickDownInButtonZone = false;
                        }
                    }
                } else {
                    clickDownInButtonZone = false;
                    buttonState = BUTTON_STATES.UNPRESSED;
                    invalidate();
                }
            }
            return (action == MotionEvent.ACTION_MOVE);
        }
        return false;
    }

    public void updateDisplay() {   //  A appeler à chaque mise à jour de colorBox (ou le cas échéant de defaultColorBow)
        setTextColor(keyColorBox.getColor(BUTTON_COLOR_TYPES.TEXT.INDEX()).RGBInt);   //  TextView.setTextColor ne devrait pas être appelé dans onDraw() car TextView.setTextColor appelle déjà lui-même invalidate() et donc onDraw() => Boucle infinie
        invalidate();   //  Semble obligatoire dans certains cas même après TextView.SetTextColor()
    }

    private void setupViewCanvasRects(int w, int h) {
        viewCanvasRectExceptOutline = new RectF(outlineStrokeWidthPx, outlineStrokeWidthPx, w - outlineStrokeWidthPx, h - outlineStrokeWidthPx);
        if (imageCount >= 1) {
            float weightSum = 0;
            for (int i = 0; i < imageCount; i = i + 1) {
                weightSum = weightSum + (float) heightWeights[i];
            }
            float hpSum = 0;
            float ht = 0;
            for (int i = 0; i < imageCount; i = i + 1) {
                ht = ((float) heightWeights[i] / (float) weightSum) * (float) viewCanvasRectExceptOutline.height();
                viewCanvasSubRects[i] = new RectF(viewCanvasRectExceptOutline.left, viewCanvasRectExceptOutline.top + hpSum,
                        viewCanvasRectExceptOutline.right, viewCanvasRectExceptOutline.top + hpSum + ht);
                viewCanvasImageSubRects[i] = getMaxSubRect(viewCanvasSubRects[i], imageRelativePositionCoeffs[i], imageAspectRatios[i], imageSizeCoeffs[i]);
                hpSum = hpSum + ht;
            }
        }
    }

    private void setupDefaultImageColorBoxes() {
        final String UNPRESSED_OUTLINE_COLOR_DEFAULT = "A0A0A0";
        final String UNPRESSED_FRONT_COLOR_DEFAULT = "000000";
        final String UNPRESSED_BACK_COLOR_DEFAULT = "C0C0C0";
        final String PRESSED_BACK_COLOR_DEFAULT = "FF9A22";
        final String PRESSED_OUTLINE_COLOR_DEFAULT = "FF9A22";
        final String TEXT_COLOR_DEFAULT = "000000";

        defaultImageColorBoxes = new ColorBox[imageCount];
        for (int i = 0; i < imageCount; i = i + 1) {
            defaultImageColorBoxes[i] = new ColorBox();
            defaultImageColorBoxes[i].setColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), UNPRESSED_FRONT_COLOR_DEFAULT);
            defaultImageColorBoxes[i].setColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX(), UNPRESSED_BACK_COLOR_DEFAULT);
            defaultImageColorBoxes[i].setColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX(), UNPRESSED_FRONT_COLOR_DEFAULT);
            defaultImageColorBoxes[i].setColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX(), PRESSED_BACK_COLOR_DEFAULT);
        }
    }

    private void setupImageColorBoxes() {
        final boolean HAS_FRONT_COLOR_FILTER_DEFAULT = true;
        final boolean HAS_BACK_COLOR_FILTER_DEFAULT = true;

        imageColorBoxes = new ColorBox[imageCount];
        hasFrontColorFilters = new boolean[imageCount];
        hasBackColorFilters = new boolean[imageCount];
        for (int i = 0; i < imageCount; i = i + 1) {
            imageColorBoxes[i] = new ColorBox();
            hasFrontColorFilters[i] = HAS_FRONT_COLOR_FILTER_DEFAULT;
            hasBackColorFilters[i] = HAS_BACK_COLOR_FILTER_DEFAULT;
            imageColorBoxes[i].setColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX(), defaultImageColorBoxes[i].getColor(BUTTON_COLOR_TYPES.UNPRESSED_FRONT.INDEX()).RGBString);
            imageColorBoxes[i].setColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX(), defaultImageColorBoxes[i].getColor(BUTTON_COLOR_TYPES.UNPRESSED_BACK.INDEX()).RGBString);
            imageColorBoxes[i].setColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX(), defaultImageColorBoxes[i].getColor(BUTTON_COLOR_TYPES.PRESSED_FRONT.INDEX()).RGBString);
            imageColorBoxes[i].setColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX(), defaultImageColorBoxes[i].getColor(BUTTON_COLOR_TYPES.PRESSED_BACK.INDEX()).RGBString);
        }

        keyColorBox = new ColorBox();
        keyColorBox.setColor(BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), defautKeyColorBox.getColor(BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX()).RGBString);
        keyColorBox.setColor(BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX(), defautKeyColorBox.getColor(BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX()).RGBString);
        keyColorBox.setColor(BUTTON_COLOR_TYPES.TEXT.INDEX(), defautKeyColorBox.getColor(BUTTON_COLOR_TYPES.TEXT.INDEX()).RGBString);
    }

    private void setupDefaultKeyColorBox() {
        final String UNPRESSED_OUTLINE_COLOR_DEFAULT = "A0A0A0";
        final String PRESSED_OUTLINE_COLOR_DEFAULT = "FF9A22";
        final String TEXT_COLOR_DEFAULT = "000000";
        final String BACK_SCREEN_COLOR_DEFAULT = "00FFFF";

        defautKeyColorBox = new ColorBox();
        defautKeyColorBox.setColor(BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), UNPRESSED_OUTLINE_COLOR_DEFAULT);
        defautKeyColorBox.setColor(BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX(), PRESSED_OUTLINE_COLOR_DEFAULT);
        defautKeyColorBox.setColor(BUTTON_COLOR_TYPES.TEXT.INDEX(), TEXT_COLOR_DEFAULT);
        defautKeyColorBox.setColor(BUTTON_COLOR_TYPES.BACK_SCREEN.INDEX(), BACK_SCREEN_COLOR_DEFAULT);
    }

    private void setupKeyColorBox() {
        keyColorBox = new ColorBox();
        keyColorBox.setColor(BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX(), defautKeyColorBox.getColor(BUTTON_COLOR_TYPES.UNPRESSED_OUTLINE.INDEX()).RGBString);
        keyColorBox.setColor(BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX(), defautKeyColorBox.getColor(BUTTON_COLOR_TYPES.PRESSED_OUTLINE.INDEX()).RGBString);
        keyColorBox.setColor(BUTTON_COLOR_TYPES.TEXT.INDEX(), defautKeyColorBox.getColor(BUTTON_COLOR_TYPES.TEXT.INDEX()).RGBString);
        keyColorBox.setColor(BUTTON_COLOR_TYPES.BACK_SCREEN.INDEX(), defautKeyColorBox.getColor(BUTTON_COLOR_TYPES.BACK_SCREEN.INDEX()).RGBString);
    }

    private void setupTextColor() {
        setTextColor(defautKeyColorBox.getColor(BUTTON_COLOR_TYPES.TEXT.INDEX()).RGBInt);   //  Nécessaire ici car seul invalidate() est ensuite appelé (et non pas ImageButtonView.updateDisplay())}
    }

    private void setupImageFrontPaint() {
        imageFrontPaint = new Paint();
        imageFrontPaint.setAntiAlias(true);
        imageFrontPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        imageFrontPaint.setStyle(Paint.Style.FILL);
    }

    private void setupImageBackPaint() {
        imageBackPaint = new Paint();
        imageBackPaint.setAntiAlias(true);
        imageBackPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        imageBackPaint.setStyle(Paint.Style.FILL);
    }

    private void setupButtonBackPaint() {
        buttonBackPaint = new Paint();
        buttonBackPaint.setAntiAlias(true);
        buttonBackPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        buttonBackPaint.setStyle(Paint.Style.FILL);
    }

    private void setupButtonOutlinePaint() {
        buttonOutlinePaint = new Paint();
        buttonOutlinePaint.setAntiAlias(true);
        buttonOutlinePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        buttonOutlinePaint.setStyle(Paint.Style.STROKE);
    }
}