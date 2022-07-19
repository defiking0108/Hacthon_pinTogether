package org.topnetwork.pintogether.base.delegate;

import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * Created by lgc on 2019/6/10.
 */
public interface FragmentDelegate extends BaseDelegate {
    @Override
    void onCreate(@Nullable Bundle savedInstanceState);

    @Override
    void onStart();

    @Override
    void onResume();

    @Override
    void onPause();

    @Override
    void onStop();

    @Override
    void onDestroy();
}
