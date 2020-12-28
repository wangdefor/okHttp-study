package com.wy.contants;

import com.wy.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * @Classname CommonContants
 * @Description CommonContants 常亮类
 * @Date 2020/12/28 17:20
 * @Created wangyong
 */
@Slf4j
public class CommonContants {

    /**
     * 登陆之后的cookie信息 直接进入浏览器进入下单页面 复制request中的header中的cookie值
     */
    public static String GOOGLE_COOKIE;

    /**
     * 获取方式 随便找一个商品下单，然后进入结算页面，打开浏览器的调试窗口，切换到控制台Tab页，在控制台中输入变量`_JdTdudfp`，即可从输出的Json中获取`eid`和`fp`。
     */
    public static final String eid = "KMIXFQHHCWMFPL7CC4GK7MSWCUGWM2AMF72VC2W3A66TSHLWVCRFREUH33JBQFUA3ELE5IWM4ITFQSX4DQHEXP6JEM";

    /**
     * 获取方式 随便找一个商品下单，然后进入结算页面，打开浏览器的调试窗口，切换到控制台Tab页，在控制台中输入变量`_JdTdudfp`，即可从输出的Json中获取`eid`和`fp`。
     */
    public static final String fp = "8a8a626d57254f9ea209f1ddda8aa2b2";

    /**
     * 茅台sku
     */
    public static final String skuId = "100012043978";


    static {
        try {
            GOOGLE_COOKIE = UserService.init();
        } catch (Exception e) {
            log.error("获取cookie 失败");
        }
    }
}
