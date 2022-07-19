package org.topnetwork.pintogether.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;


import org.topnetwork.pintogether.R;
import org.topnetwork.pintogether.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StateLayout extends FrameLayout implements View.OnClickListener {

    private static final String LOG_TAG = StateLayout.class.getSimpleName();

    private final String CONTENT = "type_content";
    private final String LOADING = "type_loading";
    private final String EMPTY = "type_empty";
    private final String ERROR = "type_error";
    private final String NO_NET = "type_no_net";

    private View loadingView;
    private View emptyView;
    private View errorView;
    private View noNetView;
    private List<View> contentViews = new ArrayList<>();
    private OnRetryClickListener onRetryClickListener;

    private int emptyImgId;
    private String emptyDesc;
    private int errorImdId;
    private String errorDesc;

    private int noNetImdId;
    private String noNetDesc;
    private String state = CONTENT;

    public StateLayout(Context context) {
        this(context, null);
    }

    public StateLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.layout_state, this);
    }

    @Override
    public void addView(@NonNull View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if ((child.getId() != R.id.id_stub_error && child.getId() != R.id.id_stub_empty && child.getId() != R.id.id_stub_loading && child.getId() != R.id.id_stub_no_net)) {
            if (child.getId() == R.id.id_layout_error) {
                return;
            }
            if (child.getId() == R.id.id_layout_empty) {
                return;
            }
            if (child.getId() == R.id.id_layout_loading) {
                return;
            }
            if (child.getId() == R.id.id_layout_no_net) {
                return;
            }
            contentViews.add(child);
        }
    }

    public void showContent() {
        switchState(CONTENT, Collections.<Integer>emptyList());
    }

    public void showContent(List<Integer> skipIds) {
        switchState(CONTENT, skipIds);
    }

    public void showLoading() {
        switchState(LOADING, Collections.<Integer>emptyList());
    }

    public void showLoading(List<Integer> skipIds) {
        switchState(LOADING, skipIds);
    }

    public void showEmpty() {
        switchState(EMPTY, Collections.<Integer>emptyList());
    }

    public void showEmpty(List<Integer> skipIds) {
        switchState(EMPTY, skipIds);
    }

    public void showError() {
        switchState(ERROR, Collections.<Integer>emptyList());
    }

    public void showError(List<Integer> skipIds) {
        switchState(ERROR, skipIds);
    }

    public void showNoNet() {
        switchState(NO_NET, Collections.<Integer>emptyList());
    }

    public void showNoNet(List<Integer> skipIds) {
        switchState(NO_NET, skipIds);
    }

    public String getState() {
        return state;
    }

    public boolean isContent() {
        return state.equals(CONTENT);
    }

    public boolean isLoading() {
        return state.equals(LOADING);
    }

    public boolean isEmpty() {
        return state.equals(EMPTY);
    }

    public boolean isError() {
        return state.equals(ERROR);
    }

    private void switchState(String state, List<Integer> skipIds) {
        this.state = state;

        switch (state) {
            case CONTENT:
                hideLoadingView();
                hideEmptyView();
                hideErrorView();
                hideNoNetView();
                setContentVisibility(true, skipIds);
                break;

            case LOADING:
                hideEmptyView();
                hideErrorView();
                hideNoNetView();

                enSureLoading();
                loadingView.setVisibility(VISIBLE);
                setContentVisibility(false, skipIds);
                break;

            case EMPTY:
                hideLoadingView();
                hideErrorView();
                hideNoNetView();

                enSureEmpty();
                emptyView.setVisibility(VISIBLE);
                setContentVisibility(false, skipIds);
                break;

            case ERROR:
                hideLoadingView();
                hideEmptyView();
                hideNoNetView();

                enSureError();
                errorView.setVisibility(VISIBLE);
                setContentVisibility(false, skipIds);
                break;
            case NO_NET:
                hideLoadingView();
                hideEmptyView();
                hideErrorView();

                enSureNoNet();
                noNetView.setVisibility(VISIBLE);
                setContentVisibility(false, skipIds);
                break;

        }
    }

    private void enSureLoading() {
        if (loadingView == null) {
            loadingView = ((ViewStub) findViewById(R.id.id_stub_loading)).inflate();

        }
    }

    private void enSureEmpty() {
        if (emptyView == null) {
            emptyView = ((ViewStub) findViewById(R.id.id_stub_empty)).inflate();
            if (emptyImgId > 0) {
                ((ImageView) findViewById(R.id.empty_img)).setImageResource(emptyImgId);
            }
            if (!StringUtils.isEmpty(emptyDesc)) {
                ((TextView) findViewById(R.id.empty_desc)).setText(emptyDesc);
            }
        } else {
            if (emptyView.getParent() == null) {
                LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                lp.gravity = Gravity.CENTER;
                addView(emptyView, lp);
            }
        }
    }

    private void enSureError() {
        if (errorView == null) {
            errorView = ((ViewStub) findViewById(R.id.id_stub_error)).inflate();
            if (errorImdId > 0) {
                ((ImageView) errorView.findViewById(R.id.error_image)).setImageResource(errorImdId);
            }
            if (!StringUtils.isEmpty(errorDesc)) {
                ((TextView) errorView.findViewById(R.id.error_tv)).setText(errorDesc);
            }
            if (onRetryClickListener != null) {
                View retry = errorView.findViewById(R.id.error_retry);
                retry.setOnClickListener(this);
            }
        }
    }

    private void enSureNoNet() {
        if (noNetView == null) {
            noNetView = ((ViewStub) findViewById(R.id.id_stub_no_net)).inflate();
            if (noNetImdId > 0) {
                ((ImageView) findViewById(R.id.no_net_img)).setImageResource(noNetImdId);
            }
            if (!StringUtils.isEmpty(noNetDesc)) {
                ((TextView) findViewById(R.id.no_net_desc)).setText(noNetDesc);
            }
        } else {
            if (noNetView.getParent() == null) {
                LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                lp.gravity = Gravity.CENTER;
                addView(noNetView, lp);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.error_retry) {
            if (onRetryClickListener != null) {
                onRetryClickListener.onRetryClick();
            }
        }
    }

    private void setContentVisibility(boolean visible, List<Integer> skipIds) {
        for (View v : contentViews) {
            if (!skipIds.contains(v.getId())) {
                v.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void hideLoadingView() {
        if (loadingView != null) {
            loadingView.setVisibility(GONE);
        }
    }

    private void hideEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(GONE);
        }
    }

    private void hideErrorView() {
        if (errorView != null) {
            errorView.setVisibility(GONE);
        }
    }

    private void hideNoNetView() {
        if (noNetView != null) {
            noNetView.setVisibility(GONE);
        }
    }

    public void setEmptyImgId(int imgId) {
        emptyImgId = imgId;
    }

    public void setEmptyDesc(String desc) {
        emptyDesc = desc;
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    public void setErrorImdId(int imgId) {
        errorImdId = imgId;
    }

    public void setErrorDesc(String desc) {
        errorDesc = desc;
    }

    public void setOnRetryClickListener(OnRetryClickListener listener) {
        onRetryClickListener = listener;
    }

    public interface OnRetryClickListener {
        void onRetryClick();
    }
}


