package com.example.prp.kmnm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.StringBuilderPrinter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

/**
 * Created by a61-201405-2055 on 16/06/13.
 */
public class ChooseSNSActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosesns);

        ImageButton menuBtn = (ImageButton)findViewById(R.id.menubtn);
        ImageButton facebookBtn = (ImageButton)findViewById(R.id.facebook);
        menuBtn.setOnClickListener(this);
        facebookBtn.setOnClickListener(this);
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
    public void onClick(View view) {
        if (view.getId() == R.id.menubtn) {
            //メニュー画面に戻る
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.twitter) {
            //URL
            String url = "https://api.twitter.com/1.1/statusupdate.json";


        } else if (view.getId() == R.id.line) {

        } else if (view.getId() == R.id.facebook) {
            //URL
            String packageName = "com.facebook.katana";
            String activityName= null;
            String text = "きみのみ";

            PackageManager pm = getPackageManager();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            List<ResolveInfo> resolves = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            ActivityInfo activityInfo = null;
            for (ResolveInfo info: resolves) {
                activityInfo = info.activityInfo;
                if (activityInfo.packageName.equals(packageName)) {
                    activityName = activityInfo.name;
                }
            }

            //Facebookアプリがない場合
            if (activityName == null) {
                Toast t = Toast.makeText(ChooseSNSActivity.this, "not installed", Toast.LENGTH_LONG);
                t.show();
                return;
            }
            ComponentName componentName = new ComponentName(packageName, activityName);
            intent.setComponent(componentName).putExtra(Intent.EXTRA_TEXT, text);
            startActivity(intent);
        }

    }
}
