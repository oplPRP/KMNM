package com.example.prp.kmnm;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.app.Activity;

/**
 * Created by a61-201405-2055 on 16/06/03.
 */
public class GameActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // 画像付きボタン
        ImageButton gameBtn = (ImageButton)findViewById(R.id.gamePushBtn);
        gameBtn.setOnClickListener(this);
    }

    public void onClick (View view) {

    }
}
