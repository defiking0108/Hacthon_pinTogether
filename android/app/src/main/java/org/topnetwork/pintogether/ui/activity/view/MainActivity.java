package org.topnetwork.pintogether.ui.activity.view;

import static com.topnetwork.zxingV2.NavagationKt.startCaptureActivity;
import static org.topnetwork.pintogether.NavigationKt.startCreateNftActivity;
import static org.topnetwork.pintogether.permission.PermissionChecker.REQUEST_CODE_PERMISSION_LOCATION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import org.topnetwork.pintogether.R;
import org.topnetwork.pintogether.base.app.BaseFragment;
import org.topnetwork.pintogether.databinding.ActivityMainBinding;
import org.topnetwork.pintogether.model.CidModel;
import org.topnetwork.pintogether.ui.activity.vm.MainActivityVm;
import org.topnetwork.pintogether.ui.fragment.view.MeFragment;
import org.topnetwork.pintogether.ui.fragment.view.PinFragment;
import org.topnetwork.pintogether.widgets.BottomNavigation;
import org.topnetwork.pintogether.base.app.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainActivityVm> {

    private String scanKey = MainActivity.class.getSimpleName();
    private BottomNavigation mBottomNavigation;

    private List<Class<? extends BaseFragment>> mFragmentClasses = new ArrayList<>();
    private SparseArray<BaseFragment> mFragments = new SparseArray<>();

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Nullable
    @Override
    protected MainActivityVm createViewModel() {
        return new ViewModelProvider(this).get(MainActivityVm.class);
    }

    @Override
    protected void afterOnCreate(@Nullable Bundle savedInstanceState) {
        super.afterOnCreate(savedInstanceState);
        initFragments();
        initBottomView();

        getCvb().ivAdd.setOnClickListener(v -> startCreateNftActivity());

    }

    /**
     * Fragment
     */
    private void initFragments() {
        mFragmentClasses.add(PinFragment.class);
        mFragmentClasses.add(MeFragment.class);
    }

    /**
     * dib导航
     */
    private void initBottomView() {
        mBottomNavigation = findViewById(R.id.navigation_bottom_menu);
        mBottomNavigation.addMenus(getMainMenu());
        mBottomNavigation.setOnItemChooseListener(this::showFragment);
        showFragment(0);
    }

    /**
     * 显示Fragment
     *
     * @param index
     */
    private void showFragment(int index) {
        FragmentManager fm = getSupportFragmentManager();
        BaseFragment fragment = getFragmentByIndex(index);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(fragment == null)return;
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.add(R.id.fragment_container,fragment);
        }

        for (int i = 0; i < fm.getFragments().size(); i++) {
            if (fragment != fm.getFragments().get(i)&& fm.getFragments().get(i).isAdded()) {
                fragmentTransaction.hide(fm.getFragments().get(i));
            }
        }
        fragmentTransaction.commit();
    }

    /**
     * 获取当前Fragment
     *
     * @param index
     * @return
     */
    private BaseFragment getFragmentByIndex(int index) {
        if (mFragments.get(index) != null) {
            return mFragments.get(index);
        }
        try {
            BaseFragment fragment = mFragmentClasses.get(index).newInstance();
            mFragments.put(index, fragment);
            return fragment;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<BottomNavigation.TabMenuItem> getMainMenu() {
        List<BottomNavigation.TabMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new BottomNavigation.TabMenuItem(R.drawable.ic_main_tab_pin_un_select, R.drawable.ic_main_tab_pin_select, getString(R.string.pin)));
        menuItems.add(new BottomNavigation.TabMenuItem(R.drawable.ic_main_tab_me_un_select, R.drawable.ic_main_tab_me_select, getString(R.string.me)));
        return menuItems;
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        super.onPermissionsGranted(requestCode, perms);
        if(requestCode == REQUEST_CODE_PERMISSION_LOCATION){
            PinFragment fragment = (PinFragment) getFragmentByIndex(0);
            fragment.init();
        }else if(requestCode == 100){
            PinFragment fragment = (PinFragment) getFragmentByIndex(0);
            startCaptureActivity(fragment.getScanKey(),100);
        }
    }
}