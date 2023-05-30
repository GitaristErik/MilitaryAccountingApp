package com.google.android.material.datepicker

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateStringsCustom {

    fun getYearMonth(timeInMillis: Long) = DateStrings.getYearMonth(timeInMillis)!!

    /**
     * Get date string with year, month, and day formatted properly for the specified Locale.
     *
     * <p>Uses {@link DateFormat#getInstanceForSkeleton(String, Locale)} for API 24+, and {@link
     * java.text.DateFormat#MEDIUM} before API 24.
     *
     * @param timeInMillis long in UTC milliseconds to turn into string with year, month, and day.
     * @param locale Locale for date string.
     * @return Date string with year, month, and day formatted properly for the specified Locale.
     */
    fun getYearMonthDay(timeInMillis: Long, locale: Locale = Locale.getDefault()) =
        DateStrings.getYearMonthDay(timeInMillis, locale)!!


    /**
     * Get date string with month and day formatted properly for the specified Locale.
     *
     * <p>Uses {@link DateFormat#getInstanceForSkeleton(String, Locale)} for API 24+, and {@link
     * java.text.DateFormat#MEDIUM} before API 24.
     *
     * @param timeInMillis long in UTC milliseconds to turn into string with month and day.
     * @param locale Locale for date string.
     * @return Date string with month and day formatted properly for the specified Locale.
     */
    fun getMonthDay(timeInMillis: Long, locale: Locale = Locale.getDefault()) =
        DateStrings.getMonthDay(timeInMillis, locale)!!

    /**
     * Return a pair of strings representing the start and end dates of this date range.
     *
     * <p>Does not show year if dates are within the same year (Nov 17 - Dec 19).
     *
     * <p>Shows year for end date if range is not within the current year (Nov 17 - Nov 19, 2018).
     *
     * <p>Shows year for start and end date if range spans several years (Dec 31, 2016 - Jan 1, 2017).
     *
     * <p>If userDefinedDateFormat is set, this format overrides the rules above.
     *
     * @param start Start date.
     * @param end End date.
     * @param userDefinedDateFormat {@link SimpleDateFormat} specified by the user, if set.
     * @return Formatted date range string.
     */
    fun getDateRange(
        start: Long?, end: Long?, userDefinedDateFormat: SimpleDateFormat?
    ) = DateStrings.getDateRangeString(start, end, userDefinedDateFormat)!!

    fun getDateRange(start: Long?, end: Long?) = DateStrings.getDateRangeString(start, end)!!

    fun getDateRangeString(start: Long?, end: Long?) = getDateRange(start, end).let {
        "${it.first} - ${it.second}"
    }

    fun getFirstDayOfMonth(timeInMillis: Long): Long = Calendar
        .getInstance().apply {
            this.timeInMillis = timeInMillis
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.let {
            it.timeInMillis + it.timeZone.getOffset(it.timeInMillis)
        }
}