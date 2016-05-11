package com.nickmillward.hackweekspacegame.Util;

/**
 * Created by nmillward on 5/10/16.
 *
 * https://github.com/CordProject/PaperCraft/blob/master/wear%2Fsrc%2Fmain%2Fjava%2Fcordproject%2Flol%2Fpapercraft%2Futil%2FMathUtil.java
 */
public class MathUtil {

    public static float lerp(float start, float end, float percent) {
        return start + percent * (end - start);
    }

}
