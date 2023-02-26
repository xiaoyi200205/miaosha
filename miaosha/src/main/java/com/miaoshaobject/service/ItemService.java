package com.miaoshaobject.service;

import com.miaoshaobject.dataobject.ItemDO;
import com.miaoshaobject.error.BusinessException;
import com.miaoshaobject.service.model.ItemModel;
import org.springframework.web.server.session.InMemoryWebSessionStore;

import java.util.List;

/**
 * @author xiaoyi
 * @create 2022-10-07 17:58
 */
public interface ItemService {

    //创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;
    //商品列表浏览
    List<ItemModel> listItem();

    //商品详情浏览
    ItemModel getItemById(Integer id);

    //库存扣减
    boolean decreaseStock(Integer itemId, Integer amount);

    //商品销量增加
    void increaseSales(Integer itemId,Integer amount);


}
