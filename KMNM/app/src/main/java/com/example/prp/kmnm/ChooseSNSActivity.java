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

    private String directory;
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosesns);

        ImageButton menuBtn = (ImageButton)findViewById(R.id.menubtn);
        ImageButton facebookBtn = (ImageButton)findViewById(R.id.facebook);
        ImageButton twitterBtn = (ImageButton)findViewById(R.id.twitter);
        menuBtn.setOnClickListener(this);
        facebookBtn.setOnClickListener(this);
        twitterBtn.setOnClickListener(this);

        Intent intent = getIntent();
        directory = intent.getStringExtra("directory");
        filename = intent.getStringExtra("filename");
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
            //Twitter
            String packageName = "com.twitter.android";
            String activityName = null;

            PackageManager pm = getPackageManager();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            List<ResolveInfo> resolves = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            ActivityInfo activityInfo = null;
            for (ResolveInfo info: resolves) {
                activityInfo = info.activityInfo;
                if (activityInfo.packageName.equals(packageName)) {
                    activityName = activityInfo.name;
                }
            }

            //Twitterアプリがない場合
            if (activityName == null) {
                Toast t = Toast.makeText(ChooseSNSActivity.this, "not installed", Toast.LENGTH_LONG);
                t.show();
                return;
            }
//            ComponentName componentName = new ComponentName(packageName, activityName);
            String path_to_img = String.format("%s/%s", directory, filename);
//            intent.setComponent(componentName);
            intent.putExtra(Intent.EXTRA_TEXT, "きみのみからの投稿です");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path_to_img));
            startActivity(intent);

        }
//        else if (view.getId() == R.id.line) {
//
//        }
        else if (view.getId() == R.id.facebook) {
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
            intent.setComponent(componentName);
            startActivity(intent);
        }

    }
}
