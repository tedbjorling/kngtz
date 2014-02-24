package com.holidaystudios.kngt.tools;

import java.util.Random;

/**
 * Created by tedbjorling on 2014-02-20.
 */
public class RandomUtils {

    private static Random generator = new Random();
    private static String seed = null;


    public static void setSeed(final String seed) {
        RandomUtils.generator.setSeed(seed.hashCode());
    }

    public static double getRandom() {
        return RandomUtils.generator.nextDouble();
    }

}
