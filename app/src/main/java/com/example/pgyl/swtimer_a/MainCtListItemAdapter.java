package com.example.pgyl.swtimer_a;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.pgyl.pekislib_a.DotMatrixDisplayView;
import com.example.pgyl.pekislib_a.StringDB;
import com.example.pgyl.pekislib_a.StringDBTables.ACTIVITY_START_STATUS;
import com.example.pgyl.pekislib_a.SymbolButtonView;

import java.util.ArrayList;

import static com.example.pgyl.pekislib_a.StringDBUtils.setStartStatusOfActivity;
import static com.example.pgyl.swtimer_a.Constants.SWTIMER_ACTIVITIES;
import static com.example.pgyl.swtimer_a.CtDisplayActivity.CTDISPLAY_EXTRA_KEYS;
import static com.example.pgyl.swtimer_a.CtRecord.MODES;

public class MainCtListItemAdapter extends BaseAdapter {

    public interface onCheckBoxClickListener {
        void onCheckBoxClick();
    }

    public void setOnItemCheckBoxClick(onCheckBoxClickListener listener) {
        mOnCheckBoxClickListener = listener;
    }

    private onCheckBoxClickListener mOnCheckBoxClickListener;

    //region Variables
    private Context context;
    private ArrayList<CtRecord> ctRecords;
    private StringDB stringDB;
    private boolean showExpirationTime;
    private boolean setClockAppAlarmOnStartTimer;
    private MainCtListItemDotMatrixDisplayUpdater mainCtListItemDotMatrixDisplayUpdater;
    //endregion

    public MainCtListItemAdapter(Context context, StringDB stringDB) {
        super();

        this.context = context;
        this.stringDB = stringDB;
        init();
    }

    private void init() {
        mOnCheckBoxClickListener = null;
        ctRecords = null;
        setupMainCtListItemDotMatrixDisplayUpdater();
    }

    public void close() {
        mainCtListItemDotMatrixDisplayUpdater.close();
        mainCtListItemDotMatrixDisplayUpdater = null;
        ctRecords = null;
        stringDB = null;
        context = null;
    }

    public void setItems(ArrayList<CtRecord> ctRecords) {
        this.ctRecords = ctRecords;
    }

    public void setClockAppAlarmOnStartTimer(boolean setClockAppAlarmOnStartTimer) {
        this.setClockAppAlarmOnStartTimer = setClockAppAlarmOnStartTimer;
    }

    public void setShowExpirationTime(boolean showExpirationTime) {
        this.showExpirationTime = showExpirationTime;
    }

