package com.wy.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.wy.contants.CommonContants;
import com.wy.model.TicketVo;
import com.wy.model.UserInfoModel;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Classname JdAppointment
 * @Description 预约功能开发
 * @Date 2020/12/28 16:21
 * @Created wangyong
 */
@Slf4j
public class JdQrLoginUtil {

    public static UserService userService = new UserService();

    /**
     * 设置utf-8编码
     */
    public static final OkHttpClient.Builder clientBuild = new OkHttpClient.Builder();

    public static void checkLogin(String cookie) throws Exception{
        CommonContants.GOOGLE_COOKIE = cookie;
        UserInfoModel user = userService.getUser();
        //判断当前分数是否足够
        while (user.getUserScoreVO() == null){
            log.info("当前用户未登录，正在请求登录");
            //跳转 并让人扫码
            loginJd();
            user = userService.getUser();
        }
        log.info("当前个人信息为 {}", JSONUtil.toJsonStr(user));
    }

    public static Headers getHeaders(){
        Map<String,String> headers = new HashMap<>();
        headers.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        headers.put("Accept","text/html,application/xhtml+xml,application/xml;" + "q=0.9,image/webp,image/apng,*/*;" + "q=0.8,application/signed-exchange;" +  "v=b3");
        headers.put("Connection","keep-alive");
        Headers h = Headers.of(headers);
        return h;
    }

    public static String loginJd() throws Exception {
        //获取登录的url
        String url = "https://passport.jd.com/new/login.aspx";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .headers(getHeaders())
                .build();
        try (Response response = clientBuild.build().newCall(request).execute()) {
            String cookie = response.headers("Set-Cookie").stream().collect(Collectors.joining(" "));
            Headers headers = getHeaders().newBuilder()
                    .add("cookie", cookie)
                    .build();
           return execute(headers);
        }
    }

    public static String getToken(Response response,String key){
        List<String> headers = response.headers("set-cookie");
        for (String header:headers) {
            String[] split = header.split(";");
            for (int i = 0; i < split.length; i++) {
                if(split[i].contains(key)){
                    String[] strings = split[i].split("=");
                    return strings[1];
                }
            }
        }
        return "";
    }

    private static String execute(Headers headers) throws Exception{
        String showUrl = "https://qr.m.jd.com/show?appid=133&size=147&t=" + Instant.now().getEpochSecond() * 1000;
        Request request = new Request.Builder()
                .headers(headers)
                .addHeader("Referer", "https://passport.jd.com/")
                .url(showUrl)
                .build();
        try (Response response = clientBuild.build().newCall(request).execute()){
            //生成二维码
            File file = createQrFile(response);
            //打开文件
            java.awt.Desktop.getDesktop().open(new File(file.getAbsolutePath()));
            TicketVo ticketVo = null;
            while (true){
                //轮询查询 先获取票据
                ticketVo = getTicket(response);
                if(StringUtils.isNotBlank(ticketVo.getTicket())){
                    break;
                }
                TimeUnit.SECONDS.sleep(1);
                log.info("请扫码登录");
            }
            //获取票据之后，进行校验
            String cookie = checkAndSetCookie(ticketVo);
            //获取cookie
            File cookieFile = new File("cookie.txt");
            //存储
            try(FileOutputStream outputStream = new FileOutputStream(cookieFile)) {
                outputStream.write(cookie.getBytes());
            }
            CommonContants.GOOGLE_COOKIE = cookie;
            return cookie;
        }
    }

    private static String checkAndSetCookie(TicketVo ticketVo) throws Exception{
        String url = "https://passport.jd.com/uc/qrCodeTicketValidation?t=" + ticketVo.getTicket();
        Headers headers = getHeaders();
        Headers referer = headers.newBuilder()
                .add("Referer", "https://passport.jd.com/uc/login?ltype=logout")
                .build();
        Request request2 = new Request.Builder()
                .url(url)
                .headers(referer)
                .build();
        try (Response response = clientBuild.build().newCall(request2).execute()) {
            //获取response3中的cookie
            String cookie = response.headers("set-cookie").stream().collect(Collectors.joining(" "));
            //将cookie进行保存
            CommonContants.GOOGLE_COOKIE = cookie;
            return cookie;
        }
    }

    private static TicketVo getTicket(Response response) throws Exception{
        String token = getToken(response,"wlfstk_smdl");
        String QRCodeKey = getToken(response,"QRCodeKey");
        String path = "/check?callback=jQuery" + RandomUtil.randomNumbers(7) +"&appid=133&token=" + token + "&_=" + Instant.now().toEpochMilli();
        String url = "https://qr.m.jd.com/" + path;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36")
                .addHeader("Referer","https://passport.jd.com/")
                .addHeader("cookie","QRCodeKey=" + QRCodeKey + "; wlfstk_smdl=" + token)
                .build();
        TicketVo ticketVo = null;
        try (Response ticResponse = clientBuild.build().newCall(request).execute()){
            String string = ticResponse.body().string();
            log.info("正在获取票据信息，票据信息为 {}",string);
            String substring = string.substring(14, string.length() - 1);
            ticketVo = JSONObject.parseObject(substring, TicketVo.class);
        }
        return ticketVo;

    }

    private static File createQrFile(Response response) throws Exception{
        FileOutputStream fileOutputStream = null;
        File file = null;
        InputStream inputStream = response.body().byteStream();
        try {
            file = new File( UUID.randomUUID().toString().replaceAll("[-]","") + ".png");
            fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[2048];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
            fileOutputStream.flush();
            return file;
        } catch (Exception e) {
            throw e;
        }finally {
            if(fileOutputStream != null ){
                fileOutputStream.close();
            }
        }
    }
}
