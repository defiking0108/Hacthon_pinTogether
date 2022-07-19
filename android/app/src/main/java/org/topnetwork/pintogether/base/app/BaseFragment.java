package org.topnetwork.pintogether.base.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import org.topnetwork.pintogether.BR;
import org.topnetwork.pintogether.utils.ProgressHUD;
import org.topnetwork.pintogether.utils.ToastUtils;
public abstract class BaseFragment<CVB extends ViewDataBinding, VM extends BaseFragmentVM> extends Fragment {

    protected String tag = getClass().getSimpleName();
    protected View rootView;

    /**
     * 具体的子类Fragment CVB用具体的代替
     */
    protected CVB cvb;

    /**
     * 具体的子类Fragment VM用具体的代替
     */
    protected VM viewModel;

    private ProgressHUD progressHUD;

    protected abstract int getLayoutResId();

    protected abstract VM createViewModel();

    protected abstract void afterViewCreated();

    public <T> T findViewById(@IdRes int id) {
        return (T) rootView.findViewById(id);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutResId = getLayoutResId();
        if (layoutResId > 0) {
            cvb = DataBindingUtil.inflate(inflater, layoutResId, container, false);
            rootView = cvb.getRoot();
            viewModel = createViewModel();
        } else {
            throw new IllegalArgumentException("layout is not a inflate");
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cvb.setVariable(BR.viewModel, viewModel);
        afterViewCreated();
        if (viewModel != null) {
            initLiveData();
            viewModel.afterOnCreate();
        }
    }

    /**
     * 初始化liveData
     */
    private void initLiveData() {
        viewModel.showToastLiveData.observe(getViewLifecycleOwner(), (Observer<String>) ToastUtils::showShort);
        viewModel.showProgressHUDField.observe(getViewLifecycleOwner(), (Observer<Boolean>) bool -> {
            if (bool) {
                showLoading("");
            } else {
                dismissLoading();
            }
        });
    }

    /**
     * 取消加载谈弹窗
     */
    protected void dismissLoading() {
        if (progressHUD != null) {
            progressHUD.dismiss();
        }
    }

    /**
     * 显示加载弹窗
     */
    protected void showLoading(String msg) {
        if (getActivity() != null) {
            progressHUD = ProgressHUD.show(getActivity(), msg,true);
        }

    }

}
