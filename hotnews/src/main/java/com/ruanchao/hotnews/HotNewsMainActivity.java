package com.ruanchao.hotnews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = "/HotNewsMainActivity/1")
public class HotNewsMainActivity extends AppCompatActivity {



    @BindView(R2.id.btn_say)
    Button mSay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_news_main);
        ButterKnife.bind(this);
    }

    @OnClick(R2.id.btn_say)
    public void say(View view){
        Toast.makeText(this,TestApp.getInstance().getText(), Toast.LENGTH_LONG).show();
    }
}
