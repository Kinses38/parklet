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
 * CalendarView utility class to aid in setting behaviour, appearance and to
 * help deal with conversions of date and time.
 */

public class ParkLetCalendarView extends CalendarPickerView {


    public ParkLetCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Initialise calendar.
     * Setting:
     * Whether weekend days are blocked from booking
     * How many months ahead a user can book by
     * The Month Display format.
     * Selection mode, multiple to allow non-consecutive days to be picked.
     *
     * @param isAvailWeekends boolean of whether property accepts weekend bookings.
     */
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

    /**
     * Called when new booking dates are observed from Booking ViewModel.
     * Highlights unavailable days.
     *
     * @param bookingDates list of new dates.
     */
    public void refreshCalendar(List<Date> bookingDates) {
        //Conversion and flattening logic moved to bookingViewModel.
        this.clearHighlightedDates();
        this.highlightDates(bookingDates);
    }

    /**
     * Refreshes the calendar if bookings are cancelled and no other booking exists.
     */
    public void refreshCalendar() {
        this.clearHighlightedDates();
    }

    /**
     * Takes selected days from the CalendarView and converts to Long
     * Epoch to pass to ViewModel in preparation to submit booking.
     *
     * @return list of long epoch timestamps.
     */
    public List<Long> getAndConvertDates() {
        List<Date> selectedDates = this.getSelectedDates();
        List<Long> datesTimeStamp = new ArrayList<>();
        for (Date date : selectedDates) {
            datesTimeStamp.add(date.getTime());
        }
        return datesTimeStamp;
    }

    /**
     * Number of days selected in current booking
     *
     * @return int number of days.
     */
    public int getSelectedCount() {
        return this.getSelectedDates().size();
    }


}
