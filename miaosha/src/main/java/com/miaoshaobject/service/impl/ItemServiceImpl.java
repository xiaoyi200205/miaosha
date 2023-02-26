package com.miaoshaobject.service.impl;

import com.miaoshaobject.dao.ItemDOMapper;
import com.miaoshaobject.dao.ItemStockDOMapper;
import com.miaoshaobject.dao.PromoDOMapper;
import com.miaoshaobject.dataobject.ItemDO;
import com.miaoshaobject.dataobject.ItemStockDO;
import com.miaoshaobject.error.BusinessException;
import com.miaoshaobject.error.EmBusinessError;
import com.miaoshaobject.service.ItemService;
import com.miaoshaobject.service.PromoService;
import com.miaoshaobject.service.model.ItemModel;
import com.miaoshaobject.service.model.PromoModel;
import com.miaoshaobject.validator.ValidationResult;
import com.miaoshaobject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaoyi
 * @create 2022-10-07 18:01
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private PromoService promoService;

    private ItemDO convertItemDOFromItemModel(ItemModel itemModel){
        if(itemModel==null){
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel,itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }

    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());
        return itemStockDO;
    }

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        //校验
        ValidationResult validate = validator.validate(itemModel);
        if(validate.isHasError()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,validate.getErrMsg());
        }

        //将itemobjecet转化为dataobject
        ItemDO itemDO = this.convertItemDOFromItemModel(itemModel);


        //写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());
        ItemStockDO itemStockDO = this.convertItemStockDOFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);

        //返回创建好的一个对象
        return this.getItemById(itemModel.getId());
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if(itemDO == null){
            return null;
        }
        //操作获得库存数量
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
        //将dataObject转化为model
        ItemModel itemModel = convertModelFromDataObject(itemDO, itemStockDO);

        //获取活动商品的信息
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
        //System.out.println(promoModel);
        if(promoModel != null&&promoModel.getStatus().intValue()!=3){
            itemModel.setPromoModel(promoModel);
        }


        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) {
        int affectedRow = itemStockDOMapper.itemDecreaseStock(itemId, amount);
        if(affectedRow>0){
            //更新库存成功
            return true;
        }else {
            //更新库存失败
            return false;
        }
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) {
        itemDOMapper.increaseSales(itemId,amount);
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOS = itemDOMapper.listItem();
        List<ItemModel> itemModelList = itemDOS.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel = this.convertModelFromDataObject(itemDO, itemStockDO);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }

    //将dataObject转化为model
    private ItemModel convertModelFromDataObject(ItemDO itemDO,ItemStockDO itemStockDO){
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }
}
