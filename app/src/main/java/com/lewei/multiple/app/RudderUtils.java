package com.lewei.multiple.app;

import android.graphics.Point;

public class RudderUtils {
    public static int getLineLength(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow((double) Math.abs(x1 - x2), 2.0d) + Math.pow((double) Math.abs(y1 - y2), 2.0d));
    }

    public static double getRadian(int ax, int ay, int bx, int by) {
        int lenA = bx - ax;
        return Math.acos(((double) lenA) / Math.sqrt(Math.pow((double) lenA, 2.0d) + Math.pow((double) (by - ay), 2.0d))) * ((double) (by < ay ? -1 : 1));
    }

    public static Point getPoint(int x1, int y1, int startx, int starty, int HRudder) {
        Point point = new Point();
        double radian = getRadian(x1, y1, startx, starty);
        point.x = (int) Math.abs(((double) startx) - (((double) HRudder) * Math.cos(radian)));
        point.y = (int) Math.abs(((double) starty) - (((double) HRudder) * Math.sin(radian)));
        return point;
    }

    public static int GetUpDown(int y1, int length, int default_y, int MaxNum) {
        return (Math.abs(y1 - default_y) * MaxNum) / length;
    }

    public static int GetLR(int x1, int length, int default_x, int MaxNum) {
        return (Math.abs(x1 - default_x) * MaxNum) / length;
    }
}
