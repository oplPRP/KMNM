package com.example.prp.kmnm;

import android.app.Fragment;
import android.graphics.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by a61-201405-2055 on 16/06/03.
 */
public class GameActivity extends Activity implements View.OnClickListener{

    int clickCounter;
    int randomNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTra
//        }

        // 画像付きボタン
        ImageButton gameBtn = (ImageButton)findViewById(R.id.gamePushBtn);
        gameBtn.setOnClickListener(this);

        //乱数生成
        randomNumber = (int)(Math.random()*30)+1;
    }

    public void onClick (View view) {
        clickCounter = clickCounter + 1;
        Log.d("乱数", String.valueOf(randomNumber));
        Log.d("クリック数", String.valueOf(clickCounter));
        //乱数とタップ数が同じになった時に画像を表示
        if (randomNumber == clickCounter){
            ImageView hitImage = (ImageView)findViewById(R.id.game_hit);
            hitImage.setImageResource(R.drawable.game_hit);
            final Handler timer = new Handler();
            timer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.game_hit).setVisibility(View.INVISIBLE);
                }
            }, 1000);
        }
    }
}
