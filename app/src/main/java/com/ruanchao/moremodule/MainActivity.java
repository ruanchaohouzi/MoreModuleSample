package com.ruanchao.moremodule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.launcher.ARouter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_botnews)
    Button mStartHotNews;

    @BindView(R.id.btn_robot)
    Button mStartRobot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_botnews)
    public  void onClickHotNews(View view){
        //startActivity(new Intent(this, HotNewsMainActivity.class));
        ARouter.getInstance().build("/HotNewsMainActivity/1").navigation();
    }

    @OnClick(R.id.btn_robot)
    public  void onClickRobot(View view){
        //startActivity(new Intent(this, HotNewsMainActivity.class));
        ARouter.getInstance().build("/RobotMainActivity/1").navigation();
    }

}
