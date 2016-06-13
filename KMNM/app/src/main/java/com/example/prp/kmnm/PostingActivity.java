package com.example.prp.kmnm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by a61-201405-2055 on 16/06/13.
 */
public class PostingActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkpost);

        ImageButton menuBtn = (ImageButton)findViewById(R.id.menubtn);
        ImageButton postBtn = (ImageButton)findViewById(R.id.postbtn);
        menuBtn.setOnClickListener(this);
        postBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.menubtn) {
            //メニュー画面に戻る
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.postbtn) {
            //投稿画面に遷移
            Intent intent = new Intent(getApplicationContext(), ChooseSNSActivity.class);
            startActivity(intent);
        }

    }
}
