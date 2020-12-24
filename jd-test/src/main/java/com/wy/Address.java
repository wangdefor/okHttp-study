package com.wy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Classname Address
 * @Description Address
 * @Date 2020/12/24 10:08
 * @Created wangyong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address implements Serializable {

    private String addressDetail;

    private String addressName;

    private String areaCode;

    private Integer cityId;

    private String cityName;

    private Integer countyId;

    private String countyName;

    private Boolean defaultAddress;

    private Long id;

    private String mobile;

    private String mobileKey;

    private String name;

    private Integer overseas;

    private String postCode;

    private Integer provinceId;

    private String provinceName;

    private Integer townId;

    private String townName;

    private Boolean yuyueAddress;

}
