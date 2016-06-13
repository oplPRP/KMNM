package com.example.prp.kmnm;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by a61-201405-2055 on 16/06/13.
 */
public class ChooseSNSActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosesns);

        ImageButton menuBtn = (ImageButton)findViewById(R.id.menubtn);
        menuBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.menubtn) {
            //メニュー画面に戻る
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.twitter) {
//            //Twitterウェブサイトに遷移
//            //文字列しか渡せない
//            Uri uri = Uri.parse("https://twitter.com/intent/tweet?text=" + "きみのみあぷりからです。");
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(intent);
        }

    }
}
