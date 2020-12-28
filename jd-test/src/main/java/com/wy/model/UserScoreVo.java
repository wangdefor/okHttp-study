package com.wy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Classname UserScoreVo
 * @Description UserScoreVo
 * @Date 2020/12/23 17:53
 * @Created wangyong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserScoreVo implements Serializable {


    private Integer accountScore;

    private Integer activityScore;

    private Integer consumptionScore;

    private Integer financeScore;

    private String pin;

    private Integer riskScore;

    private Integer totalScore;


}
