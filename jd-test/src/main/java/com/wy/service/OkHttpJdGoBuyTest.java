package com.wy.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.wy.model.*;
import com.wy.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wy.contants.CommonContants.GOOGLE_COOKIE;
import static com.wy.contants.CommonContants.eid;
import static com.wy.contants.CommonContants.fp;
import static com.wy.contants.CommonContants.skuId;

/**
 * @Classname JdTest
 * @Description 抢购功能
 * @Date 2020/12/23 14:20
 * @Created wangyong
 */
@Slf4j
public class OkHttpJdGoBuyTest {

    /**
     * 设置utf-8编码
     */
    public static final OkHttpClient.Builder clientBuild = new OkHttpClient.Builder();


    /**
     * 获取个人信息
     *
     * @return
     * @throws IOException
     */
    public static UserInfoModel getUserInfo() throws IOException {
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
            System.out.println(substring);
            UserInfoModel infoModel = JSONObject.parseObject(substring, UserInfoModel.class);
            log.info("正在获取请求个人信息 {}", JSONObject.toJSONString(infoModel));
            return infoModel;
        }
    }

    public static GoBuyUrl getGoBuyUrl() throws IOException, InterruptedException {
        //获取商品抢购链接
        String url = "https://itemko.jd.com/itemShowBtn";
        String params = "?callback=jQuery" + RandomUtil.randomNumbers(7) + "&skuId=" + skuId + "&from=pc" + "&_=" + Instant.now().toEpochMilli();
        log.info("params {}", params);
        Request request = new Request.Builder()
                .url(url + params)
                .get()
                .addHeader(HttpHeaders.COOKIE, GOOGLE_COOKIE)
                .addHeader("Host", "itemko.jd.com")
                .addHeader("Referer", "https://item.jd.com/" + skuId + ".html")
                .addHeader("User-Agent", "")
                .build();
        GoBuyUrl goBuyUrl = null;
        while (true) {
            try (Response response = clientBuild.build().newCall(request).execute()) {
                String result = response.body().string();
                log.info("正在获取前抢购链接 {}", result);
                String substring = result.substring("jQuery0000000".length() + 1, result.length() - 2);
                goBuyUrl = JSONObject.parseObject(substring, GoBuyUrl.class);
                headerBuild.add("cookie",response.headers("Set-cookie").stream().collect(Collectors.joining(";")));
                log.info("json mapping to goBuyUrl {}", JSONObject.toJSONString(goBuyUrl));
            }
            if (!StringUtils.isNotBlank(goBuyUrl.getUrl())) {
                log.info("抢购链接获取失败，稍后将会自动重试");
                //睡眠几百毫秒
                TimeUnit.MILLISECONDS.sleep(RandomUtil.randomLong(100, 500));
                continue;
            }
            String replace = goBuyUrl.getUrl().replace("divide", "marathon")
                    .replace("user_routing", "captcha.html");
            goBuyUrl.setUrl("https:" + replace);
            return goBuyUrl;
        }
    }

    public static Headers.Builder headerBuild = new Headers.Builder();

    public static void visitGoBuyUrl() throws Exception {
        Response response = null;
        try {
            GoBuyUrl goBuyUrl = getGoBuyUrl();
            Request request = new Request.Builder()
                    .url(goBuyUrl.getUrl())
                    .get()
                    .headers(headerBuild.build())
                    .addHeader(HttpHeaders.COOKIE, GOOGLE_COOKIE)
                    .addHeader("Host", "marathon.jd.com")
                    .addHeader("Referer", "https://item.jd.com/" + skuId + ".html")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36")
                    .build();
            response = clientBuild.followRedirects(Boolean.FALSE).build().newCall(request).execute();
            String cookies = response.headers("Set-cookie").stream().collect(Collectors.joining(";"));
            headerBuild.add("cookie",cookies);
        } catch (Exception e) {
            log.error("进入结算订单页面异常", e);
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public static void visitJieSuan() throws Exception {
        log.info("访问订单结算页面");
        String url = "https://marathon.jd.com/seckill/seckill.action?skuId=" + skuId + "&num=2&rid" + Instant.now().getEpochSecond();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .headers(headerBuild.build())
                .addHeader(HttpHeaders.COOKIE, GOOGLE_COOKIE)
                .addHeader("Host", "marathon.jd.com")
                .addHeader("Referer", "https://item.jd.com/" + skuId + ".html")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36")
                .build();
        Response response = null;
        try {
            response = clientBuild.followRedirects(Boolean.FALSE).build().newCall(request).execute();
            headerBuild.add("cookie",response.headers("Set-Cookie").stream().collect(Collectors.joining( )));
            String string = response.body().string();
            log.info("访问订单结算页面结果为 {},", string);
        } catch (Exception e) {
            log.error("问订单结算异常", e);
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public static void submitOrder(SeckillSkuVO initInfo) throws Exception {
        String url = "https://marathon.jd.com/seckillnew/orderService/pc/submitOrder.action?skuId=" + skuId;
        SubmitOrderVo orderVo = buildSubmit(initInfo);
        FormBody.Builder builder = new FormBody.Builder();
        Map<String,Object> map = JSONObject.parseObject(JSONObject.toJSONString(orderVo), Map.class);
        map.forEach((key,value) -> builder.add(key,String.valueOf(value)));
        log.info("组装订单参数" + JSONObject.toJSONString(orderVo));
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                //.headers(headerBuild.build())
                .addHeader(HttpHeaders.COOKIE, GOOGLE_COOKIE)
                .addHeader("Host", "marathon.jd.com")
                .addHeader("Host", "https://marathon.jd.com/seckill/seckill.action?skuId=" + skuId + "&num=" + 2 + "&rid=" + Instant.now().getEpochSecond())
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36")
                .build();
        Response response = null;
        try {
            response = clientBuild.followRedirects(Boolean.FALSE).build().newCall(request).execute();
            String string = response.body().string();
            log.info("提交订单，返回结果 {}", string);
            if (string.contains("success") && string.contains("true")) {
                log.info("抢购成功");
            }
            throw new Exception("提交订单失败");
        } catch (Exception e) {
            log.info("提交订单结算异常", e);
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public static SubmitOrderVo buildSubmit(SeckillSkuVO initInfo) {
        Address address = initInfo.getAddressList().get(0);
        InvoiceInfo info = initInfo.getInvoiceInfo();
        PaymentType paymentType = initInfo.getPaymentTypeList().get(0);
        SubmitOrderVo orderVo = SubmitOrderVo.builder()
                .addressDetail(address.getAddressDetail())
                .addressId(address.getId())
                .areaCode(address.getAreaCode())
                .cityId(address.getCityId())
                .codTimeType(3)
                .countyId(address.getCountyId())
                .eid(eid)
                .email("")
                .fp(fp)
                .invoce(info != null)
                .invoiceCompanyName(info.getInvoiceCompany())
                .invoiceContent(info.getInvoiceContentType())
                .invoiceEmail("")
                .invoicePhone(info.getInvoicePhone())
                .invoicePhoneKey(info.getInvoicePhoneKey())
                .invoiceTaxpayerNO("")
                .invoiceTitle(info.getInvoiceTitle())
                .isModifyAddress(false)
                .mobile(address.getMobile())
                .mobileKey(address.getMobileKey())
                .name(initInfo.getAddressList().get(0).getName())
                .num(2)
                .overseas(address.getOverseas())
                .password("")
                .paymentType(paymentType.getPaymentId())
                .phone("")
                .postCode("")
                .provinceId(address.getProvinceId())
                .pru("")
                .skuId(skuId)
                .token(initInfo.getToken())
                .townId(address.getTownId())
                .yuShou(true)
                .build();
        return orderVo;
    }

    public static SeckillSkuVO getInitInfo() {
        String url = "https://marathon.jd.com/seckillnew/orderService/pc/init.action";
        FormBody.Builder form = new FormBody.Builder();
        form.add("sku", skuId)
                .add("num", "2")
                .add("isModifyAddress", "false");
        Request request = new Request.Builder()
                .url(url)
                .post(form.build())
                .addHeader(HttpHeaders.COOKIE, GOOGLE_COOKIE)
                .addHeader("Host", "marathon.jd.com")
                .addHeader("User-Agent", "")
                .build();
        Response response = null;
        try {
            response = clientBuild.followRedirects(Boolean.FALSE).build().newCall(request).execute();
            if (response.code() == 200) {
                String string = response.body().string();
                log.info("获取抢购初始化信息成功 {}", string);
                SeckillSkuVO skuVO = JSONObject.parseObject(string, SeckillSkuVO.class);
                log.info("SkillSkuVo build success {}", JSONObject.toJSONString(skuVO));
                return skuVO;
            }
        } catch (Exception e) {
            log.info("获取抢购初始化信息异常", e);

        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SeckillSkuVO initInfo = getInitInfo();
        while (true) {
            //获取抢购url
            try {
                visitGoBuyUrl();
            } catch (Exception e) {
                log.error("进入订单结算页面失败", e);
                continue;
            }
            //访问抢购订单结算页面
            while (true) {
                try {
                    //提交订单
                    visitJieSuan();
                    submitOrder(initInfo);
                    return;
                } catch (Exception e) {
                    log.error("提交失败", e);
                }
            }
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderResult implements Serializable {

        private Integer resultCode;

        private Boolean success;

        private String totalMoney;

        private String appUrl;

        private Long orderId;

        private String pcUrl;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GoBuyUrl implements Serializable {

        private String type;

        private String state;

        private String url;
    }
}
