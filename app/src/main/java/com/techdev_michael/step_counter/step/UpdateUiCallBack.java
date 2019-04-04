package com.techdev_michael.step_counter.step;

import com.techdev_michael.step_counter.step.bean.State;

/**
 * 步数更新回调
 * Created by dylan on 16/9/27.
 */
public interface UpdateUiCallBack {
    /**
     * 更新UI步数
     *
     * @param stepCount 步数
     */
    void updateUi(int stepCount,int runStepCount);

    void updateState(State state);

}
