/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.apps.detection.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

public class TimeStamp implements Comparable<TimeStamp> {

    private static final String FORMAT = "yyyy:DDD:HH:mm:ss.SSS";
    private static final String FORMATMONTH = "yyyy:MM:dd:HH:mm:ss.SSS";
    private static final String DIRECTORYFORMAT = "yyyyDDDHHmmss";

    private SimpleDateFormat format;
    private Calendar calendar;
    private double epochTime;
    private long epochMilliseconds;

    public TimeStamp(int year, int jday, int hour, int min, int sec, int msec) {

        format = new SimpleDateFormat(FORMAT);
        calendar = new GregorianCalendar();
        calendar.setTimeZone(new SimpleTimeZone(0, "GMT"));
        format.setCalendar(calendar);

        DecimalFormat format2 = new DecimalFormat("00");
        DecimalFormat format3 = new DecimalFormat("000");
        DecimalFormat format4 = new DecimalFormat("0000");
        try {
            Date date = format.parse(
                    format4.format(year) + ":" + format3.format(jday) + ":" + format2.format(hour) + ":" + format2.format(min) + ":" + format2.format(sec) + "." + format3.format(msec));
            calendar.setTime(date);
            epochMilliseconds = date.getTime();
            epochTime = epochMilliseconds / 1000;
            double milliseconds = epochMilliseconds - epochTime * 1000.0;
            epochTime += milliseconds / 1000.0;
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public TimeStamp(int year, int month, int dayOfMonth, int hour, int min, int sec, int msec) {

        format = new SimpleDateFormat(FORMATMONTH);
        calendar = new GregorianCalendar();
        calendar.setTimeZone(new SimpleTimeZone(0, "GMT"));
        format.setCalendar(calendar);

        DecimalFormat format2 = new DecimalFormat("00");
        DecimalFormat format3 = new DecimalFormat("000");
        DecimalFormat format4 = new DecimalFormat("0000");
        try {
            Date date = format.parse(
                    format4.format(year)
                    + ":"
                    + format2.format(month)
                    + ":"
                    + format2.format(dayOfMonth)
                    + ":"
                    + format2.format(hour)
                    + ":"
                    + format2.format(min)
                    + ":"
                    + format2.format(sec)
                    + "."
                    + format3.format(msec));
            calendar.setTime(date);
            epochMilliseconds = date.getTime();
            epochTime = epochMilliseconds / 1000;
            double milliseconds = epochMilliseconds - epochTime * 1000.0;
            epochTime += milliseconds / 1000.0;
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public TimeStamp(TimeStamp T) {
        format = new SimpleDateFormat(FORMAT);
        calendar = new GregorianCalendar();
        calendar.setTimeZone(new SimpleTimeZone(0, "GMT"));
        format.setCalendar(calendar);
        epochTime = T.epochTime;
        epochMilliseconds = T.epochMilliseconds;
        calendar.setTime(new Date(epochMilliseconds));
    }

    public TimeStamp(double epochTime) {

        format = new SimpleDateFormat(FORMAT);
        calendar = new GregorianCalendar();
        calendar.setTimeZone(new SimpleTimeZone(0, "GMT"));
        format.setCalendar(calendar);

        this.epochTime = epochTime;
        epochMilliseconds = (long) (epochTime * 1000.0);
        calendar.setTime(new Date(epochMilliseconds));
    }

    public TimeStamp(String s) {

        format = new SimpleDateFormat(FORMAT);
        calendar = new GregorianCalendar();
        calendar.setTimeZone(new SimpleTimeZone(0, "GMT"));
        format.setCalendar(calendar);

        try {
            Date date = format.parse(s);
            epochMilliseconds = date.getTime();
            epochTime = epochMilliseconds / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public TimeStamp() {

        format = new SimpleDateFormat(FORMAT);
        calendar = new GregorianCalendar();
        calendar.setTimeZone(new SimpleTimeZone(0, "GMT"));
        format.setCalendar(calendar);

        this.epochTime = 0.0;
        epochMilliseconds = (long) (epochTime * 1000.0);
        calendar.setTime(new Date(epochMilliseconds));
    }

    public long epochAsLong() {
        return epochMilliseconds;
    }

    public double epochAsDouble() {
        return epochTime;
    }

    public void plus(double increment) {
        epochTime += increment;
        epochMilliseconds = (long) (epochTime * 1000.0);
        calendar.setTime(new Date(epochMilliseconds));
    }

    public double minus(TimeStamp T) {
        return epochTime - T.epochTime;
    }

    @Override
    public String toString() {
        return format.format(new Date(epochMilliseconds));
    }

    public boolean gt(TimeStamp T) {
        if (epochTime > T.epochTime) {
            return true;
        } else {
            return false;
        }
    }

    public boolean lt(TimeStamp T) {
        if (epochTime < T.epochTime) {
            return true;
        } else {
            return false;
        }
    }

    public boolean ge(TimeStamp T) {
        if (epochTime >= T.epochTime) {
            return true;
        } else {
            return false;
        }
    }

    public boolean le(TimeStamp T) {
        if (epochTime <= T.epochTime) {
            return true;
        } else {
            return false;
        }
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public int getDayOfMonth() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getJDay() {
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    public int getHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMin() {
        return calendar.get(Calendar.MINUTE);
    }

    public int getSec() {
        return calendar.get(Calendar.SECOND);
    }

    public int getMsec() {
        return calendar.get(Calendar.MILLISECOND);
    }

    public String getDirectoryName() {
        SimpleDateFormat altFormat = new SimpleDateFormat(DIRECTORYFORMAT);
        altFormat.setCalendar(calendar);
        return altFormat.format(new Date(epochMilliseconds));
    }

    @Override
    public int compareTo(TimeStamp T) {

        if (lt(T)) {
            return -1;
        } else if (gt(T)) {
            return 1;
        } else {
            return 0;
        }
    }

}
