package com.wy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Classname InvoiceInfo
 * @Description TODO
 * @Date 2020/12/24 10:12
 * @Created wangyong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceInfo implements Serializable {

    private String invoiceCode;

    private String invoiceCompany;

    private Integer invoiceContentType;

    private String invoicePhone;

    private String invoicePhoneKey;

    private Integer invoiceTitle;

    private Integer invoiceType;
}
