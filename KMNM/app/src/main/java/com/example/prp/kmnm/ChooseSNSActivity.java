package com.example.prp.kmnm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.StringBuilderPrinter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import com.facebook.FacebookContentProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by a61-201405-2055 on 16/06/13.
 */
public class ChooseSNSActivity extends Activity implements View.OnClickListener {

//    int viewWidth;
//    int viewHeight;

    private static String directory;
    private static String filename;

    private static ShareDialog shareDialog;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosesns);

        ImageButton menuBtn = (ImageButton)findViewById(R.id.menubtn);
        ImageButton facebookBtn = (ImageButton)findViewById(R.id.facebook);
        ImageButton twitterBtn = (ImageButton)findViewById(R.id.twitter);
//        ImageButton gamePolicyBtn = (ImageButton)findViewById(R.id.gamepolicy);

        menuBtn.setOnClickListener(this);
        facebookBtn.setOnClickListener(this);
        twitterBtn.setOnClickListener(this);
//        gamePolicyBtn.setOnClickListener(this);

        Intent intent = getIntent();
        directory = intent.getStringExtra("directory");
        filename = intent.getStringExtra("filename");

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(this);
    }

    public static void facebookShare() {
        Bitmap bm = BitmapFactory.decodeFile(directory + "/" + filename);
        SharePhoto sharePhoto = new SharePhoto.Builder()
                .setBitmap(bm)
                .setCaption("きみのみ")
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(sharePhoto)
                .build();
        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        RelativeLayout rL = (RelativeLayout)findViewById(R.id.mainrelativeLayout);
//        viewWidth = rL.getWidth();
//        viewHeight = rL.getHeight();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.menubtn) {
            //メニュー画面に戻る
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.twitter) {
            //Twitter
            String packageName = "com.twitter.android";
//            String activityName = "com.twitter.applib.composer.TextFirstComposer.Activity";

//            PackageManager pm = getPackageManager();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
//            List<ResolveInfo> resolves = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//            ActivityInfo activityInfo = null;
//            for (ResolveInfo info: resolves) {
//                activityInfo = info.activityInfo;
//                if (activityInfo.packageName.equals(packageName)) {
//                    activityName = activityInfo.name;
//                }
//            }

//            //Twitterアプリがない場合
//            if (activityName == null) {
//                Toast t = Toast.makeText(ChooseSNSActivity.this, "not installed", Toast.LENGTH_LONG);
//                t.show();
//                return;
//            }
//            ComponentName componentName = new ComponentName(packageName, activityName);
//            String url = "twitter://post?message=きみのみからの投稿です";
            String path_to_img = String.format("%s/%s", directory, filename);
//            intent.setComponent(componentName);
            intent.putExtra(Intent.EXTRA_TEXT, "きみのみからの投稿です");
            intent.setPackage(packageName);
//            intent.putExtra(Intent.ACTION_VIEW, Uri.parse(url));
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path_to_img));
            startActivity(intent);

        }
//        else if (view.getId() == R.id.line) {
//
//        }
        else if (view.getId() == R.id.facebook) {

            facebookShare();

//            //URL
//            String packageName = "com.facebook.katana";
//            String activityName= null;
//
//            PackageManager pm = getPackageManager();
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("image/jpg");
//            List<ResolveInfo> resolves = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//            ActivityInfo activityInfo = null;
//            for (ResolveInfo info: resolves) {
//                activityInfo = info.activityInfo;
//                if (activityInfo.packageName.equals(packageName)) {
//                    activityName = activityInfo.name;
//                }
//            }
//
//            //Facebookアプリがない場合
//            if (activityName == null) {
//                Toast t = Toast.makeText(ChooseSNSActivity.this, "not installed", Toast.LENGTH_LONG);
//                t.show();
//                return;
//            }
//            ComponentName componentName = new ComponentName(packageName, activityName);
//            String path_to_img = String.format("%s/%s", directory, filename);
//            intent.setComponent(componentName);
//            intent.putExtra(Intent.EXTRA_TEXT, "きみのみからの投稿です");
//            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path_to_img));
//            startActivity(intent);
        }
//        else if (view.getId() == R.id.gamepolicy) {
//            //ルール説明ポップアップ
//            final PopupWindow ruleWindow = new PopupWindow(getApplicationContext());
//
//            //レイアウト設定
//            final View popupView = getLayoutInflater().inflate(R.layout.policypopup_layout, null);
//
//            popupView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
//
//            ruleWindow.setContentView(popupView);
//
//            //タップ時に他のViewでキャッチされないための設定
////            ruleWindow.setOutsideTouchable(true);
////            ruleWindow.setFocusable(true);
//
//            //　背景設定
//            ruleWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.popupbackground, null));
//
//            //表示サイズ設定
//            ruleWindow.setWidth(viewWidth);
//            ruleWindow.setHeight(viewHeight);
//
//            ruleWindow.showAtLocation(findViewById(R.id.background), Gravity.CENTER, 0, 0);
//
//            //画面内タップで閉じる
//            popupView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    //fadeoutしない
////                    popupView.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
//                    if (ruleWindow.isShowing()) {
//                        ruleWindow.dismiss();
//                    }
//                }
//            });
//        }

    }
}
