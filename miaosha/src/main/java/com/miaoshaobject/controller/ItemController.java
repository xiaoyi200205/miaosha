package com.miaoshaobject.controller;

import com.miaoshaobject.controller.viewObject.ItemVO;
import com.miaoshaobject.error.BusinessException;
import com.miaoshaobject.response.CommonReturnType;
import com.miaoshaobject.service.CacheService;
import com.miaoshaobject.service.ItemService;
import com.miaoshaobject.service.model.ItemModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xiaoyi
 * @create 2022-10-07 20:28
 */
@Controller
@RequestMapping(value = "/item")
@CrossOrigin(allowCredentials = "true",originPatterns = "*")
public class ItemController extends BaseController{

    @Autowired
    private ItemService itemService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CacheService cacheService;

    //创建商品的controller
    @RequestMapping(value = "/create",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title")String title
                                        ,@RequestParam(name = "description")String description
                                        ,@RequestParam(name = "price") BigDecimal price
                                        ,@RequestParam(name = "stock")Integer stock
                                        ,@RequestParam(name = "imgUrl")String imgUrl
                                       ) throws BusinessException {
        //封装service请求来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);

        ItemModel itemModelForReturn = itemService.createItem(itemModel);

        //将创建完的商品信息返回给前端
        ItemVO itemVO = convertItemVOFromItemModel(itemModelForReturn);

        return CommonReturnType.create(itemVO);
    }

    //商品详情页浏览
    @RequestMapping(value = "/get",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id")Integer id){
        /*
        * 多级缓存，现在这里有三级缓存：1.热点缓存 2.redis缓存 3.数据库
        * */
        ItemModel itemModel = null;

        //先取本地缓存
        itemModel = (ItemModel)cacheService.getFromCommonCache("item_" + id);

        //根据商品的id到redis内获取
        if(itemModel == null) {
            itemModel = (ItemModel) redisTemplate.opsForValue().get("item_" + id);
            //若redis内不存在对应的itemModel，则访问下游的service
            if(itemModel == null){
                itemModel =  itemService.getItemById(id);
                //然后设置itemModel到redis内
                redisTemplate.opsForValue().set("item_"+id,itemModel);
                //使用一种比较笨的方法来社子和过期时间，就是我们直接设定
                redisTemplate.expire("item_"+id,10, TimeUnit.MINUTES);
            }
            //填充热点缓存
            cacheService.setCommonCache("item_"+id,itemModel);
        }
        ItemVO itemVO = convertItemVOFromItemModel(itemModel);
        return CommonReturnType.create(itemVO);

    }


    //商品列表页面浏览
    @RequestMapping(value = "/list",method = {RequestMethod.GET}/*,consumes = {CONTENT_TYPE_FORMED}*/)
    @ResponseBody
    public CommonReturnType listItem(){
        //使用api将list内的itemModel转化为itemVO
        List<ItemModel> itemModelList = itemService.listItem();
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = this.convertItemVOFromItemModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }




    private ItemVO convertItemVOFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);
        if(itemModel.getPromoModel()!=null){
            //有正在进行或是即将进行的秒杀活动
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getItemId());
            //itemVO.setStartDate(itemModel.getPromoModel().getStartDate());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
           //itemVO.setStartDate(DateTime.parse(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))));
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else{
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }

}
