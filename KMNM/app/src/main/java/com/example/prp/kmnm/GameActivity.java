package com.example.prp.kmnm;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Camera;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.KeyEvent;
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


    private AudioAttributes audioAttributes;
    private SoundPool soundpool;
    private int soundBGM;
    private int soundClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTra
//        }

        // 画像付きボタン
        final ImageButton gameBtn = (ImageButton)findViewById(R.id.gamePushBtn);
        ImageButton policyBtn = (ImageButton)findViewById(R.id.policy);
        gameBtn.setOnClickListener(this);
        policyBtn.setOnClickListener(this);

        // BGM
        audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build();
        soundpool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(2).build();

        // クリックボタン非活性
        gameBtn.setEnabled(false);

        // BGM事前ロード
        soundBGM = soundpool.load(this, R.raw.bgm, 0);
        soundClick = soundpool.load(this, R.raw.clicksound, 0);
        soundpool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundpool.play(soundBGM, 1.0f, 1.0f, 0, -1, 0);
                // 音声ロードが終わったためボタンを活性
                gameBtn.setEnabled(true);
                Log.d("loadcomp", "loadcomp");
            }
        });

        //乱数生成
        randomNumber = (int)(Math.random()*30)+1;
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
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        RelativeLayout rL = (RelativeLayout)findViewById(R.id.relativeLayout);
        viewWidth = rL.getWidth();
        viewHeight = rL.getHeight();
    }

    public void onClick (View view) {
        if (view.getId() == R.id.gamePushBtn) {
            soundpool.play(soundClick, 1.0f, 1.0f, 1, 0, 0);
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
                        Intent intent = new Intent(getApplicationContext(), PostingActivity.class);
                        startActivity(intent);
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

            ruleWindow.showAtLocation(findViewById(R.id.gameBg), Gravity.CENTER, 0, 0);

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
