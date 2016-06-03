package com.example.prp.kmnm;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.content.Intent;

public class MainActivity extends Activity implements View.OnClickListener{

    int viewWidth;
    int viewHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 画像付きボタン
        ImageButton startBtn = (ImageButton)findViewById(R.id.start);
        ImageButton policyBtn = (ImageButton)findViewById(R.id.policy);
        startBtn.setOnClickListener(this);
        policyBtn.setOnClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        RelativeLayout rL = (RelativeLayout)findViewById(R.id.relativeLayout);
        viewWidth = rL.getWidth();
        viewHeight = rL.getHeight();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.start) {
            //ゲーム画面起動
            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.policy) {
            //ルール説明ポップアップ
            final PopupWindow ruleWindow = new PopupWindow(MainActivity.this);

            //レイアウト設定
            final View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);

            popupView.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));

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
