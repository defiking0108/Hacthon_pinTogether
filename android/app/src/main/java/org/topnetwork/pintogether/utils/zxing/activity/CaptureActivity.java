package org.topnetwork.pintogether.utils.zxing.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.topnetwork.zxing.IScanCallBack;
import com.topnetwork.zxingV2.ScanManager;

import org.jetbrains.annotations.Nullable;
import org.topnetwork.pintogether.R;
import org.topnetwork.pintogether.utils.LogUtils;
import org.topnetwork.pintogether.utils.ToastUtils;
import org.topnetwork.pintogether.utils.UIUtils;
import org.topnetwork.pintogether.utils.zxing.QRUtils;
import org.topnetwork.pintogether.utils.zxing.camera.CameraManager;
import org.topnetwork.pintogether.utils.zxing.decoding.CaptureActivityHandler;
import org.topnetwork.pintogether.utils.zxing.decoding.InactivityTimer;
import org.topnetwork.pintogether.utils.zxing.decoding.RGBLuminanceSource;
import org.topnetwork.pintogether.utils.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

;


/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class CaptureActivity extends AppCompatActivity implements Callback {

    private static final int REQUEST_CODE_SCAN_GALLERY = 100;

    private CaptureActivityHandler handler;
    private RelativeLayout mRlViewfinderView;
    private ViewfinderView viewfinderView;
    private TextView mTvLable;
    private TextView mTvPhotoAlbum;//相册
    private ImageView back;
    private ImageView iv_back;
    private TextView tv_showmy;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private ProgressDialog mProgress;
    private String photo_path;
    private Bitmap scanBitmap;
    //	private Button cancelScanButton;
    public static final int RESULT_CODE_QR_SCAN = 0xA1;
    public static final String INTENT_EXTRA_KEY_QR_SCAN = "qr_scan_result";
    private Toast toast;
    private Boolean hasInit = false;

    private IScanCallBack scanCallBack = new IScanCallBack() {

        @Override
        public void after(@Nullable String msg) {
            ToastUtils.showShort(msg);
        }

        @Override
        public void before(@Nullable String msg) {
            ToastUtils.showShort(msg);
        }

        @Override
        public void finish() {
            CaptureActivity.this.finish();
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIUtils.setTransparentStatusBar(this);
        setContentView(R.layout.activity_scanner);
        mRlViewfinderView = findViewById(R.id.rl_finder);
        ScanManager.INSTANCE.registerScanCallBack(scanCallBack);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasInit) {
            hasInit = true;
            initCamera();
        }
    }

    private void initCamera() {
        //StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.black), false);
        //ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
        CameraManager.init(getApplication());

        viewfinderView = findViewById(R.id.viewfinder_content);
        viewfinderView.setDrawTextInfoAfter(new ViewfinderView.IDrawTextInfoAfter() {
            @Override
            public void drawTextInfo(int x, int y) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTvLable.getLayoutParams();
                layoutParams.setMargins(0, y + 15, 0, 0);
                mTvLable.setLayoutParams(layoutParams);
            }
        });
        mTvPhotoAlbum = findViewById(R.id.tv_photo_album);
        mTvPhotoAlbum.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, 1);
        });
        mTvLable = findViewById(R.id.tv_label);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener((view) -> {
            finish();
        });
        //tv_showmy = findViewById(R.id.tv_showmy);
        //tv_showmy.setOnClickListener(v -> {
//            Navigation.startMyQr();
        //IntentRoute.getIntentRoute().withType(ARouteHandleType.CODE_QR_CODE).navigation();

        //});
//		cancelScanButton = (Button) this.findViewById(R.id.btn_cancel_scan);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

        //添加toolbar
