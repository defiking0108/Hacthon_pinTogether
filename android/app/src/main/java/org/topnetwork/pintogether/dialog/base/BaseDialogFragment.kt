package com.topnetwork.base.dialog.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import org.topnetwork.pintogether.R
import org.topnetwork.pintogether.dialog.base.NormalBaseDialogFragment

abstract class BaseDialogFragment : NormalBaseDialogFragment() {

    companion object {
        const val TAG: String = "Dialog"
        const val DEFAULT_DIM_AMOUNT: Float = 0.5F

        @JvmStatic
        fun <T : BaseDialogFragment> show(fm: FragmentManager, clazz: Class<T>): T {
            return show(fm, clazz, null)
        }

        @JvmStatic
        fun <T : BaseDialogFragment> show(fm: FragmentManager, clazz: Class<T>, args: Bundle?): T {
            val fragment = clazz.newInstance() as T
            if (args != null) {
                fragment.arguments = args
            }
            fragment.show(fm, clazz.simpleName)
            return fragment
        }

    }

    lateinit var rootView: View

    /**
     * 布局资源
     */
    abstract fun getLayoutRes(): Int

    /**
     * 绑定View初始化数据
     */
    abstract fun bindView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return when {
            getLayoutRes() > 0 -> inflater.inflate(getLayoutRes(), container, false)
            else -> View(context)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //去除Dialog默认头部
        dialog?.run {
            //requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCanceledOnTouchOutside(isCancelableOutside())
        }
        rootView = view
        bindView()
    }

    protected fun <T : View> findViewById(@IdRes id: Int): T {
        return rootView.findViewById(id)
    }

    override fun onStart() {
        super.onStart()
        setDialogStyle()
    }

    /**
     * 设置弹窗样式
     */
    open fun setDialogStyle() {
        dialog?.window?.run {
//            clearFlags()
            addMoreWindowParam(this)
            isCancelable = isCanCancelable()
            //设置窗体背景色透明
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            if (getDialogAnimationRes() > 0) {
                setWindowAnimations(getDialogAnimationRes())
            }
            attributes = attributes.apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                dimAmount = getDimAmount()
                gravity = getGravity()
            }
        }
    }

    open fun addMoreWindowParam(window: Window){

    }

    /**
     * 点击外部是否取消弹窗
     */
    open fun isCancelableOutside(): Boolean = true

    /**
     * 是否能取消
     */
    open fun isCanCancelable(): Boolean = true

    /**
     * 弹窗动画
     */
    open fun getDialogAnimationRes(): Int = 0

    /**
     * 弹窗透明度
     */
    open fun getDimAmount(): Float = DEFAULT_DIM_AMOUNT

    /**
     * 弹窗显示位置
     */
    open fun getGravity(): Int = Gravity.CENTER

    /**
     * 确定回调
     */
    var mConfirmListener: ((T: Any?) -> Unit)? = null

    /**
     * 取消回调
     */
    var mCancelListener: (() -> Unit)? = null

    /**
     * 设置确定回调
     */
    fun setConfirmListener(block: (T: Any?) -> Unit) {
        mConfirmListener = block
    }

    /**
     * 设置取消回调
     */
    fun setCancelListener(block: () -> Unit) {
        mCancelListener = block
    }

    override fun dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss()
        Handler(Looper.getMainLooper()).apply {
            val msg = obtainMessage()
            msg.obj = null
            post { msg }
        }
    }

}