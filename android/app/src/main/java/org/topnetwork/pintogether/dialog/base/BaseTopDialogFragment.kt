package com.topnetwork.base.dialog.base

import android.view.Gravity
import org.topnetwork.pintogether.R

abstract class BaseTopDialogFragment : BaseDialogFragment() {

    override fun getGravity(): Int = Gravity.TOP

    override fun getDialogAnimationRes(): Int = R.style.DialogAnimTopToDown

}