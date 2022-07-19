package org.topnetwork.pintogether.base.app

import androidx.lifecycle.MutableLiveData

open class ToolbarBaseActivityVm : BaseActivityVM(){
    private val TAG = "ToolbarBaseActivityVm"

    var setToolBarTitleLiveData = MutableLiveData<String>()  //标题
    var setToolBarTitleIconLiveData  = MutableLiveData<String>() //标题icon

}