package com.wy;

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

/**
 * @Classname JdTest
 * @Description JdTest
 * @Date 2020/12/23 14:20
 * @Created wangyong
 */
@Slf4j
public class OkHttpJdYuyueTest {

    /**
     * 登陆之后的cookie信息 直接进入浏览器进入下单页面 复制request中的header中的cookie值
     */
    public static final String GOOGLE_COOKIE = "__jdu=624862094; PCSYCityID=CN_310000_310100_310107; shshshfpa=cea9288b-63d3-e015-6e16-f5ff5eaaa1a5-1608691474; user-key=53a36954-5e29-4d7f-8d87-882457172e1a; pinId=3c58XF1KC6ABNB2Km3viKQ; pin=%E6%B1%AA%E7%9A%84for; unick=%E6%B1%AA%E7%9A%84for; _tp=2J8BuqLMzfgd3eYqVWA1Fyah7aTMXt9%2F8Sns9dtyrL0%3D; _pst=%E6%B1%AA%E7%9A%84for; shshshfpb=nS6Ht2MXZTv%20eT%20eV6AyT6w%3D%3D; ipLocation=%u4e0a%u6d77; answer-code=\"\"; areaId=2; ipLoc-djd=2-2826-51945-0.2806736194; unpl=V2_ZzNtbUAHEBxxXxFRcxFfA2JRFg5LA0EUcg1HXCkYCVYwChNcclRCFnQURldnGlkUZwsZWEtcQhZFCEdkex5fDGQzEl1FVkEXdQ1BZEsaXDVmMxVeR15EFnQAQ2RLGVQBVzPGyOmPyIInSQSNy7yKr8kzFVhFUUoccAx2VUsYbE4JAl9aQVJKEnYJTlFLGGwG; __jdv=76161171|baidu-pinzhuan|t_288551095_baidupinzhuan|cpc|0f3d30c8dba7459bb52f2eb5eba8ac7d_0_82344bac59544134889ea57592a6080c|1608859989954; cn=1; __jda=122270672.624862094.1608279990.1608810485.1608859990.8; __jdc=122270672; shshshfp=b8a2b9a959d9e8584e204a27a0ecb1e0; shshshsID=c6452032d9d6d033bcb4ae2455871bd5_2_1608860010769; wlfstk_smdl=zs2lqop4ctpwfcfk1674e7m5sxzb43w4; TrackID=1ELW2i75u6RZLK2R4OykcLWpa-SsyVZWg3B2_LNTVSFw0uDgwilSBz6g1hXbmxv_tM3Z5obN4xbyrN9Qn8tomO2wcK1RthKpry79Bris9ydo; thor=ABD285720EEFCED11559EF874D6F93AC9E7005C914DFACB83992681F810142DBCB84A83DBC1D10BD7855260CF2A1D21AAD94262E959BB6B0860F4AED0BBFADAAB5F38C719C5DC0A1409A0391FEC36425C38B13244A66B2A42D3C33D7E9F2C8CCA59DEA22AB42CE25407D72108697A6494ED04D14228A4FAD604EDF13DCC0A4AB; ceshi3.com=201; 3AB9D23F7A4B3C9B=KMIXFQHHCWMFPL7CC4GK7MSWCUGWM2AMF72VC2W3A66TSHLWVCRFREUH33JBQFUA3ELE5IWM4ITFQSX4DQHEXP6JEM; __jdb=122270672.7.624862094|8.1608859990; JSESSIONID=F8FDDFFA5C1F0B4ED384613A806E3F3D.s1";

    /**
     * 茅台sku
     */
    public static final String skuId = "100012043978";

    /**
     * 设置utf-8编码
     */
    public static final OkHttpClient.Builder clientBuild = new OkHttpClient.Builder();


    /**
     * 第一步获取预约url
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
