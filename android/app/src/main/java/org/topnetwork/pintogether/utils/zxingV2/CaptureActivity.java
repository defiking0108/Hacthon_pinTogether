/*
 * Copyright (C) 2018 Jenly Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.topnetwork.pintogether.utils.zxingV2;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.topnetwork.zxing.IScanCallBack;
import com.topnetwork.zxingV2.ScanManager;

import org.topnetwork.pintogether.R;
import org.topnetwork.pintogether.utils.ActivityUtils;
import org.topnetwork.pintogether.utils.ToastUtils;
import org.topnetwork.pintogether.utils.UIUtils;
import org.topnetwork.pintogether.utils.zxingV2.camera.CameraManager;
import org.topnetwork.pintogether.utils.zxingV2.util.CodeUtils;
import org.topnetwork.pintogether.utils.zxingV2.util.UriUtils;


public class CaptureActivity extends AppCompatActivity implements OnCaptureCallback {

    public static final int REQUEST_CODE_PHOTO = 0X02;
    public static final String SCAN_KEY = "SCAN_KEY";
    private String scanKey = "";

    private SurfaceView surfaceView;
    private ViewfinderView viewfinderView;

    private CaptureHelper mCaptureHelper;

    private IScanCallBack scanCallBack = new IScanCallBack() {

        @Override
        public void after(@org.jetbrains.annotations.Nullable String msg) {
            ToastUtils.showShort(msg);
        }

        @Override
        public void before(@org.jetbrains.annotations.Nullable String msg) {
            ToastUtils.showShort(msg);
        }

        @Override
        public void finish() {
            CaptureActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.push(this);
        UIUtils.setTransparentStatusBar(this);
        int layoutId = getLayoutId();
        if (isContentView(layoutId)) {
            setContentView(layoutId);
        }
        initUI();
        mCaptureHelper.onCreate();
        ScanManager.INSTANCE.registerScanCallBack(scanCallBack);
        scanKey = getIntent().getStringExtra(SCAN_KEY);
    }

    /**
     * 初始化
     */
    public void initUI() {
        surfaceView = findViewById(getSurfaceViewId());
        int viewfinderViewId = getViewfinderViewId();
        if (viewfinderViewId != 0) {
            viewfinderView = findViewById(viewfinderViewId);
        }

        //相册
        TextView mTvPhotoAlbum = findViewById(R.id.tv_photo_album);
        mTvPhotoAlbum.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, REQUEST_CODE_PHOTO);
        });

        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener((view) -> {
            finish();
        });
        initCaptureHelper();
    }

    public void initCaptureHelper() {
        mCaptureHelper = new CaptureHelper(this, surfaceView, viewfinderView, null);
        mCaptureHelper.setOnCaptureCallback(this);
    }

    /**
     * 返回true时会自动初始化{@link #setContentView(int)}，返回为false是需自己去初始化{@link #setContentView(int)}
     *
     * @param layoutId
     * @return 默认返回true
     */
    public boolean isContentView(@LayoutRes int layoutId) {
        return true;
    }

    /**
     * 布局id
     *
     * @return
     */
    public int getLayoutId() {
        return R.layout.zxl_capture;
    }

    /**
     * {@link #viewfinderView} 的 ID
     *
     * @return 默认返回{@code R.id.viewfinderView}, 如果不需要扫码框可以返回0
     */
    public int getViewfinderViewId() {
        return R.id.viewfinderView;
    }


    /**
     * 预览界面{@link #surfaceView} 的ID
     *
     * @return
     */
    public int getSurfaceViewId() {
        return R.id.surfaceView;
    }

    /**
     * 获取 {@link #ivTorch} 的ID
     *
     * @return 默认返回{@code R.id.ivTorch}, 如果不需要手电筒按钮可以返回0
     */
//    public int getIvTorchId() {
//        return R.id.ivTorch;
//    }

    /**
     * Get {@link CaptureHelper}
     *
     * @return {@link #mCaptureHelper}
     */
    public CaptureHelper getCaptureHelper() {
        return mCaptureHelper;
    }

    /**
     * Get {@link CameraManager} use {@link #getCaptureHelper()#getCameraManager()}
     *
     * @return {@link #mCaptureHelper#getCameraManager()}
     */
    @Deprecated
    public CameraManager getCameraManager() {
        return mCaptureHelper.getCameraManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCaptureHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCaptureHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ActivityUtils.remove(this);
        mCaptureHelper.onDestroy();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCaptureHelper.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * 接收扫码结果回调
     *
     * @param result 扫码结果
     * @return 返回true表示拦截，将不自动执行后续逻辑，为false表示不拦截，默认不拦截
     */
    @Override
    public boolean onResultCallback(String result) {
        if (TextUtils.isEmpty(result)) {
            Toast.makeText(CaptureActivity.this, "扫描失败", Toast.LENGTH_SHORT).show();
        } else {
            if (result != null && result.length() != 0) {
                ScanManager.INSTANCE.scanResult(scanKey, result);
            } else {
                Toast.makeText(CaptureActivity.this, "扫描失败", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PHOTO:
                if (data != null) {
                    parsePhoto(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 解析相册返回的intent
     *
     * @param data
     */
    private void parsePhoto(Intent data) {
        final String path = UriUtils.getImagePath(this, data);
        Log.d("CaptureActivity", "path:" + path);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        //异步解析
        asyncThread(() -> {
            final String result = CodeUtils.parseCode(path);
            runOnUiThread(() -> {
                Log.d("CaptureActivity", "result:" + result);
                if(result != null && result.length() != 0){
                    ScanManager.INSTANCE.scanResult(scanKey, result);
                }
                //Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
            });

        });

    }

    /**
     * 异步处理
     *
     * @param runnable
     */
    private void asyncThread(Runnable runnable) {
        new Thread(runnable).start();
    }


}