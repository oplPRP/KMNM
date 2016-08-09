package com.example.prp.kmnm;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.Image;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
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
    private AudioAttributes audioAttributes;
    private SoundPool soundpool;
    private int soundBGM;
    private int soundClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 画像付きボタン
        ImageButton startBtn = (ImageButton)findViewById(R.id.start);
        ImageButton policyBtn = (ImageButton)findViewById(R.id.policy);
        ImageButton gamePolicyBtn = (ImageButton)findViewById(R.id.gamepolicy);
        gamePolicyBtn.setOnClickListener(this);
        startBtn.setOnClickListener(this);
        policyBtn.setOnClickListener(this);

        // BGM
        audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build();
        soundpool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(2).build();

        // BGM事前ロード
        soundBGM = soundpool.load(this, R.raw.home_bgm, 1);
        soundClick = soundpool.load(this, R.raw.gamestart_se, 1);
        soundpool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundpool.play(soundBGM, 1.0f, 1.0f, 0, -1, 1);
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        soundpool.release();
        //画面解放
        cleanupView(findViewById(R.id.mainrelativeLayout));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //画面解放
        cleanupView(findViewById(R.id.mainrelativeLayout));
    }

    public static final void cleanupView(View view) {
        if(view instanceof ImageButton) {
            ImageButton ib = (ImageButton)view;
            ib.setImageDrawable(null);
        }
        view.setBackground(null);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        RelativeLayout rL = (RelativeLayout)findViewById(R.id.mainrelativeLayout);
        viewWidth = rL.getWidth();
        viewHeight = rL.getHeight();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.start) {

            //ゲーム画面起動
            soundpool.play(soundClick, 1.0f, 1.0f, 1, 0, 0);
            final Handler timer = new Handler();
            timer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                    startActivity(intent);
                }
            }, 5000);
        } else if (view.getId() == R.id.policy) {
            //ルール説明ポップアップ
            final PopupWindow ruleWindow = new PopupWindow(getApplicationContext());

            //レイアウト設定
            final View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);

            popupView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));

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
        } else if (view.getId() == R.id.gamepolicy) {
            //ルール説明ポップアップ
            final PopupWindow ruleWindow = new PopupWindow(getApplicationContext());

            //レイアウト設定
            final View popupView = getLayoutInflater().inflate(R.layout.policypopup_layout, null);

            popupView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));

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
            popupView.setOnClickListener(new View.OnClickListener() {
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
