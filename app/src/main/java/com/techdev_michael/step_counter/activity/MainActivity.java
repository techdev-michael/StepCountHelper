package com.techdev_michael.step_counter.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.techdev_michael.step_counter.Constant;
import com.techdev_michael.step_counter.R;
import com.techdev_michael.step_counter.step.UpdateUiCallBack;
import com.techdev_michael.step_counter.step.bean.State;
import com.techdev_michael.step_counter.step.service.StepService;
import com.techdev_michael.step_counter.step.utils.SharedPreferencesUtils;
import com.techdev_michael.step_counter.util.CalorieCostCalculator;
import com.techdev_michael.step_counter.view.StepArcView;

/**
 * 记步主页
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_data, tvRunSteps, tvWalkSteps, tvDistance, tvCalorieCost;
    private StepArcView cc;
    private TextView tv_set;
    private TextView tv_isSupport;
    private SharedPreferencesUtils sp;

    private static final String DEFAULT_TARGET_STEPS = "7000";


    private void assignViews() {

        tv_data = findViewById(R.id.tv_data);
        cc = findViewById(R.id.cc);
        tv_set = findViewById(R.id.tv_set);
        tv_isSupport = findViewById(R.id.tv_isSupport);
        tvRunSteps = findViewById(R.id.tv_run_steps);
        tvWalkSteps = findViewById(R.id.tv_walk_steps);
        tvDistance = findViewById(R.id.tv_distance);
        tvCalorieCost = findViewById(R.id.tv_calorie_cost);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();
        initData();
        addListener();

        int sex = SPUtils.getInstance().getInt(Constant.SEX, -1);
        int height = SPUtils.getInstance().getInt(Constant.HEIGHT, -1);
        float weight = SPUtils.getInstance().getFloat(Constant.WEIGHT, -1);

        if (-1 == sex || height == -1 || weight == -1) {
            startActivity(new Intent(this, GenderSetActivity.class));
        }

    }


    private void addListener() {
        tv_set.setOnClickListener(this);
        tv_data.setOnClickListener(this);
        tvCalorieCost.setOnClickListener(this);
        tvDistance.setOnClickListener(this);
    }

    private void initData() {
        sp = new SharedPreferencesUtils(this);
        //获取用户设置的计划锻炼步数，没有设置过的话默认7000
        String planWalk_QTY = (String) sp.getParam("planWalk_QTY", DEFAULT_TARGET_STEPS);
        //设置当前步数为0
        cc.setCurrentCount(Integer.parseInt(planWalk_QTY), 0);
        tv_isSupport.setText("计步中...");
        setupService();
    }


    private boolean isBind = false;

    /**
     * 开启计步服务
     */
    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        isBind = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);

    }

    /**
     * 用于查询应用服务（application Service）的状态的一种interface，
     * 更详细的信息可以参考Service 和 context.bindService()中的描述，
     * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用的。
     */
    ServiceConnection conn = new ServiceConnection() {
        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接。
         * @param name 实际所连接到的Service组件名称
         * @param service 服务的通信信道的IBind，可以通过Service访问对应服务
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            StepService stepService = ((StepService.StepBinder) service).getService();
            //设置初始化数据
            String planWalk_QTY = (String) sp.getParam("planWalk_QTY", DEFAULT_TARGET_STEPS);
            int[] stepRecord = stepService.getStepCount();
            if (null != stepRecord && stepRecord.length == 2) {
                int runStepCount = stepRecord[1];
                int stepCount = stepRecord[0];

                updateDashborad(planWalk_QTY, stepCount, runStepCount);
            }

            //设置步数监听回调
            stepService.registerCallback(new UpdateUiCallBack() {
                @Override
                public void updateUi(int stepCount, int runStepCount) {

                    String planWalk_QTY = (String) sp.getParam("planWalk_QTY", DEFAULT_TARGET_STEPS);
                    updateDashborad(planWalk_QTY, stepCount, runStepCount);
                }

                @Override
                public void updateState(State state) {

                    if (state == State.STOP) {
                        tv_isSupport.setText("停止");
                    } else if (state == State.WALK) {
                        tv_isSupport.setText("步行中");
                    } else if (state == State.RUNNING) {
                        tv_isSupport.setText("跑步中");
                    }

                }
            });
        }

        /**
         * 当与Service之间的连接丢失的时候会调用该方法，
         * 这种情况经常发生在Service所在的进程崩溃或者被Kill的时候调用，
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()。
         * @param name 丢失连接的组件名称
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @SuppressLint("DefaultLocale")
    private void updateDashborad(String targetSteps, int totalSteps, int runSteps) {

        cc.setCurrentCount(Integer.valueOf(targetSteps), totalSteps);
        tvRunSteps.setText(String.valueOf(runSteps));
        tvWalkSteps.setText(String.valueOf(totalSteps - runSteps < 0 ? 0 : totalSteps - runSteps));

        int sex = SPUtils.getInstance().getInt(Constant.SEX, -1);
        int height = SPUtils.getInstance().getInt(Constant.HEIGHT, -1);
        float weight = SPUtils.getInstance().getFloat(Constant.WEIGHT, -1);

        if (-1 == sex || height == -1 || weight == -1) {
            tvDistance.setText("--/--");
            tvCalorieCost.setText("--/--");
        } else {
            tvDistance.setText(String.format("%.3f km", CalorieCostCalculator.calcDistance(totalSteps, sex, height)));
            tvCalorieCost.setText(String.format("%.2f kcal", CalorieCostCalculator.calcCaloriesCost(weight, totalSteps)));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_set:
                startActivity(new Intent(this, SetPlanActivity.class));
                break;
            case R.id.tv_data:
                startActivity(new Intent(this, HistoryActivity.class));
                break;

            case R.id.tv_calorie_cost:
            case R.id.tv_distance:

                int sex = SPUtils.getInstance().getInt(Constant.SEX, -1);
                int height = SPUtils.getInstance().getInt(Constant.HEIGHT, -1);
                float weight = SPUtils.getInstance().getFloat(Constant.WEIGHT, -1);

                if (-1 == sex || height == -1 || weight == -1) {
                    startActivity(new Intent(this, GenderSetActivity.class));
                }

                break;

            default:
                break;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBind) {
            this.unbindService(conn);
        }
    }
}
