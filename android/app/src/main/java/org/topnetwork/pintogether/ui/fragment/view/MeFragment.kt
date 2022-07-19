package org.topnetwork.pintogether.ui.fragment.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import org.topnetwork.pintogether.AppData
import org.topnetwork.pintogether.R
import org.topnetwork.pintogether.base.app.BaseFragment
import org.topnetwork.pintogether.databinding.FragmentMeBinding
import org.topnetwork.pintogether.dialog.CommonDialog
import org.topnetwork.pintogether.startLogin
import org.topnetwork.pintogether.ui.fragment.vm.MeFragmentVm
import org.topnetwork.pintogether.utils.ActivityUtils

class MeFragment : BaseFragment<FragmentMeBinding, MeFragmentVm>(){
    var arrayList:ArrayList<Fragment> = arrayListOf()
    var titles:ArrayList<String> = arrayListOf()
    var mAdapter:Adapter ?= null
    override fun getLayoutResId(): Int = R.layout.fragment_me
    override fun createViewModel(): MeFragmentVm = ViewModelProvider(this).get(
        MeFragmentVm::class.java
    )

    override fun afterViewCreated() {
        initVp()
        cvb?.tvAddress?.text = AppData.address
    }

    private fun initVp(){
        arrayList.add(GetNftFragment())
        arrayList.add(CreateNftFragment())

        titles.add("我领取的NFT")
        titles.add("我创建的NFT")

        mAdapter = Adapter(childFragmentManager,titles,arrayList)
        //将adapter加载到viewpager里面
        cvb?.pager?.adapter = mAdapter
        //设置预加载数量，提高显示效果体验
        cvb?.pager?.offscreenPageLimit = 2
        //tablayout与viewpager联动
        cvb?.tab?.setupWithViewPager(cvb?.pager)

        cvb?.tvExit?.setOnClickListener {
            CommonDialog.getInstance()
                .setTitle("确认退出")
                .setConfirm("确认")
                .setCancel("取消")
                .setCanCancel(false)
                .setConfirmClickListener {
                    ActivityUtils.finishAll()
                    AppData.clear()
                    startLogin()
                }
                .show(childFragmentManager)
        }
    }

    inner class Adapter(fm: FragmentManager, titles:List<String>, datas: List<Fragment>) : FragmentPagerAdapter(fm) {

        private val fragments = datas
        private val pageTitles = titles

        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size

        override fun getPageTitle(position: Int): CharSequence{
            return pageTitles[position]
        }
    }
}