package com.miaoshaobject.service.impl;

import com.miaoshaobject.dao.OrderDOMapper;
import com.miaoshaobject.dao.SequenceDOMapper;
import com.miaoshaobject.dataobject.OrderDO;
import com.miaoshaobject.dataobject.SequenceDO;
import com.miaoshaobject.error.BusinessException;
import com.miaoshaobject.error.EmBusinessError;
import com.miaoshaobject.service.ItemService;
import com.miaoshaobject.service.OrderService;
import com.miaoshaobject.service.UserService;
import com.miaoshaobject.service.model.ItemModel;
import com.miaoshaobject.service.model.OrderModel;
import com.miaoshaobject.service.model.UserModel;
import javafx.util.converter.LocalDateTimeStringConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author xiaoyi
 * @create 2022-10-08 12:50
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;


    @Autowired
    private UserService userService;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId,Integer promoId, Integer amount) throws BusinessException {

        //1.校验下单状态，下单的商品是否存在，用户是否合法，购买的数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        if(itemModel == null){
            throw  new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品信息不存在");
        }
        UserModel userModel = userService.getUserById(userId);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户信息不存在");
        }
        if(amount<=0||amount>99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"数量信息不正确");
        }

        //1.2校验活动信息
        if(promoId !=null){
            //(1)校验对应活动是否存在这个适用商品
            if(promoId.intValue()!=itemModel.getId()){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动信息不正确");
                //(2)校验活动是否正在进行中
            }else if (itemModel.getPromoModel().getStatus().intValue()!=2){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动还未开始");
            }
        }


        //2.落单减库存，支付减库存
        boolean result = itemService.decreaseStock(itemId,amount);
        if(!result){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        //3.订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        orderModel.setPromoId(promoId);
        if(promoId!=null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else {
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));



        //生成交易流水号，即订单ID
        orderModel.setId(generateOrderNo());
        OrderDO orderDO = convertOrderDOFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);

        //3.1将指定商品的销量加
        itemService.increaseSales(itemId,amount);
        //4.返回前端
        return orderModel;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateOrderNo(){
        //订单号有16位
        StringBuilder stringBuilder = new StringBuilder();
        //前8位为事件信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDay = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        stringBuilder.append(nowDay);
        //中间6位为自增序列
        //获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue()+sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKey(sequenceDO);

        //拼接字符串
        String sequenceStr = String.valueOf(sequence);
        for(int i=0;i<6-sequenceStr.length();i++){
            stringBuilder.append("0");
        }
        stringBuilder.append(sequenceStr);

        //最后两位为分库分表,暂时写死
        stringBuilder.append("00");
        return stringBuilder.toString();
    }

    private OrderDO convertOrderDOFromOrderModel(OrderModel orderModel){
        if(orderModel == null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderDO;
    }
}
