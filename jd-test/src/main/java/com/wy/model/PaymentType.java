package com.wy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Classname PaymentType
 * @Description PaymentType
 * @Date 2020/12/24 10:13
 * @Created wangyong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentType implements Serializable {

    private Integer paymentId;

    private String paymentName;
}
