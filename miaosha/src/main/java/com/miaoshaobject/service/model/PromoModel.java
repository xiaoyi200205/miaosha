package com.miaoshaobject.service.model;

import lombok.Data;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author xiaoyi
 * @create 2022-10-08 16:25
 */
@Data
public class PromoModel implements Serializable {
    private Integer id;

    //秒杀活动状态1还没有开始，2.进行中，3.已经结束
    private Integer status;
    //秒杀的活动名称
    private String promoName;

    //秒杀活动的开始时间
    private DateTime startDate;

    //秒杀活动的结束时间
    private DateTime endDate;

    //秒杀活动的是适用商品
    private Integer itemId;

    //秒杀活动的商品销售价格
    private BigDecimal promoItemPrice;

}
