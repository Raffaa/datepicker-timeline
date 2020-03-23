package com.github.badoualy.datepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class MonthView extends RecyclerView {

    private static String[] MONTHS = DateFormatSymbols.getInstance().getShortMonths();

    private MonthAdapter adapter;
    private LinearLayoutManager layoutManager;
    private OnMonthSelectedListener onMonthSelectedListener;

    private int defaultColor, colorSelected, colorBeforeSelection;

    private int startYear = 1970, startMonth = 0;
    private int yearDigitCount = 2;
    private boolean yearOnNewLine = false;

    private int selectedYear, selectedMonth;
    private int selectedPosition = -1;
    private int monthCount = Integer.MAX_VALUE;

    public MonthView(Context context) {
        super(context);
        init();
    }

    public MonthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MonthView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        final Calendar calendar = Calendar.getInstance();
        setSelectedMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), false);

        setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        adapter = new MonthAdapter();
        setLayoutManager(layoutManager);
        setAdapter(adapter);
    }

    public void setSelectedMonth(int year, int month) {
        setSelectedMonth(year, month, true, true);
    }

    public void setSelectedMonth(int year, int month, boolean callListener) {
        setSelectedMonth(year, month, callListener, true);
    }

    public void setSelectedMonth(int year, int month, boolean callListener, boolean centerOnPosition) {
        onMonthSelected(year, month, callListener, centerOnPosition);
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public int getSelectedMonth() {
        return selectedMonth;
    }

    private void onMonthSelected(int year, int month, boolean callListener, boolean centerOnPosition) {
        int oldPosition = selectedPosition;
        selectedPosition = getPositionForDate(year, month);
        selectedYear = year;
        selectedMonth = month;

        if (selectedPosition == oldPosition) {
            if (centerOnPosition) {
                centerOnPosition(selectedPosition);
            }
            return;
        }

        if (adapter != null && layoutManager != null) {
            final int rangeStart = Math.min(oldPosition, selectedPosition);
            final int rangeEnd = Math.max(oldPosition, selectedPosition);
            adapter.notifyItemRangeChanged(rangeStart, rangeEnd - rangeStart + 1);

            // Animate scroll
            if (centerOnPosition) {
                centerOnPosition(selectedPosition);
            }

            if (callListener && onMonthSelectedListener != null) {
                onMonthSelectedListener.onMonthSelected(year, month, selectedPosition);
            }
        } else if (centerOnPosition) {
            post(new Runnable() {
                @Override
                public void run() {
                    centerOnPosition(selectedPosition);
                }
            });
        }
    }

    public void centerOnPosition(int position) {
        if (getChildCount() == 0) {
            return;
        } else {
            if (!isLaidOut()) {
                return;
            }
        }
        // Animate scroll
        int offset = getMeasuredWidth() / 2 - getItemWidth() / 2;
        layoutManager.scrollToPositionWithOffset(position, offset);
    }

    public void centerOnDate(int year, int month) {
        centerOnPosition(getPositionForDate(year, month));
    }

    public void centerOnSelection() {
        centerOnPosition(selectedPosition);
    }

    void scrollToYearPosition(int year, int offsetYear) {
        if (getChildCount() == 0) {
            return;
        } else {
            if (!isLaidOut()) {
                return;
            }
        }
        // Animate scroll
        layoutManager.scrollToPositionWithOffset(getPositionForDate(year + 1, 0),
                offsetYear + getMeasuredWidth() / 2 - getItemWidth() / 2);
    }

    int getItemWidth() {
        return getChildAt(0).getMeasuredWidth();
    }

    int getYearWidth() {
        return getItemWidth() * 12;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    private int getYearForPosition(int position) {
        return (position + startMonth) / 12 + startYear;
    }

    private int getMonthForPosition(int position) {
        return (startMonth + position) % 12;
    }

    private int getPositionForDate(int year, int month) {
        return (12 * (year - startYear) + month) - startMonth;
    }

    public OnMonthSelectedListener getOnMonthSelectedListener() {
        return onMonthSelectedListener;
    }

    public void setOnMonthSelectedListener(OnMonthSelectedListener onMonthSelectedListener) {
        this.onMonthSelectedListener = onMonthSelectedListener;
    }

    public int getMonthCount() {
        return monthCount;
    }

    void setMonthCount(int monthCount) {
        if (this.monthCount == monthCount) {
            return;
        }

        this.monthCount = monthCount;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    /**
     * Default indicator and text color
     */
    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public int getColorBeforeSelection() {
        return colorBeforeSelection;
    }

    /**
     * Color when month is before the current selected month
     */
    public void setColorBeforeSelection(int colorBeforeSelection) {
        this.colorBeforeSelection = colorBeforeSelection;
    }

    public int getColorSelected() {
        return colorSelected;
    }

    /**
     * Color when month is selected
     */
    public void setColorSelected(int colorSelected) {
        this.colorSelected = colorSelected;
    }

    public int getYearDigitCount() {
        return yearDigitCount;
    }

    public void setYearDigitCount(int yearDigitCount) {
        if (yearDigitCount < 0 || yearDigitCount > 4) {
            throw new IllegalArgumentException("yearDigitCount cannot be smaller than 0 or greater than 4");
        }
        this.yearDigitCount = yearDigitCount;
    }

    public boolean isYearOnNewLine() {
        return yearOnNewLine;
    }

    public void setYearOnNewLine(boolean yearOnNewLine) {
        this.yearOnNewLine = yearOnNewLine;
    }

    public void setFirstDate(int startYear, int startMonth) {
        this.startYear = startYear;
        this.startMonth = startMonth;
        selectedYear = startYear;
        selectedMonth = startMonth;
        selectedPosition = 0;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setLastDate(int endYear, int endMonth) {
        if (endYear < startYear || (endYear == startYear && endMonth < startMonth)) {
            throw new IllegalArgumentException("Last visible date cannot be before first visible date");
        }

        Calendar firstDate = Calendar.getInstance();
        firstDate.set(startYear, startMonth, 1);
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(endYear, endMonth, 1);
        int diffYear = lastDate.get(Calendar.YEAR) - firstDate.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + lastDate.get(Calendar.MONTH) - firstDate.get(Calendar.MONTH);

        setMonthCount(diffMonth + 1);
    }

    public interface OnMonthSelectedListener {

        void onMonthSelected(int year, int month, int index);
    }

    public interface DateLabelAdapter {

        CharSequence getLabel(Calendar calendar, int index);
    }

    private class MonthAdapter extends RecyclerView.Adapter<MonthViewHolder> {

        MonthAdapter() {

        }

        @NonNull
        @Override
        public MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.mti_item_month, parent, false);
            return new MonthViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MonthViewHolder holder, int position) {
            final int year = getYearForPosition(position);
            final int month = getMonthForPosition(position);
            holder.bind(year, month, position == selectedPosition, position < selectedPosition);
        }

        @Override
        public int getItemCount() {
            return monthCount;
        }
    }

    private class MonthViewHolder extends RecyclerView.ViewHolder {

        private final TextView lbl;
        private final DotView indicator;

        private int year, month;

        MonthViewHolder(View root) {
            super(root);

            indicator = root.findViewById(R.id.mti_view_indicator);
            lbl = root.findViewById(R.id.mti_month_lbl);

            root.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMonthSelected(year, month, true, true);
                }
            });
        }

        void bind(int year, int month, boolean selected, boolean beforeSelection) {
            this.year = year;
            this.month = month;

            String text = MONTHS[month];
            text = text.substring(0, Math.min(text.length(), 3)).toUpperCase(Locale.US);
            if (yearDigitCount > 0) {
                text += yearOnNewLine ? "\n" : " ";
                text += year % (int) Math.pow(10, yearDigitCount);
            }
            lbl.setText(text);
            int color = selected ? colorSelected : beforeSelection ? colorBeforeSelection : defaultColor;
            lbl.setTextColor(color);
            indicator.setColor(color);
            indicator.setCircleSizeDp(selected ? 12 : 5);
        }
    }
}
