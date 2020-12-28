package com.wy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Classname TicketVo
 * @Description TODO
 * @Date 2020/12/28 14:40
 * @Created wangyong
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TicketVo implements Serializable {

    private String ticket;
}
