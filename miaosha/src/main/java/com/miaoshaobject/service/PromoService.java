package com.miaoshaobject.service;

import com.miaoshaobject.service.model.PromoModel;

/**
 * @author xiaoyi
 * @create 2022-10-08 16:35
 */
public interface PromoService {
    PromoModel getPromoByItemId(Integer itemId);
}