//        addToolbar();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.scanner_menu, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
////        switch (item.getItemId()){
////            case R.id.scan_local:
////                //打开手机中的相册
////                Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"
////                innerIntent.setType("image/*");
////                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
////                this.startActivityForResult(wrapperIntent, REQUEST_CODE_SCAN_GALLERY);
////                return true;
////        }
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];

                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);

            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);

            }

        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);

        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();

        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);

            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
        return null;

    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());

    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());

    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (data != null) {
                    LogUtils.eTag("CaptureActivity", "onActivityResult: " + data.getData());
                    Uri selectedImage = data.getData();
                    String path = getPath(this, selectedImage);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor == null) return;
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    QRUtils.analyzeBitmap(picturePath, new QRUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            LogUtils.eTag("CaptureActivity", result);
//                            ScanManager.INSTANCE.scanResult(result);

//                            Intent intent = new Intent();
//                            //把返回数据存入Intent
//                            intent.putExtra("result", result);
//                            //设置返回数据
//                            setResult(RESULT_OK, intent);
//                            CaptureActivity.this.finish();
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            ToastUtils.showShort("解析失败");
                            LogUtils.eTag("CaptureActivity", "null");
                        }
                    });

                }
                break;
            case REQUEST_CODE_SCAN_GALLERY:

                LogUtils.d("CaptureActivity", "REQUEST_CODE_SCAN_GALLERY");
                //获取选中图片的路径
                Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                if (cursor == null) return;
                if (cursor.moveToFirst()) {
                    photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                }
                cursor.close();

                mProgress = new ProgressDialog(CaptureActivity.this);
                mProgress.setMessage("正在扫描...");
                mProgress.setCancelable(false);
                mProgress.show();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Result result = scanningImage(photo_path);
                        if (result != null) {
//                                Message m = handler.obtainMessage();
//                                m.what = R.id.decode_succeeded;
//                                m.obj = result.getText();
//                                handler.sendMessage(m);
//                            ScanManager.INSTANCE.scanResult(result.getText());
//                            Intent resultIntent = new Intent();
//                            Bundle bundle = new Bundle();
//                            bundle.putString(INTENT_EXTRA_KEY_QR_SCAN, result.getText());
//                            resultIntent.putExtras(bundle);
//                            CaptureActivity.this.setResult(RESULT_CODE_QR_SCAN, resultIntent);

                        } else {
                            Message m = handler.obtainMessage();
                            m.what = R.id.decode_failed;
                            m.obj = "Scan failed!";
                            handler.sendMessage(m);
                        }
                    }
                }).start();
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 扫描二维码图片的方法
     *
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (com.google.zxing.FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = findViewById(R.id.scanner_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

        //quit the scan view
//		cancelScanButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				CaptureActivity.this.finish();
//			}
//		});
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        if (inactivityTimer != null) {
            inactivityTimer.shutdown();
        }
        ScanManager.INSTANCE.unRegisterScanCallBack();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
//        playBeepSoundAndVibrate();
        String resultString = result.getText();
        //FIXME
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
//            Intent resultIntent = new Intent();
//            Bundle bundle = new Bundle();
//            bundle.putString(INTENT_EXTRA_KEY_QR_SCAN, resultString);
//            System.out.println("sssssssssssssssss scan 0 = " + resultString);
            // 不能使用Intent传递大于40kb的bitmap，可以使用一个单例对象存储这个bitmap
//            bundle.putParcelable("bitmap", barcode);
//            Logger.d("saomiao",resultString);
//            resultIntent.putExtras(bundle);
//            this.setResult(RESULT_CODE_QR_SCAN, resultIntent);


            if (resultString != null && resultString.length() != 0) {
//                Intent intent = new Intent();
//                //把返回数据存入Intent
//                intent.putExtra("result", result.getText());
//                //设置返回数据
//                setResult(RESULT_OK, intent);
//                CaptureActivity.this.finish();
                LogUtils.eTag("CaptureActivity ", resultString);
//                ScanManager.INSTANCE.scanResult(resultString);
            } else {
                if (toast == null) {
                    toast = Toast.makeText(CaptureActivity.this, "未能识别，请扫描有效的二维码", Toast.LENGTH_SHORT);
                } else {
                    toast.setText("未能识别，请扫描有效的二维码");
                }

                toast.show();
//                Toast.makeText(CaptureActivity.this,"未能识别，请扫描有效的二维码",Toast.LENGTH_SHORT).show();
                Message m = handler.obtainMessage();
                m.what = R.id.restart_preview;
                m.obj = "Scan again!";
                handler.sendMessage(m);
            }

        }
//        CaptureActivity.this.finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder, CaptureActivity.this);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

}