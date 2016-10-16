package com.lewei.multiple.utils;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

@SuppressLint({"SimpleDateFormat"})
public class DateUtils {
    public static Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.parse(formatter.format(currentTime), new ParsePosition(8));
    }

    public static Date getNowDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.parse(formatter.format(currentTime), new ParsePosition(8));
    }

    public static String getStringDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static String getStringDateShort() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public static String getTimeShort() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public static Date strToDateLong(String strDate) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate, new ParsePosition(0));
    }

    public static String dateToStrLong(Date dateDate) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateDate);
    }

    public static String dateToStr(Date dateDate) {
        return new SimpleDateFormat("yyyy-MM-dd").format(dateDate);
    }

    public static Date strToDate(String strDate) {
        return new SimpleDateFormat("yyyy-MM-dd").parse(strDate, new ParsePosition(0));
    }

    public static String timeToStr(long milliseconds) {
        String str = "";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(milliseconds));
    }

    public static Date getNow() {
        return new Date();
    }

    public static Date getLastDate(long day) {
        return new Date(new Date().getTime() - (122400000 * day));
    }

    public static String getStringToday() {
        return new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date());
    }

    public static String getHour() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).substring(11, 13);
    }

    public static String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).substring(14, 16);
    }

    public static String getUserDate(String sformat) {
        return new SimpleDateFormat(sformat).format(new Date());
    }

    public static String getTwoHour(String st1, String st2) {
        String[] kk = st1.split(":");
        String[] jj = st2.split(":");
        if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0])) {
            return "0";
        }
        double y = Double.parseDouble(kk[0]) + (Double.parseDouble(kk[1]) / 60.0d);
        double u = Double.parseDouble(jj[0]) + (Double.parseDouble(jj[1]) / 60.0d);
        if (y - u > 0.0d) {
            return new StringBuilder(String.valueOf(y - u)).toString();
        }
        return "0";
    }

    public static String getTwoDay(String sj1, String sj2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return new StringBuilder(String.valueOf((myFormatter.parse(sj1).getTime() - myFormatter.parse(sj2).getTime()) / 86400000)).toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getPreTime(String sj1, String jj) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String mydate1 = "";
        try {
            Date date1 = format.parse(sj1);
            date1.setTime(((date1.getTime() / 1000) + ((long) (Integer.parseInt(jj) * 60))) * 1000);
            mydate1 = format.format(date1);
        } catch (Exception e) {
        }
        return mydate1;
    }

    public static String getNextDay(String nowdate, String delay) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String mdate = "";
            Date d = strToDate(nowdate);
            d.setTime(((d.getTime() / 1000) + ((long) (((Integer.parseInt(delay) * 24) * 60) * 60))) * 1000);
            return format.format(d);
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isLeapYear(String ddate) {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(strToDate(ddate));
        int year = gc.get(1);
        if (year % 400 == 0) {
            return true;
        }
        if (year % 4 != 0) {
            return false;
        }
        if (year % 100 == 0) {
            return false;
        }
        return true;
    }

    public static String getEDate(String str) {
        String[] k = new SimpleDateFormat("yyyy-MM-dd").parse(str, new ParsePosition(0)).toString().split(" ");
        return k[2] + k[1].toUpperCase(Locale.getDefault()) + k[5].substring(2, 4);
    }

    public static String getEndDateOfMonth(String dat) {
        String str = dat.substring(0, 8);
        int mon = Integer.parseInt(dat.substring(5, 7));
        if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) {
            return new StringBuilder(String.valueOf(str)).append("31").toString();
        }
        if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
            return new StringBuilder(String.valueOf(str)).append("30").toString();
        }
        if (isLeapYear(dat)) {
            return new StringBuilder(String.valueOf(str)).append("29").toString();
        }
        return new StringBuilder(String.valueOf(str)).append("28").toString();
    }

    public static boolean isSameWeekDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(1) - cal2.get(1);
        if (subYear == 0) {
            if (cal1.get(3) == cal2.get(3)) {
                return true;
            }
        } else if (1 == subYear && 11 == cal2.get(2)) {
            if (cal1.get(3) == cal2.get(3)) {
                return true;
            }
        } else if (-1 == subYear && 11 == cal1.get(2) && cal1.get(3) == cal2.get(3)) {
            return true;
        }
        return false;
    }

    public static String getSeqWeek() {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        String week = Integer.toString(c.get(3));
        if (week.length() == 1) {
            week = "0" + week;
        }
        return new StringBuilder(String.valueOf(Integer.toString(c.get(1)))).append(week).toString();
    }

    public static String getWeek(String sdate, String num) {
        Date dd = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(dd);
        if (num.equals("1")) {
            c.set(7, 2);
        } else if (num.equals("2")) {
            c.set(7, 3);
        } else if (num.equals("3")) {
            c.set(7, 4);
        } else if (num.equals("4")) {
            c.set(7, 5);
        } else if (num.equals("5")) {
            c.set(7, 6);
        } else if (num.equals("6")) {
            c.set(7, 7);
        } else if (num.equals("0")) {
            c.set(7, 1);
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
    }

    public static String getWeek(String sdate) {
        Date date = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }

    public static String getWeekStr(String sdate) {
        String str = "";
        str = getWeek(sdate);
        if ("1".equals(str)) {
            return "\u661f\u671f\u65e5";
        }
        if ("2".equals(str)) {
            return "\u661f\u671f\u4e00";
        }
        if ("3".equals(str)) {
            return "\u661f\u671f\u4e8c";
        }
        if ("4".equals(str)) {
            return "\u661f\u671f\u4e09";
        }
        if ("5".equals(str)) {
            return "\u661f\u671f\u56db";
        }
        if ("6".equals(str)) {
            return "\u661f\u671f\u4e94";
        }
        if ("7".equals(str)) {
            return "\u661f\u671f\u516d";
        }
        return str;
    }

    public static long getDays(String date1, String date2) {
        if (date1 == null || date1.equals("") || date2 == null || date2.equals("")) {
            return 0;
        }
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        Date mydate = null;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
        } catch (Exception e) {
        }
        return (date.getTime() - mydate.getTime()) / 86400000;
    }

    public static String getNowMonth(String sdate) {
        sdate = sdate.substring(0, 8) + "01";
        Date date = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return getNextDay(sdate, new StringBuilder(String.valueOf(1 - c.get(7))).toString());
    }

    public static String getNo(int k) {
        return new StringBuilder(String.valueOf(getUserDate("yyyyMMddhhmmss"))).append(getRandom(k)).toString();
    }

    public static String getRandom(int i) {
        Random jjj = new Random();
        if (i == 0) {
            return "";
        }
        String jj = "";
        for (int k = 0; k < i; k++) {
            jj = new StringBuilder(String.valueOf(jj)).append(jjj.nextInt(9)).toString();
        }
        return jj;
    }

    public static boolean RightDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        if (date == null) {
            return false;
        }
        if (date.length() > 10) {
            sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
