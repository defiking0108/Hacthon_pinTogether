package com.topnetwork.base.dialog.base

import android.view.Gravity
import org.topnetwork.pintogether.R

abstract class BaseBottomDialogFragment : BaseDialogFragment() {

    override fun getGravity(): Int = Gravity.BOTTOM

    override fun getDialogAnimationRes(): Int = R.style.DialogAnimBottomToUp

}