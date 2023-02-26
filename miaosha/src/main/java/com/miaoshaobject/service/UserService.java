package com.miaoshaobject.service;

import com.miaoshaobject.error.BusinessException;
import com.miaoshaobject.service.model.UserModel;

/**
 * @author xiaoyi
 * @create 2022-09-29 21:07
 */
public interface UserService {
    //通过用户id获取用户对象
    UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BusinessException;

    /*
    * telphone  是用户注册手机
    * password  是用户加密后的密码
    * */
    UserModel validateLogin(String telphone,String password) throws BusinessException;
}
