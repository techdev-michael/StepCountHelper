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
 *  记步页面的主页
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     *  界面显示的文本，分别为 查看历史步数、跑步步数、走路步数、运动距离、消耗卡路里数
     */
    private TextView tv_data, tvRunSteps, tvWalkSteps, tvDistance, tvCalorieCost;

    /**
     *  显示步数的环形条
     */
    private StepArcView cc;

    /**
     * 设置锻炼计划
     */
    private TextView tv_set;

    /**
     * 查看历史步数
     */
    private TextView tv_isSupport;

    /**
     *  本地存储数据的工具类
     */
    private SharedPreferencesUtils sp;

    /**
     *  设置默认的锻炼计划为 7000 步
     */
    private static final String DEFAULT_TARGET_STEPS = "7000";

    /**
     *  初始化布局，即将页面上的文字内容进行填充
     */
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
        setContentView(R.layout.activity_main); // 设置页面布局
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

    /**
     *  初始化数据，设置锻炼计划
     */
    private void initData() {
        sp = new SharedPreferencesUtils(this);
        //获取用户设置的计划锻炼步数，没有设置过的话默认7000
        String planWalk_QTY = (String) sp.getParam("planWalk_QTY", DEFAULT_TARGET_STEPS);
        //设置当前步数为0
        cc.setCurrentCount(Integer.parseInt(planWalk_QTY), 0);
        tv_isSupport.setText("计步中...");

        // 开始运行后台服务进行计步
        setupService();
    }


    private boolean isBind = false;

    /**
     *  开启计步服务
     */
    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        isBind = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);

    }

    /**
     *  创建计步服务，每次有计步数据发生变化时就更新页面显示
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
                // 更新页面上的计步数据
                updateDashborad(planWalk_QTY, stepCount, runStepCount);
            }

            //设置步数监听回调
            stepService.registerCallback(new UpdateUiCallBack() {
                @Override
                public void updateUi(int stepCount, int runStepCount) {
                    // 更新页面上的计步数据
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
         * 和计步服务断开
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    /**
     *  更新仪表板数据
     * @param targetSteps 目标步数
     * @param totalSteps 今日总步数
     * @param runSteps 跑步步数
     */
    @SuppressLint("DefaultLocale")
    private void updateDashborad(String targetSteps, int totalSteps, int runSteps) {

        // 设置当前步数
        cc.setCurrentCount(Integer.valueOf(targetSteps), totalSteps);
        // 设置跑步步数
        tvRunSteps.setText(String.valueOf(runSteps));
        // 设置步行步数
        tvWalkSteps.setText(String.valueOf(totalSteps - runSteps < 0 ? 0 : totalSteps - runSteps));

        // 获取第一次打开页面时输入的性别信息
        int sex = SPUtils.getInstance().getInt(Constant.SEX, -1);
        // 获取第一次打开页面时输入的身高信息
        int height = SPUtils.getInstance().getInt(Constant.HEIGHT, -1);
        // 获取第一次打开页面时输入的体重信息
        float weight = SPUtils.getInstance().getFloat(Constant.WEIGHT, -1);

        if (-1 == sex || height == -1 || weight == -1) {
            // 没有设置性别、身高、体重的话就显示“--”
            tvDistance.setText("--/--");
            tvCalorieCost.setText("--/--");
        } else {
            // 计算运动距离和卡路里消耗，并显示到界面上
            tvDistance.setText(String.format("%.3f km", CalorieCostCalculator.calcDistance(totalSteps, sex, height)));
            tvCalorieCost.setText(String.format("%.2f kcal", CalorieCostCalculator.calcCaloriesCost(weight, totalSteps)));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_set:
                // 跳转到设置锻炼计划页面
                startActivity(new Intent(this, SetPlanActivity.class));
                break;
            case R.id.tv_data:
                // 跳转到历史步数界面
                startActivity(new Intent(this, HistoryActivity.class));
                break;

            case R.id.tv_calorie_cost:
            case R.id.tv_distance:
                // 如果没有设置性别、身高、体重，则点击卡路里消耗和运动里程则会跳转到设置页面
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
            // 页面销毁时，解除计步服务的绑定
            this.unbindService(conn);
        }
    }
}
