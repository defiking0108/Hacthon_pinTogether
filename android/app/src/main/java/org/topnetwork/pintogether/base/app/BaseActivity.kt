package org.topnetwork.pintogether.base.app

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import org.topnetwork.pintogether.R
import org.topnetwork.pintogether.BR
import org.topnetwork.pintogether.base.app.BaseActivityVM
import org.topnetwork.pintogether.base.delegate.BaseActivityDelegate
import org.topnetwork.pintogether.permission.PermissionChecker
import org.topnetwork.pintogether.utils.*
import org.topnetwork.pintogether.utils.UIUtils.setTransparentStatusBar
import org.topnetwork.pintogether.widgets.StateLayout

abstract class BaseActivity<CVB : ViewDataBinding, VM : BaseActivityVM>() : AppCompatActivity(), PermissionChecker.PermissionCallbacks {
    private var TAG = "BaseActivity"

    var cvb: CVB? = null

    var viewModel: VM? = null

    var stateLayout: StateLayout? = null

    private var emptyView: View? = null
    private var progressHUD: ProgressHUD? = null

    private val delegate: BaseActivityDelegate? //Activity生命周期代理类

    protected abstract val layoutResId: Int

    protected abstract fun createViewModel(): VM?

    init {
        delegate = createActivityDelegate()
    }

    /**
     * todo 可以是一个delegate list
     * 创建Activity生命周期代理类
     *
     * @return
     */
    private fun createActivityDelegate(): BaseActivityDelegate {
        return BaseActivityDelegate(this)
    }

    /**
     * 初始化
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        beforeOnCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
        initView(savedInstanceState)
    }

    /**
     * 初始化
     * @param savedInstanceState
     */
    protected open fun beforeOnCreate(savedInstanceState: Bundle?) {
       setTransparentStatusBar(this)
    }

    /**
     * 初始化liveData
     */
    protected fun initLiveData() {
        viewModel?.run {
            showProgressHUDField.observe(this@BaseActivity, (Observer { isShow: Boolean ->
                if (isShow) {
                    showLoading()
                } else {
                    dismissLoading()
                }
            } as Observer<Boolean>))
            showCanCancelLoadingField.observe(this@BaseActivity, Observer {
                showCancelLoading(it)
            })
            finishCurrentActivityLiveData.observe(this@BaseActivity, Observer { ActivityUtils.pop() })
            showToastMsgLiveData.observe(this@BaseActivity, Observer { msg: String? -> ToastUtils.showShort(msg) })
            showViewStatusField.observe(this@BaseActivity, Observer {
                stateLayout?.run {
                    when (it) {
                        1 -> showNoNet()
                        2 -> showError()
                        3 -> showEmpty()
                        else -> showContent()
                    }
                }

            })
        }
    }

    /**
     * 初始化
     * @param savedInstanceState
     */
    protected open fun afterOnCreate(savedInstanceState: Bundle?) {

    }


    override fun onStart() {
        super.onStart()
        viewModel?.onStart()
    }


    override fun onResume() {
        super.onResume()
        viewModel?.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel?.onPause()
    }

    override fun onStop() {
        viewModel?.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        viewModel?.onDestroy()
        dismissLoading()
        delegate?.onDestroy()
        super.onDestroy()
    }

    /**
     * 初始化子View
     * @param savedInstanceState
     */
    protected open fun initView(savedInstanceState: Bundle?) {
        cvb = DataBindingUtil.setContentView(this, layoutResId)
        stateLayout = findViewById(R.id.id_state_layout)
        setSubclassDataBinding(savedInstanceState)

    }

    /**
     * 设置子View DataBing
     */
    protected fun setSubclassDataBinding(savedInstanceState: Bundle?) {
        cvb?.run {
            viewModel = createViewModel()
            setVariable(BR.viewModel, viewModel)
            lifecycleOwner = this@BaseActivity
            delegate?.onCreate(savedInstanceState)
            initLiveData()
            afterOnCreate(savedInstanceState)
            viewModel?.afterOnCreate()
        }
    }

    /**
     * 取消加载谈弹窗
     */
    protected open fun dismissLoading() {
        progressHUD?.dismiss()
        progressHUD = null
    }

    /**
     * 显示加载弹窗
     */
    protected open fun showLoading(msg: String = "") {
        if (progressHUD == null) {
            progressHUD = ProgressHUD.show(this, msg)
        }

    }

    /**
     * 显示加载弹窗
     */
    protected open fun showCancelLoading(msg: String = "") {
        if (progressHUD == null) {
            progressHUD = ProgressHUD.show(this, msg, true)
        }

    }

    /**
     * 显示加载弹窗
     */
    protected open fun showTopCancelLoading(msg: String = "") {
        if (progressHUD == null) {
            progressHUD = ProgressHUD.show(ActivityUtils.peek(), msg, true)
        }

    }

    /**
     * @param emptyDesc 展示empty
     */
    protected open fun showEmptyView(emptyDesc: String?) {
        if (emptyView != null) {
            emptyView!!.visibility = View.VISIBLE
            return
        }
        val viewStub: ViewStub = findViewById(R.id.id_stub_include_empty)
        emptyView = viewStub.inflate()
        if (!StringUtils.isEmpty(emptyDesc)) {
            val noContentText: TextView = emptyView!!.findViewById(R.id.id_stub_include_empty_desc)
            noContentText.text = emptyDesc
        }
    }

    /**
     * 隐藏empty
     */
    protected open fun hideEmptyView() {
        emptyView?.visibility = View.GONE
    }


    /**
     * 设置StateLayout - 空界面
     */
    protected fun setStateLayoutEmptyView(msg: String?, resId: Int?) {
        msg?.run {
            stateLayout?.setEmptyDesc(msg)
        }
        resId?.run {
            stateLayout?.setEmptyImgId(resId)
        }
    }

    /**
     * 退出应用时间过长
     *
     * @return
     */
    protected open fun shouldTimeOut(): Boolean {
        return true
    }

    /**
     * 处理动态请求权限的结果
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        delegate?.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //请求权限成功回调
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {}

    //请求权限失败回调
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        LogUtils.dTag(TAG, "onPermissionsDenied == $requestCode, denied permissions == $perms")
//        if (PermissionChecker.somePermissionPermanentlyDenied(this, perms)) {
//            LogUtils.dTag(TAG, "somePermissionPermanentlyDenied == $requestCode, denied permissions == $perms")
//
//            // 永久拒绝权限引导设置页
//            val builder = StringBuilder()
//            var permissionName: String?
//            if (perms.isNotEmpty()) {
//                for (perm in perms) {
//                    permissionName = PermissionChecker.getPermissionName(perm)
//                    if (StringUtils.isNotEmpty(permissionName)) {
//                        builder.append(permissionName)
//                        builder.append("，")
//                    }
//                }
//            }
//            var tip = builder.toString()
//            tip = tip.substring(0, tip.length - 1)
//            val prompt: String
//            prompt = getString(R.string.app_name) + getString(R.string.permission_permanently_denied_setting, tip)
//            CommonDialog.getInstance()
//                .setTitle(prompt)
//                .setConfirm(getString(R.string.str_confirm))
//                .setCancel(getString(R.string.str_cancel))
//                .setConfirmClickListener {
//                    AppUtils.startAppSettings(this@BaseActivity, Constants.REQUEST_CODE_PERMISSION_SETTING)
//                    null
//                }
//                .show(supportFragmentManager)
//        }
    }

    companion object {
        private const val INTERVAL_TIME = 30000
    }

}