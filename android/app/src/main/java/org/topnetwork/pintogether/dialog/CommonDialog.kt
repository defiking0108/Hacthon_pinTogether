package org.topnetwork.pintogether.dialog

import android.view.View
import androidx.fragment.app.FragmentManager
import com.topnetwork.base.dialog.base.BaseCenterDialogFragment
import kotlinx.android.synthetic.main.dialog_common.*
import org.topnetwork.pintogether.R

class CommonDialog : BaseCenterDialogFragment() {

    private var titleStr: String? = ""
    private var contentStr: String? = ""
    private var confirmStr: String? = ""
    private var cancelStr: String? = ""
    private var canCancel = true
    private var isCancelableOnClickConfirm = true
    private var dimAmount = DEFAULT_DIM_AMOUNT

    override fun getLayoutRes(): Int = R.layout.dialog_common

    override fun getDimAmount(): Float = dimAmount

    override fun bindView() {
        tv_title.run {
            if (titleStr != null && titleStr!!.isNotBlank()) {
                visibility = View.VISIBLE
                text = titleStr
            }
        }

        tv_content.run {
            if (contentStr != null && contentStr!!.isNotBlank()) {
                visibility = View.VISIBLE
                text = contentStr
            }
        }

        tv_cancel.run {
            if (cancelStr != null && cancelStr!!.isNotBlank()) {
                visibility = View.VISIBLE
                text = cancelStr
                line.visibility = View.VISIBLE
                setOnClickListener {
                    mCancelListener?.invoke()
                    dismiss()
                }
            }
        }
        tv_confirm.run {
            text = confirmStr
            setOnClickListener {
                mConfirmListener?.invoke(null)
                if (isCancelableOnClickConfirm) {
                    dismiss()
                }
            }
        }
    }

    override fun isCanCancelable(): Boolean = canCancel

    override fun isCancelableOutside(): Boolean = canCancel

    class Builder {

        private var titleStr: String? = ""
        private var contentStr: String? = ""
        private var confirmStr: String? = ""
        private var cancelStr: String? = ""
        private var canCancel = true
        private var isCancelableOnClickConfirm = true
        private var dimAmount = DEFAULT_DIM_AMOUNT

        /**
         * 确定回调
         */
        private var mConfirmListener: ((T: Any?) -> Unit)? = null

        /**
         * 取消回调
         */
        private var mCancelListener: (() -> Unit)? = null

        /**
         * 设置是否能取消
         */
        fun setCanCancel(canCancel: Boolean): Builder {
            this.canCancel = canCancel
            return this
        }

        /**
         * 设置标题
         */
        fun setTitle(titleStr: String?): Builder {
            this.titleStr = titleStr
            return this
        }

        /**
         * 设置内容
         */
        fun setContent(contentStr: String?): Builder {
            this.contentStr = contentStr
            return this
        }

        /**
         * 设置确定文案
         */
        fun setConfirm(confirmStr: String?): Builder {
            this.confirmStr = confirmStr
            return this
        }

        /**
         * 设置取消文案
         */
        fun setCancel(cancelStr: String?): Builder {
            this.cancelStr = cancelStr
            return this
        }

        /**
         * 确定按钮事件
         */
        fun setConfirmClickListener(block: (T: Any?) -> Unit): Builder {
            this.mConfirmListener = block
            return this
        }

        /**
         * 取消按钮事件
         */
        fun setCancelClickListener(block: () -> Unit): Builder {
            this.mCancelListener = block
            return this
        }

        /**
         * 取消按钮事件
         */
        fun setCancelableOnClickConfirm(cancel: Boolean): Builder {
            this.isCancelableOnClickConfirm = cancel
            return this
        }

        /**
         * 设置阴影
         */
        fun setDimAmount(dimAmount: Float): Builder {
            this.dimAmount = dimAmount
            return this
        }

//        fun build(): CommonDialog {
//            return CommonDialog().apply {
//                canCancel = this@Builder.canCancel
//                titleStr = this@Builder.titleStr
//                contentStr = this@Builder.contentStr
//                confirmStr = this@Builder.confirmStr
//                cancelStr = this@Builder.cancelStr
//                mConfirmListener = this@Builder.mConfirmListener
//                mCancelListener = this@Builder.mCancelListener
//            }
//        }

        fun show(fm: FragmentManager) {
            CommonDialog().apply {
                canCancel = this@Builder.canCancel
                isCancelableOnClickConfirm = this@Builder.isCancelableOnClickConfirm
                titleStr = this@Builder.titleStr
                contentStr = this@Builder.contentStr
                confirmStr = this@Builder.confirmStr
                cancelStr = this@Builder.cancelStr
                mConfirmListener = this@Builder.mConfirmListener
                mCancelListener = this@Builder.mCancelListener
                dimAmount = this@Builder.dimAmount
            }
                .show(fm, "CommonDialog")
        }

    }

    companion object {
        @JvmStatic
        fun getInstance(): Builder = Builder()
    }

}