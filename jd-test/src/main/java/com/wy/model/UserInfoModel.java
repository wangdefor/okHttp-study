package com.wy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Classname UserInfoModel
 * @Description UserInfoModel
 * @Date 2020/12/23 17:37
 * @Created wangyong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoModel implements Serializable {

    private String householdAppliance;

    private String imgUrl;

    private String lastLoginTime;

    private String nickName;

    private Integer plusStatus;

    private Integer userLevel;

    private UserScoreVo userScoreVO;


}
