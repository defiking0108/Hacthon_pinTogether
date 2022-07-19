package org.topnetwork.pintogether.base.delegate;

import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * Created by lgc on 2019/6/10.
 */
public interface BaseDelegate {

    void onCreate(@Nullable Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}
