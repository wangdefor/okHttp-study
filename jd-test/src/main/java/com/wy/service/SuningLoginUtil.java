package com.wy.service;

import cn.hutool.core.util.RandomUtil;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.awt.*;
import java.io.File;
import java.time.Instant;
import java.util.stream.Collectors;

/**
 * @Classname SuningLoginUtil
 * @Description 苏宁登录
 * @Date 2020/12/28 19:06
 * @Created wangyong
 */
public class SuningLoginUtil {

    /**
     * 设置utf-8编码
     */
    public static final OkHttpClient.Builder clientBuild = new OkHttpClient.Builder();


    public static void getQrCode() throws Exception{
        Request request = new Request.Builder()
                .url("https://passport.suning.com/ids/qrLoginUuidGenerate.htm?image=true&yys=" + Instant.now().toEpochMilli())
                .addHeader("accept","image/avif,image/webp,image/apng,image/*,*/*;q=0.8")
                .addHeader("Referer","https://passport.suning.com/ids/login")
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36")
                .build();
        try (Response response = clientBuild.build().newCall(request).execute()){
            File qrFile = JdQrLoginUtil.createQrFile(response);
            //打开文件通过手机端扫描
            Desktop.getDesktop().open(qrFile);
            String cookies = response.headers("Set-cookie").stream().collect(Collectors.joining(";"));
            while (true){
                //获取uuid
                String uuid = JdQrLoginUtil.getToken(response, "ids_qr_uuid");
                String checkUrl = "https://passport.suning.com/ids/qrLoginStateProbe?callback=jQuery172014565409013477582_" + Instant.now().toEpochMilli();
                FormBody formBody = new FormBody.Builder()
                        .add("uuid",uuid)
                        .add("service","")
                        .add("terminal","PC")
                        .build();
                Request  checkRequest = new Request.Builder()
                        .addHeader("cookie",cookies)
                        .addHeader("cookie","tradeLdc=NJYH; route=50a502a0427482ba659e8aa77109d46b; _snck=160921242585238431; ")
                        .addHeader("Host","passport.suning.com")
                        .addHeader("Referer","https://passport.suning.com/ids/login")
                        .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36")
                        .post(formBody)
                        .url(checkUrl)
                        .build();
                try(Response checkResponse = clientBuild.build().newCall(checkRequest).execute()) {
                    //返回的状态值为2代表登录已扫码
                    System.out.println(checkResponse.body().string());
                    System.out.println(checkResponse.headers("Set-cookie").stream().collect(Collectors.joining(";")));
                }
            }
        }
    }

    public static void main(String[] args) throws Exception{
        getQrCode();
    }

}
