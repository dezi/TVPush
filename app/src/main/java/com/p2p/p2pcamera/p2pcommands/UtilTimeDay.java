package com.p2p.p2pcamera.p2pcommands;

import com.p2p.p2pcamera.P2PPacker;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.TimeZone;

public class UtilTimeDay
{
    public int year;
    public int month;
    public int day;
    public int wday;
    public int hour;
    public int minute;
    public int second;

    public long millis;

    public boolean isBigEndian;

    public UtilTimeDay(int year, int month, int day, int wday, int hour, int minute, int second, boolean isBigEndian)
    {
        this.year = year;
        this.month = month;
        this.day = day;
        this.wday = wday;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.isBigEndian = isBigEndian;

        updateTimeInMillis();
    }

    public UtilTimeDay(int year, int month, int day, int hour, int minute, int second, boolean isBigEndian)
    {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.isBigEndian = isBigEndian;

        updateTimeInMillis();

        GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getDefault());
        gregorianCalendar.setTimeInMillis(this.millis);
        this.wday = gregorianCalendar.get(Calendar.DAY_OF_WEEK);
    }

    public UtilTimeDay(long dateTimeMS, boolean GMT, boolean isBigEndian)
    {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(GMT ? TimeZone.getTimeZone("gmt") : TimeZone.getDefault());
        gregorianCalendar.setTimeInMillis(dateTimeMS);

        this.year = gregorianCalendar.get(Calendar.YEAR);
        this.month = (gregorianCalendar.get(Calendar.MONTH) + 1);
        this.day = gregorianCalendar.get(Calendar.DAY_OF_MONTH);
        this.hour = gregorianCalendar.get(Calendar.HOUR_OF_DAY);
        this.minute = gregorianCalendar.get(Calendar.MINUTE);
        this.second = gregorianCalendar.get(Calendar.SECOND);
        this.isBigEndian = isBigEndian;

        updateTimeInMillis();
    }

    public void parse(byte[] data, int offset, boolean isBigEndian)
    {
        this.year = P2PPacker.byteArrayToShort(data, offset, isBigEndian);
        this.month = data[2 + offset];
        this.day = data[3 + offset];
        this.wday = data[4 + offset];
        this.hour = data[5 + offset];
        this.minute = data[6 + offset];
        this.second = data[7 + offset];
        this.isBigEndian = isBigEndian;

        updateTimeInMillis();
    }

    public void parse(byte[] data, boolean isBigEndian)
    {
        parse(data, 0, isBigEndian);
    }

    public byte[] build()
    {
        byte[] data = new byte[8];

        System.arraycopy(P2PPacker.shortToByteArray((short) year, isBigEndian), 0, data, 0, 2);

        data[2] = (byte) month;
        data[3] = (byte) day;
        data[4] = (byte) wday;
        data[5] = (byte) hour;
        data[6] = (byte) minute;
        data[7] = (byte) second;

        return data;
    }

    public long getTimeInMillis()
    {
        return this.millis;
    }

    private void updateTimeInMillis()
    {
        Calendar instance = Calendar.getInstance(TimeZone.getDefault());
        instance.set(this.year, this.month - 1, this.day, this.hour, this.minute, this.second);
        this.millis = instance.getTimeInMillis();
    }
}
