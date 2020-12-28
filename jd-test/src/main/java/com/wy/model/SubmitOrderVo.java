package com.wy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Classname SubmitOrderVo
 * @Description TODO
 * @Date 2020/12/24 9:47
 * @Created wangyong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmitOrderVo implements Serializable {

    private String skuId;

    private Integer num;

    private Long addressId;

    private Boolean yuShou;

    private Boolean isModifyAddress;

    private String name;

    private Integer provinceId;

    private Integer cityId;

    private Integer countyId;

    private Integer townId;

    private String addressDetail;

    private String mobile;

    private String mobileKey;

    private String email;

    private String postCode;

    private Integer invoiceTitle;

    private String invoiceCompanyName;

    private Integer invoiceContent;

    private String invoiceTaxpayerNO;

    private String invoiceEmail;

    private String invoicePhone;

    private String invoicePhoneKey;

    private Boolean invoce;

    private String password;

    private Integer codTimeType;

    private Integer paymentType;

    private String areaCode;

    private Integer overseas;

    private String phone;

    private String eid;

    private String fp;

    private String token;

    private String pru;
}
