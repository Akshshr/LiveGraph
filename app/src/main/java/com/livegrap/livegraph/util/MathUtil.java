package com.livegrap.livegraph.util;

public class MathUtil {

    public static float clamp(float val, float min, float max) {
        return val < min ? min : val > max ? max : val;
    }


    /**
     * Map value from in range [fromStart, fromStop] to new range [toStart, toStop].
     * Example map(2, 0, 10, 0, 100) -> 20
     *
     * @param val
     * @param fromStart
     * @param fromStop
     * @param toStart
     * @param toStop
     * @return value in new range.
     */
    public static float map(float val, float fromStart, float fromStop, float toStart, float toStop) {
        if(fromStart == fromStop) {
            return toStart;
        }
        return toStart + (toStop - toStart) * ((val - fromStart) / (fromStop - fromStart));
    }

    public static int map(int val, int fromStart, int fromStop, int toStart, int toStop) {
        return Math.round(toStart + (toStop - toStart)
                * ((val - fromStart) / (float) (fromStop - fromStart)));
    }

    public static long map(long val, long fromStart, long fromStop, long toStart, long toStop) {
        return Math.round(toStart + (toStop - toStart)
                * ((val - fromStart) / (double) (fromStop - fromStart)));
    }

    public static double map(double val, double fromStart, double fromStop, double toStart, double toStop) {
        return toStart + (toStop - toStart)
                * ((val - fromStart) / (fromStop - fromStart));
    }

}
