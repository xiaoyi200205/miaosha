package com.miaoshaobject.service;

import com.miaoshaobject.error.BusinessException;
import com.miaoshaobject.service.model.OrderModel;

/**
 * @author xiaoyi
 * @create 2022-10-08 12:49
 */
public interface OrderService {
    //使用1.通过前端url上传过来的秒杀活动id，然后下单接口内校验对应的商品有秒杀活动且已经开始
    //2.直接在下单接口内判断对应的商品是否存在秒杀活动，若存在进行中的一秒杀价格下单
    OrderModel createOrder(Integer UserId,Integer itemId,Integer promoId,Integer amount) throws BusinessException;
}
