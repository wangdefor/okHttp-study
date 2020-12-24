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
    public static final String GOOGLE_COOKIE = "__jdu=624862094; PCSYCityID=CN_310000_310100_310107; shshshfpa=cea9288b-63d3-e015-6e16-f5ff5eaaa1a5-1608691474; user-key=53a36954-5e29-4d7f-8d87-882457172e1a; pinId=3c58XF1KC6ABNB2Km3viKQ; pin=%E6%B1%AA%E7%9A%84for; unick=%E6%B1%AA%E7%9A%84for; ceshi3.com=201; _tp=2J8BuqLMzfgd3eYqVWA1Fyah7aTMXt9%2F8Sns9dtyrL0%3D; _pst=%E6%B1%AA%E7%9A%84for; shshshfpb=nS6Ht2MXZTv%20eT%20eV6AyT6w%3D%3D; unpl=V2_ZzNtbUtUQRBwWxYGfxFYAGIDEFhLXktBJQxAUHMaDAJmChMPclRCFnQURldnGl4UZwYZXkJcQRdFCEdkeBBVAWMDE1VGZxBFLV0CFSNGF1wjU00zQwBBQHcJFF0uSgwDYgcaDhFTQEJ2XBVQL0oMDDdRFAhyZ0AVRQhHZHwaWQxgABNVR2dzEkU4dlx5Gl0HZDMTbUNnAUEpD0dQeRhcSGAAF1RFVEIdcDhHZHg%3d; ipLocation=%u4e0a%u6d77; answer-code=\"\"; areaId=2; __jdv=76161171|baidu-pinzhuan|t_288551095_baidupinzhuan|cpc|0f3d30c8dba7459bb52f2eb5eba8ac7d_0_82344bac59544134889ea57592a6080c|1608773413909; ipLoc-djd=2-2826-51945-0.2806736194; __jda=122270672.624862094.1608279990.1608773408.1608780558.6; __jdc=122270672; cn=0; wlfstk_smdl=r0kaea8gle7rzzwiqep0d7a14w52xkao; TrackID=1BBNYL1LsPecSjYfEEIRAfteoBgbzRB-QyH9g8vVLZyZsSHaEAKWKozdIMz5f6JmSKbbn61GxCIiit3Z80C8ubLFxid9WN9B45DXvJbgqDQU; thor=ABD285720EEFCED11559EF874D6F93ACD7025B0153597E525E21FB53064B981808F181FCD472CBA030F39B2491FE32C4FE2D74F5FEEA7E96045E42BAADE635D29C1702327BFD8B931A1E21BB82F7938FE41BF3B02550775E7F6F80D617881614C55B319DAEEE250D8AE41DB339D24EA713FD6256C99E48492D6C608FBD04089F; shshshfp=b8a2b9a959d9e8584e204a27a0ecb1e0; shshshsID=58a50301ab37582377e7602ef4636420_1_1608780581748; 3AB9D23F7A4B3C9B=KMIXFQHHCWMFPL7CC4GK7MSWCUGWM2AMF72VC2W3A66TSHLWVCRFREUH33JBQFUA3ELE5IWM4ITFQSX4DQHEXP6JEM; __jdb=122270672.6.624862094|6.1608780558; JSESSIONID=D3CBEB7923AC1E3FB88FBCE2E7353C3E.s1";
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
