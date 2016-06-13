package com.example.prp.kmnm;

import android.app.Fragment;
import android.graphics.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

/**
 * Created by a61-201405-2055 on 16/06/03.
 */
public class GameActivity extends Activity implements View.OnClickListener{

    int clickCounter;
    int randomNumber;
    int viewWidth;
    int viewHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTra
//        }

        // 画像付きボタン
        ImageButton gameBtn = (ImageButton)findViewById(R.id.gamePushBtn);
        ImageButton policyBtn = (ImageButton)findViewById(R.id.policy);
        gameBtn.setOnClickListener(this);
        policyBtn.setOnClickListener(this);

        //乱数生成
        randomNumber = (int)(Math.random()*30)+1;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        RelativeLayout rL = (RelativeLayout)findViewById(R.id.relativeLayout);
        viewWidth = rL.getWidth();
        viewHeight = rL.getHeight();
    }

    public void onClick (View view) {
        if (view.getId() == R.id.start) {
            clickCounter = clickCounter + 1;
            Log.d("乱数", String.valueOf(randomNumber));
            Log.d("クリック数", String.valueOf(clickCounter));
            //乱数とタップ数が同じになった時に画像を表示
            if (randomNumber == clickCounter) {
                ImageView hitImage = (ImageView) findViewById(R.id.game_hit);
                hitImage.setImageResource(R.drawable.game_hit);
                final Handler timer = new Handler();
                timer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.game_hit).setVisibility(View.INVISIBLE);
                    }
                }, 1000);
            }
        }else if (view.getId() == R.id.policy) {
            //ルール説明ポップアップ
            final PopupWindow ruleWindow = new PopupWindow(GameActivity.this);

            //レイアウト設定
            final View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);

            popupView.setAnimation(AnimationUtils.loadAnimation(GameActivity.this, R.anim.fade_in));

            ruleWindow.setContentView(popupView);

            //タップ時に他のViewでキャッチされないための設定
//            ruleWindow.setOutsideTouchable(true);
//            ruleWindow.setFocusable(true);

            //　背景設定
            ruleWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.popupbackground, null));

            //表示サイズ設定
            ruleWindow.setWidth(viewWidth);
            ruleWindow.setHeight(viewHeight);

            ruleWindow.showAtLocation(findViewById(R.id.background), Gravity.CENTER, 0, 0);

            //画面内タップで閉じる
            popupView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //fadeoutしない
//                    popupView.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
                    if (ruleWindow.isShowing()) {
                        ruleWindow.dismiss();
                    }
                }
            });
        }
    }
}
