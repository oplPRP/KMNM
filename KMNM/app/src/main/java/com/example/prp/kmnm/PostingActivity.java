package com.example.prp.kmnm;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;

import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.media.AudioAttributes;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.BitmapCompat;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HandshakeCompletedEvent;

/**
 * Created by a61-201405-2055 on 16/06/13.
 */
public class PostingActivity extends Activity implements View.OnClickListener {

    private AudioAttributes audioAttributes;
    private SoundPool soundpool;
    private int soundBGM;
    private int soundClick;

    private String directory;
    private String filename;

    private ImageButton postBtn;

    //Camera
    private Size mPreviewSize;
    private TextureView mTextureView;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mPreviewSession;
    private ImageButton mBtnTakingPhoto;

    //Camera
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Camera
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build();
        StrictMode.setThreadPolicy(policy);
        //Camera FullScreen
        setContentView(R.layout.activity_checkpost);
        mTextureView = (TextureView) findViewById(R.id.texturePicView);
        mTextureView.setSurfaceTextureListener(mCameraViewStatusChanged);
        mBtnTakingPhoto = (ImageButton) findViewById(R.id.takePhoto);
        mBtnTakingPhoto.setOnClickListener(mBtnShotClicked);

        ImageButton menuBtn = (ImageButton) findViewById(R.id.menubtn);
        postBtn = (ImageButton) findViewById(R.id.postbtn);
        menuBtn.setOnClickListener(this);
        postBtn.setVisibility(View.INVISIBLE);
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

