package org.topnetwork.pintogether.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import org.topnetwork.pintogether.R;
import org.topnetwork.pintogether.utils.FastClickYUtil;
import org.topnetwork.pintogether.utils.SizeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BottomNavigation extends LinearLayout implements View.OnClickListener {

    private List<TabBarItem> mTabBars = new ArrayList<>();

    public BottomNavigation(Context context) {
        super(context);
    }

    public BottomNavigation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomNavigation(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BottomNavigation(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onClick(View view) {
        int index = (Integer) view.getTag();
        checked(index);
    }

    public static class TabMenuItem {

        private int nomalIcon;
        private int pressIcon;
        private String title;
        private int bobble;
        private String tag;//菜单标识
        private int index;//菜单index

        public TabMenuItem(int nomalIcon, int pressIcon, String title) {
            this(nomalIcon, pressIcon, title, 0);
        }

        public TabMenuItem(int nomalIcon, int pressIcon, String title, int bobble) {
            this.nomalIcon = nomalIcon;
            this.pressIcon = pressIcon;
            this.title = title;
            this.bobble = bobble;
        }

        public int getNomalIcon() {
            return nomalIcon;
        }

        public void setNomalIcon(int nomalIcon) {
            this.nomalIcon = nomalIcon;
        }

        public int getPressIcon() {
            return pressIcon;
        }

        public void setPressIcon(int pressIcon) {
            this.pressIcon = pressIcon;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getBobble() {
            return bobble;
        }

        public void setBobble(int bobble) {
            this.bobble = bobble;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public Map<String, Integer> addMenus(List<TabMenuItem> menus) {
        return addMenus(menus, SizeUtils.sp2px(10),
                getResources().getColor(R.color.color_707070),
                getResources().getColor(R.color.color_4E76FA));
    }

    public Map<String, Integer> addMenus(List<TabMenuItem> menus, int textSize, int nomalColor, int chooseColor) {
        HashMap<String, Integer> menuIndexMap = new HashMap<>();

        LayoutParams params = new LayoutParams(
                0, LayoutParams.MATCH_PARENT, 1);
        for (int i = 0; i < menus.size(); i++) {
            TabMenuItem menu = menus.get(i);
            TabBarItem tabBarItem = new TabBarItem(getContext());
            tabBarItem.setBubbleNum(menu.getBobble());
            tabBarItem.setIcon(menu.getNomalIcon(), menu.getPressIcon());
            tabBarItem.setTextSize(textSize);
            tabBarItem.setText(menu.getTitle(), nomalColor, chooseColor);
            tabBarItem.setTag(i);
            tabBarItem.setOnClickListener(this);
            mTabBars.add(tabBarItem);
            addView(tabBarItem, params);
            menuIndexMap.put(menu.getTag(), menu.getIndex());
        }
        checked(0);

        return menuIndexMap;
    }

    public void checked(int index) {
        if(FastClickYUtil.isFastCheckClick()){
            return;
        }
        for (int i = 0; i < mTabBars.size(); i++) {
            TabBarItem tabBarItem = mTabBars.get(i);
            if (i == index) {
                tabBarItem.setCheckedItem();
            } else {
                tabBarItem.setDisCheckedItem();
            }
            tabBarItem.changeCheckedStatus();
        }
        if (mListener != null) {
            mListener.chooseItem(index);
        }

    }

    /**
     * 设置气泡
     *
     * @param index
     * @param bobble
     */
    public void setBobble(int index, int bobble) {
        if (mTabBars.size() <= index) return;
        mTabBars.get(index).setBubbleNum(bobble);
    }

    private OnItemChooseListener mListener;

    public void setOnItemChooseListener(OnItemChooseListener lisetenr) {
        mListener = lisetenr;
    }

    public interface OnItemChooseListener {
        void chooseItem(int index);
    }

}
