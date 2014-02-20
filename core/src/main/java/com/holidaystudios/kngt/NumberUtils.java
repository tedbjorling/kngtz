package com.holidaystudios.kngt;

import java.util.Random;

/**
 * Created by tedbjorling on 2014-02-20.
 */
public class NumberUtils {

    private static Random generator = new Random();
    private static String seed = null;


    public static void setSeed(final String seed) {
        NumberUtils.generator.setSeed(seed.hashCode());
    }

    public static double getRandom() {
        return NumberUtils.generator.nextDouble();
    }

}
