package com.techdev_michael.step_counter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.SPUtils;
import com.techdev_michael.step_counter.Constant;
import com.techdev_michael.step_counter.R;

public class GenderSetActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout rlGenderMale, rlGenderFemale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender_set);

        rlGenderMale = findViewById(R.id.rl_gender_male);
        rlGenderFemale = findViewById(R.id.rl_gender_female);

        rlGenderFemale.setOnClickListener(this);
        rlGenderMale.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.rl_gender_female) {

            SPUtils.getInstance().put(Constant.SEX, Constant.FEMALE);
            Intent intent = new Intent(this, AgeSetActivity.class);
            intent.putExtra(Constant.SEX, Constant.FEMALE);
            startActivity(intent);

        } else if (v.getId() == R.id.rl_gender_male) {

            SPUtils.getInstance().put(Constant.SEX, Constant.MALE);
            Intent intent = new Intent(this, AgeSetActivity.class);
            intent.putExtra(Constant.SEX, Constant.MALE);
            startActivity(intent);

        }
    }
}
