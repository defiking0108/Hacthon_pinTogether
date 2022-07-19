## **PinTogether**

基于IPFS的一款web3项目

通过私钥或者助记词的导入进行登录并可以在地图上创建nft(IPFS技术),他人会在地图上进行nft的领取

### **技术**

- **IPFS技术**

主要运用此技术进行数据存储（nft图片）

具体代码org.topnetwork.pintogether.ui.activity.vm.CreateNftActivityVm中:

```
fun uploadFile(getPath: String, url: String) {
        var file = File(getPath);
        var size: Long = file.length();
        var httpClient = OkHttpClient()
        var mediaType: MediaType? = "image/*".toMediaTypeOrNull();
        var requestBody: RequestBody = RequestBody.create(mediaType, file);

        var multipartBody: MultipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.getName(), requestBody)
            .build()

        var request: Request = Request.Builder()
            .header(
                "Authorization",
                ""
            )
            .url('https://api.nft.storage/upload')
            .post(multipartBody)
            .build()

        var call: Call = httpClient.newCall(request);
  
    }
```

### **使用**

1,项目目录下的v1.0.0-release.apk可以直接在线安装使用

2,测试私钥:7b5772ef94e26b5b376f5ec2c6b7d60e23bd57cce7e4c8bb6dfe848da7a6c142

