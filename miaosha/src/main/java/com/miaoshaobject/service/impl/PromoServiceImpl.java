package com.miaoshaobject.service.impl;

import com.miaoshaobject.dao.PromoDOMapper;
import com.miaoshaobject.dataobject.PromoDO;
import com.miaoshaobject.service.PromoService;
import com.miaoshaobject.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author xiaoyi
 * @create 2022-10-08 16:37
 */
@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoDOMapper promoDOMapper;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        //获取对应商品的秒杀活动信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);
        //System.out.println(promoDO.toString()+"  id == "+itemId);

        //dataobject-->model
        PromoModel promoModel = convertPromoModelFromPromoDO(promoDO);
        if(promoModel == null){
            return null;
        }

        //判断秒杀活动是否是正在进行或即将开始
        DateTime now = new DateTime();
        if(promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);
        }else if(promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);
        }else{
            promoModel.setStatus(2);
        }
        //System.out.println(promoModel);
        return promoModel;
    }
    private PromoModel convertPromoModelFromPromoDO(PromoDO promoDO){
        if(promoDO == null){
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO,promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }
}
