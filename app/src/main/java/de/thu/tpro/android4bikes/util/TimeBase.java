package de.thu.tpro.android4bikes.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//TODO: JUnit-Testing
public class TimeBase {
    private static SimpleDateFormat simpleDateFormat;
    //constants-------------------------------------------------------------------------------

    /**
     * set the date representation using the string constants of this class
     *
     * @param representation which date format should be used?
     */
    public static void setDateRepresentation(DateRepresentation representation) {
        simpleDateFormat = new SimpleDateFormat(representation.getType());
    }

    /**
     * get the current time.
     *
     * @return unix timestamp (long) representing the current time.
     */
    public static long getCurrentUnixTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * get the system time regarding a certain unix timestamp as an object of the class date
     *
     * @param timeStamp unix time stamp (long) in milliseconds to generate the date object
     * @return date object representing the time stamp
     */
    public static Date getDateFromMilliSecondsAsSystemTime(long timeStamp) {
        Date date = new Date(timeStamp); //date expects the time in milliseconds
        return date;
    }

    /**
     * get the system time regarding a certain unix timestamp as string
     *
     * @param timeStamp unix time stamp (long) in milliseconds to generate the date object
     * @return date object representing the time stamp
     */
    public static String getDateFromMilliSecondsAsString(long timeStamp) {
        Date date = new Date(timeStamp); //date expects the time in milliseconds
        String datestring = simpleDateFormat.format(date);
        return datestring;
    }

    /**
     * get the system time regarding a certain unix timestamp as an object of the class date
     *
     * @param timeStamp time stamp (long) in seconds to generate the date object
     * @return date object representing the time stamp
     */
    public static Date getDateFromSecondsAsSystemTime(long timeStamp) {
        Date date = new Date(timeStamp * 1000); //date expects the time in milliseconds
        return date;
    }

    /**
     * get the unix timestamp of a date specified by three parameters (year, month, day)
     *
     * @param year  year as integer value
     * @param month month as integer value
     * @param day   as integer value
     * @return unix timestamp regarding the specified date
     */
    public static long getUnixTimestampFromYearMonthDayOfMonth(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }

    /**
     * get the unix timestamp of a date specified by three parameters (year, month, day, hour, minute, second)
     *
     * @param year   year as integer value
     * @param month  month as integer value
     * @param day    as integer value
     * @param hour   as integer value
     * @param minute as integer value
     * @param second as integer value
     * @return unix timestamp regarding the specified date
     */
    public static long getUnixTimestampFromYearMonthDayOfMonthHourMinuteSeconds(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
        return calendar.getTimeInMillis();
    }

    //constants-----------------------------------------------------------------------------
    public enum DateRepresentation {
        DAY_MONTH_YEAR("dd/MM/yyyy"),
        HOUR_MINUTE_SECOND_MONTH_DAY_YEAR("HH:mm:ss dd/MM/yyyy"),
        HOUR_MINUTE("HH:mm"),
        HOUR_MINUTE_SECOND("HH:mm:ss"),
        MONTH_YEAR("MM/yyyy"),
        YEAR("yyyy"),
        MONTH("MM"),
        DAY("dd"),
        SECOND("ss"),
        MINUTE("mm"),
        HOUR("HH");

        private String type;

        DateRepresentation(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
