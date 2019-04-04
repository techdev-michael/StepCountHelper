package com.techdev_michael.step_counter.step.accelerometer;

import android.util.Log;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.techdev_michael.step_counter.step.bean.State;

/**
 * Created by dylan on 16/9/27.
 */

/*
 * 根据StepDetector传入的步点"数"步子
 * */
public class StepCount implements StepCountListener {

    private int count = 0;
    private int mCount = 0;
    private StepValuePassListener mStepValuePassListener;
    private long timeOfLastPeak = 0;
    private long timeOfThisPeak = 0;
    private StepDetector stepDetector;
    private State mState = State.STOP;
    private String TAG = StepCount.class.getSimpleName();

    private static final int AVERAGE_STEP_DURATION_COUNTER = 5;

    private Queue<Long> intervalQueue = new ArrayBlockingQueue<>(AVERAGE_STEP_DURATION_COUNTER);
    private int runSteps = 0 ;

    public StepCount() {
        stepDetector = new StepDetector();
        stepDetector.initListener(this);
    }

    public StepDetector getStepDetector() {
        return stepDetector;
    }

    /*
     * 连续走十步才会开始计步
     * 连续走了9步以下,停留超过3秒,则计数清空
     * */
    @Override
    public void countStep() {

        this.timeOfLastPeak = this.timeOfThisPeak;
        this.timeOfThisPeak = System.currentTimeMillis();

        long diff = timeOfThisPeak - timeOfLastPeak;

        if (diff <= 3000L) {

            //  根据前四个步伐耗时平均值判断是跑步还是走路
            if (intervalQueue.size() < AVERAGE_STEP_DURATION_COUNTER) {
                intervalQueue.add(diff);
            } else {
                intervalQueue.remove();
                intervalQueue.add(diff);
                long totalTimeSpent = 0;
                long averageTimeSpent = 0;
                for (Long a : intervalQueue) {
                    totalTimeSpent += a;
                }

                averageTimeSpent = totalTimeSpent / intervalQueue.size();

                if (averageTimeSpent >= 0 && averageTimeSpent <= 380) {
                    Log.e(TAG, "countStep: 跑步");
                    this.mState = State.RUNNING;
                    notifyStateChanged();
                } else if (averageTimeSpent >= 450) {
                    Log.e(TAG, "countStep: 走路");
                    this.mState = State.WALK;
                    notifyStateChanged();
                }
            }

            if (this.count < 9) {
                this.count++;
            } else if (this.count == 9) {
                this.count++;
                this.mCount += this.count;
                if(this.mState == State.RUNNING){
                    this.runSteps += this.count ;
                }
                notifyListener();
            } else {
                this.mCount++;
                if(this.mState == State.RUNNING){
                    this.runSteps ++ ;
                }
                notifyListener();
            }


        } else {//超时
            this.count = 1;//为1,不是0
            this.mState = State.STOP;
            notifyStateChanged();
        }

    }

    public void initListener(StepValuePassListener listener) {
        this.mStepValuePassListener = listener;
    }

    public void notifyStateChanged() {
        if (this.mStepValuePassListener != null)
            this.mStepValuePassListener.stateChanged(this.mState);
    }

    public void notifyListener() {
        if (this.mStepValuePassListener != null)
            this.mStepValuePassListener.stepChanged(this.mCount,this.runSteps);
    }


    public void setSteps(int initValue,int runSteps) {
        this.mCount = initValue;
        this.runSteps = runSteps;
        this.count = 0;
        timeOfLastPeak = 0;
        timeOfThisPeak = 0;
        notifyListener();
    }
}
