package org.topnetwork.pintogether.widgets;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.topnetwork.pintogether.R;
import org.topnetwork.pintogether.utils.StringUtils;

public class TabBarItem extends LinearLayout {

    private boolean isChoosed;
    private String mTextContent;
    private int mTextSize;
    private int mTextNomalColor;
    private int mTextCheckColor;
    private Bitmap mNomalIcon;
    private Bitmap mCheckIcon;
    private int mBubbleNum;
    private int mGap;

    private ImageView mBtnImage;
    private TextView mBtnText;
    private TextView mBubble;
    private View mGapView;
    private AnimatorSet mScaleAnim;
    private float mDefaultScalePercent = 1.2f;

    public TabBarItem(Context context) {
        this(context, null);
    }

    public TabBarItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabBarItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTabBarItem(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context
     * @param attrs
     */
    private void initTabBarItem(Context context, AttributeSet attrs) {
        initTabItem(context, attrs);  //获取尺寸
        addChilds(context);              //添加子view
        initScaleAnim(); // 图标缩放动画
        changeCheckedStatus();          //根据状态显示当前view
    }

    /**
     * mBtnImage动画
     */
    private void initScaleAnim() {
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(mBtnImage, "scaleX", 1.0f, mDefaultScalePercent, 1.0f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(mBtnImage, "scaleY", 1.0f, mDefaultScalePercent, 1.0f);
        mScaleAnim = new AnimatorSet();
        mScaleAnim.playTogether(scaleXAnim, scaleYAnim);
        mScaleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mScaleAnim.setDuration(500);
    }

    /**
     * 添加子控件
     *
     * @param context
     */
    private void addChilds(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
        View tabItem = LayoutInflater.from(context).inflate(R.layout.item_tabbar_layout, null);
        mBtnImage = tabItem.findViewById(R.id.item_tabbar_image);
        mBtnText = tabItem.findViewById(R.id.item_tabbar_title);
        mBubble = tabItem.findViewById(R.id.item_tabbar_bubble);
        mGapView = tabItem.findViewById(R.id.item_tabbar_gap);
        ViewGroup.LayoutParams params = mGapView.getLayoutParams();
        params.height = mGap;
        mGapView.setLayoutParams(params);
        setBubbleNum(mBubbleNum);
        addView(tabItem);
    }

    /**
     * 设置菜单图标
     *
     * @param nomalIcon
     * @param pressIcon
     */
    public void setIcon(int nomalIcon, int pressIcon) {
        mNomalIcon = BitmapFactory.decodeResource(getResources(), nomalIcon);
        mCheckIcon = BitmapFactory.decodeResource(getResources(), pressIcon);
    }

    /**
     * 设置气泡
     *
     * @param bubbleNum
     */
    public void setBubbleNum(int bubbleNum) {
        mBubbleNum = bubbleNum;
        if (mBubbleNum > 0) {
            mBubble.setVisibility(View.VISIBLE);
            //mBubble.setText(String.valueOf(mBubbleNum));
        } else {
            mBubble.setVisibility(View.GONE);
        }
    }

    public TextView getBtnText() {
        return mBtnText;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 改变当前显示状态
     */
    public void changeCheckedStatus() {
        if (isChoosed) {
            mBtnImage.setImageBitmap(mCheckIcon);
            mBtnText.setTextColor(mTextCheckColor);
        } else {
            mBtnImage.setImageBitmap(mNomalIcon);
            mBtnText.setTextColor(mTextNomalColor);
        }
        // 缩放
//		if (mScaleAnim != null && isChoosed) {
//			mScaleAnim.start();
//		}
        mBtnText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        if (StringUtils.isEmpty(mTextContent)) {
            mBtnText.setVisibility(View.GONE);
        } else {
            mBtnText.setVisibility(View.VISIBLE);
            mBtnText.setText(mTextContent);
        }
    }

    /**
     * 判断当前是否是选中状态
     *
     * @return
     */
    public boolean getCheckedStatu() {
        return isChoosed;
    }

    /**
     * 设置为选中状态
     */
    public void setCheckedItem() {
        isChoosed = true;
        changeCheckedStatus();
    }

    /**
     * 设置文本
     *
     * @param text
     * @param nomalColor
     * @param chooseColor
     */
    public void setText(String text, int nomalColor, int chooseColor) {
        mTextNomalColor = nomalColor;
        mTextCheckColor = chooseColor;
        mTextContent = text;
    }

    /**
     * 设置文字大小
     *
     * @param size
     */
    public void setTextSize(int size) {
        mTextSize = size;
    }

    /**
     * 设置为未选中状态
     */
    public void setDisCheckedItem() {
        isChoosed = false;
        changeCheckedStatus();
    }

    /**
     * 初始配置的数据
     *
     * @param context
     * @param attrs
     */
    private void initTabItem(Context context, AttributeSet attrs) {
        TypedArray typeArr = context.obtainStyledAttributes(attrs, R.styleable.TabBarItem);
        int count = typeArr.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = typeArr.getIndex(i);

            if (attr == R.styleable.TabBarItem_checked_item) {
                isChoosed = typeArr.getBoolean(attr, false);
            } else if (attr == R.styleable.TabBarItem_text) {
                mTextContent = typeArr.getString(attr);
            } else if (attr == R.styleable.TabBarItem_text_size) {
                mTextSize = (int) typeArr.getDimension(attr, TypedValue.applyDimension
                        (TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
            } else if (attr == R.styleable.TabBarItem_nomal_color) {
                mTextNomalColor = typeArr.getColor(attr, getResources().getColor(R.color.color_707070));
            } else if (attr == R.styleable.TabBarItem_check_color) {
                mTextCheckColor = typeArr.getColor(attr, getResources().getColor(R.color.color_4E76FA));
            } else if (attr == R.styleable.TabBarItem_nomal_icon) {
                mNomalIcon = ((BitmapDrawable) typeArr.getDrawable(attr)).getBitmap();
            } else if (attr == R.styleable.TabBarItem_check_icon) {
                mCheckIcon = ((BitmapDrawable) typeArr.getDrawable(attr)).getBitmap();
            } else if (attr == R.styleable.TabBarItem_bubble_num) {
                mBubbleNum = typeArr.getInteger(attr, 0);
            } else if (attr == R.styleable.TabBarItem_menu_gap) {
                mGap = typeArr.getDimensionPixelOffset(attr, 0);
            }
        }
        typeArr.recycle();
    }
}

