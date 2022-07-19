package org.topnetwork.pintogether.base.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlinx.android.synthetic.main.view_defaule_toolbar.view.*
import org.topnetwork.pintogether.R
import org.topnetwork.pintogether.databinding.ActivityToolbarBaseBinding
import org.topnetwork.pintogether.utils.BarUtils
import org.topnetwork.pintogether.utils.StringUtils
import org.topnetwork.pintogether.extension.loadImage
abstract class ToolbarBaseActivity<CVB : ViewDataBinding, VM : ToolbarBaseActivityVm> : BaseActivity<CVB, VM>() {

    private var activityToolbarBaseBinding: ActivityToolbarBaseBinding? = null
    protected var content: LinearLayout? = null

    //toolbar相关view
    private var mIvBack: ImageView? = null //返回
    private var mIvClose: ImageView? = null //关闭
    private var mTvTitle: TextView? = null  //标题
    private var mIvTitle: ImageView? = null //标题图标
    var mTvRight: TextView? = null //右文本
    private var mIvMore: ImageView? = null//右icon

    protected var mToolbarLayout:View ?= null

    override fun afterOnCreate(savedInstanceState: Bundle?) {
        super.afterOnCreate(savedInstanceState)
        viewModel?.run {
            setToolBarTitleLiveData.observe(this@ToolbarBaseActivity,
                { title: String? -> setToolbarTitle(title) })
            setToolBarTitleIconLiveData.observe(this@ToolbarBaseActivity,
                { url -> setToolbarTitleIcon(url) })

        }
    }

    /**
     * 初始化view
     */
    override fun initView(savedInstanceState: Bundle?) {
        activityToolbarBaseBinding = DataBindingUtil.setContentView(this, R.layout.activity_toolbar_base)
        content = activityToolbarBaseBinding?.content
        cvb = DataBindingUtil.inflate(LayoutInflater.from(this), layoutResId, content, true)
        content?.run {
            stateLayout = findViewById(R.id.id_state_layout)
            removeAllViews()
            addDefaultToolbar(this)
            addView(cvb?.root)
            setToolbarListener()
            setSubclassDataBinding(savedInstanceState)

        }

    }
    /**
     * 是否要添加默认toolbar
     */
    @SuppressLint("InflateParams")
    private fun addDefaultToolbar(content: ViewGroup) {
        cvb?.run {
            if (root.findViewById<TextView>(R.id.id_toolbar_tv_title) == null) {
                val toolbarLayout = LayoutInflater.from(this@ToolbarBaseActivity)
                    .inflate(R.layout.view_defaule_toolbar, null, false)

                if(!isHaveToolbarTopMargin()){
                    val rlBar = toolbarLayout.findViewById<RelativeLayout>(R.id.rl_bar)
                    val layoutParams:RelativeLayout.LayoutParams = rlBar.layoutParams as RelativeLayout.LayoutParams
                    layoutParams.setMargins(0, 0, 0, 0)
                }

                if(isHaveToolbarTopMargin()){
                    val stateBarHeight = BarUtils.getStatusBarHeight()
                    if(stateBarHeight > 0 ){
                        val params:RelativeLayout.LayoutParams = toolbarLayout.rl_bar.layoutParams as RelativeLayout.LayoutParams
                        params.setMargins(0, stateBarHeight.toInt(),0,0)
                    }
                }
                mToolbarLayout = toolbarLayout
                content.addView(toolbarLayout)
            }
        }

    }

    protected open fun isHaveToolbarTopMargin(): Boolean {
        return true
    }

    /**
     * 设置toolbar监听事件
     */
    private fun setToolbarListener() {
        mIvBack = findViewById(R.id.id_toolbar_iv_back)
        mIvClose = findViewById(R.id.id_toolbar_iv_close)
        mTvTitle = findViewById(R.id.id_toolbar_tv_title)
        mIvTitle = findViewById(R.id.id_toolbar_iv_title_icon)
        mIvMore = findViewById(R.id.id_toolbar_iv_more)
        mTvRight = findViewById(R.id.id_toolbar_tv_right)

        if (showBack()) {
            mIvBack?.run { this.visibility = View.VISIBLE;setImageResource(backViewIcon);setOnClickListener { onBackPressed() } }
        }
    }


    /**
     * 设置标题
     *
     * @param resId
     */
    protected fun setToolbarTitle(resId: Int) {
        if (resId <= 0) return
        mTvTitle?.text = (getString(resId))
    }

    /**
     * 设置标题
     *
     * @param title
     */
    protected fun setToolbarTitle(title: String?) {
        mTvTitle?.text = title
    }

    /**
     * 设置标题
     *
     * @param iconUrl
     */
    protected fun setToolbarTitleIcon(iconUrl: String?) {
        if (StringUtils.isEmpty(iconUrl)) return
        mIvTitle?.run {
            visibility = View.VISIBLE
            iconUrl?.run { loadImage(this) }

        }

    }

    /**
     * 设置更多按钮文案
     *
     * @param resId
     */
    protected fun setMoreText(resId: Int) {
        if (resId <= 0) return
        setMoreText(getString(resId))
    }

    /**
     * 设置更多按钮文案
     *
     * @param text
     */
    protected fun setMoreText(text: String?) {
        mTvRight?.run {
            visibility = View.VISIBLE
            this.text = text
            setOnClickListener { onMorePressed() }
        }
    }

    /**
     * 设置更多按钮图标
     */
    protected fun setMoreIcon() {
        setMoreIcon(R.drawable.ic_toolbar_more)
    }

    /**
     * 设置更多按钮图标
     *
     * @param resId
     */
    protected fun setMoreIcon(resId: Int) {
        mIvMore?.run {
            visibility = View.VISIBLE
            setImageResource(resId)
            setOnClickListener { onMorePressed() }
        }
    }


    /**
     * 更多按钮事件
     */
    protected open fun onMorePressed() {}

    /**
     * 消息点击事件
     */
    protected fun onMsgPressed() {}

    /**
     * 关闭按钮事件
     */
    protected open fun onClosePressed() {
        finish()
    }

    /**
     * 显示返回按钮 默认显示
     *
     * @return
     */
    protected fun showBack(): Boolean {
        return true
    }

    /**
     * 返回按钮图标
     *
     * @return
     */
    protected val backViewIcon: Int
        protected get() = R.drawable.ic_toolbar_back

    /**
     * 显示关闭按钮 默认不显示
     *
     * @return
     */
    protected fun showClose() {
        mIvClose?.run {
            visibility = View.VISIBLE
            setImageResource(closeViewIcon)
            setOnClickListener { onClosePressed() }
        }
    }

    /**
     * 隐藏关闭按钮
     */
    protected fun hideClose() {
        mIvClose?.visibility = View.INVISIBLE
    }

    /**
     * 关闭按钮图标
     *
     * @return
     */
    protected val closeViewIcon: Int
        get() = R.drawable.ic_toolbar_close


}