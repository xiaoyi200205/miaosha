package com.miaoshaobject.controller;


import com.miaoshaobject.error.BusinessException;
import com.miaoshaobject.error.EmBusinessError;
import com.miaoshaobject.response.CommonReturnType;
import com.miaoshaobject.service.OrderService;
import com.miaoshaobject.service.model.OrderModel;
import com.miaoshaobject.service.model.UserModel;

import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xiaoyi
 * @create 2022-10-08 14:47
 */
@Controller
@RequestMapping(value = "/order")
@CrossOrigin(allowCredentials = "true",originPatterns = "*")
public class OrderController extends BaseController{
    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate;

    //封装下单请求
    @RequestMapping(value = "/create",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId")Integer itemId,
                                        @RequestParam(name = "amount")Integer amount,
                                        @RequestParam(name = "promoId",required = false)Integer promoId
                                        ) throws BusinessException {
        //Boolean is_login = (Boolean)httpServletRequest.getSession().getAttribute("IS_LOGIN");
//        System.out.println(is_login);
//        System.out.println(this.httpServletRequest.getSession().getAttribute("IS_LOGIN"));

        //获取token
        String token = this.httpServletRequest.getParameterMap().get("token")[0];
        if(StringUtils.isEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }
//        if(is_login == null || !is_login.booleanValue()) {
//            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
//        }
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);

        //会话过期
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

//        //获取用户登录信息
//        UserModel userModel = (UserModel)httpServletRequest.getSession().getAttribute("LOGIN_USER");


        OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId,promoId, amount);
        return CommonReturnType.create(orderModel);

    }
}
