package com.techdev_michael.step_counter.step;

import com.techdev_michael.step_counter.step.bean.State;

/**
 * 步数更新回调
 */
public interface UpdateUiCallBack {
    /**
     * 更新UI步数
     *
     * @param stepCount 步数
     */
    void updateUi(int stepCount,int runStepCount);

    /**
     *  更新当前运动状态，如跑步、走路
     * @param state
     */
    void updateState(State state);

}
