package com.miaoshaobject.service.impl;

import com.miaoshaobject.dao.UserDoMapper;
import com.miaoshaobject.dao.UserPasswordDoMapper;
import com.miaoshaobject.dataobject.UserDo;
import com.miaoshaobject.dataobject.UserPasswordDo;
import com.miaoshaobject.error.BusinessException;
import com.miaoshaobject.error.EmBusinessError;
import com.miaoshaobject.service.UserService;
import com.miaoshaobject.service.model.UserModel;
import com.miaoshaobject.validator.ValidationResult;
import com.miaoshaobject.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xiaoyi
 * @create 2022-09-29 21:08
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDoMapper userDoMapper;

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    UserPasswordDoMapper userPasswordDoMapper;

    @Override
    public UserModel getUserById(Integer id) {
        UserDo userDo = userDoMapper.selectByPrimaryKey(id);

        if(userDo == null){
            return null;
        }
        //通过用户的i获取用户的加密密码信息
        UserPasswordDo userPasswordDo = userPasswordDoMapper.selectByUserId(userDo.getId());
        return convertFromDataObject(userDo,userPasswordDo);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if (userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
//        if(StringUtils.isEmpty(userModel.getName())
//                ||userModel.getGender()==null
//                ||userModel.getAge()==null
//                ||StringUtils.isEmpty(userModel.getTelphone())){
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//        }
        ValidationResult validationResult = validator.validate(userModel);
        if(validationResult.isHasError()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,validationResult.getErrMsg());
        }

        //实现model--》转为dataObject
        UserDo userDo = convertFromModel(userModel);

        //捕获异常，手机相同就是就不能再次注册
        try{
            userDoMapper.insertSelective(userDo);
        }catch(DuplicateKeyException exception){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号已注册");
        }
        userModel.setId(userDo.getId());

        UserPasswordDo userPasswordDo = convertPasswordFromModel(userModel);
        userPasswordDoMapper.insertSelective(userPasswordDo);
    }

    @Override
    public UserModel validateLogin(String telphone, String password) throws BusinessException {
        //通过用户的手机获取用户的信息
        UserDo userDo = userDoMapper.selectByTelphone(telphone);
        //System.out.println("UserDao:"+userDo);
        if(userDo == null){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDo userPasswordDo = userPasswordDoMapper.selectByUserId(userDo.getId());
        //System.out.println("passwordDao:"+userPasswordDo);
        UserModel userModel = convertFromDataObject(userDo,userPasswordDo);
        //System.out.println("usermodel:"+userModel);

        //比对加密的密码和否和传输进来的密码相匹对
        if (!StringUtils.equals(password,userModel.getEncrptPassword())){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;
    }

    private UserPasswordDo convertPasswordFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserPasswordDo userPasswordDo = new UserPasswordDo();
        userPasswordDo.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDo.setUserId(userModel.getId());
        return userPasswordDo;
    }
    private UserDo convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserDo userDo  = new UserDo();
        BeanUtils.copyProperties(userModel,userDo);
        return userDo;
    }

    private UserModel convertFromDataObject(UserDo userDo, UserPasswordDo userPasswordDo){
        if(userDo == null ){
            return null;
        }

        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDo,userModel);
        if(userPasswordDo != null){
            userModel.setEncrptPassword(userPasswordDo.getEncrptPassword());
        }

        return userModel;
    }
}
