package org.topnetwork.pintogether.ui.activity.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.fasterxml.jackson.core.JsonProcessingException
import org.topnetwork.pintogether.base.app.BaseActivity
import org.topnetwork.pintogether.databinding.ActivityLoginBinding
import org.topnetwork.pintogether.ui.activity.vm.LoginActivityVm
import com.google.android.material.tabs.TabLayout
import com.google.common.collect.ImmutableList
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.wallet.DeterministicSeed

import org.topnetwork.pintogether.*
import org.topnetwork.pintogether.net.ObjectMapperUtils.getObjectMapper
import org.topnetwork.pintogether.permission.PermissionChecker
import org.topnetwork.pintogether.sp_is_login
import org.topnetwork.pintogether.utils.*
import org.web3j.crypto.*
import java.util.*
import kotlin.collections.ArrayList

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginActivityVm>() {
    var currentIndex = 0
    override val layoutResId: Int
        get() = R.layout.activity_login

    override fun createViewModel(): LoginActivityVm = ViewModelProvider(this).get(
        LoginActivityVm::class.java
    )
    private val BACK_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION"
    override fun afterOnCreate(savedInstanceState: Bundle?) {
        super.afterOnCreate(savedInstanceState)
        initView()

        if(PermissionChecker.hasPermissions(
                this,Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ))else{
            PermissionChecker.requestPermissions(
                this,
                PermissionChecker.REQUEST_CODE_PERMISSION_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,BACK_LOCATION_PERMISSION
            ) }
    }

    private fun initView(){
        cvb?.run {
            val tab0: TabLayout.Tab = tab.newTab()
            tab0.tag = 0
            tab0.setText("私钥")
            tab.addTab(tab0)

            val tab1: TabLayout.Tab = tab.newTab()
            tab1.tag = 1
            tab1.setText("助记词")
            tab.addTab(tab1)

            tab.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val index : Int = tab?.tag as Int
                    if(index != currentIndex){
                        cvb?.edit?.setText("")
                    }
                    currentIndex = index

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

            })

            tvLogin.setOnClickListener {
                var text = cvb?.edit?.text.toString()
                if(StringUtils.isEmpty(text)){
                    ToastUtils.showLong("请输入内容")
                    return@setOnClickListener
                }
                try {
                    var address:String = ""
                    //Numeric.toHexStringNoPrefixZeroPadded(keypair.privateKey, Keys.PRIVATE_KEY_LENGTH_IN_HEX)
                    if(currentIndex == 0){
                        var isHex = checkHexString(text)
                        if(!isHex){
                            ToastUtils.showLong("请输入正确的私钥")
                            return@setOnClickListener
                        }
                        if(text.length != 64 && text.length != 66){
                            ToastUtils.showLong("请输入正确的私钥")
                            return@setOnClickListener
                        }

                        var bytes = NumericUtil.hexToBytes(text)
                        //通过私钥生成地址
                        val keyPair: ECKeyPair = ECKeyPair.create(bytes)
                        val walletFile: WalletFile =
                            org.web3j.crypto.Wallet.create("111111", keyPair, 1024, 1)
                        var jsonWalletFile = ""
                        try {
                            jsonWalletFile =
                                getObjectMapper()
                                    .writeValueAsString(walletFile)
                        } catch (e: JsonProcessingException) {
                            e.printStackTrace()

                        }

                        val credentials: Credentials = Credentials.create(keyPair)
                        address = Keys.toChecksumAddress(
                            Keys.getAddress(
                                credentials.getEcKeyPair().getPublicKey()
                            )
                        )
                        PrefsUtil.put(sp_is_login,true)
                        PrefsUtil.put(sp_login_type,0)
                        PrefsUtil.put(sp_login_data,Base64.encode(bytes,Base64.DEFAULT))
                        PrefsUtil.put(sp_address,address)
                        AppData.isLogin = true
                        AppData.address = address
                        startMain()
                        //finish()

                    }else{
                        var mnemonicStr: String = text
                        mnemonicStr = mnemonicStr.trim { it <= ' ' }.replace("\n", "").toLowerCase()
                        val mnemonicArray:ArrayList<String> = arrayListOf()
                        if (mnemonicStr.length > 0) {
                            val finalMnemonicStr = mnemonicStr
                            RxUtils.AsyncTask(object : AsyncTask<Int>() {
                                override fun doAsyncTask(): Int {
                                    val mnemonics =
                                        finalMnemonicStr.split(" ".toRegex()).toTypedArray()
                                    if (mnemonics.size < 12) {
                                        return 1
                                    }
                                    mnemonicArray.addAll(ArrayList(Arrays.asList(*mnemonics)))
                                    if (MnemonicUtil.validateMnemonics(mnemonicArray)) {
                                        return 2
                                    }
                                    return 200
                                }

                                override fun completed(status: Int) {
                                    if (status == 200) {
                                        var path =  "m/44'/" + "60" + "'/" + 0 + "'/0/0"
                                        //助记词验证成功 然后
                                        //生成种子
                                        val seeds = DeterministicSeed(mnemonicArray, null, "", 0L)
//                                        //主密钥对象
//                                        val keyChain = DeterministicKeyChain.builder().seed(seed).build()
//
//                                        val keyChain1: DeterministicKey = keyChain.getKeyByPath(generatePath(path), true)
//                                        val privateKeyAsHex =  keyChain1.privateKeyAsHex
//
//                                        Log.e("HDMnemonicKeystore",privateKeyAsHex)

                                        val pathArray = path.split("/".toRegex()).toTypedArray()
                                        var dkKey = HDKeyDerivation.createMasterPrivateKey(seeds.seedBytes)
                                        for (i in 1 until pathArray.size) {
                                            var childNumber: ChildNumber
                                            childNumber = if (pathArray[i].endsWith("'")) {
                                                val number = pathArray[i].substring(
                                                    0,
                                                    pathArray[i].length - 1
                                                ).toInt()
                                                ChildNumber(number, true)
                                            } else {
                                                val number = pathArray[i].toInt()
                                                ChildNumber(number, false)
                                            }
                                            dkKey =
                                                HDKeyDerivation.deriveChildKey(dkKey, childNumber)
                                        }
                                        val keyPair = ECKeyPair.create(dkKey.privKeyBytes)
                                        val walletFile = Wallet.create("111111", keyPair, 1024, 1)
                                        var jsonWalletFile = ""
                                        try {
                                            jsonWalletFile =
                                                getObjectMapper()
                                                    .writeValueAsString(walletFile)
                                        } catch (e: JsonProcessingException) {
                                            e.printStackTrace()
                                        }
                                        val credentials = Credentials.create(keyPair)
                                        address =
                                            Keys.toChecksumAddress(Keys.getAddress(credentials.ecKeyPair.publicKey))
                                        PrefsUtil.put(sp_is_login,true)
                                        PrefsUtil.put(sp_login_type,1)
                                        PrefsUtil.put(sp_login_data,Base64.encode(keyPair.privateKey.toByteArray(),Base64.DEFAULT))
                                        PrefsUtil.put(sp_address,address)
                                        AppData.isLogin = true
                                        AppData.address = address
                                        startMain()
                                        //finish()
                                    } else if (status == 1) {
                                        ToastUtils.showShort("助记词验证失败")
                                    } else if (status == 2) {
                                        ToastUtils.showShort("助记词验证失败")
                                    }
                                }

                                override fun failed(e: Throwable?) {}
                            })
                        }
                    }

                }catch (e:Exception){

                }

            }
        }
    }

    fun generatePath(path: String): ImmutableList<ChildNumber> {
        val list: MutableList<ChildNumber> = java.util.ArrayList()
        for (p in path.split("/").toTypedArray()) {
            if ("m".equals(p, ignoreCase = true) || "" == p.trim { it <= ' ' }) {
                continue
            } else if (p[p.length - 1] == '\'') {
                list.add(ChildNumber(p.substring(0, p.length - 1).toInt(), true))
            } else {
                list.add(ChildNumber(p.toInt(), false))
            }
        }
        val builder = ImmutableList.builder<ChildNumber>()
        return builder.addAll(list).build()
    }

    fun checkHexString(string: String): Boolean {
        for (element in string) {
            var cInt: Int = element.toInt()
            if ((cInt in 48..57)
                || (cInt in 65..70)
                || (cInt in 97..102)
            ) {

            } else {
                return false
            }
        }
        return true
    }
}
