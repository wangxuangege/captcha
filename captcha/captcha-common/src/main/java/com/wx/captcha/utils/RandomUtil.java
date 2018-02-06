package com.wx.captcha.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.util.Random;
import java.util.Set;

/**
 * @author xinquan.huangxq
 */
public final class RandomUtil {
    private static final Random RANDOM = new Random();

    /**
     * 获取[min,max]之间size个随机数
     *
     * @param size
     * @param min
     * @param max
     * @return
     */
    public static int[] getNextRandom(int size, int min, int max) {
        Preconditions.checkArgument(size > 0 && min <= max);

        int[] result = new int[size];
        for (int i = 0; i < size; ++i) {
            result[i] = RANDOM.nextInt(max - min + 1) + min;
        }
        return result;
    }


    /**
     * 获取[min,max]之间尽可能size个不同的随机数
     *
     * @param size
     * @param min
     * @param max
     * @param maxCount 防止循环次数过高，用于指定最多循环的次数
     * @return
     */
    public static int[] getNextUnSameRandom(int size, int min, int max, int maxCount) {
        Preconditions.checkArgument(size > 0 && min <= max && maxCount >= max - min + 1);

        Set<Integer> set = Sets.newHashSet();
        int count = 0;
        while (count++ < maxCount && set.size() < size) {
            set.add(RANDOM.nextInt(max - min + 1) + min);
        }
        int[] result = new int[set.size()];
        int j = 0;
        for (Integer i : set) {
            result[j++] = i;
        }
        return result;
    }

    /**
     * 获取区间随机的整数
     *
     * @param min
     * @param max
     * @return
     */
    public static int getNextRandom(int min, int max) {
        Preconditions.checkArgument(min <= max);

        return RANDOM.nextInt(max - min + 1) + min;
    }

    /**
     * 随机double
     *
     * @param startInclusive
     * @param endInclusive
     * @return
     */
    public static double nextDouble(double startInclusive, double endInclusive) {
        Preconditions.checkArgument(endInclusive >= startInclusive, "Start value must be smaller or equal to end value.");
        Preconditions.checkArgument(startInclusive >= 0.0D, "Both range values must be non-negative.");

        return startInclusive == endInclusive ? startInclusive : startInclusive + (endInclusive - startInclusive) * RANDOM.nextDouble();
    }

    /**
     * 获取0-1之间的double
     *
     * @return
     */
    public static double nextDouble() {
        return nextDouble(0.0d, 1.0d);
    }

    /**
     * 随机float
     *
     * @param startInclusive
     * @param endInclusive
     * @return
     */
    public static float nextFloat(float startInclusive, float endInclusive) {
        Preconditions.checkArgument(endInclusive >= startInclusive, "Start value must be smaller or equal to end value.");
        Preconditions.checkArgument(startInclusive >= 0.0F, "Both range values must be non-negative.");

        return startInclusive == endInclusive ? startInclusive : startInclusive + (endInclusive - startInclusive) * RANDOM.nextFloat();
    }

    /**
     * 获取0-1之间的float
     *
     * @return
     */
    public static float nextFloat() {
        return nextFloat(0.0f, 1.0f);
    }

    /**
     * 获取true、false的随机序列
     *
     * @return
     */
    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }
}
