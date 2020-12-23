package com.wy;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.List;

/**
 * @Classname JdTest
 * @Description JdTest
 * @Date 2020/12/23 14:20
 * @Created wangyong
 */
@Slf4j
public class JdTest {

    /**
     * 登陆之后的cookie信息 直接进入浏览器进入下单页面 复制request中的header中的cookie值
     */
    public static final String GOOGLE_COOKIE = "__jdu=624862094; areaId=2; PCSYCityID=CN_310000_310100_310107; shshshfpa=cea9288b-63d3-e015-6e16-f5ff5eaaa1a5-1608691474; user-key=53a36954-5e29-4d7f-8d87-882457172e1a; TrackID=1TrSVOnuuJrzxuJoYHPY4eq1NSrNRawfaDMY3EkJFbb3P_QevGAQEvjtKsshMiv0TrqHVl9b_WfTIhSOusK7ITdJMuI04PdK4tK7q0wO_nfQ; pinId=3c58XF1KC6ABNB2Km3viKQ; pin=%E6%B1%AA%E7%9A%84for; unick=%E6%B1%AA%E7%9A%84for; ceshi3.com=201; _tp=2J8BuqLMzfgd3eYqVWA1Fyah7aTMXt9%2F8Sns9dtyrL0%3D; _pst=%E6%B1%AA%E7%9A%84for; shshshfpb=nS6Ht2MXZTv%20eT%20eV6AyT6w%3D%3D; unpl=V2_ZzNtbUJUF0Z2CkIGKEoJVmJUG1RLBRAXdgFEAHMcWQ1lBxEJclRCFnQURldnGl4UZwEZX0RcRhNFCEdkeBBVAWMDE1VGZxBFLV0CFSNGF1wjU00zQwBBQHcJFF0uSgwDYgcaDhFTQEJ2XBVQL0oMDDdRFAhyZ0AVRQhHZHwaWQxgABNVR2dzEkU4dlJ6HVoMYzMTbUNnAUEpAEJUch0RAmQGG1pBVksQRQl2Vw%3d%3d; __jdv=76161171|baidu-pinzhuan|t_288551095_baidupinzhuan|cpc|0f3d30c8dba7459bb52f2eb5eba8ac7d_0_12eb235cbbdb4f888cb3283e9449352e|1608701869166; thor=ABD285720EEFCED11559EF874D6F93ACBBACA2B58C307CE02F76717D3EE2D11D35BDC73B160E9B6AFE99EB312705068CFC3530AD347175E848BE76793C8D3AC5C3CB57A49265D884AD33CD6CA40393AE757460378DB2ABB8D3B7D2A5671F6A0FC7B24CC06A47A39DE6D7E814BDCC4E3733CFA2E5587655D4A9DFB9AB5E45A04C; cn=2; __jda=122270672.624862094.1608279990.1608691473.1608701869.3; __jdc=122270672; shshshfp=b8a2b9a959d9e8584e204a27a0ecb1e0; shshshsID=1e1adca505b399a26757fc32c0c4c32b_2_1608701894826; 3AB9D23F7A4B3C9B=KMIXFQHHCWMFPL7CC4GK7MSWCUGWM2AMF72VC2W3A66TSHLWVCRFREUH33JBQFUA3ELE5IWM4ITFQSX4DQHEXP6JEM; ipLoc-djd=17-1381-50718-53772.2889967991; ipLocation=%u6e56%u5317; __jdb=122270672.3.624862094|3.1608701869; JSESSIONID=D1076FA715FE8FED23CA76FC02C3CC56.s1";

    /**
     * 茅台sku
     */
    public static final String skuId = "100012043978";

    /**
     * 设置utf-8编码
     */
    public static final RestTemplate restTemplate = getInstance("UTF-8");

    /**
     * 第一步获取预约url
     *
     * @return
     */
    public static Info getUrl() throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, GOOGLE_COOKIE);
        headers.add("Referer","https://item.jd.com/" + skuId + ".html");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("callback","fetchJSON");
        map.add("sku",skuId);
        map.add("_", Instant.now().toEpochMilli() + "");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        String goodsInfoUrl = "https://yushou.jd.com/youshouinfo.action?";
        URI uri = new URI(goodsInfoUrl);
        ResponseEntity<String> exchange = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
        String body = exchange.getBody();
        log.info("正在进行获取预约url请求 {}",body);
        String substring = body.substring(10, body.length() - 2);
        Info parse = JSONObject.parseObject(substring, Info.class);
        log.info("解析之后的url请求为 {}",JSONObject.toJSONString(parse));
        return parse;
    }

    public static void yuyue() throws URISyntaxException {
        Info url = getUrl();
        MultiValueMap<String, String> map2 = new LinkedMultiValueMap<>();
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add(HttpHeaders.COOKIE, GOOGLE_COOKIE);
        HttpEntity<MultiValueMap<String, String>> entity2 = new HttpEntity<>(map2, headers2);
        ResponseEntity<String> exchange = restTemplate.exchange("https:" + url.url, HttpMethod.GET, entity2, String.class);
        String message= "您已成功预约";
        if(exchange.getBody().contains(message)){
            log.info("您已成功预约过了，无需重复预约");
        }else{
            log.info("预约失败");
        }
    }


    public static void main(String[] args) throws URISyntaxException {
        yuyue();
    }


    public static RestTemplate getInstance(String charset) {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> httpMessageConverter : list) {
            if (httpMessageConverter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) httpMessageConverter).setDefaultCharset(Charset.forName(charset));
                break;
            }
        }
        return restTemplate;
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
