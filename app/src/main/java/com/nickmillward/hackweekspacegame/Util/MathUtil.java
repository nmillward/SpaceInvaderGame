package com.nickmillward.hackweekspacegame.Util;

/**
 * Created by nmillward on 5/10/16.
 *
 * https://github.com/CordProject/PaperCraft/blob/master/wear%2Fsrc%2Fmain%2Fjava%2Fcordproject%2Flol%2Fpapercraft%2Futil%2FMathUtil.java
 */
public class MathUtil {

    public static float lerp(float a, float b, float pct) {
        return a + pct * (b - a);
    }

}
