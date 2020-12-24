package com.wy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Classname SeckillSkuVO
 * @Description SeckillSkuVO
 * @Date 2020/12/24 10:14
 * @Created wangyong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeckillSkuVO implements Serializable {

    private List<Address> addressList;

    private Integer buyNum;

    private String code;

    private Integer freight;

    private InvoiceInfo invoiceInfo;

    private List<PaymentType> paymentTypeList;

    private Skill seckillSkuVO;

    private String token;

    private ShipmentParam shipmentParam;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Skill implements Serializable {


        private List<Object> attachs;

        private String color;

        private ExtMap extMap;

        private List<Gift> gifts;

        private BigDecimal height;

        private BigDecimal jdPrice;

        private BigDecimal length;

        private Integer num;

        private BigDecimal rePrice;

        private String size;

        private String skuId;

        private String skuImgUrl;

        private String skuName;

        private BigDecimal skuPrice;

        private BigDecimal thirdCategoryId;

        private String venderName;

        private Integer venderType;

        private BigDecimal weight;

        private BigDecimal width;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ShipmentParam implements Serializable {

        private Integer shipmentTimeType;

        private String shipmentTimeTypeName;

        private String shipmentTypeName;

        private Integer shipmentType;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Gift implements Serializable {

        private Integer num;

        private String sku;

        private String skuName;

        private Integer type;

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ExtMap implements Serializable {

        private String YuShou;

        private String is7ToReturn;

        private String new7ToReturn;

        private String thwa;

        private String SoldOversea;

    }
}
