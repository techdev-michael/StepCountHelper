package com.techdev_michael.step_counter.util;

public class CalorieCostCalculator {

    /**
     * 计算卡路里消耗
     *
     * @param weight
     * @param steps
     * @return
     */
    public static float calcCaloriesCost(float weight, int steps) {

        return weight / 2000f * steps;

    }


    /**
     * 根据步数、性别、身高计算运动距离
     *
     * @param steps
     * @param sex
     * @param height
     * @return
     */
    public static float calcDistance(int steps, int sex, int height) {

        float BSL = sex == 1 ? 0.85f : 0.8f * height;

        float VSL = 0.5f * BSL;

        // convert m to km
        return steps * VSL / 1000;

    }


}
