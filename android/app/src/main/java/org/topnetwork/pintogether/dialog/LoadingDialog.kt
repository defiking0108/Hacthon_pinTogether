package org.topnetwork.pintogether.dialog

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.topnetwork.base.dialog.base.BaseCenterDialogFragment
import kotlinx.android.synthetic.main.dialog_loding.*
import org.topnetwork.pintogether.R

class LoadingDialog : BaseCenterDialogFragment() {

    private var canCancel = false

    override fun getDimAmount(): Float = 0f

    override fun getLayoutRes(): Int = R.layout.dialog_loding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        canCancel = arguments?.getBoolean(KEY_CAN_CANCEL, false) ?: false
        super.onViewCreated(view, savedInstanceState)
    }

    override fun bindView() {
        val msg = arguments?.getString(KEY_MSG)
        loading_progress.indeterminateDrawable = resources.getDrawable(R.drawable.loading_anim)
        tv_message.visibility = if (msg.isNullOrEmpty()) View.GONE else View.VISIBLE
        tv_message.text = msg
    }

    override fun dismissAllowingStateLoss() {
        if (loading_progress != null) {
            loading_progress.indeterminateDrawable = resources.getDrawable(R.drawable.ic_loading)
        }
        super.dismissAllowingStateLoss()
    }

    override fun addMoreWindowParam(window: Window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    companion object {
        const val TAG = "LoadingDialog"
        const val KEY_MSG = "key_msg"
        const val KEY_CAN_CANCEL = "key_can_cancel"
    }

    override fun isCancelableOutside(): Boolean = canCancel

    override fun isCanCancelable(): Boolean = canCancel

}