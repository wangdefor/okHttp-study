package com.wy.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.wy.model.UserInfoModel;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.FileInputStream;
import java.time.Instant;

import static com.wy.contants.CommonContants.GOOGLE_COOKIE;

/**
 * @Classname UserService
 * @Description 获取用户信息用于判断是否登录
 * @Date 2020/12/28 16:22
 * @Created wangyong
 */
@Slf4j
public class UserService {

    /**
     * 设置utf-8编码
     */
    public static final OkHttpClient.Builder clientBuild = new OkHttpClient.Builder();


    /**
     * 获取用户信息
     *
     * @return
     */
    public UserInfoModel getUser() throws Exception{
        String url = "https://passport.jd.com/user/petName/getUserInfoForMiniJd.action?callback=jQuery" +
                RandomUtil.randomNumbers(7)
                + "&_=" + Instant.now().toEpochMilli();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader(HttpHeaders.COOKIE, GOOGLE_COOKIE)
                .addHeader("Referer", "https://order.jd.com/center/list.action")
                .build();
        try (Response response = clientBuild.build().newCall(request).execute()) {
            String result = response.body().string();
            log.info("正在获取请求个人信息 {}", result);
            String substring = result.substring("jQuery0000000".length() + 1, result.length() - 2);
            log.info("正在截取并转化个人信息 {}", substring);
            UserInfoModel infoModel = JSONObject.parseObject(substring, UserInfoModel.class);
            log.info("正在获取请求个人信息 {}", JSONObject.toJSONString(infoModel));
            return infoModel;
        }

    }

    /**
     * 初始化相关参数
     */
    public static String init() throws Exception{
        //获取cookie
        File file = new File("cookie.txt");
        if(!file.exists()){
            String cookie = JdQrLoginUtil.loginJd();
            GOOGLE_COOKIE = cookie;
        }
        //获取相关值
        try (FileInputStream inputStream = new FileInputStream(file)){
            int available = inputStream.available();
            byte[] bytes = new byte[available];
            inputStream.read(bytes);
            String cookie = new String(bytes);
            JdQrLoginUtil.checkLogin(cookie);
            return GOOGLE_COOKIE;
        }
    }
}
