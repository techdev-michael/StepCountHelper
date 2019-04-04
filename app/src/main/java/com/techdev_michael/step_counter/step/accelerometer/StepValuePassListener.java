package com.techdev_michael.step_counter.step.accelerometer;

import com.techdev_michael.step_counter.step.bean.State;

/**
 * Created by dylan on 16/9/27.
 */
public interface StepValuePassListener {

    void stepChanged(int steps , int runSteps);

    void stateChanged(State state);

}
