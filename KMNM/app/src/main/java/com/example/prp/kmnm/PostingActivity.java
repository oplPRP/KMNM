package com.example.prp.kmnm;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by a61-201405-2055 on 16/06/13.
 */
public class PostingActivity extends Activity implements View.OnClickListener{

    private AudioAttributes audioAttributes;
    private SoundPool soundpool;
    private int soundBGM;
    private int soundClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkpost);

        ImageButton menuBtn = (ImageButton)findViewById(R.id.menubtn);
        ImageButton postBtn = (ImageButton)findViewById(R.id.postbtn);
        menuBtn.setOnClickListener(this);
        postBtn.setOnClickListener(this);

        // BGM
        audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build();
        soundpool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(2).build();

        // BGM事前ロード
        soundBGM = soundpool.load(this, R.raw.home_bgm, 1);
        soundClick = soundpool.load(this, R.raw.gamestart_se, 1);
        soundpool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
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
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.menubtn) {
            //メニュー画面に戻る
            soundpool.play(soundClick, 1.0f, 1.0f, 1, 0, 0);
            final Handler timer = new Handler();
            timer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                    startActivity(intent);
                }
            }, 5000);
        } else if (view.getId() == R.id.postbtn) {
            //投稿画面に遷移
            Intent intent = new Intent(getApplicationContext(), ChooseSNSActivity.class);
            startActivity(intent);
        }

    }
}
