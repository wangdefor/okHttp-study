package com.wy.service;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;

import static com.wy.contants.CommonContants.GOOGLE_COOKIE;
import static com.wy.contants.CommonContants.skuId;

/**
 * @Classname jd预约功能
 * @Description jd预约功能
 * @Date 2020/12/23 14:20
 * @Created wangyong
 */
@Slf4j
public class JdAppointment {



    /**
     * 设置utf-8编码
     */
    public static final OkHttpClient.Builder clientBuild = new OkHttpClient.Builder();


    /**
     * 第一步获取预约url
     *
     *
     * @return
     */
    public static Info getUrl() throws IOException {
        String goodsInfoUrl = "https://yushou.jd.com/youshouinfo.action?";
        FormBody body = new FormBody.Builder()
                .add("callback", "fetchJSON")
                .add("sku", skuId)
                .add("_", Instant.now().toEpochMilli() + "")
                .build();
        Request request = new Request.Builder()
                .url(goodsInfoUrl)
                .post(body)
                .addHeader(HttpHeaders.COOKIE, GOOGLE_COOKIE)
                .addHeader("Referer", "https://item.jd.com/" + skuId + ".html")
                .build();
        try (Response response = clientBuild.build().newCall(request).execute()) {
            String result = response.body().string();
            log.info("正在进行获取预约url请求 {}", result);
            String substring = result.substring(10, result.length() - 2);
            Info parse = JSONObject.parseObject(substring, Info.class);
            log.info("解析之后的url请求为 {}", JSONObject.toJSONString(parse));
            return parse;
        }
    }

    public static void yuYue() throws IOException {
        Info url = getUrl();
        Request request = new Request.Builder()
                .url("https:" + url.getUrl())
                .addHeader(HttpHeaders.COOKIE, GOOGLE_COOKIE)
                .build();
        try (Response response = clientBuild.build().newCall(request).execute()) {
            String result = response.body().string();
            log.info(result);
            String message = "成功";
            if (result.contains(message)) {
                log.info("您已成功预约过了，无需重复预约");
            } else {
                log.info("预约失败");
            }
        }
    }


    public static void main(String[] args) throws IOException {
        yuYue();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Info implements Serializable {

        private String info;

        private String url;
    }
}
