package com.example.prp.kmnm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by a61-201405-2055 on 16/06/02.
 */
public class SplashActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //スプラッシュ用のビューを取得する
        setContentView(R.layout.splash);

        //2秒後にMainActivityに遷移
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                //SplashActivity終了
                SplashActivity.this.finish();
            }
        }, 2 * 1000);
    }
}
