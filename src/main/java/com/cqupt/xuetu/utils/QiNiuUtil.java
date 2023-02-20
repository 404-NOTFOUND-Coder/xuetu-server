package com.cqupt.xuetu.utils;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import java.io.FileInputStream;
import java.util.Map;

public class QiNiuUtil {
    //外链域名地址
    public static String qiniu_img_url_pre = "rdrqjc56z.hn-bkt.clouddn.com";

    //AccessKey
    public static String ACCESS_KEY = "DtoTaokirsb0WJwUmSZ_p3F4tx3xeGyodRScmRYA";

    //SecretKey
    public static String SECRET_KEY = "znY5qVR3lsFzM1ZMoxKB9ox2TnOnSlJVeRXMqYVJ";

    //存储空间名称
    public static String bucketname = "xuetu";


    /**
     * 上传文件
     */
    public static String upload2Qiniu(FileInputStream file, String uploadFileName) {
        //构造一个带指定Zone对象的配置类,Zone.zone0()代表华东地区
        //构造中是地区，不同的地域，参数不同
        Configuration cfg = new Configuration(Region.huanan());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //默认不指定key的情况下，以文件内容的hash值作为文件名
//        String localFilePath = "/Users/mac/Desktop/tedt.jpg";
        String key = uploadFileName;
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        String upToken = auth.uploadToken(bucketname);
        try {
            Response response = uploadManager.put(file, key, upToken, null, null);
            //解析上传成功的结果
            String bodyString = response.bodyString();
            Map map = JSON.parseObject(bodyString, Map.class);
            String hash = (String) map.get("key");
            if (StringUtils.isNotEmpty(hash)) {
                return qiniu_img_url_pre + uploadFileName;
            }
            System.out.println(response.bodyString());
            // 访问路径
            System.out.println(qiniu_img_url_pre + uploadFileName);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
                return null;
            } catch (QiniuException ex2) {
                //ignore
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }
}