package com.jwzt.download;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jwzt.download.down.SelectActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }




    @OnClick(R.id.btn_down)
    public void goDownPager(){

        Intent intent=new Intent(this, SelectActivity.class);
        this.startActivity(intent);






    }





    @Override
    protected void onResume() {

        super.onResume();
        ButterKnife.bind(this);

    }


    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();

    }
}
