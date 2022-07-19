package org.topnetwork.pintogether.ui.fragment.view

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import org.topnetwork.pintogether.AppData
import org.topnetwork.pintogether.R
import org.topnetwork.pintogether.adapter.GetNftAdapter
import org.topnetwork.pintogether.base.app.BaseLazyLoadFragment
import org.topnetwork.pintogether.databinding.FragmentCreateNftBinding
import org.topnetwork.pintogether.model.MeGetNft
import org.topnetwork.pintogether.model.NearBy
import org.topnetwork.pintogether.startNftDetails
import org.topnetwork.pintogether.ui.fragment.vm.CreateNftFragmentVm
import org.topnetwork.pintogether.utils.UIUtils
import org.topnetwork.pintogether.widgets.recycler.SpaceItemDecoration

class CreateNftFragment : BaseLazyLoadFragment<FragmentCreateNftBinding, CreateNftFragmentVm>() {
    var mAdapter: GetNftAdapter?= null
    override fun getLayoutResId(): Int = R.layout.fragment_create_nft

    override fun createViewModel(): CreateNftFragmentVm = ViewModelProvider(this).get(
        CreateNftFragmentVm::class.java
    )

    override fun afterViewCreated() {
        initRv()
        viewModel?.data?.observe(this, Observer {
            mAdapter?.setNewData(it)

            if(mAdapter?.data.isNullOrEmpty()){
                cvb?.ivEmpty?.visibility = View.VISIBLE
            }else{
                cvb?.ivEmpty?.visibility = View.GONE
            }
        })

        cvb?.swip?.setOnRefreshListener {
            viewModel?.createNft(AppData.address,null)
            cvb?.swip?.isRefreshing = false
        }
    }

    override fun onFragmentFirst() {
        super.onFragmentFirst()
        viewModel?.createNft(AppData.address,null)
    }

    override fun onFragmentVisible() {
        super.onFragmentVisible()
        viewModel?.createNft(AppData.address,null)
    }

    private fun initRv(){
        mAdapter = GetNftAdapter()
        mAdapter?.run {
            setOnItemClickListener { adapter, _, position ->
                val item = adapter.getItem(position)
                startNftDetails((item as NearBy).giftId,1,item)
            }
        }
        cvb?.rv?.run {
            adapter = mAdapter
            setHasFixedSize(true)
            addItemDecoration(SpaceItemDecoration(2, UIUtils.dip2px(16f), true))
            layoutManager = GridLayoutManager(this.context, 2)
        }
    }
}