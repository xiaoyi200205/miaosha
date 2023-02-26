package com.miaoshaobject.service.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xiaoyi
 * @create 2022-10-08 11:18
 */
//用户下单的交易模型
@Data
public class OrderModel {
    //购买的订单号
    private String id;

    //购买商品的单价，当时购买商品的价格,若promoId不等于空，则是以秒杀价格下单。
    private BigDecimal itemPrice;

    //购买的用户id
    private Integer userId;

    //若是非空，则是以秒杀商品的方式下单
    private Integer promoId;

    //购买的商品id
    private Integer itemId;
    //购买的数量
    private Integer amount;
    //购买的金额
    private BigDecimal orderPrice;

}
