package org.topnetwork.pintogether

import org.topnetwork.pintogether.utils.PrefsUtil

object AppData {
     var city = "杭州"
     var address = ""
     var isLogin = false

     fun clear(){
         address = ""
         isLogin = false
         PrefsUtil.put(sp_is_login,false)
         PrefsUtil.put(sp_login_type,0)
         PrefsUtil.put(sp_login_data, "")
         PrefsUtil.put(sp_address,"")
     }

}