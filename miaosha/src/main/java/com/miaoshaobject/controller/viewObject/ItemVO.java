package com.miaoshaobject.controller.viewObject;

import lombok.Data;
import org.joda.time.DateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author xiaoyi
 * @create 2022-10-07 20:28
 */
@Data
public class ItemVO {
    private Integer id;

    //商品名
    private String title;

    //商品价格
    private BigDecimal price;

    //商品库存
    private Integer stock;

    //商品的描述
    private String description;

    //商品的销量
    private Integer sales;

    //商品描述图片的URL
    private String imgUrl;

    //商品是否在秒杀活动和中，和状态
    //1.还未开始 2.进行中  3.已经结束
    private Integer promoStatus;

    //秒杀活动价格
    private BigDecimal promoPrice;

    //秒杀活动的ID
    private Integer promoId;

    //秒杀活动的开始时间
    private String startDate;

}
