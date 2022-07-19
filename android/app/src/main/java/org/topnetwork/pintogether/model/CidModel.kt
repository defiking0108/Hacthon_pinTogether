package org.topnetwork.pintogether.model
class CidModel {
    var ok:Boolean  = false
    var value:Value ?= null

    class Value{
        var cid :String = ""
        var created:String = ""
        var type :String = ""
        var scope:String = ""
        var size:String  = ""
        var name:String = ""
        var pin:Pin ?= null
        var files:ArrayList<Files>  = arrayListOf()
    }
    class Pin{
        var cid:String = ""
        var created:String = ""
        var size:String = ""
        var status:String = ""
    }
    class Files{
        var name:String = ""
        var type:String = ""
    }
}