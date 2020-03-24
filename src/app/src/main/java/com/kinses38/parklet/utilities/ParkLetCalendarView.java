package com.kinses38.parklet.utilities;

import android.content.Context;
import android.util.AttributeSet;

import com.savvi.rangedatepicker.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Extends from https://github.com/savvisingh/DateRangePicker
 * written by Sarabjeet Singh
 * <p>
 * CalendarView utilty class to aid in setting behaviour, appearance and to
 * help deal with conversions of date and time.
 */

public class ParkLetCalendarView extends CalendarPickerView {


    public ParkLetCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initCalendar(boolean isAvailWeekends) {
        //Days are set as rows, so Sun = 1, Sat = 7
        final Integer SUNDAY = 1;
        final Integer SATURDAY = 7;
        //Max number of months you can book ahead by
        final int MONTHSAHEAD = 2;

        ArrayList<Integer> disableWeekends = new ArrayList<>();
        if (!isAvailWeekends) {
            disableWeekends.add(SUNDAY);
            disableWeekends.add(SATURDAY);
        }

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.MONTH, MONTHSAHEAD);
        this.init(minDate.getTime(), maxDate.getTime(),
                new SimpleDateFormat("MMMM  YYYY", Locale.getDefault()))
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                .withDeactivateDates(disableWeekends);
    }

    public void refreshCalendar(List<Calendar> calendar) {
        this.clearHighlightedDates();
        ArrayList<Date> d = new ArrayList<>();
        for (Calendar c : calendar) {
            d.add(c.getTime());
        }
        this.highlightDates(d);
    }

    //In the case that all bookings are cleared.
    public void refreshCalendar() {
        this.clearHighlightedDates();
    }

    //Todo be cleverer with date conversions. Also will timestamps containing hours mess with date comparison in firebase?
    public List<Long> getAndConvertDates() {
        List<Date> selectedDates = this.getSelectedDates();
        List<Long> datesTimeStamp = new ArrayList<>();
        for (Date date : selectedDates) {
            datesTimeStamp.add(date.getTime());
        }
        return datesTimeStamp;
    }

    public int getSelectedCount() {
        return this.getSelectedDates().size();
    }


}
