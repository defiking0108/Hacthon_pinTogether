package com.topnetwork.base.dialog.base

import android.view.Gravity
import org.topnetwork.pintogether.R

abstract class BaseCenterDialogFragment : BaseDialogFragment() {

    override fun getGravity(): Int = Gravity.CENTER

    override fun getDialogAnimationRes(): Int = R.style.DialogAnimScale

}