    //Camera
    private final TextureView.SurfaceTextureListener mCameraViewStatusChanged = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //Textureが有効化されたらカメラを初期化
            prepareCameraView();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    //Camera
    private final View.OnClickListener mBtnShotClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            takePicture();
            postBtn.setVisibility(View.VISIBLE);
        }
    };

    //Camera
    private void prepareCameraView() {
        //Camera機能にアクセスするためのCameraManagerの取得
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            //BackCameraを取得してOpen
            for (String strCameraID : manager.getCameraIdList()) {
                //Cameraから情報を取得するためのCharacteristics
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(strCameraID);
                //TODO ここでインカメラにできるかも
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                //ストリームの設定を取得
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                //配列から最大の組み合わせを取得する
                mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];

                //プレビュー画面のサイズ調整
                this.configureTransform();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
                    return;
                }
                manager.openCamera(strCameraID, new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(CameraDevice camera) {
                        mCameraDevice = camera;
                        createCameraPreviewSession();
                    }

                    @Override
                    public void onDisconnected(CameraDevice cmdCamera) {
                        cmdCamera.close();
                        mCameraDevice = null;
                    }

                    @Override
                    public void onError(CameraDevice cmdCamera, int error) {
                        cmdCamera.close();
                        mCameraDevice = null;
                    }
                }, null);

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //Camera
    protected void createCameraPreviewSession() {
        if(null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        if(null == texture) {
            return;
        }
        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface surface = new Surface(texture);
        try {
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        mPreviewBuilder.addTarget(surface);
        try {
            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    mPreviewSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Toast.makeText(PostingActivity.this, "onConfigureFailed", Toast.LENGTH_LONG).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //Camera
    protected void updatePreview() {
        if(null == mCameraDevice) {
            return;
        }
        //オートフォーカスモードに設定する
        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
        //別スレッドで実行
        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        Handler backgroundHandeler = new Handler(thread.getLooper());
        try {
            //画像を繰り返し取得してTextureViewに表示する
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, backgroundHandeler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //Camera
    protected void takePicture() {
        if(null == mCameraDevice) {
            return;
        }
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraDevice.getId());
            Size[] jpegSizes = null;
            int width = 640;
            int height = 480;
            if (characteristics != null) {
                //デバイスがサポートしているストリーム設定からJPEGの出力サイズを取得する
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                if (jpegSizes != null && 0 < jpegSizes.length) {
                    width = jpegSizes[0].getWidth();
                    height = jpegSizes[0].getHeight();
                }
            }

            //画像を取得するためのImageReaderの作成
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List outputSurfaces = new ArrayList(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(mTextureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

//            //画像を調整する
//            int rotation = getWindowManager().getDefaultDisplay().getRotation();
//            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            //ファイルの保存先のディレクトリとファイル名
            //TODO ここチェックして
            String strSaveDir = Environment.getExternalStorageDirectory().toString();
            String strSaveFilename = "pic_" + System.currentTimeMillis() + ".jpg";

            directory = strSaveDir;
            filename = strSaveFilename;

            final File file = new File(strSaveDir, strSaveFilename);

            //別スレッドで画像の保存処理を実行
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        //Fragmentで取得した画像を表示。保存ボタンが押されたら画像の保存を実行する。
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        saveImage(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }
                public void saveImage(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        //生成した画像を出力する
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if(null != output) {
                            output.close();
                        }
                    }
                }
            };
            //別スレッドで実行
            HandlerThread thread = new HandlerThread("CameraPicture");
            thread.start();
            final Handler backgroundHandler = new Handler(thread.getLooper());
            reader.setOnImageAvailableListener(readerListener, backgroundHandler);

            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    //画像の保存が完了したらToast表示
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(PostingActivity.this, "Saved:"+file, Toast.LENGTH_SHORT).show();

                    //もう一度カメラのプレビュー表示を開始する
//                    createCameraPreviewSession();
                }
            };
            mCameraDevice.createCaptureSession(outputSurfaces, new StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, backgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, backgroundHandler);
            //保存した画像を反映させる
            String[] paths = {strSaveDir + "/" + strSaveFilename};
            String[] mimeTypes = {"image/jpeg"};
            MediaScannerConnection.scanFile(getApplicationContext(), paths, mimeTypes, mScanSavedFileCompleted);
            directory = strSaveDir;
            filename = strSaveFilename;
            ImageView takenPhoto = (ImageView)findViewById(R.id.pictureTaken);
            Bitmap bm = BitmapFactory.decodeFile(directory + "/" + filename);
            takenPhoto.setImageBitmap(bm);
            takenPhoto.bringToFront();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private MediaScannerConnection.OnScanCompletedListener mScanSavedFileCompleted = new MediaScannerConnection.OnScanCompletedListener() {
        @Override
        public void onScanCompleted(String path, Uri uri) {

        }
    };

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.configureTransform();
    }
    private void configureTransform() {
        //画面の回転に合わせて
        if(null == mTextureView || null == mPreviewSize) {
            return;
        }
        Display dsply = getWindowManager().getDefaultDisplay();

        int rotation = dsply.getRotation();
        Matrix matrix = new Matrix();

        Point pntDisplay = new Point();
        dsply.getSize(pntDisplay);

        RectF rctView = new RectF(0,0,pntDisplay.x,pntDisplay.y);
        RectF rctPreview = new RectF(0,0,mPreviewSize.getHeight(),mPreviewSize.getWidth());
        float centerX = rctView.centerX();
        float centerY = rctView.centerX();

        rctPreview.offset(centerX - rctPreview.centerX(), centerY - rctPreview.centerY());
        matrix.setRectToRect(rctView, rctPreview, Matrix.ScaleToFit.FILL);
        float scale = Math.max((float)rctView.width()/mPreviewSize.getWidth(), (float)rctView.height()/mPreviewSize.getHeight());
        matrix.postScale(scale,scale,centerX,centerY);

        switch (rotation) {
            case Surface.ROTATION_0:
                matrix.postRotate(0,centerX,centerY);
                break;
            case Surface.ROTATION_90:
                matrix.postRotate(270,centerX,centerY);
                break;
            case Surface.ROTATION_180:
                matrix.postRotate(180,centerX,centerY);
                break;
            case Surface.ROTATION_270:
                matrix.postRotate(90,centerX,centerY);
                break;
        }
        mTextureView.setTransform(matrix);
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
            intent.putExtra("directory", directory);
            intent.putExtra("filename", filename);
            startActivity(intent);
        }

    }
}
