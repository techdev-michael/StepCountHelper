package com.techdev_michael.step_counter.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.techdev_michael.step_counter.Constant;
import com.techdev_michael.step_counter.R;
import com.techdev_michael.step_counter.view.AgeNumberLayout;
import com.techdev_michael.step_counter.view.HeightNumberLayout;

import yanzhikai.ruler.BooheeRuler;
import yanzhikai.ruler.RulerCallback;
import yanzhikai.ruler.Utils.RulerStringUtil;

public class HeightSetActivity extends AppCompatActivity {
    private BooheeRuler br_top_head;
    private HeightNumberLayout knl_top_head;

    private ImageView ivReplace;

    private TextView tvNext;

    private float scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height_set);

        ivReplace = findViewById(R.id.iv_replace);
        tvNext = findViewById(R.id.tv_next);

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SPUtils.getInstance().put(Constant.HEIGHT, Integer.valueOf(RulerStringUtil.resultValueOf(scale, br_top_head.getFactor())));

                startActivity(new Intent(HeightSetActivity.this, MainActivity.class));

            }
        });

        int sex = SPUtils.getInstance().getInt(Constant.SEX, Constant.MALE);
        ivReplace.setImageResource(sex == Constant.MALE ? R.drawable.gender_setting_boy : R.drawable.gender_setting_girl);

        br_top_head = findViewById(R.id.br_top_head);
        knl_top_head = findViewById(R.id.knl_bottom_head);

        knl_top_head.bindRuler(br_top_head);

        br_top_head.setCallback(knl_top_head);

        br_top_head.setCallback(new RulerCallback() {
            @Override
            public void onScaleChanging(float scale) {

                HeightSetActivity.this.scale = scale;

                knl_top_head.onScaleChanging(scale);

            }
        });

    }
}
