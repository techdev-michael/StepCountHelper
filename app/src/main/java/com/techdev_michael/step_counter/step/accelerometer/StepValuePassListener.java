package com.techdev_michael.step_counter.step.accelerometer;

import com.techdev_michael.step_counter.step.bean.State;

/**
 *  传递计步数据的监听器
 */
public interface StepValuePassListener {

    void stepChanged(int steps , int runSteps);

    void stateChanged(State state);

}