    @Override
    public int getCount() {
        return (ctRecords != null) ? ctRecords.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private void onButtonModeSelectionClick(View rowv, int pos) {
        long nowm = System.currentTimeMillis();
        ctRecords.get(pos).setSelectedOn(!ctRecords.get(pos).isSelected());   //  Invert selection
        if (mOnCheckBoxClickListener != null) {
            mOnCheckBoxClickListener.onCheckBoxClick();
        }
        paintView(rowv, pos, nowm);
    }

    private void onButtonModeRunClick(View rowv, int pos) {
        long nowm = System.currentTimeMillis();
        if (!ctRecords.get(pos).isRunning()) {
            ctRecords.get(pos).start(nowm, setClockAppAlarmOnStartTimer);
        } else {
            ctRecords.get(pos).stop(nowm);
        }
        paintView(rowv, pos, nowm);
    }

    private void onButtonSplitResetClick(View rowv, int pos) {
        long nowm = System.currentTimeMillis();
        if ((ctRecords.get(pos).isRunning()) || (ctRecords.get(pos).isSplitted())) {
            ctRecords.get(pos).split(nowm);
        } else {
            ctRecords.get(pos).reset();
        }
        paintView(rowv, pos, nowm);
    }

    private void onButtonClockAppAlarmClick(int pos) {
        ctRecords.get(pos).setClockAppAlarmOn(!ctRecords.get(pos).isClockAppAlarmOn());
    }

    private void onTimeLabelClick(int pos) {
        launchCtDisplayActivity(ctRecords.get(pos).getIdct());
    }

    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {   //  Viewholder pattern non utilisé à cause de la custom view DotMatrixDisplayView (ses variables globales ne sont pas récupérées par un getTag())
        LayoutInflater inflater = LayoutInflater.from(context);
        rowView = inflater.inflate(R.layout.mainctlistitem, null);
        MainCtListItemViewHolder viewHolder = buildViewHolder(rowView);
        rowView.setTag(viewHolder);

        setupViewHolder(viewHolder, rowView, position);
        long nowm = System.currentTimeMillis();
        paintView(rowView, position, nowm);
        return rowView;
    }

    public void paintView(View rowView, int position, long nowm) {    //  Décoration proprement dite du getView
        int pos = position;
        MainCtListItemViewHolder viewHolder = (MainCtListItemViewHolder) rowView.getTag();

        final String ON_COLOR_1 = "668CFF";
        final String BACK_COLOR_1 = "000000";
        boolean b = ctRecords.get(pos).isSelected();
        String frontColor = (b ? BACK_COLOR_1 : ON_COLOR_1);
        String backColor = (b ? ON_COLOR_1 : BACK_COLOR_1);
        String extraColor = (b ? BACK_COLOR_1 : ON_COLOR_1);
        viewHolder.buttonModeSelection.setColors(frontColor, backColor, extraColor);

        final String ON_COLOR_2 = "FF9A22";
        final String OFF_COLOR_2 = "000000";
        final String BACK_COLOR_2 = "606060";
        backColor = BACK_COLOR_2;
        b = ctRecords.get(pos).isRunning();
        frontColor = (b ? ON_COLOR_2 : OFF_COLOR_2);
        extraColor = (b ? OFF_COLOR_2 : ON_COLOR_2);
        viewHolder.buttonModeRun.setColors(frontColor, backColor, extraColor);

        b = ctRecords.get(pos).isSplitted();
        frontColor = (b ? ON_COLOR_2 : OFF_COLOR_2);
        extraColor = (b ? OFF_COLOR_2 : ON_COLOR_2);
        viewHolder.buttonSplitReset.setColors(frontColor, backColor, extraColor);

        b = ctRecords.get(pos).isClockAppAlarmOn();
        frontColor = (b ? ON_COLOR_2 : OFF_COLOR_2);
        extraColor = (b ? OFF_COLOR_2 : ON_COLOR_2);
        viewHolder.buttonClockAppAlarm.setColors(frontColor, backColor, extraColor);

        mainCtListItemDotMatrixDisplayUpdater.displayTimeAndLabel(viewHolder.buttonDotMatrixDisplayTimeLabel, ctRecords.get(pos), showExpirationTime, nowm);
    }

    private MainCtListItemViewHolder buildViewHolder(View rowView) {
        MainCtListItemViewHolder viewHolder = new MainCtListItemViewHolder();
        viewHolder.buttonModeSelection = rowView.findViewById(R.id.STATE_BTN_MODE_SELECTION);
        viewHolder.buttonModeRun = rowView.findViewById(R.id.STATE_BTN_RUN);
        viewHolder.buttonSplitReset = rowView.findViewById(R.id.STATE_BTN_SPLIT_RESET);
        viewHolder.buttonClockAppAlarm = rowView.findViewById(R.id.STATE_BTN_CLOCK_APP_ALARM);
        viewHolder.buttonDotMatrixDisplayTimeLabel = rowView.findViewById(R.id.BTN_DOT_MATRIX_DISPLAY_TIME_LABEL);
        return viewHolder;
    }

    private void setupViewHolder(MainCtListItemViewHolder viewHolder, View rowView, int position) {
        final long BUTTON_MIN_CLICK_TIME_INTERVAL_MS = 500;
        final float STATE_BUTTON_SYMBOL_SIZE_COEFF = 0.75f;   //  Pour que le symbole ne frôle pas les bords de sa View

        final View rowv = rowView;
        final int pos = position;

        viewHolder.buttonModeSelection.setSVGImageResource((ctRecords.get(pos).getMode().equals(MODES.CHRONO)) ? R.raw.ct_chrono : R.raw.ct_timer);
        viewHolder.buttonModeSelection.setSymbolSizeCoeff(STATE_BUTTON_SYMBOL_SIZE_COEFF);
        viewHolder.buttonModeSelection.setMinClickTimeInterval(BUTTON_MIN_CLICK_TIME_INTERVAL_MS);
        viewHolder.buttonModeSelection.setCustomOnClickListener(new SymbolButtonView.onCustomClickListener() {
            @Override
            public void onCustomClick() {
                onButtonModeSelectionClick(rowv, pos);
            }
        });
        viewHolder.buttonModeRun.setSVGImageResource(R.raw.ct_run);
        viewHolder.buttonModeRun.setSymbolSizeCoeff(STATE_BUTTON_SYMBOL_SIZE_COEFF);
        viewHolder.buttonModeRun.setCustomOnClickListener(new SymbolButtonView.onCustomClickListener() {
            @Override
            public void onCustomClick() {
                onButtonModeRunClick(rowv, pos);
            }
        });
        viewHolder.buttonSplitReset.setSVGImageResource(R.raw.ct_split);
        viewHolder.buttonSplitReset.setSymbolSizeCoeff(STATE_BUTTON_SYMBOL_SIZE_COEFF);
        viewHolder.buttonSplitReset.setMinClickTimeInterval(BUTTON_MIN_CLICK_TIME_INTERVAL_MS);
        viewHolder.buttonSplitReset.setCustomOnClickListener(new SymbolButtonView.onCustomClickListener() {
            @Override
            public void onCustomClick() {
                onButtonSplitResetClick(rowv, pos);
            }
        });
        viewHolder.buttonClockAppAlarm.setSVGImageResource(R.raw.ct_bell);
        viewHolder.buttonClockAppAlarm.setSymbolSizeCoeff(STATE_BUTTON_SYMBOL_SIZE_COEFF);
        viewHolder.buttonClockAppAlarm.setMinClickTimeInterval(BUTTON_MIN_CLICK_TIME_INTERVAL_MS);
        viewHolder.buttonClockAppAlarm.setCustomOnClickListener(new SymbolButtonView.onCustomClickListener() {
            @Override
            public void onCustomClick() {
                onButtonClockAppAlarmClick(pos);
            }
        });
        viewHolder.buttonDotMatrixDisplayTimeLabel.setMinClickTimeInterval(BUTTON_MIN_CLICK_TIME_INTERVAL_MS);
        viewHolder.buttonDotMatrixDisplayTimeLabel.setOnCustomClickListener(new DotMatrixDisplayView.onCustomClickListener() {
            @Override
            public void onCustomClick() {
                onTimeLabelClick(pos);
            }
        });
        mainCtListItemDotMatrixDisplayUpdater.setupDimensions(viewHolder.buttonDotMatrixDisplayTimeLabel);
        mainCtListItemDotMatrixDisplayUpdater.setupBackColor(viewHolder.buttonDotMatrixDisplayTimeLabel);
    }

    private void setupMainCtListItemDotMatrixDisplayUpdater() {
        mainCtListItemDotMatrixDisplayUpdater = new MainCtListItemDotMatrixDisplayUpdater();
    }

    private void launchCtDisplayActivity(int idct) {
        setStartStatusOfActivity(stringDB, SWTIMER_ACTIVITIES.CT_DISPLAY.toString(), ACTIVITY_START_STATUS.COLD);
        Intent callingIntent = new Intent(context, CtDisplayActivity.class);
        callingIntent.putExtra(CTDISPLAY_EXTRA_KEYS.CURRENT_CHRONO_TIMER_ID.toString(), idct);
        context.startActivity(callingIntent);
    }
}
