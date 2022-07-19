package org.topnetwork.pintogether.base.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import org.topnetwork.pintogether.base.app.BaseFragment
import org.topnetwork.pintogether.base.app.BaseFragmentVM
import org.topnetwork.pintogether.utils.LogUtils
abstract class BaseLazyLoadFragment<CVB : ViewDataBinding, VM : BaseFragmentVM> : BaseFragment<CVB, VM>() {
    protected var LogTag = javaClass.simpleName

    /**
     * 当前Fragment是否首次可见，默认是首次可见
     */
    private var mIsFirstVisible = true
    /**
     * 当前Fragment的View是否已经创建
     */
    private var isViewCreated = false
    /**
     * 当前Fragment的可见状态，一种当前可见，一种当前不可见
     */
    private var currentVisibleState = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isViewCreated = true
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        LogUtils.e(javaClass.simpleName, "-----> onStart")
        //isHidden()是Fragment是否处于隐藏状态和isVisible()有区别
        //getUserVisibleHint(),Fragement是否可见
        if (!isHidden && userVisibleHint) {//如果Fragment没有隐藏且可见
            //执行分发的方法,三种结果对应自Fragment的三个回调，对应的操作，Fragment首次加载，可见，不可见
            disPatchFragment(true)
        }

    }


    override fun onResume() {
        super.onResume()
        LogUtils.e(javaClass.simpleName, "-----> onResume")
        if (!mIsFirstVisible) {
            //表示点击home键又返回操作,设置可见状态为ture
            if (!isHidden && !userVisibleHint && currentVisibleState) {
                disPatchFragment(true)
            }
        }

    }


    override fun onPause() {
        super.onPause()
        //表示点击home键,原来可见的Fragment要走该方法，更改Fragment的状态为不可见
        if (!isHidden && userVisibleHint) {
            disPatchFragment(false)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        LogUtils.e(javaClass.simpleName, "-----> onStart")
        //当 View 被销毁的时候我们需要重新设置 isViewCreated mIsFirstVisible 的状态
        isViewCreated = false
        mIsFirstVisible = true
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        LogUtils.e(tag, "----->$isVisibleToUser")
        if (isViewCreated) {
            if (isVisibleToUser && !currentVisibleState) {//Fragment可见且状态不是可见(从一个Fragment切换到另外一个Fragment,后一个设置状态为可见)
                disPatchFragment(true)
            } else if (!isVisibleToUser && currentVisibleState) {//Fragment不可见且状态是可见(从一个Fragment切换到另外一个Fragment,前一个更改状态为不可见)
                disPatchFragment(false)
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        val visible = !hidden
        currentVisibleState = visible
        if (visible) {//Fragment可见
            if (mIsFirstVisible) {//可见又是第一次
                mIsFirstVisible = false//改变首次可见的状态
                onFragmentFirst()
            } else {//可见但不是第一次
                onFragmentVisible()
                LogUtils.e(javaClass.simpleName, "可见")
            }
        } else {//不可见
            onFragmentInVisible()
            LogUtils.e(javaClass.simpleName, "不可见")
        }

    }

    /**
     * @param visible Fragment当前是否可见，然后调用相关方法
     */
    private fun disPatchFragment(visible: Boolean) {
        currentVisibleState = visible
        if (visible) {//Fragment可见
            if (mIsFirstVisible) {//可见又是第一次
                mIsFirstVisible = false//改变首次可见的状态
                onFragmentFirst()
            } else {//可见但不是第一次
                onFragmentVisible()
                LogUtils.e(javaClass.simpleName, "可见")
            }
        } else {//不可见
            onFragmentInVisible()
            LogUtils.e(javaClass.simpleName, "不可见")
        }
    }

    //Fragment首次可见的方法
    open fun onFragmentFirst() {
        LogUtils.e(javaClass.simpleName, "首次可见")
    }

    //Fragment可见的方法
    open fun onFragmentVisible() {//子Fragment调用次方法，执行可见操作
        LogUtils.e(javaClass.simpleName, "可见")
    }

    //Fragment不可见的方法
    open fun onFragmentInVisible() {
        LogUtils.e(javaClass.simpleName, "不可见")
    }

}
