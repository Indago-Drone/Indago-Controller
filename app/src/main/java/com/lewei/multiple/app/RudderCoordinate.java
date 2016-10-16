package com.lewei.multiple.app;

public class RudderCoordinate {
    public static int leftBottom;
    public static int leftLeft;
    public static int leftRight;
    public static int leftTop;
    public static int rightBottom;
    public static int rightLeft;
    public static int rightRight;
    public static int rightTop;

    static {
        leftLeft = 0;
        leftRight = 0;
        leftTop = 0;
        leftBottom = 0;
        rightLeft = 0;
        rightRight = 0;
        rightTop = 0;
        rightBottom = 0;
    }

    public static void setInitLeft(int x, int y, int length) {
        leftLeft = x - length;
        leftRight = x + length;
        leftTop = y - length;
        leftBottom = y + length;
    }

    public static void setInitRight(int x, int y, int length) {
        rightLeft = x - length;
        rightRight = x + length;
        rightTop = y - length;
        rightBottom = y + length;
    }

    public static int getLeftLeft() {
        return leftLeft;
    }

    public static int getLeftRight() {
        return leftRight;
    }

    public static int getLeftTop() {
        return leftTop;
    }

    public static int getLeftBottom() {
        return leftBottom;
    }

    public static int getRightLeft() {
        return rightLeft;
    }

    public static int getRightRight() {
        return rightRight;
    }

    public static int getRightTop() {
        return rightTop;
    }

    public static int getRightBottom() {
        return rightBottom;
    }
}
